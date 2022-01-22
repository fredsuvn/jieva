package xyz.srclab.common.io

import xyz.srclab.common.base.remainingLength
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.nio.ByteBuffer
import java.nio.charset.Charset

/**
 * Non-Synchronized version of [ByteArrayOutputStream].
 */
open class BytesAppender @JvmOverloads constructor(size: Int = 32) : OutputStream() {

    protected var buf: ByteArray
    protected var count = 0

    init {
        if (size < 0) {
            throw IllegalArgumentException("Negative initial size: $size")
        }
        buf = ByteArray(size)
    }

    private fun ensureCapacity(minCapacity: Int) {
        // overflow-conscious code
        if (minCapacity - buf.size > 0) grow(minCapacity)
    }

    private fun grow(minCapacity: Int) {
        // overflow-conscious code
        val oldCapacity = buf.size
        var newCapacity = oldCapacity shl 1
        if (newCapacity - minCapacity < 0) newCapacity = minCapacity
        if (newCapacity - MAX_ARRAY_SIZE > 0) newCapacity = hugeCapacity(minCapacity)
        buf = buf.copyOf(newCapacity)
    }

    private fun hugeCapacity(minCapacity: Int): Int {
        if (minCapacity < 0) throw OutOfMemoryError()
        return if ((minCapacity > MAX_ARRAY_SIZE)) Int.MAX_VALUE else MAX_ARRAY_SIZE
    }

    override fun write(b: Int) {
        ensureCapacity(count + 1)
        buf[count] = b.toByte()
        count += 1
    }

    override fun write(b: ByteArray, off: Int, len: Int) {
        if (((off < 0) || (off > b.size) || (len < 0) ||
                ((off + len) - b.size > 0))
        ) {
            throw IndexOutOfBoundsException()
        }
        ensureCapacity(count + len)
        System.arraycopy(b, off, buf, count, len)
        count += len
    }

    open fun writeTo(out: OutputStream) {
        out.write(buf, 0, count)
    }

    open fun append(b: Int) {
        write(b)
    }

    open fun append(b: Byte) {
        write(b.toInt())
    }

    open fun append(array: ByteArray) {
        write(array)
    }

    open fun append(array: ByteArray, offset: Int) {
        write(array, offset, remainingLength(array.size, offset))
    }

    open fun append(array: ByteArray, offset: Int, length: Int) {
        write(array, offset, length)
    }

    open fun append(byteBuffer: ByteBuffer) {
        while (byteBuffer.hasRemaining()) {
            append(byteBuffer.get())
        }
    }

    open fun reset() {
        count = 0
    }

    open fun toBytes(): ByteArray {
        return buf.copyOf(count)
    }

    open fun size(): Int {
        return count
    }

    override fun toString(): String {
        return String(buf, 0, count)
    }

    open fun toString(charset: Charset): String {
        return String(buf, 0, count, charset)
    }

    override fun close() {
    }

    companion object {
        private const val MAX_ARRAY_SIZE = Int.MAX_VALUE - 8
    }
}