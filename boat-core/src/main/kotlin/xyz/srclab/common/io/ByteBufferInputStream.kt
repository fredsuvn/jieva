package xyz.srclab.common.io

import xyz.srclab.common.base.checkRangeInBounds
import xyz.srclab.common.base.toUnsignedInt
import java.io.InputStream
import java.nio.ByteBuffer

/**
 * Makes [ByteBuffer] as [InputStream].
 */
open class ByteBufferInputStream(
    val source: ByteBuffer
) : InputStream() {

    override fun read(): Int {
        if (!source.hasRemaining()) {
            return -1
        }
        return source.get().toUnsignedInt()
    }

    override fun read(b: ByteArray): Int {
        return read(b, 0, b.size)
    }

    override fun read(b: ByteArray, off: Int, len: Int): Int {
        checkRangeInBounds(off, off + len, 0, b.size)
        val remaining = source.remaining()
        if (remaining == 0) {
            return -1
        }
        return if (remaining <= len) {
            source.get(b, off, remaining)
            remaining
        } else {
            source.get(b, off, len)
            len
        }
    }

    override fun available(): Int {
        return source.remaining()
    }

    override fun mark(readlimit: Int) {
        source.mark()
    }

    override fun reset() {
        source.reset()
    }

    override fun markSupported(): Boolean {
        return true
    }
}