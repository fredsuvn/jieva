package xyz.srclab.common.io

import xyz.srclab.common.base.checkInBounds
import xyz.srclab.common.base.checkRangeInBounds
import java.io.OutputStream

/**
 * Makes array as destination of [OutputStream].
 */
open class BytesOutputStream(
    private val destination: ByteArray,
    private val offset: Int,
    private val length: Int
) : OutputStream() {

    init {
        checkRangeInBounds(offset, offset + length, 0, destination.size)
    }

    private var pos = offset

    override fun write(b: Int) {
        pos.checkInBounds(offset, offset + length)
        destination[pos] = b.toByte()
        pos++
    }

    override fun write(b: ByteArray, off: Int, len: Int) {
        checkRangeInBounds(off, off + len, 0, b.size)
        val nextPos = pos + len
        (nextPos - 1).checkInBounds(offset, offset + length)
        System.arraycopy(b, off, destination, pos, len)
        pos = nextPos
    }
}