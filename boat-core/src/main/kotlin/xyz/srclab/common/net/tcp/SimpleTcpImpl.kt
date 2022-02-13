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

