package xyz.srclab.common.net.tcp

import xyz.srclab.common.base.*
import java.io.InputStream
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.charset.Charset

/**
 * Server with TCP/IP.
 */
interface TcpClient {

    val remoteAddress: InetSocketAddress

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

    fun send(data: InputStream) {
        val buffer = ByteArray(DEFAULT_IO_BUFFER_SIZE)
        var len = data.read(buffer)
        while (len >= 0) {
            if (len > 0) {
                send(buffer)
            }
            len = data.read(buffer)
        }
    }

    fun send(data: String) {
        send(data.getBytes(DEFAULT_CHARSET))
    }

    fun send(data: String, charset: Charset) {
        send(data.getBytes(charset))
    }

    fun receive(dest: ByteBuffer): Int

    fun receive(dest: ByteBuffer, readTimeoutMillis: Long): Int

    fun receiveBytes(): ByteArray

    fun receiveBytes(readTimeoutMillis: Long): ByteArray

    fun receiveString(): String {
        return receiveString(DEFAULT_CHARSET)
    }

    fun receiveString(readTimeoutMillis: Long): String {
        return receiveString(DEFAULT_CHARSET, readTimeoutMillis)
    }

    fun receiveString(charset: Charset): String {
        return receiveBytes().getString(charset)
    }

    fun receiveString(charset: Charset, readTimeoutMillis: Long): String {
        return receiveBytes(readTimeoutMillis).getString(charset)
    }

    companion object {

        /**
         * Creates a new simple [TcpClient].
         */
        @JvmOverloads
        @JvmStatic
        fun simpleClient(
            remoteAddress: InetSocketAddress,
            timeoutMillis: Long = 5000,
            bufferSize: Int = DEFAULT_IO_BUFFER_SIZE
        ): TcpClient {
            return SimpleTcpClient(remoteAddress, timeoutMillis, bufferSize)
        }

        /**
         * Creates a new simple [TcpClient].
         */
        @JvmOverloads
        @JvmStatic
        fun simpleClient(
            remoteAddress: CharSequence,
            timeoutMillis: Long = 5000,
            bufferSize: Int = DEFAULT_IO_BUFFER_SIZE
        ): TcpClient {
            return SimpleTcpClient(remoteAddress, timeoutMillis, bufferSize)
        }
    }
}