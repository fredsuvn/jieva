package xyz.srclab.common.net.tcp

import xyz.srclab.common.base.DEFAULT_IO_BUFFER_SIZE
import xyz.srclab.common.io.newByteBuffer
import xyz.srclab.common.run.RunLatch
import xyz.srclab.common.run.newCachedThreadPoolRunner
import java.io.IOException
import java.io.InputStream
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.*
import java.util.concurrent.Executor

/**
 * Server with TCP/IP.
 */
interface TcpServer {

    /**
     * Starts server.
     */
    fun start()

    /**
     * Stops server. Same as `stop(false)`.
     */
    fun stop() {
        stop(false)
    }

    /**
     * Stops server.
     */
    fun stop(immediately: Boolean)

    /**
     * Blocks current thread until the server has stopped.
     */
    fun await()

    companion object {

        /**
         * Creates a new simple NIO [TcpServer].
         */
        @JvmOverloads
        @JvmStatic
        fun nioServer(
            bindAddress: InetSocketAddress,
            channelHandler: TcpChannelHandler,
            executor: Executor = newCachedThreadPoolRunner().asExecutor(),
            bufferSize: Int = DEFAULT_IO_BUFFER_SIZE,
            directBuffer: Boolean = false
        ): TcpServer {
            return NIOTcpServer(bindAddress, channelHandler, executor, bufferSize, directBuffer)
        }

        private class NIOTcpServer(
            val bindAddress: InetSocketAddress,
            private val channelHandler: TcpChannelHandler,
            private val executor: Executor,
            private val bufferSize: Int,
            private val directBuffer: Boolean = false
        ) : TcpServer {

            private var selector: Selector? = null
            private var serverSocketChannel: ServerSocketChannel? = null
            private val listenLatch: RunLatch = RunLatch.newRunLatch()

            @Synchronized
            override fun start() {
                if (selector !== null || serverSocketChannel !== null) {
                    throw IllegalStateException("Server has been started, stop it first!")
                }
                start0()
            }

            @Synchronized
            override fun stop(immediately: Boolean) {
                val selector = this.selector
                if (selector === null) {
                    throw IllegalStateException("Server has not been started, start it first!")
                }
                val serverSocketChannel = this.serverSocketChannel
                if (serverSocketChannel === null) {
                    throw IllegalStateException("Server has not been started, start it first!")
                }
                selector.close()
                serverSocketChannel.close()
                this.selector = null
                this.serverSocketChannel = null
            }

            override fun await() {
                listenLatch.await()
            }

            private fun start0() {
                val selector = Selector.open()
                val serverSocketChannel = ServerSocketChannel.open()
                serverSocketChannel.configureBlocking(false)
                serverSocketChannel.bind(bindAddress)
                serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT)
                this.selector = selector
                this.serverSocketChannel = serverSocketChannel
                executor.execute { listen(selector) { handleKey(it) } }
            }

            private fun listen(selector: Selector, handler: (SelectionKey) -> Unit) {
                listenLatch.lockTo(1)
                while (true) {
                    try {
                        val readyChannels = selector.select()
                        if (readyChannels == 0) {
                            continue
                        }
                        val selectedKeys = selector.selectedKeys()
                        val iterator = selectedKeys.iterator()
                        while (iterator.hasNext()) {
                            val key = iterator.next()
                            handler(key)
                            iterator.remove();
                        }
                    } catch (e: ClosedSelectorException) {
                        break
                    }
                }
                listenLatch.unlock()
            }

            private fun handleKey(key: SelectionKey) {
                val selector = key.selector()
                if (key.isAcceptable) {
                    //val server = key.channel() as ServerSocketChannel
                    val socketChannel = serverSocketChannel!!.accept()
                    socketChannel.configureBlocking(false)
                    socketChannel.register(selector, SelectionKey.OP_READ)
                    //socketChannel.register(selector, SelectionKey.OP_WRITE)
                    val context = SimpleContext(selector, socketChannel)
                    executor.execute { channelHandler.onOpen(context) }
                }
                if (key.isReadable) {
                    val socketChannel = key.channel() as SocketChannel
                    val context = SimpleContext(selector, socketChannel)
                    val buffer = newByteBuffer(bufferSize, directBuffer)
                    try {
                        val readCount = socketChannel.read(buffer)
                        if (readCount < 0) {
                            socketChannel.close()
                            executor.execute { channelHandler.onClose(context) }
                            return
                        }
                    } catch (e: IOException) {
                        socketChannel.close()
                        executor.execute { channelHandler.onClose(context) }
                        return
                    }
                    buffer.flip()
                    executor.execute { channelHandler.onReceive(context, buffer.asReadOnlyBuffer()) }
                }
            }

            private inner class SimpleContext(
                private val selector: Selector,
                private val socketChannel: SocketChannel
            ) : TcpContext {

                override val remoteAddress: InetSocketAddress = socketChannel.remoteAddress as InetSocketAddress

                override fun write(data: ByteBuffer) {
                    socketChannel.write(data)
                    socketChannel.register(selector, SelectionKey.OP_READ)
                }

                override fun write(data: InputStream) {
                    val buffer = ByteArray(bufferSize)
                    var len = data.read(buffer)
                    while (len >= 0) {
                        if (len > 0) {
                            write(buffer)
                        }
                        len = data.read(buffer)
                    }
                }

                override fun closeChannel() {
                    socketChannel.close()
                }
            }
        }
    }
}