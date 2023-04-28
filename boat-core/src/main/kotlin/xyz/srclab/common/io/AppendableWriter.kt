package xyz.srclab.common.io

import xyz.srclab.common.base.charsRef
import xyz.srclab.common.base.endIndex
import java.io.Writer

/**
 * Makes [Appendable] as [Writer].
 */
open class AppendableWriter<T : Appendable>(
    private val destination: T
) : Writer() {

    override fun write(b: Int) {
        destination.append(b.toChar())
    }

    override fun write(b: CharArray, off: Int, len: Int) {
        val chars = b.charsRef(off, endIndex(off, len))
        destination.append(chars)
    }

    override fun close() {
    }

    override fun flush() {
    }
}