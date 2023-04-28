package xyz.srclab.common.io

import xyz.srclab.common.base.CharsRef
import xyz.srclab.common.base.checkRangeInBounds
import java.io.Reader
import kotlin.math.min

/**
 * Makes [CharSequence] as source of [Reader].
 */
open class CharSeqReader<T : CharSequence>(
    private val source: T,
    private val offset: Int,
    private val length: Int
) : Reader() {

    init {
        checkRangeInBounds(offset, offset + length, 0, source.length)
    }

    private var pos = offset
    private var mark = offset

    override fun read(): Int {
        if (pos >= offset + length) {
            return -1
        }
        val cur = source[pos]
        pos++
        return cur.code
    }

    override fun read(b: CharArray, off: Int, len: Int): Int {
        checkRangeInBounds(off, off + len, 0, b.size)
        if (pos >= offset + length) {
            return -1
        }
        val remaining = offset + length - pos
        val readLength = min(remaining, len)
        if (source is CharsRef) {
            source.subSequence(0, readLength).copyTo(b, off)
        } else {
            var i = 0
            while (i < readLength) {
                b[off + i] = source[pos + i]
                i++
            }
        }
        pos += readLength
        return readLength
    }

    override fun close() {
    }

    open fun available(): Int {
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