package xyz.srclab.common.io

import java.io.Writer

/**
 * Makes array as destination of [Appendable].
 */
open class AppendableWriter<T : Appendable>(
    val destination: T
) : Writer() {

    override fun write(b: Int) {
        destination.append(b.toChar())
    }

    override fun write(b: CharArray) {
        write(b, 0, b.size)
    }

    override fun write(b: CharArray, off: Int, len: Int) {
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