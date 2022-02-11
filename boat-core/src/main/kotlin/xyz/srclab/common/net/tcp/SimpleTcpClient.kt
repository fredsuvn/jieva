package xyz.srclab.common.net.tcp

import xyz.srclab.common.base.DEFAULT_IO_BUFFER_SIZE
import xyz.srclab.common.run.RunLatch
import xyz.srclab.common.run.newCachedThreadPoolRunner
import java.io.InputStream
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executor
import java.util.function.Supplier

/**
 * A simple [TcpClient] implementation with NIO.
 */
open class SimpleTcpClient @JvmOverloads constructor(
    val serverAddress: InetSocketAddress,
    private val channelHandlers: List<TcpChannelHandler>,
    private val executor: Executor = newCachedThreadPoolRunner().asExecutor(),
    private val bufferSize: Int = DEFAULT_IO_BUFFER_SIZE,
    private val bufferSupplier: Supplier<ByteBuffer> = Supplier { ByteBuffer.allocateDirect(bufferSize) }
) : TcpClient {

    private var selector: Selector? = null
    private val listenLatch: RunLatch = RunLatch.newRunLatch()

    private val clients: MutableMap<InetSocketAddress, Boolean> = ConcurrentHashMap()

    override fun connect() {
        if (selector !== null) {
            throw IllegalStateException("Client has been connected, disconnect it first!")
        }
        selector = start0()
    }

    override fun disconnect(immediately: Boolean) {
        val selector = this.selector
        if (selector === null) {
            throw IllegalStateException("Client has not been connected, disconnect it first!")
        }
        selector.close()
    }

    override fun await() {
        listenLatch.await()
    }

    private fun start0(): Selector {
        val socketChannel = SocketChannel.open()
        socketChannel.configureBlocking(false)
        val selector = Selector.open()
        socketChannel.register(selector, SelectionKey.OP_CONNECT)
        socketChannel.connect(serverAddress)


        val serverSocketChannel = ServerSocketChannel.open()
        serverSocketChannel.configureBlocking(false)
        val serverSocket = serverSocketChannel.socket()
        serverSocket.bind(serverAddress)
        val selector = Selector.open()
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT)

        executor.run { listen(selector) }

        return selector
    }

    private fun listen(selector: Selector) {
        listenLatch.close()
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
                    executor.run { handleKey(key) }
                    iterator.remove();
                }
            } catch (e: ClosedSelectorException) {
                break
            }
        }
        listenLatch.open()
    }

    private fun handleKey(key: SelectionKey) {
        if (!key.isAcceptable) {
            return
        }

        val selector = key.selector()
        val server = key.channel() as ServerSocketChannel
        val socketChannel = server.accept()
        registerChannel(selector, socketChannel, SelectionKey.OP_READ)
        registerChannel(selector, socketChannel, SelectionKey.OP_WRITE)

        val context = SimpleContext(selector, socketChannel)

        //val address = socketChannel.remoteAddress as InetSocketAddress
        //var newClient = false
        //clients.computeIfAbsent(address) {
        //    newClient = true
        //    true
        //}
        //if (newClient) {
        //    for (channelHandler in channelHandlers) {
        //        channelHandler.onOpen(context)
        //    }
        //}

        if (key.isReadable) {
            val buffer = bufferSupplier.get()
            socketChannel.read(buffer)
            buffer.flip()
            for (channelHandler in channelHandlers) {
                channelHandler.onReceive(context, buffer.asReadOnlyBuffer())
            }
        }
    }

    private fun registerChannel(selector: Selector, channel: SelectableChannel, ops: Int) {
        channel.configureBlocking(false)
        channel.register(selector, ops)
    }

    private inner class SimpleContext(
        private val selector: Selector,
        private val socketChannel: SocketChannel
    ) : TcpContext {

        override val remoteAddress: InetSocketAddress
            get() = socketChannel.remoteAddress as InetSocketAddress

        override fun write(data: ByteBuffer) {
            socketChannel.write(data)
            registerChannel(selector, socketChannel, SelectionKey.OP_READ);
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