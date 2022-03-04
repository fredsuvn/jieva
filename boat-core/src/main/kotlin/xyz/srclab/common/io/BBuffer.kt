@file:JvmName("BBuffer")

package xyz.srclab.common.io

import xyz.srclab.common.base.checkLengthInRange
import xyz.srclab.common.base.defaultCharset
import xyz.srclab.common.base.getString
import xyz.srclab.common.base.remainingLength
import java.nio.ByteBuffer
import java.nio.charset.Charset

fun newByteBuffer(capacity: Int, direct: Boolean): ByteBuffer {
    return if (direct) ByteBuffer.allocateDirect(capacity) else ByteBuffer.allocate(capacity)
}

@JvmOverloads
fun ByteBuffer.getBytes(length: Int = this.remaining(), useWrappedArray: Boolean = false): ByteArray {
    length.checkLengthInRange(0, this.remaining())
    if (
        useWrappedArray
        && this.hasArray()
        && length == this.remaining()
        && this.arrayOffset() == 0
        && this.position() == 0
        && this.remaining() == this.array().size
    ) {
        this.position(this.remaining())
        return this.array()
    }
    val array = ByteArray(length)
    this.get(array)
    return array
}

fun ByteBuffer.getBytes(useWrappedArray: Boolean): ByteArray {
    return getBytes(this.remaining(), useWrappedArray)
}

@JvmOverloads
fun ByteBuffer.getString(length: Int = this.remaining(), charset: Charset = defaultCharset()): String {
    length.checkLengthInRange(0, this.remaining())
    if (this.hasArray()) {
        val array = this.array()
        val arrayOffset = this.arrayOffset() + this.position()
        val result = array.getString(arrayOffset, length, charset)
        this.position(this.position() + length)
        return result
    }
    return getBytes(length).getString(charset)
}

fun ByteBuffer.getString(charset: Charset): String {
    return getString(this.remaining(), charset)
}

@JvmOverloads
fun ByteBuffer.getBuffer(length: Int, direct: Boolean = false): ByteBuffer {
    length.checkLengthInRange(0, this.remaining())
    return if (!direct) {
        val array = ByteArray(length)
        this.get(array)
        ByteBuffer.wrap(array)
    } else {
        val result = ByteBuffer.allocateDirect(length)
        val oldLimit = this.limit()
        this.limit(this.position() + length)
        result.put(this)
        this.limit(oldLimit)
        result.flip()
        result
    }
}

@JvmOverloads
fun ByteArray.getBuffer(
    offset: Int = 0,
    length: Int = remainingLength(this.size, offset),
    direct: Boolean = false
): ByteBuffer {
    val buffer = newByteBuffer(length, direct)
    buffer.put(this, offset, length)
    buffer.flip()
    return buffer
}

fun ByteArray.getBuffer(direct: Boolean): ByteBuffer {
    return getBuffer(0, size, direct)
}