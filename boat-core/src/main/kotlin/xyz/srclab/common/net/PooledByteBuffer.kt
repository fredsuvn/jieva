package xyz.srclab.common.net

import java.nio.ByteBuffer

/**
 * Pooled [ByteBuffer] which is usually managed by [ByteBufferPool].
 *
 * Using [asByteBuffer] can simply create a [PooledByteBuffer] from a [ByteBuffer].
 */
interface PooledByteBuffer {

    /**
     * Returns this as [ByteBuffer], any operation is reflected each other.
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