package xyz.srclab.common.net

import xyz.srclab.common.io.newByteBuffer
import xyz.srclab.common.net.tcp.TcpChannelHandler
import xyz.srclab.common.net.tcp.TcpContext
import xyz.srclab.common.net.tcp.TcpServer
import xyz.srclab.common.run.RunLatch
import java.io.IOException
import java.io.InputStream
import java.net.SocketAddress
import java.nio.ByteBuffer
import java.nio.channels.*
import java.util.concurrent.Executor
import java.util.function.Supplier


open class NIONetServer<S,C> (
    private val bindAddress: SocketAddress,
    private val channelHandler: TcpChannelHandler,
    private val executor: Executor,
    private val bufferSize: Int,
    private val directBuffer: Boolean,
    private val serverChannel:Supplier<S>,
    private val clientChannel:Supplier<C>
) : TcpServer where S:SelectableChannel,S:NetworkChannel,C:SelectableChannel,C:NetworkChannel{

    private var selector: Selector? = null
    private var serverSocketChannel: SelectableChannel? = null
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
        val serverSocketChannel = serverChannel.get()
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

        override val remoteAddress: SocketAddress = socketChannel.remoteAddress

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

        override fun close() {
            socketChannel.close()
        }
    }
}