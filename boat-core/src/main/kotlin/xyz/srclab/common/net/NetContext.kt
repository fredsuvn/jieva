package xyz.srclab.common.net

import xyz.srclab.common.base.remainingLength
import java.io.InputStream
import java.net.SocketAddress
import java.nio.ByteBuffer

/**
 * Context for a net channel.
 */
interface NetContext {

    val remoteAddress: SocketAddress

    fun write(data: ByteArray) {
        write(data, 0)
    }

    fun write(data: ByteArray, offset: Int = 0) {
        write(data, offset, remainingLength(data.size, offset))
    }

    fun write(data: ByteArray, offset: Int = 0, length: Int = remainingLength(data.size, offset)) {
        val buffer = ByteBuffer.wrap(data, offset, length)
        write(buffer)
    }

    fun write(data: ByteBuffer)

    fun write(data: InputStream)

    fun close()
}