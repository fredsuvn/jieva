package xyz.srclab.common.net.buffer

import java.nio.ByteBuffer

/**
 * Byte buffer which is managed by [ByteBufferPool].
 */
interface PooledByteBuffer {

    /**
     * Returns a [ByteBuffer] associated by this, any operation is reflected each other.
     */
    fun asByteBuffer(): ByteBuffer

    companion object {

        @JvmName("of")
        @JvmStatic
        fun ByteBuffer.asPooledByteBuffer(): PooledByteBuffer {
            return object : PooledByteBuffer {
                override fun asByteBuffer(): ByteBuffer = this@asPooledByteBuffer
            }
        }
    }
}