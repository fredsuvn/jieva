package xyz.srclab.common.io

import xyz.srclab.common.base.checkRangeInBounds
import xyz.srclab.common.base.toUnsignedInt
import java.io.InputStream

/**
 * Makes array as source of [InputStream].
 */
open class BytesInputStream(
    val source: ByteArray,
    private val offset: Int,
    private val length: Int
) : InputStream() {

    init {
        checkRangeInBounds(offset, offset + length, 0, source.size)
    }

    private var pos = offset
    private var mark = offset

    override fun read(): Int {
        if (pos >= offset + length) {
            return -1
        }
        val cur = source[pos]
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
            System.arraycopy(source, pos, b, off, remaining)
            remaining
        } else {
            System.arraycopy(source, pos, b, off, len)
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

    /**
     * Return copy of range of [source].
     */
    fun toByteArray(): ByteArray {
        return source.copyOfRange(offset, offset + length)
    }
}