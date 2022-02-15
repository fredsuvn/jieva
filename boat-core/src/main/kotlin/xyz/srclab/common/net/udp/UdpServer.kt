package xyz.srclab.common.net.udp

import xyz.srclab.common.io.newByteBuffer
import xyz.srclab.common.net.NetServer
import xyz.srclab.common.net.nioListen
import xyz.srclab.common.net.tcp.TcpChannelHandler
import xyz.srclab.common.net.tcp.TcpServer
import xyz.srclab.common.run.RunLatch
import java.io.IOException
import java.net.SocketAddress
import java.nio.channels.*
import java.util.concurrent.Executor

/**
 * Server for UDP.
 */
interface UdpServer : NetServer {

    //companion object {
    //
    //
    //
    //    private class NIOUdpServer(
    //        override val bindAddress: SocketAddress,
    //        private val channelHandler: TcpChannelHandler,
    //        private val executor: Executor,
    //        private val bufferSize: Int,
    //        private val directBuffer: Boolean = false
    //    ) : TcpServer {
    //
    //        private var datagramChannel:DatagramChannel? = null
    //        private var selector: Selector? = null
    //        private val listenLatch: RunLatch = RunLatch.newRunLatch()
    //
    //        @Synchronized
    //        override fun start() {
    //            if (selector == null || datagramChannel !== null) {
    //                throw IllegalStateException("Server has been started, stop it first!")
    //            }
    //        }
    //
    //        @Synchronized
    //        override fun stop(immediately: Boolean) {
    //            val selector = this.selector
    //            if (selector === null) {
    //                throw IllegalStateException("Server has not been started, start it first!")
    //            }
    //            val datagramChannel = this.datagramChannel
    //            if (datagramChannel === null) {
    //                throw IllegalStateException("Server has not been started, start it first!")
    //            }
    //            selector.close()
    //            datagramChannel.close()
    //            this.selector = null
    //            this.datagramChannel = null
    //        }
    //
    //        override fun await() {
    //            listenLatch.await()
    //        }
    //
    //        private fun start0() {
    //            val selector = Selector.open()
    //            val datagramChannel = DatagramChannel.open()
    //            datagramChannel.configureBlocking(false)
    //            datagramChannel.bind(bindAddress)
    //            datagramChannel.register(selector, SelectionKey.OP_ACCEPT)
    //            datagramChannel.register(selector, SelectionKey.OP_READ)
    //            this.selector = selector
    //            this.datagramChannel = datagramChannel
    //            executor.execute { nioListen(listenLatch, selector) { handleKey(it) } }
    //        }
    //
    //        private fun handleKey(key: SelectionKey) {
    //            val selector = key.selector()
    //            //if (key.isAcceptable) {
    //            //    val serverChannel = key.channel() as DatagramChannel
    //            //    val socketChannel = serverChannel.
    //            //    socketChannel.configureBlocking(false)
    //            //    socketChannel.register(selector, SelectionKey.OP_READ)
    //            //    //socketChannel.register(selector, SelectionKey.OP_WRITE)
    //            //    val context = SimpleContext(selector, socketChannel)
    //            //    executor.execute { channelHandler.onOpen(context) }
    //            //}
    //            if (key.isReadable) {
    //                val channel = key.channel() as DatagramChannel
    //                val context = SimpleContext(selector, channel)
    //                val buffer = newByteBuffer(bufferSize, directBuffer)
    //                try {
    //                    val readCount = channel.read(buffer)
    //                    if (readCount < 0) {
    //                        channel.close()
    //                        executor.execute { channelHandler.onClose(context) }
    //                        return
    //                    }
    //                } catch (e: IOException) {
    //                    channel.close()
    //                    executor.execute { channelHandler.onClose(context) }
    //                    return
    //                }
    //                buffer.flip()
    //                executor.execute { channelHandler.onReceive(context, buffer.asReadOnlyBuffer()) }
    //            }
    //        }
    //    }
    //}
}