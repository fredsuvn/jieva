package xyz.srclab.common.io

import xyz.srclab.common.base.checkRangeInBounds
import xyz.srclab.common.base.toUnsignedInt
import java.io.InputStream

/**
 * Wraps array as [InputStream].
 */
open class BytesInputStreamWrapper @JvmOverloads constructor(
    private val array: ByteArray,
    private val offset: Int = 0,
    private val length: Int = array.size - offset
) : InputStream() {

    private var pos = offset
    private var mark = offset

    override fun read(): Int {
        if (pos >= offset + length) {
            return -1
        }
        val cur = array[pos]
        pos++
        return cur.toUnsignedInt()
    }

    override fun read(b: ByteArray): Int {
        return read(b, 0, b.size)
    }

    override fun read(b: ByteArray, off: Int, len: Int): Int {
        checkRangeInBounds(off, off + len, 0, b.size)
        if (pos >= offset + length) {
            return -1
        }
        val remaining = offset + length - pos
        return if (remaining <= len) {
            System.arraycopy(array, pos, b, off, remaining)
            remaining
        } else {
            System.arraycopy(array, pos, b, off, len)
            len
        }
    }

    override fun available(): Int {
        return offset + length - pos
    }

    override fun mark(readlimit: Int) {
        mark = pos
    }

    override fun reset() {
        pos = mark
    }

    override fun markSupported(): Boolean {
        return true
    }
}