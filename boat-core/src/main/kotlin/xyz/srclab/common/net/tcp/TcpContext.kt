package xyz.srclab.common.net.tcp

import xyz.srclab.common.base.remainingLength
import java.io.InputStream
import java.net.SocketAddress
import java.nio.ByteBuffer
import java.nio.channels.SocketChannel

/**
 * Context for TCP channel.
 */
interface TcpContext {

    /**
     * Remote address.
     */
    val remoteAddress: SocketAddress

    /**
     * Remote channel object.
     * Same remote client will return same remote channel object (it's usually [SocketChannel]).
     */
    val remoteChannel: Any

    /**
     * Current server.
     */
    val server: TcpServer

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

    fun disconnect()
}