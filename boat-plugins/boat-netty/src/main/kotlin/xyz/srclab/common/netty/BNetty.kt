@file:JvmName("BNetty")

package xyz.srclab.common.netty

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import xyz.srclab.common.base.DEFAULT_CHARSET
import xyz.srclab.common.base.checkLengthInRange
import xyz.srclab.common.base.getString
import xyz.srclab.common.base.remainingLength
import java.nio.charset.Charset

fun newByteBuf(capacity: Int, direct: Boolean): ByteBuf {
    return if (direct) Unpooled.directBuffer(capacity, capacity) else Unpooled.buffer(capacity, capacity)
}

@JvmOverloads
fun ByteBuf.getBytes(length: Int = this.readableBytes(), useWrappedArray: Boolean = false): ByteArray {
    length.checkLengthInRange(0, this.readableBytes())
    if (
        useWrappedArray
        && this.hasArray()
        && length == this.readableBytes()
        && this.arrayOffset() == 0
        && this.readerIndex() == 0
        && this.readableBytes() == this.array().size
    ) {
        this.readerIndex(this.readableBytes())
        return this.array()
    }
    val array = ByteArray(length)
    this.readBytes(array)
    return array
}

fun ByteBuf.getBytes(useWrappedArray: Boolean): ByteArray {
    return getBytes(this.readableBytes(), useWrappedArray)
}

@JvmOverloads
fun ByteBuf.getString(length: Int = this.readableBytes(), charset: Charset = DEFAULT_CHARSET): String {
    length.checkLengthInRange(0, this.readableBytes())
    if (this.hasArray()) {
        val array = this.array()
        val arrayOffset = this.arrayOffset() + this.readerIndex()
        val result = array.getString(arrayOffset, length, charset)
        this.readerIndex(this.readerIndex() + length)
        return result
    }
    return getBytes(length).getString(charset)
}

fun ByteBuf.getString(charset: Charset): String {
    return getString(this.readableBytes(), charset)
}

@JvmOverloads
fun ByteBuf.getBuffer(length: Int, direct: Boolean = false): ByteBuf {
    length.checkLengthInRange(0, this.readableBytes())
    return if (!direct) {
        val array = ByteArray(length)
        this.readBytes(array)
        Unpooled.wrappedBuffer(array)
    } else {
        val result = Unpooled.directBuffer(length, length)
        result.writeBytes(this, length)
        result
    }
}

@JvmOverloads
fun ByteArray.getBuffer(
    offset: Int = 0,
    length: Int = remainingLength(this.size, offset),
    direct: Boolean = false
): ByteBuf {
    val buffer = newByteBuf(length, direct)
    buffer.writeBytes(this, offset, length)
    return buffer
}

fun ByteArray.getBuffer(direct: Boolean): ByteBuf {
    return getBuffer(0, size, direct)
}