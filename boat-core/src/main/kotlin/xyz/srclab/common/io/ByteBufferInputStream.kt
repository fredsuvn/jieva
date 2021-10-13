package xyz.srclab.common.io

import xyz.srclab.common.base.checkRangeInBounds
import xyz.srclab.common.base.toUnsignedInt
import java.io.InputStream
import java.nio.ByteBuffer

/**
 * Implementation of [InputStream] which uses a [ByteBuffer] to store the data.
 */
class ByteBufferInputStream(
    private val buffer: ByteBuffer
) : InputStream() {

    override fun read(): Int {
        if (!buffer.hasRemaining()) {
            return -1
        }
        return buffer.get().toUnsignedInt()
    }

    override fun read(b: ByteArray): Int {
        return read(b, 0, b.size)
    }

    override fun read(b: ByteArray, off: Int, len: Int): Int {
        checkRangeInBounds(off, off + len, 0, b.size)
        val remaining = buffer.remaining()
        if (remaining == 0) {
            return -1
        }
        return if (remaining <= len) {
            buffer.get(b, off, remaining)
            remaining
        } else {
            buffer.get(b, off, len)
            len
        }
    }

    override fun available(): Int {
        return buffer.remaining()
    }

    override fun mark(readlimit: Int) {
        buffer.mark()
    }

    override fun reset() {
        buffer.reset()
    }

    override fun markSupported(): Boolean {
        return true
    }
}