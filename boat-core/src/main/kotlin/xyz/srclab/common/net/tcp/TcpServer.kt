package xyz.srclab.common.net.tcp

import xyz.srclab.common.base.DEFAULT_IO_BUFFER_SIZE
import xyz.srclab.common.net.NetServer
import xyz.srclab.common.net.buffer.ByteBufferPool
import xyz.srclab.common.net.buffer.PooledByteBuffer
import xyz.srclab.common.net.nioListen
import xyz.srclab.common.run.AsyncRunner
import xyz.srclab.common.run.RunLatch
import java.io.IOException
import java.io.InputStream
import java.net.SocketAddress
import java.nio.ByteBuffer
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel
import java.util.concurrent.BlockingQueue
import java.util.concurrent.Executor
import java.util.concurrent.LinkedBlockingQueue

/**
 * Server for TCP/IP.
 */
interface TcpServer : NetServer {

    companion object {

        /**
         * Creates a new simple NIO [TcpServer].
         */
        @JvmOverloads
        @JvmStatic
        fun nioServer(
            bindAddress: SocketAddress,
            channelHandler: TcpChannelHandler,
            executor: Executor = AsyncRunner.asExecutor(),
            bufferSize: Int = DEFAULT_IO_BUFFER_SIZE,
            bufferPool: ByteBufferPool = ByteBufferPool.simpleByteBufferPool()
        ): TcpServer {
            return NioTcpServer(bindAddress, channelHandler, executor, bufferSize, bufferPool)
        }

        private class NioTcpServer(
            override val bindAddress: SocketAddress,
            private val channelHandler: TcpChannelHandler,
            private val executor: Executor,
            private val bufferSize: Int,
            private val bufferPool: ByteBufferPool
        ) : TcpServer {

            private var selector: Selector? = null
            private var serverSocketChannel: ServerSocketChannel? = null
            private val listenLatch: RunLatch = RunLatch.newRunLatch()

            private var tempBuffer: PooledByteBuffer? = null

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
                listenLatch.lockDown()
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
                executor.execute {
                    listenLatch.lockTo(1)
                    nioListen(selector) { handleKey(it) }
                    listenLatch.lockDown()
                }
            }

            private fun handleKey(key: SelectionKey) {
                val selector = key.selector()
                if (key.isAcceptable) {
                    //val socketChannel = serverSocketChannel!!.accept()
                    val serverChannel = key.channel() as ServerSocketChannel
                    val clientChannel = serverChannel.accept()
                    clientChannel.configureBlocking(false)
                    clientChannel.register(selector, SelectionKey.OP_READ)
                    //socketChannel.register(selector, SelectionKey.OP_WRITE)
                    val remoteAddress = clientChannel.remoteAddress
                    executor.execute {
                        val context = SimpleContext(selector, clientChannel, remoteAddress)
                        channelHandler.onOpen(context)
                    }
                }
                if (key.isReadable) {
                    val socketChannel = key.channel() as SocketChannel
                    val remoteAddress = socketChannel.remoteAddress
                    val buffer = getBuffer()
                    val buf = buffer.asByteBuffer()
                    try {
                        val readCount = socketChannel.read(buf)
                        if (readCount < 0) {
                            //println("0000000000000000000")
                            socketChannel.close()
                            executor.execute {
                                val context = SimpleContext(selector, socketChannel, remoteAddress)
                                channelHandler.onClose(context)
                            }
                            tempBuffer = buffer
                            return
                        }
                        if (readCount == 0) {
                            tempBuffer = buffer
                            return
                        }
                    } catch (e: IOException) {
                        println("eeeeeeeeee: $e")
                        socketChannel.close()
                        executor.execute {
                            val context = SimpleContext(selector, socketChannel, remoteAddress)
                            channelHandler.onClose(context)
                        }
                        tempBuffer = buffer
                        return
                    }
                    executor.execute {
                        buf.flip()
                        val context = SimpleContext(selector, socketChannel, remoteAddress)
                        channelHandler.onReceive(context, buf.asReadOnlyBuffer())
                        bufferPool.releaseBuffer(buffer)
                    }
                    tempBuffer = null
                }
            }

            private fun getBuffer(): ByteBufferPool.PooledByteBuffer {
                val tempBuffer = this.tempBuffer
                if (tempBuffer !== null) {
                    return tempBuffer
                }
                return bufferPool.getBuffer()
            }

            private class SelectHandler(
                private val server: ServerSocketChannel,
                private val executor: Executor
            ) {

                private val blockingQueue: BlockingQueue<SocketChannel> = LinkedBlockingQueue()

                fun start() {
                }

                private fun start0() {

                }
            }

            private inner class SimpleContext(
                private val selector: Selector,
                private val socketChannel: SocketChannel,
                override val remoteAddress: SocketAddress
            ) : TcpContext {

                override val server: TcpServer = this@NioTcpServer

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

                override fun disconnect() {
                    socketChannel.close()
                }
            }
        }
    }
}