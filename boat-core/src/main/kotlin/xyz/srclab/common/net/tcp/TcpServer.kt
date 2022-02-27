package xyz.srclab.common.net.tcp

import xyz.srclab.common.base.DEFAULT_IO_BUFFER_SIZE
import xyz.srclab.common.net.ByteBufferPool
import xyz.srclab.common.net.NetSelector
import xyz.srclab.common.net.NetSelector.Companion.toNetSelector
import xyz.srclab.common.net.NetServer
import xyz.srclab.common.net.PooledByteBuffer
import xyz.srclab.common.run.AsyncRunner
import xyz.srclab.common.run.RunLatch
import java.io.IOException
import java.io.InputStream
import java.net.SocketAddress
import java.nio.ByteBuffer
import java.nio.channels.*
import java.util.concurrent.Executor

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

            private var selector: NetSelector? = null
            private var serverChannel: ServerSocketChannel? = null
            private val waitLatch: RunLatch = RunLatch.newRunLatch()

            private var tempBuffer: PooledByteBuffer? = null

            @Synchronized
            override fun start() {
                if (selector !== null || serverChannel !== null) {
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
                val serverChannel = this.serverChannel
                if (serverChannel === null) {
                    throw IllegalStateException("Server has not been started, start it first!")
                }
                selector.close()
                serverChannel.close()
                this.selector = null
                this.serverChannel = null
                waitLatch.lockDown()
            }

            override fun await() {
                waitLatch.await()
            }

            private fun start0() {
                val select = Selector.open()
                val serverChannel = ServerSocketChannel.open()
                serverChannel.configureBlocking(false)
                serverChannel.bind(bindAddress)
                serverChannel.register(select, SelectionKey.OP_ACCEPT)
                val selector = select.toNetSelector()
                this.selector = selector
                this.serverChannel = serverChannel
                executor.execute {
                    waitLatch.lockTo(1)
                    while (true) {
                        try {
                            handleKey(selector.next())
                        } catch (e: ClosedSelectorException) {
                            break
                        }
                    }
                    waitLatch.lockDown()
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
                        //println("eeeeeeeeee: $e")
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

            private fun getBuffer(): PooledByteBuffer {
                val tempBuffer = this.tempBuffer
                if (tempBuffer !== null) {
                    return tempBuffer
                }
                return bufferPool.getBuffer()
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