package xyz.srclab.common.io

import xyz.srclab.common.base.checkRangeInBounds
import java.io.Writer

/**
 * Makes array as destination of [Appendable].
 */
open class AppendableWriter<T : Appendable>(
    private val destination: T
) : Writer() {

    override fun write(b: Int) {
        destination.append(b.toChar())
    }

    override fun write(b: CharArray, off: Int, len: Int) {
        checkRangeInBounds(off, off + len, 0, b.size)
        var i = off
        while (i < off + len) {
            destination.append(b[i])
            i++
        }
    }

    override fun close() {
    }

    override fun flush() {
    }
}