package xyz.srclab.common.net.tcp

import xyz.srclab.common.base.DEFAULT_IO_BUFFER_SIZE
import xyz.srclab.common.base.remainingLength
import java.io.InputStream
import java.net.InetSocketAddress
import java.nio.ByteBuffer

/**
 * Context for a TCP channel.
 */
interface TcpContext {

    val remoteAddress: InetSocketAddress

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

    fun write(data: InputStream) {
        val buffer = ByteArray(DEFAULT_IO_BUFFER_SIZE)
        var len = data.read(buffer)
        while (len >= 0) {
            if (len > 0) {
                write(buffer)
            }
            len = data.read(buffer)
        }
    }

    fun closeChannel()
}