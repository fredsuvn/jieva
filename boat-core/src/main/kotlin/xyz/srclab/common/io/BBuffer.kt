@file:JvmName("BBuffer")

package xyz.srclab.common.io

import xyz.srclab.common.base.*
import java.io.InputStream
import java.io.OutputStream
import java.nio.ByteBuffer
import java.nio.MappedByteBuffer
import java.nio.charset.Charset

/**
 * Creates and returns new a [ByteBuffer].
 */
fun newByteBuffer(capacity: Int, direct: Boolean): ByteBuffer {
    return if (direct) ByteBuffer.allocateDirect(capacity) else ByteBuffer.allocate(capacity)
}

/**
 * Gets bytes from [this] buffer.
 *
 * @param length the length to read
 * @param useWrappedArray return wrapped byte array directly if this buffer has a wrapped byte array
 */
@JvmOverloads
fun ByteBuffer.getBytes(length: Int = this.remaining(), useWrappedArray: Boolean = false): ByteArray {
    length.checkInBounds(0, this.remaining() + 1)
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

/**
 * Gets all bytes from [this] buffer.
 * @param useWrappedArray return wrapped byte array directly if this buffer has a wrapped byte array
 */
fun ByteBuffer.getBytes(useWrappedArray: Boolean): ByteArray {
    return getBytes(this.remaining(), useWrappedArray)
}

/**
 * Gets bytes as string from [this] buffer.
 * @param length the length to read
 */
@JvmOverloads
fun ByteBuffer.getString(length: Int = this.remaining(), charset: Charset = defaultCharset()): String {
    length.checkInBounds(0, this.remaining() + 1)
    if (this.hasArray()) {
        val array = this.array()
        val arrayOffset = this.arrayOffset() + this.position()
        val result = array.getString(arrayOffset, length, charset)
        this.position(this.position() + length)
        return result
    }
    return getBytes(length).getString(charset)
}

/**
 * Gets all bytes as string from [this] buffer.
 */
fun ByteBuffer.getString(charset: Charset): String {
    return getString(this.remaining(), charset)
}

/**
 * Gets bytes as buffer from [this] buffer.
 * @param length the length to read
 */
@JvmOverloads
fun ByteBuffer.getBuffer(length: Int, direct: Boolean = false): ByteBuffer {
    length.checkInBounds(0, this.remaining() + 1)
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

/**
 * Gets bytes as buffer from [this] byte array.
 */
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

/**
 * Gets all bytes as buffer from [this] byte array.
 */
fun ByteArray.getBuffer(direct: Boolean): ByteBuffer {
    return getBuffer(0, size, direct)
}

/**
 * Returns a [InputStream] of which content from [this] [ByteBuffer].
 */
fun ByteBuffer.asInputStream(): ByteBufferInputStream {
    return ByteBufferInputStream(this)
}

/**
 * Returns a [OutputStream] of which content from [this] [ByteBuffer].
 */
fun ByteBuffer.asOutputStream(): ByteBufferOutputStream {
    return ByteBufferOutputStream(this)
}

/**
 * Tries to close [this] buffer.
 */
@JvmName("closeBuffer")
fun MappedByteBuffer.close(): Boolean {
    this.force()
    if (isJdk9OrHigher()) {
        this.invokeCleaner()
        return true
    }
    if (this is sun.nio.ch.DirectBuffer) {
        val cleaner = this.cleaner()
        if (cleaner !== null) {
            cleaner.clean()
            return true
        }
    }
    return false
}