package xyz.srclab.common.net.udp

import xyz.srclab.common.io.newByteBuffer
import xyz.srclab.common.net.NetServer
import xyz.srclab.common.net.nioListen
import xyz.srclab.common.run.RunLatch
import java.io.IOException
import java.net.SocketAddress
import java.nio.channels.DatagramChannel
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.util.concurrent.Executor

/**
 * Server for UDP.
 */
interface UdpServer : NetServer {

    companion object {

        fun nioServer(
            bindAddress: SocketAddress,
            channelHandler: UdpChannelHandler,
            executor: Executor,
            bufferSize: Int,
            directBuffer: Boolean = false
        ): UdpServer {
            return NIOUdpServer(bindAddress, channelHandler, executor, bufferSize, directBuffer)
        }

        private class NIOUdpServer(
            override val bindAddress: SocketAddress,
            private val channelHandler: UdpChannelHandler,
            private val executor: Executor,
            private val bufferSize: Int,
            private val directBuffer: Boolean = false
        ) : UdpServer {

            private var datagramChannel: DatagramChannel? = null
            private var selector: Selector? = null
            private val listenLatch: RunLatch = RunLatch.newRunLatch()

            @Synchronized
            override fun start() {
                if (selector == null || datagramChannel !== null) {
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
                val datagramChannel = this.datagramChannel
                if (datagramChannel === null) {
                    throw IllegalStateException("Server has not been started, start it first!")
                }
                selector.close()
                datagramChannel.close()
                this.selector = null
                this.datagramChannel = null
            }

            override fun await() {
                listenLatch.await()
            }

            private fun start0() {
                val selector = Selector.open()
                val datagramChannel = DatagramChannel.open()
                datagramChannel.configureBlocking(false)
                datagramChannel.bind(bindAddress)
                datagramChannel.register(selector, SelectionKey.OP_ACCEPT)
                datagramChannel.register(selector, SelectionKey.OP_READ)
                this.selector = selector
                this.datagramChannel = datagramChannel
                executor.execute {
                    listenLatch.lockTo(1)
                    nioListen(selector) { handleKey(it) }
                    listenLatch.lockDown()
                }
            }

            private fun handleKey(key: SelectionKey) {
                val selector = key.selector()
                //if (key.isAcceptable) {
                //    val serverChannel = key.channel() as DatagramChannel
                //    val socketChannel = serverChannel.
                //    socketChannel.configureBlocking(false)
                //    socketChannel.register(selector, SelectionKey.OP_READ)
                //    //socketChannel.register(selector, SelectionKey.OP_WRITE)
                //    val context = SimpleContext(selector, socketChannel)
                //    executor.execute { channelHandler.onOpen(context) }
                //}
                if (key.isReadable) {
                    val channel = key.channel() as DatagramChannel
                    val buffer = newByteBuffer(bufferSize, directBuffer)
                    try {
                        val clientAddress = channel.receive(buffer)
                        if (clientAddress === null) {
                            return
                        }
                        executor.execute {
                            buffer.flip()
                            channelHandler.onReceive(clientAddress, buffer)
                        }
                        return
                    } catch (e: IOException) {
                        return
                    }
                }
            }
        }
    }
}