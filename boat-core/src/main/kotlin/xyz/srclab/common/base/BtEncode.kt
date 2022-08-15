/**
 * Encode utilities.
 */
@file:JvmName("BtEncode")

package xyz.srclab.common.base

import xyz.srclab.common.divNumber
import xyz.srclab.common.io.toUnclose
import java.io.InputStream
import java.io.OutputStream
import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.util.*

/**
 * Returns the base64 length for [dataSize].
 */
fun base64Length(dataSize: Int): Int {
    return divNumber(dataSize, 3) * 4
}

/**
 * Returns the base64 length for [dataSize].
 */
fun base64Length(dataSize: Long): Long {
    return divNumber(dataSize, 3) * 4
}

/**
 * Returns data length from [base64Size].
 */
fun deBase64Length(base64Size: Int): Int {
    return divNumber(base64Size, 4) * 3
}

/**
 * Returns data length from [base64Size].
 */
fun deBase64Length(base64Size: Long): Long {
    return divNumber(base64Size, 4) * 3
}

/**
 * Returns base64 of [data].
 */
@JvmOverloads
fun base64(data: CharSequence, charset: Charset = BtProps.charset()): String {
    return base64(data.getBytes(charset)).getString8Bit()
}

/**
 * Returns base64 of [data].
 */
fun base64(data: ByteArray): ByteArray {
    return Base64.getEncoder().encode(data)
}

/**
 * Returns base64 of [data].
 */
fun base64(data: ByteBuffer): ByteBuffer {
    return Base64.getEncoder().encode(data)
}

/**
 * Encodes data from [data] to [dest] in Base64, returns the number of read bytes.
 */
fun base64(data: InputStream, dest: OutputStream): Long {
    val out = dest.toUnclose()
    val encOut = Base64.getEncoder().wrap(out)
    val result = data.copyTo(encOut)
    encOut.close()
    return result
}

/**
 * Returns de-base64 of [base64].
 */
@JvmOverloads
fun deBase64(base64: CharSequence, charset: Charset = BtProps.charset()): String {
    return deBase64(base64.getBytes8Bit()).getString(charset)
}

/**
 * Returns de-base64 of [base64].
 */
fun deBase64(base64: ByteArray): ByteArray {
    return Base64.getDecoder().decode(base64)
}

/**
 * Returns de-base64 of [base64].
 */
fun deBase64(base64: ByteBuffer): ByteBuffer {
    return Base64.getDecoder().decode(base64)
}

/**
 * Decodes data from [base64] to [dest] in Base64, returns the number of written bytes.
 */
fun deBase64(base64: InputStream, dest: OutputStream): Long {
    val encIn = Base64.getDecoder().wrap(base64)
    val output = dest.toUnclose()
    encIn.copyTo(output)
    return output.count
}

/**
 * Returns the hex length for [dataSize].
 */
fun hexLength(dataSize: Int): Int {
    return dataSize * 2
}

/**
 * Returns the hex length for [dataSize].
 */
fun hexLength(dataSize: Long): Long {
    return dataSize * 2
}

/**
 * Returns data length from [hexSize].
 */
fun deHexLength(hexSize: Int): Int {
    return hexSize / 2
}

/**
 * Returns data length from [hexSize].
 */
fun deHexLength(hexSize: Long): Long {
    return hexSize / 2
}

/**
 * Returns hex of [data].
 */
@JvmOverloads
fun hex(data: CharSequence, charset: Charset = BtProps.charset()): String {
    return hex(data.getBytes(charset)).getString8Bit()
}

/**
 *Returns hex of [data].
 */
fun hex(data: ByteArray): ByteArray {
    val result = ByteArray(hexLength(data.size))
    hex(data, 0, data.size, result, 0)
    return result
}

/**
 * Returns hex of [data].
 */
fun hex(data: ByteBuffer): ByteBuffer {
    val dest = ByteBuffer.allocate(hexLength(data.remaining()))
    hex(data, dest)
    dest.flip()
    return dest
}

/**
 * Encodes data from [data] to [dest] in Hex, returns the number of read bytes.
 */
fun hex(data: InputStream, dest: OutputStream): Long {
    var i = data.read()
    var count = 0L
    while (i != -1) {
        val b = i and 0x000000ff
        dest.write(hex0(b ushr 4).toInt())
        dest.write(hex0(b and 0x0000000f).toInt())
        count++
        i = data.read()
    }
    return count
}

/**
 * Encodes data from [data] to [dest] in Hex.
 */
fun hex(data: ByteArray, dataOffset: Int, dataLength: Int, dest: ByteArray, destOffset: Int) {
    var i = dataOffset
    var j = destOffset
    while (i < dataOffset + dataLength) {
        val b = data[i++].toInt() and 0x000000ff
        dest[j++] = hex0(b ushr 4)
        dest[j++] = hex0(b and 0x0000000f)
    }
}

/**
 * Encodes data from [data] to [dest] in Hex.
 */
fun hex(data: ByteBuffer, dest: ByteBuffer) {
    while (data.hasRemaining()) {
        val b = data.get().toInt() and 0x000000ff
        dest.put(hex0(b ushr 4))
        dest.put(hex0(b and 0x0000000f))
    }
}

/**
 * Returns de-hex of [data].
 */
@JvmOverloads
fun deHex(data: CharSequence, charset: Charset = BtProps.charset()): String {
    return hex(data.getBytes(charset)).getString8Bit()
}

/**
 *Returns de-hex of [hex].
 */
fun deHex(hex: ByteArray): ByteArray {
    val result = ByteArray(deHexLength(hex.size))
    deHex(hex, 0, hex.size, result, 0)
    return result
}

/**
 * Returns de-hex of [hex].
 */
fun deHex(hex: ByteBuffer): ByteBuffer {
    val dest = ByteBuffer.allocate(hexLength(hex.remaining()))
    hex(hex, dest)
    dest.flip()
    return dest
}

/**
 * Decodes data from [hex] to [dest] in Hex, returns the number of read bytes.
 */
fun deHex(hex: InputStream, dest: OutputStream): Long {
    var i = hex.read()
    var count = 0L
    while (i != -1) {
        val b1 = deHex0(i.toByte())
        val i2 = hex.read()
        if (i2 == -1) {
            throw IllegalArgumentException("Wrong hex size, at position: ${count + 1}.")
        }
        val b2 = deHex0(i2.toByte())
        dest.write((b1 shl 4) or b2)
        count += 2
        i = hex.read()
    }
    return count
}

/**
 * Decodes data from [hex] to [dest] in Hex.
 */
fun deHex(hex: ByteArray, hexOffset: Int, hexLength: Int, dest: ByteArray, destOffset: Int) {
    var i = hexOffset
    var j = destOffset
    while (i < hexOffset + hexLength) {
        val b1 = deHex0(hex[i++])
        val b2 = deHex0(hex[i++])
        dest[j++] = ((b1 shl 4) or b2).toByte()
    }
}

/**
 * Decodes data from [hex] to [dest] in Hex.
 */
fun deHex(hex: ByteBuffer, dest: ByteBuffer) {
    while (hex.hasRemaining()) {
        val b1 = deHex0(hex.get())
        val b2 = deHex0(hex.get())
        dest.put(((b1 shl 4) or b2).toByte())
    }
}

private fun hex0(i: Int): Byte {
    return BtEncodeHolder.hexCodes[i * 2]
}

private fun deHex0(i: Byte): Int {
    val c = i.toInt().toChar()
    if (c in '0'..'9') {
        return BtEncodeHolder.hexCodes[(c - '0') * 2 + 1].toInt()
    }
    if (c in 'a'..'f') {
        return BtEncodeHolder.hexCodes[(c - 'a' + 10) * 2 + 1].toInt()
    }
    if (c in 'A'..'F') {
        return BtEncodeHolder.hexCodes[(c - 'a' + 10) * 2 + 1].toInt()
    }
    throw IllegalArgumentException("Illegal hex char: ${i.toInt().toChar()}")
}

private object BtEncodeHolder {
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