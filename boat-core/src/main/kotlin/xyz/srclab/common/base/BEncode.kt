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
 * Returns data length from [base64Size].
 */
fun getDeBase64Length(base64Size: Int): Int {
    return newSizeForBlock(base64Size, 4, 3)
}

/**
 * Returns data length from [base64Size].
 */
fun getDeBase64Length(base64Size: Long): Long {
    return newSizeForBlock(base64Size, 4, 3)
}

/**
 * Base64 encodes [this].
 */
@JvmOverloads
fun CharSequence.base64(charset: Charset = defaultCharset()): String {
    return this.toByteArray(charset).base64().bytesToString8Bit()
}

/**
 * Base64 encodes [this].
 */
fun ByteArray.base64(): ByteArray {
    return Base64.getEncoder().encode(this)
}

/**
 * Base64 encodes [this].
 */
fun ByteBuffer.base64(): ByteBuffer {
    return Base64.getEncoder().encode(this)
}

/**
 * Base64 encodes [this].
 */
@JvmOverloads
fun ByteArray.base64(offset: Int, length: Int = remainingLength(this.size, offset)): ByteArray {
    return this.asInputStream(offset, length).base64()
}

/**
 * Base64 encodes [this].
 */
fun InputStream.base64(): ByteArray {
    val output = BytesAppender(getBase64Length(this.available()))
    val encOut = Base64.getEncoder().wrap(output)
    this.copyTo(encOut)
    encOut.close()
    return output.toBytes()
}

/**
 * Base64 encodes [this] into [dest], returns read bytes number.
 */
fun InputStream.base64(dest: OutputStream): Long {
    val out = dest.unclose()
    val encOut = Base64.getEncoder().wrap(out)
    val result = this.copyTo(encOut)
    encOut.close()
    return result
}

/**
 * Base64 decodes [this].
 */
@JvmOverloads
fun CharSequence.deBase64(charset: Charset = defaultCharset()): String {
    return this.toByteArray8Bit().deBase64().bytesToString(charset)
}

/**
 * Base64 decodes [this].
 */
fun ByteArray.deBase64(): ByteArray {
    return Base64.getDecoder().decode(this)
}

/**
 * Base64 decodes [this].
 */
fun ByteBuffer.deBase64(): ByteBuffer {
    return Base64.getDecoder().decode(this)
}

/**
 * Base64 decodes [this].
 */
@JvmOverloads
fun ByteArray.deBase64(offset: Int, length: Int = remainingLength(this.size, offset)): ByteArray {
    return this.asInputStream(offset, length).deBase64()
}

/**
 * Base64 decodes [this].
 */
fun InputStream.deBase64(): ByteArray {
    val encIn = Base64.getDecoder().wrap(this)
    val output = BytesAppender(getBase64Length(this.available()))
    encIn.copyTo(output)
    return output.toBytes()
}

/**
 * Base64 decodes [this] into [dest], returns read bytes number.
 */
fun InputStream.deBase64(dest: OutputStream): Long {
    val unclose = this.unclose()
    val encIn = Base64.getDecoder().wrap(unclose)
    encIn.copyTo(dest)
    return unclose.count
}

/**
 * Returns the hex length for [dataSize].
 */
fun getHexLength(dataSize: Int): Int {
    return dataSize * 2
}

/**
 * Returns the hex length for [dataSize].
 */
fun getHexLength(dataSize: Long): Long {
    return dataSize * 2
}

/**
 * Returns data length from [hexSize].
 */
fun getDeHexLength(hexSize: Int): Int {
    return hexSize / 2
}

/**
 * Returns data length from [hexSize].
 */
fun getDeHexLength(hexSize: Long): Long {
    return hexSize / 2
}

/**
 * Hex encodes [this].
 */
@JvmOverloads
fun CharSequence.hex(charset: Charset = defaultCharset()): String {
    return this.toByteArray(charset).hex().bytesToString8Bit()
}

/**
 * Hex encodes [this].
 */
fun ByteArray.hex(): ByteArray {
    return this.hex(0, this.size)
}

/**
 * Hex encodes [this].
 */
fun ByteBuffer.hex(): ByteBuffer {
    val dest = ByteBuffer.allocate(getHexLength(this.remaining()))
    this.hex(dest)
    dest.flip()
    return dest
}

/**
 * Hex encodes [this].
 */
@JvmOverloads
fun ByteArray.hex(offset: Int, length: Int = remainingLength(this.size, offset)): ByteArray {
    val dest = ByteArray(getHexLength(length))
    hex(this, offset, length, dest, 0)
    return dest
}

/**
 * Hex encodes [this].
 */
fun InputStream.hex(): ByteArray {
    val output = BytesAppender(getHexLength(this.available()))
    this.hex(output)
    return output.toBytes()
}

/**
 * Hex encodes [source] to [dest].
 */
fun hex(source: ByteArray, sourceOffset: Int, sourceLength: Int, dest: ByteArray, destOffset: Int) {
    var i = sourceOffset
    var j = destOffset
    while (i < sourceOffset + sourceLength) {
        val b = source[i++].toInt() and 0x000000ff
        dest[j++] = hex0(b ushr 4)
        dest[j++] = hex0(b and 0x0000000f)
    }
}

/**
 * Hex encodes [this] to [dest].
 */
fun ByteBuffer.hex(dest: ByteBuffer) {
    while (this.hasRemaining()) {
        val b = this.get().toInt() and 0x000000ff
        dest.put(hex0(b ushr 4))
        dest.put(hex0(b and 0x0000000f))
    }
}

/**
 * Hex encodes [this] to [dest], returns read bytes number.
 */
fun InputStream.hex(dest: OutputStream): Long {
    var i = this.read()
    var count = 0L
    while (i != -1) {
        val b = i and 0x000000ff
        dest.write(hex0(b ushr 4).toInt())
        dest.write(hex0(b and 0x0000000f).toInt())
        count++
        i = this.read()
    }
    return count
}

/**
 * Hex decodes [this].
 */
@JvmOverloads
fun CharSequence.deHex(charset: Charset = defaultCharset()): String {
    return this.toByteArray8Bit().deHex().bytesToString(charset)
}

/**
 * Hex decodes [this].
 */
fun ByteArray.deHex(): ByteArray {
    return this.deHex(0, this.size)
}

/**
 * Hex decodes [this].
 */
fun ByteBuffer.deHex(): ByteBuffer {
    val dest = ByteBuffer.allocate(getDeHexLength(this.remaining()))
    this.deHex(dest)
    dest.flip()
    return dest
}

/**
 * Hex decodes [this].
 */
@JvmOverloads
fun ByteArray.deHex(offset: Int, length: Int = remainingLength(this.size, offset)): ByteArray {
    val dest = ByteArray(getDeHexLength(length))
    deHex(this, offset, length, dest, 0)
    return dest
}

/**
 * Hex decodes [this].
 */
fun InputStream.deHex(): ByteArray {
    val output = BytesAppender(getDeHexLength(this.available()))
    this.deHex(output)
    return output.toBytes()
}

/**
 * Hex decodes [source] to [dest].
 */
fun deHex(source: ByteArray, sourceOffset: Int, sourceLength: Int, dest: ByteArray, destOffset: Int) {
    var i = sourceOffset
    var j = destOffset
    while (i < sourceOffset + sourceLength) {
        val b1 = deHex0(source[i++])
        val b2 = deHex0(source[i++])
        dest[j++] = ((b1 shl 4) or b2).toByte()
    }
}

/**
 * Hex decodes [this] to [dest].
 */
fun ByteBuffer.deHex(dest: ByteBuffer) {
    while (this.hasRemaining()) {
        val b1 = deHex0(this.get())
        val b2 = deHex0(this.get())
        dest.put(((b1 shl 4) or b2).toByte())
    }
}

/**
 * Hex decodes [this] to [dest], returns read bytes number.
 */
fun InputStream.deHex(dest: OutputStream): Long {
    var i = this.read()
    var count = 0L
    while (i != -1) {
        val b1 = deHex0(i.toByte())
        val i2 = this.read()
        if (i2 == -1) {
            throw IllegalArgumentException("Illegal hex, end source data at position: ${count + 1}!")
        }
        val b2 = deHex0(i2.toByte())
        dest.write((b1 shl 4) or b2)
        count += 2
        i = this.read()
    }
    return count
}

private fun hex0(i: Int): Byte {
    return BEncodeHolder.hexCodes[i * 2]
}

private fun deHex0(i: Byte): Int {
    val c = i.toInt().toChar()
    if (c in '0'..'9') {
        return BEncodeHolder.hexCodes[(c - '0') * 2 + 1].toInt()
    }
    if (c in 'a'..'f') {
        return BEncodeHolder.hexCodes[(c - 'a' + 10) * 2 + 1].toInt()
    }
    if (c in 'A'..'F') {
        return BEncodeHolder.hexCodes[(c - 'a' + 10) * 2 + 1].toInt()
    }
    throw IllegalArgumentException("Illegal hex char: ${i.toInt().toChar()}")
}

private object BEncodeHolder {
    val hexCodes = byteArrayOf(
        '0'.code.toByte(), 0,
        '1'.code.toByte(), 1,
        '2'.code.toByte(), 2,
        '3'.code.toByte(), 3,
        '4'.code.toByte(), 4,
        '5'.code.toByte(), 5,
        '6'.code.toByte(), 6,
        '7'.code.toByte(), 7,
        '8'.code.toByte(), 8,
        '9'.code.toByte(), 9,
        'a'.code.toByte(), 10,
        'b'.code.toByte(), 11,
        'c'.code.toByte(), 12,
        'd'.code.toByte(), 13,
        'e'.code.toByte(), 14,
        'f'.code.toByte(), 15,
    )
}