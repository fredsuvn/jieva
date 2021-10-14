package xyz.srclab.common.io

import java.io.OutputStream
import java.nio.BufferOverflowException
import java.nio.ByteBuffer

/**
 * Wraps [ByteBuffer] as [OutputStream].
 *
 * Note this implementation will throw [BufferOverflowException] when writing overflow.
 */
open class ByteBufferOutputStreamWrapper(
    private val buffer: ByteBuffer
) : OutputStream() {

    override fun write(b: Int) {
        buffer.put(b.toByte())
    }

    override fun write(b: ByteArray) {
        buffer.put(b)
    }

    override fun write(b: ByteArray, off: Int, len: Int) {
        buffer.put(b, off, len)
    }
}