package xyz.srclab.common.net.tcp

import xyz.srclab.common.base.defaultBufferSize
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
            tcpListener: TcpListener,
            executor: Executor = AsyncRunner,
            bufferSize: Int = defaultBufferSize(),
            bufferPool: ByteBufferPool = ByteBufferPool.simpleByteBufferPool()
        ): TcpServer {
            return NioTcpServer(bindAddress, tcpListener, executor, bufferSize, bufferPool)
        }

        private class NioTcpServer(
            override val bindAddress: SocketAddress,
            private val tcpListener: TcpListener,
            private val executor: Executor,
            private val bufferSize: Int,
            private val bufferPool: ByteBufferPool
        ) : TcpServer {

            private var selector: NetSelector? = null
            private var serverChannel: ServerSocketChannel? = null
            private val waitLatch: RunLatch = RunLatch.newRunLatch()

            private var tempBuffer: PooledByteBuffer? = null

            //private var readNode: KeyNode? = null
            //private var writeNode: KeyNode? = null

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
                if (selector !== null) {
                    selector.close()
                    this.selector = null
                }
                val serverChannel = this.serverChannel
                if (serverChannel !== null) {
                    serverChannel.close()
                    this.serverChannel = null
                }
                //readNode = null
                //writeNode = null
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
                    selectKeys(selector)
                    waitLatch.lockDown()
                }
            }

            private fun selectKeys(selector: NetSelector) {
                while (true) {
                    try {
                        val key = selector.next()
                        handleKey(key)
                        //val newNode = KeyNode(key)
                        //val writeNode = this.writeNode
                        //if (writeNode === null) {
                        //    this.writeNode = newNode
                        //    this.readNode = newNode
                        //    executor.execute {
                        //        handleKeys()
                        //    }
                        //} else {
                        //    writeNode.next = newNode
                        //    this.writeNode = newNode
                        //}
                    } catch (e: ClosedSelectorException) {
                        break
                    }
                }
            }

            //private fun handleKeys() {
            //    while (true) {
            //        val currentRead = this.readNode
            //        if (currentRead === null) {
            //            return
            //        }
            //        if (currentRead.isUsed) {
            //            val nextRead = currentRead.next
            //            if (nextRead === null) {
            //                sleep(1)
            //                continue
            //            } else {
            //                this.readNode = nextRead
            //                continue
            //            }
            //        }
            //        //Do with key
            //        val key = currentRead.key
            //        handleKey(key)
            //        currentRead.isUsed = true
            //    }
            //}

            private fun handleKey(key: SelectionKey) {
                if (key.isAcceptable) {
                    val serverChannel = key.channel() as ServerSocketChannel
                    val clientChannel = serverChannel.accept()
                    try {
                        val clientContext = NioTcpContext(clientChannel)
                        clientChannel.configureBlocking(false)
                        clientChannel.register(key.selector(), SelectionKey.OP_READ, clientContext)
                        executor.execute {
                            tcpListener.onOpen(clientContext)
                        }
                    } catch (e: ClosedChannelException) {
                        clientChannel.close()
                    }
                }
                if (key.isReadable) {
                    val clientChannel = key.channel() as SocketChannel
                    //val clientContext = clientChannels[clientChannel]
                    //if (clientContext === null) {
                    //    key.cancel()
                    //    clientChannel.close()
                    //    return
                    //}
                    val buffer = getBuffer()
                    val buf = buffer.asByteBuffer()
                    try {
                        val readCount = clientChannel.read(buf)
                        if (readCount < 0) {
                            executor.execute {
                                tcpListener.onClose(NioTcpContext(clientChannel))
                                key.interestOps(0)
                                clientChannel.close()
                            }
                            tempBuffer = buffer
                            return
                        }
                        if (readCount == 0) {
                            tempBuffer = buffer
                            return
                        }
                    } catch (e: IOException) {
                        executor.execute {
                            tcpListener.onClose(NioTcpContext(clientChannel))
                            key.interestOps(0)
                            clientChannel.close()
                        }
                        tempBuffer = buffer
                        return
                    }
                    executor.execute {
                        buf.flip()
                        tcpListener.onReceive(NioTcpContext(clientChannel), buf.asReadOnlyBuffer())
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

            private inner class NioTcpContext(
                private var socketChannel: SocketChannel,
            ) : TcpContext {

                var onOpen: Boolean = false

                override val remoteAddress: SocketAddress = socketChannel.remoteAddress
                override val remoteChannel: Any get() = socketChannel

                override val server: TcpServer = this@NioTcpServer

                override fun send(data: ByteBuffer) {
                    socketChannel.write(data)
                }

                override fun send(data: InputStream) {
                    val buffer = ByteArray(bufferSize)
                    var len = data.read(buffer)
                    while (len >= 0) {
                        if (len > 0) {
                            send(buffer)
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