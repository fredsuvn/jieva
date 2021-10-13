package xyz.srclab.common.io

import xyz.srclab.common.base.checkIndexInBounds
import java.io.OutputStream

/**
 * Implementation of [OutputStream] which uses an array to store the data.
 */
open class BytesOutputStream @JvmOverloads constructor(
    private val array: ByteArray,
    private val offset: Int = 0,
    private val length: Int = array.size - offset
) : OutputStream() {

    private var pos = offset

    override fun write(b: Int) {
        checkIndexInBounds(pos, offset, offset + length)
        array[pos] = b.toByte()
        pos++
    }

    override fun write(b: ByteArray) {
        write(b, 0, b.size)
    }

    override fun write(b: ByteArray, off: Int, len: Int) {
        val nextPos = pos + len
        checkIndexInBounds(nextPos - 1, offset, offset + length)
        System.arraycopy(b, off, array, pos, len)
        pos = nextPos
    }
}