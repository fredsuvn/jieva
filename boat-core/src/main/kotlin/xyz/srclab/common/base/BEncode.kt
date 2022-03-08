@file:JvmName("BEncode")

package xyz.srclab.common.base

import xyz.srclab.common.io.BytesAppender
import xyz.srclab.common.io.asInputStream
import xyz.srclab.common.io.unclose
import java.io.InputStream
import java.io.OutputStream
import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.util.*

/**
 * Returns the base64 length for [dataSize].
 */
fun getBase64Length(dataSize: Int): Int {
    return newSizeForBlock(dataSize, 3, 4)
}

/**
 * Returns the base64 length for [dataSize].
 */
fun getBase64Length(dataSize: Long): Long {
    return newSizeForBlock(dataSize, 3, 4)
}

/**
 * Returns data base64 length for [base64Size].
 */
fun getDeBase64Length(base64Size: Int): Int {
    return newSizeForBlock(base64Size, 4, 3)
}

/**
 * Returns data base64 length for [base64Size].
 */
fun getDeBase64Length(base64Size: Long): Long {
    return newSizeForBlock(base64Size, 4, 3)
}

/**
 * Returns base64 bytes (as string) of [this].
 */
@JvmOverloads
fun CharSequence.base64(charset: Charset = defaultCharset()): String {
    return this.charsToBytes(charset).base64().bytesToString8Bit()
}

/**
 * Returns base64 bytes of [this].
 */
fun ByteArray.base64(): ByteArray {
    return Base64.getEncoder().encode(this)
}

/**
 * Returns base64 bytes of [this].
 */
fun ByteBuffer.base64(): ByteBuffer {
    return Base64.getEncoder().encode(this)
}

/**
 * Returns base64 bytes of [this].
 */
@JvmOverloads
fun ByteArray.base64(offset: Int, length: Int = remainingLength(this.size, offset)): ByteArray {
    return this.asInputStream(offset, length).base64()
}

/**
 * Returns base64 bytes of [this].
 */
fun InputStream.base64(): ByteArray {
    val output = BytesAppender(getBase64Length(this.available()))
    val encOut = Base64.getEncoder().wrap(output)
    this.copyTo(encOut)
    encOut.close()
    return output.toBytes()
}

/**
 * Writes base64 bytes of [this] into [dest], returns length of written base64 bytes.
 */
fun InputStream.base64(dest: OutputStream): Long {
    val out = dest.unclose()
    val encOut = Base64.getEncoder().wrap(out)
    val result = this.copyTo(encOut)
    encOut.close()
    return result
}

/**
 * De-base64 [this] and return source string.
 */
@JvmOverloads
fun CharSequence.deBase64(charset: Charset = defaultCharset()): String {
    return this.charsToBytes8Bit().deBase64().bytesToString(charset)
}

/**
 * De-base64 [this] and return source bytes.
 */
fun ByteArray.deBase64(): ByteArray {
    return Base64.getDecoder().decode(this)
}

/**
 * De-base64 [this] and return source bytes.
 */
fun ByteBuffer.deBase64(): ByteBuffer {
    return Base64.getDecoder().decode(this)
}

/**
 * De-base64 [this] and return source bytes.
 */
@JvmOverloads
fun ByteArray.deBase64(offset: Int, length: Int = remainingLength(this.size, offset)): ByteArray {
    return this.asInputStream(offset, length).deBase64()
}

/**
 * De-base64 [this] and return source bytes.
 */
fun InputStream.deBase64(): ByteArray {
    val encIn = Base64.getDecoder().wrap(this)
    val output = BytesAppender(getBase64Length(this.available()))
    encIn.copyTo(output)
    return output.toBytes()
}

/**
 * De-base64 [this] and writes into [dest], returns length of written bytes.
 */
fun InputStream.deBase64(dest: OutputStream): Long {
    val encIn = Base64.getDecoder().wrap(this)
    return encIn.copyTo(dest)
}

private object HexUtil {

    private val hexBytes: ByteArray = ByteArray(16) {
        it.toByte()
    }

    fun hex(
        source: ByteArray, sourceOffset: Int, sourceLength: Int,
        dest: ByteArray, destOffset: Int,
    ) {
        var i = sourceOffset
        var j = destOffset
        while (i < sourceOffset + sourceLength) {
            val b = source[i].toInt() and 0x0000ffff
            dest[j++] = (b shr 4).toByte()
            dest[j++] = b.toByte()
            i++
        }
    }

    fun hex(source: ByteBuffer, dest: ByteBuffer) {
        while (source.hasRemaining()) {
            val b = source.get().toInt() and 0x0000ffff
            dest.put((b shr 4).toByte())
            dest.put(b.toByte())
        }
    }
}