package xyz.srclab.common.io

import java.io.OutputStream

/**
 * Wraps given [source] and prevent [close]. This stream will count the written bytes in [count].
 */
open class UncloseOutputStream<T : OutputStream>(
    val source: T
) : OutputStream() {

    private var _count = 0L

    val count: Long
        get() = _count

    override fun close() {
    }

    override fun flush() {
        source.flush()
    }

    override fun write(b: Int) {
        source.write(b)
        _count++
    }

    override fun write(b: ByteArray) {
        source.write(b)
        _count += b.size
    }

    override fun write(b: ByteArray, off: Int, len: Int) {
        source.write(b, off, len)
        _count += len
    }

    override fun equals(other: Any?): Boolean {
        if (other is UncloseOutputStream<*>) {
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