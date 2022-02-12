package xyz.srclab.common.net.tcp

import xyz.srclab.common.base.DEFAULT_IO_BUFFER_SIZE
import xyz.srclab.common.base.epochMillis
import xyz.srclab.common.io.getBytes
import xyz.srclab.common.io.newByteBuffer
import xyz.srclab.common.net.socket.parseInetSocketAddress
import xyz.srclab.common.run.AsyncRunner
import xyz.srclab.common.run.RunLatch
import java.io.IOException
import java.io.InputStream
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.*
import java.util.concurrent.Executor

/**
 * A simple [TcpClient] implementation with NIO.
 */
open class SimpleTcpClient @JvmOverloads constructor(
    override val remoteAddress: InetSocketAddress,
    private val timeoutMillis: Long = 5000,
    private val bufferSize: Int = DEFAULT_IO_BUFFER_SIZE,
) : TcpClient {

    private var selector: Selector? = null
    private var socketChannel: SocketChannel? = null

    @JvmOverloads
    constructor(
        remoteAddress: CharSequence,
        connectTimeoutMillis: Long = 5000,
        bufferSize: Int = DEFAULT_IO_BUFFER_SIZE,
    ) : this(remoteAddress.parseInetSocketAddress(), connectTimeoutMillis, bufferSize)

    @Synchronized
    override fun connect() {
        if (selector !== null || socketChannel !== null) {
            throw IllegalStateException("Client has been connected, disconnect it first!")
        }
        val selector = Selector.open()
        val socketChannel = SocketChannel.open()
        socketChannel.configureBlocking(false)
        socketChannel.connect(remoteAddress)
        socketChannel.register(selector, SelectionKey.OP_CONNECT)
        this.selector = selector
        this.socketChannel = socketChannel
        val readyChannels = selector.select(timeoutMillis)
        if (readyChannels == 0) {
            disconnect()
            throw IllegalStateException("Connect timeout: $remoteAddress")
        }
        val selectedKeys = selector.selectedKeys()
        val iterator = selectedKeys.iterator()
        while (iterator.hasNext()) {
            val key = iterator.next()
            if (key.isConnectable) {
                socketChannel.register(selector, SelectionKey.OP_READ)
                //socketChannel.register(selector, SelectionKey.OP_WRITE)
                socketChannel.finishConnect()
                iterator.remove()
                break
            }
        }
        socketChannel.finishConnect()
    }

    @Synchronized
    override fun disconnect(immediately: Boolean) {
        val selector = this.selector
        if (selector === null) {
            throw IllegalStateException("Client has not been connected, disconnect it first!")
        }
        val socketChannel = this.socketChannel
        if (socketChannel === null) {
            throw IllegalStateException("Client has not been connected, disconnect it first!")
        }
        selector.close()
        socketChannel.close()
        this.selector = null
        this.socketChannel = null
    }

    override fun send(data: ByteBuffer) {
        val socketChannel = this.socketChannel
        if (socketChannel === null) {
            throw IllegalStateException("Client has not been connected, disconnect it first!")
        }
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

    override fun receive(dest: ByteBuffer): Int {
        val socketChannel = this.socketChannel
        if (socketChannel === null) {
            throw IllegalStateException("Client has not been connected, disconnect it first!")
        }
        return socketChannel.read(dest)
    }

    override fun receive(dest: ByteBuffer, readTimeoutMillis: Long): Int {
        val selector = this.selector
        val socketChannel = this.socketChannel
        if (selector == null || socketChannel === null) {
            throw IllegalStateException("Client has not been connected, disconnect it first!")
        }
        return receive0(selector, socketChannel, dest, readTimeoutMillis)
    }

    override fun receiveBytes(): ByteArray {
        val socketChannel = this.socketChannel
        if (socketChannel === null) {
            throw IllegalStateException("Client has not been connected, disconnect it first!")
        }
        val buffer = ByteBuffer.allocate(bufferSize)
        socketChannel.read(buffer)
        buffer.flip()
        return buffer.getBytes()
    }

    override fun receiveBytes(readTimeoutMillis: Long): ByteArray {
        val selector = this.selector
        val socketChannel = this.socketChannel
        if (selector == null || socketChannel === null) {
            throw IllegalStateException("Client has not been connected, disconnect it first!")
        }
        val buffer = ByteBuffer.allocate(bufferSize)
        receive0(selector, socketChannel, buffer, readTimeoutMillis)
        buffer.flip()
        return buffer.getBytes()
    }

    private fun receive0(
        selector: Selector,
        socketChannel: SocketChannel,
        dest: ByteBuffer,
        readTimeoutMillis: Long
    ): Int {
        val startTime = epochMillis()
        while (true) {
            val readyChannels = selector.select(readTimeoutMillis)
            if (readyChannels == 0) {
                disconnect()
                throw IllegalStateException("Receive timeout: $remoteAddress")
            }
            val selectedKeys = selector.selectedKeys()
            val iterator = selectedKeys.iterator()
            while (iterator.hasNext()) {
                val key = iterator.next()
                if (key.isReadable) {
                    iterator.remove()
                    return socketChannel.read(dest)
                }
            }
            val now = epochMillis()
            if (now - startTime > readTimeoutMillis) {
                throw IllegalStateException("Receive timeout: $remoteAddress")
            }
        }
    }
}

/**
 * A simple [TcpServer] implementation with NIO.
 */
open class SimpleTcpServer @JvmOverloads constructor(
    val bindAddress: InetSocketAddress,
    private val channelHandler: TcpChannelHandler,
    private val executor: Executor = AsyncRunner.asExecutor(),
    private val bufferSize: Int = DEFAULT_IO_BUFFER_SIZE,
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
        Thread { listen(selector, listenLatch, executor) { handleKey(it) } }.start()
        //executor.run { listen(selector, listenLatch, executor) { handleKey(it) } }
    }

    private fun listen(selector: Selector, listenLatch: RunLatch, executor: Executor, handler: (SelectionKey) -> Unit) {
        listenLatch.lock()
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
                    executor.run { handler(key) }
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
            channelHandler.onOpen(context)
        }
        if (key.isReadable) {
            val socketChannel = key.channel() as SocketChannel
            val context = SimpleContext(selector, socketChannel)
            val buffer = newByteBuffer(bufferSize, directBuffer)
            try {
                val readCount = socketChannel.read(buffer)
                if (readCount < 0) {
                    channelHandler.onClose(context)
                    socketChannel.close()
                    return
                }
            } catch (e: IOException) {
                channelHandler.onClose(context)
                socketChannel.close()
                return
            }
            buffer.flip()
            channelHandler.onReceive(context, buffer.asReadOnlyBuffer())
        }
    }

    private inner class SimpleContext(
        private val selector: Selector,
        private val socketChannel: SocketChannel
    ) : TcpContext {

        override val remoteAddress: InetSocketAddress
            get() = socketChannel.remoteAddress as InetSocketAddress

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