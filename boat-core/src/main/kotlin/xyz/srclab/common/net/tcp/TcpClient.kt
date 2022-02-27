package xyz.srclab.common.net.tcp

import xyz.srclab.common.base.*
import xyz.srclab.common.io.getBytes
import xyz.srclab.common.net.parseInetSocketAddress
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket
import java.net.SocketAddress
import java.nio.ByteBuffer
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.SocketChannel
import java.nio.charset.Charset

/**
 * Client for TCP/IP.
 */
interface TcpClient {

    /**
     * Connects to server.
     */
    fun connect()

    /**
     * Disconnects to server. Same as `disconnect(false)`.
     */
    fun disconnect() {
        disconnect(false)
    }

    /**
     * Disconnects to server.
     */
    fun disconnect(immediately: Boolean)

    fun send(data: ByteArray) {
        send(data, 0)
    }

    fun send(data: ByteArray, offset: Int = 0) {
        send(data, offset, remainingLength(data.size, offset))
    }

    fun send(data: ByteArray, offset: Int = 0, length: Int = remainingLength(data.size, offset)) {
        val buffer = ByteBuffer.wrap(data, offset, length)
        send(buffer)
    }

    fun send(data: ByteBuffer)

    fun send(data: InputStream)

    fun send(data: String) {
        send(data.getBytes(DEFAULT_CHARSET))
    }

    fun send(data: String, charset: Charset) {
        send(data.getBytes(charset))
    }

    /**
     * If return -1, means server disconnect this client.
     */
    fun receive(dest: ByteBuffer): Int

    /**
     * If read -1, means server disconnect this client.
     */
    fun receive(): InputStream

    fun receiveBytes(): ByteArray

    fun receiveString(): String {
        return receiveString(DEFAULT_CHARSET)
    }

    fun receiveString(charset: Charset): String {
        return receiveBytes().getString(charset)
    }

    companion object {

        /**
         * Creates a new simple BIO [TcpClient].
         */
        @JvmOverloads
        @JvmStatic
        fun bioClient(
            remoteAddress: SocketAddress,
            timeoutMillis: Long = 5000,
            bufferSize: Int = DEFAULT_IO_BUFFER_SIZE
        ): TcpClient {
            return BIOTcpClient(remoteAddress, timeoutMillis, bufferSize)
        }

        /**
         * Creates a new simple BIO [TcpClient].
         */
        @JvmOverloads
        @JvmStatic
        fun bioClient(
            remoteAddress: CharSequence,
            timeoutMillis: Long = 5000,
            bufferSize: Int = DEFAULT_IO_BUFFER_SIZE
        ): TcpClient {
            return BIOTcpClient(remoteAddress.parseInetSocketAddress(), timeoutMillis, bufferSize)
        }

        /**
         * Creates a new simple NIO [TcpClient].
         */
        @JvmOverloads
        @JvmStatic
        fun nioClient(
            remoteAddress: SocketAddress,
            timeoutMillis: Long = 5000,
            bufferSize: Int = DEFAULT_IO_BUFFER_SIZE
        ): TcpClient {
            return NIOTcpClient(remoteAddress, timeoutMillis, bufferSize)
        }

        /**
         * Creates a new simple NIO [TcpClient].
         */
        @JvmOverloads
        @JvmStatic
        fun nioClient(
            remoteAddress: CharSequence,
            timeoutMillis: Long = 5000,
            bufferSize: Int = DEFAULT_IO_BUFFER_SIZE
        ): TcpClient {
            return NIOTcpClient(remoteAddress.parseInetSocketAddress(), timeoutMillis, bufferSize)
        }

        private class BIOTcpClient(
            private val remoteAddress: SocketAddress,
            private val timeoutMillis: Long,
            private val bufferSize: Int
        ) : TcpClient {

            private var socket: Socket? = null

            @Synchronized
            override fun connect() {
                if (socket !== null) {
                    throw IllegalStateException("Client has been connected, disconnect it first!")
                }
                val socket = Socket()
                socket.soTimeout = timeoutMillis.toInt()
                //socket.bind(remoteAddress)
                socket.connect(remoteAddress)
                this.socket = socket
            }

            @Synchronized
            override fun disconnect(immediately: Boolean) {
                val socket = this.socket
                if (socket === null) {
                    throw IllegalStateException("Client has not been connected, disconnect it first!")
                }
                socket.close()
                this.socket = null
            }

            override fun send(data: ByteBuffer) {
                val out = getOutputStream()
                out.write(data.getBytes())
                out.flush()
            }

            override fun send(data: ByteArray, offset: Int, length: Int) {
                val out = getOutputStream()
                out.write(data, offset, length)
                out.flush()
            }

            override fun send(data: InputStream) {
                val out = getOutputStream()
                data.copyTo(out, bufferSize)
                out.flush()
            }

            private fun getOutputStream(): OutputStream {
                val socket = this.socket
                if (socket === null) {
                    throw IllegalStateException("Client has not been connected, disconnect it first!")
                }
                return socket.getOutputStream()
            }

            override fun receive(dest: ByteBuffer): Int {
                val inp = getInputStream()
                val buffer = ByteArray(dest.remaining())
                return inp.read(buffer)
            }

            override fun receive(): InputStream {
                return getInputStream()
            }

            override fun receiveBytes(): ByteArray {
                val inp = getInputStream()
                val buffer = ByteArray(bufferSize)
                val readCount = inp.read(buffer)
                return if (readCount == bufferSize) buffer else buffer.copyOfRange(0, readCount)
            }

            private fun getInputStream(): InputStream {
                val socket = this.socket
                if (socket === null) {
                    throw IllegalStateException("Client has not been connected, disconnect it first!")
                }
                return socket.getInputStream()
            }
        }

        private class NIOTcpClient(
            private val remoteAddress: SocketAddress,
            private val timeoutMillis: Long,
            private val bufferSize: Int,
        ) : TcpClient {

            private var selector: Selector? = null
            private var socketChannel: SocketChannel? = null

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
                    throw IllegalStateException("Connect timeout for $timeoutMillis millis: $remoteAddress")
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
                //socketChannel.finishConnect()
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
                val selector = this.selector
                val socketChannel = this.socketChannel
                if (selector == null || socketChannel === null) {
                    throw IllegalStateException("Client has not been connected, disconnect it first!")
                }
                return receive0(selector, socketChannel, dest, timeoutMillis)
            }

            override fun receive(): InputStream {
                val selector = this.selector
                val socketChannel = this.socketChannel
                if (selector == null || socketChannel === null) {
                    throw IllegalStateException("Client has not been connected, disconnect it first!")
                }
                return NIOInputStream(selector, socketChannel)
            }

            override fun receiveBytes(): ByteArray {
                val selector = this.selector
                val socketChannel = this.socketChannel
                if (selector == null || socketChannel === null) {
                    throw IllegalStateException("Client has not been connected, disconnect it first!")
                }
                val buffer = ByteBuffer.allocate(bufferSize)
                receive0(selector, socketChannel, buffer, timeoutMillis)
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
                        throw IllegalStateException("Receive timeout in $timeoutMillis millis: $remoteAddress")
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
                        throw IllegalStateException("Receive timeout in $timeoutMillis millis: $remoteAddress")
                    }
                }
            }

            private inner class NIOInputStream(
                private val selector: Selector,
                private val socketChannel: SocketChannel
            ) : InputStream() {

                override fun read(): Int {
                    val buffer = ByteBuffer.allocate(1)
                    val readCount = receive0(selector, socketChannel, buffer, timeoutMillis)
                    if (readCount < 0) {
                        return readCount
                    }
                    if (readCount == 0) {
                        return read()
                    }
                    buffer.flip()
                    return buffer.get().toUnsignedInt()
                }

                override fun read(b: ByteArray): Int {
                    val buffer = ByteBuffer.wrap(b)
                    val readCount = receive0(selector, socketChannel, buffer, timeoutMillis)
                    if (readCount < 0) {
                        return readCount
                    }
                    return buffer.position()
                }

                override fun read(b: ByteArray, off: Int, len: Int): Int {
                    checkRangeInBounds(off, off + len, 0, b.size)
                    val buffer = ByteBuffer.allocate(len)
                    val readCount = receive0(selector, socketChannel, buffer, timeoutMillis)
                    if (readCount < 0) {
                        return readCount
                    }
                    buffer.flip()
                    buffer.get(b, off, len)
                    return buffer.position()
                }
            }
        }
    }
}