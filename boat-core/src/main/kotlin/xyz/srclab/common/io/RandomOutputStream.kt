package xyz.srclab.common.io

import xyz.srclab.common.base.checkInBounds
import xyz.srclab.common.base.checkRangeInBounds
import java.io.OutputStream
import java.io.RandomAccessFile

/**
 * Makes [RandomAccessFile] as destination of [OutputStream].
 */
open class RandomOutputStream(
    private val destination: RandomAccessFile,
    private val offset: Long,
    private val length: Long
) : OutputStream() {

    init {
        checkRangeInBounds(offset, offset + length, 0, destination.length())
        destination.seek(offset)
    }

    private var pos = offset

    override fun write(b: Int) {
        pos.checkInBounds(offset, offset + length)
        destination.write(b)
        pos++
    }

    override fun write(b: ByteArray, off: Int, len: Int) {
        checkRangeInBounds(off, off + len, 0, b.size)
        val nextPos = pos + len
        (nextPos - 1).checkInBounds(offset, offset + length)
        destination.write(b, off, len)
        pos = nextPos
    }
}