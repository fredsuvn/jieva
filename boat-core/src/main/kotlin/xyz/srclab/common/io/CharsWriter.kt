package xyz.srclab.common.io

import xyz.srclab.common.base.checkIndexInBounds
import xyz.srclab.common.base.checkRangeInBounds
import java.io.Writer

/**
 * Makes array as destination of [Writer].
 */
open class CharsWriter(
    val destination: CharArray,
    private val offset: Int,
    private val length: Int
) : Writer() {

    init {
        checkRangeInBounds(offset, offset + length, 0, destination.size)
    }

    private var pos = offset

    override fun write(b: Int) {
        pos.checkIndexInBounds(offset, offset + length)
        destination[pos] = b.toChar()
        pos++
    }

    override fun write(b: CharArray, off: Int, len: Int) {
        checkRangeInBounds(off, off + len, 0, b.size)
        val nextPos = pos + len
        (nextPos - 1).checkIndexInBounds(offset, offset + length)
        System.arraycopy(b, off, destination, pos, len)
        pos = nextPos
    }

    override fun close() {
    }

    override fun flush() {
    }
}