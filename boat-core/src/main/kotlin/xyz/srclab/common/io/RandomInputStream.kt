package xyz.srclab.common.io

import xyz.srclab.common.base.checkRangeInBounds
import java.io.InputStream
import java.io.RandomAccessFile

/**
 * Makes [RandomAccessFile] as source of [InputStream].
 */
open class RandomInputStream(
    val source: RandomAccessFile,
    private val offset: Long,
    private val length: Long
) : InputStream() {

    init {
        checkRangeInBounds(offset, offset + length, 0, source.length())
        source.seek(offset)
    }

    private var pos = offset
    private var mark = offset

    override fun read(): Int {
        if (pos >= offset + length) {
            return -1
        }
        val cur = source.read()
        pos++
        return cur
    }

    override fun read(b: ByteArray, off: Int, len: Int): Int {
        if (pos >= offset + length) {
            return -1
        }
        val result = source.read(b, off, len)
        pos += result
        return result
    }

    override fun available(): Int {
        val l = offset + length - pos
        return if (l > Integer.MAX_VALUE) Integer.MAX_VALUE else l.toInt()
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