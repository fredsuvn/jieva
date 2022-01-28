@file:JvmName("BBuffer")

package xyz.srclab.common.io

import xyz.srclab.common.base.remainingLength
import java.nio.ByteBuffer

/**
 * Reads all bytes of given [ByteBuffer].
 *
 * If [tryInternalArray] is true, and the [ByteBuffer] wraps an array, and
 *
 * ```
 * buffer.arrayOffset() == 0 && buffer.position() == 0 && buffer.limit() == array.size
 * ```
 *
 * then `buffer.array()` will be returned.
 */
@JvmOverloads
fun ByteBuffer.toBytes(tryInternalArray: Boolean = false): ByteArray {
    if (tryInternalArray && this.hasArray()) {
        val array = this.array()
        if (this.arrayOffset() == 0 && this.position() == 0 && this.limit() == array.size) {
            this.position(this.limit())
            return array
        }
    }
    val array = ByteArray(this.remaining())
    this.get(array)
    return array
}

fun ByteBuffer.getBuffer(length: Int): ByteBuffer {
    val array = ByteArray(length)
    this.get(array)
    return ByteBuffer.wrap(array)
}

/**
 * Return a [ByteBuffer] with full data come from [this] array, of which position is `0`, limit is size of array.
 */
fun ByteArray.toBuffer(direct: Boolean): ByteBuffer {
    return toBuffer(0, this.size, direct)
}

/**
 * Return a [ByteBuffer] with full data come from [this] array, of which position is `0`, limit is [length].
 */
@JvmOverloads
fun ByteArray.toBuffer(
    offset: Int = 0,
    length: Int = remainingLength(this.size, offset),
    direct: Boolean = false
): ByteBuffer {
    val buffer = if (direct) ByteBuffer.allocateDirect(length) else ByteBuffer.allocate(length)
    buffer.put(this, offset, length)
    buffer.flip()
    return buffer
}















