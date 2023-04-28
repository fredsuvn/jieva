package xyz.srclab.common.io

import java.io.InputStream

/**
 * Wraps given [source] and prevent [close]. This stream will count the read bytes in [count].
 */
open class UncloseInputStream<T : InputStream>(
    val source: T
) : InputStream() {

    private var _count = 0L
    private var countMark = -1L

    open val count: Long
        get() = _count

    override fun close() {
    }

    override fun read(): Int {
        return source.read().let {
            _count++
            it
        }
    }

    override fun read(b: ByteArray): Int {
        return source.read(b).let {
            _count += b.size
            it
        }
    }

    override fun read(b: ByteArray, off: Int, len: Int): Int {
        return source.read(b, off, len).let {
            _count += len
            it
        }
    }

    override fun skip(n: Long): Long {
        return source.skip(n)
    }

    override fun available(): Int {
        return source.available()
    }

    override fun mark(readlimit: Int) {
        source.mark(readlimit)
        countMark = _count
    }

    override fun reset() {
        source.reset()
        _count = countMark
    }

    override fun markSupported(): Boolean {
        return source.markSupported()
    }

    override fun equals(other: Any?): Boolean {
        if (other is UncloseInputStream<*>) {
            return source == other.source
        }
        return source == other
    }

    override fun hashCode(): Int {
        return source.hashCode()
    }

    override fun toString(): String {
        return source.toString()
    }
}