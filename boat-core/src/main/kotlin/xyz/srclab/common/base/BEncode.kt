@file:JvmName("BEncode")

package xyz.srclab.common.base

import xyz.srclab.common.io.BytesAppender
import xyz.srclab.common.io.asInputStream
import java.io.InputStream
import java.io.OutputStream
import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.util.*

fun getBase64Length(sourceSize: Int): Int {
    val rl = sourceSize / 3 * 4
    return if (sourceSize % 3 == 0) rl else rl + 4
}

fun getBase64Length(sourceSize: Long): Long {
    val rl = sourceSize / 3 * 4
    return if (sourceSize % 3 == 0L) rl else rl + 4
}

fun getDeBase64Length(base64Size: Int): Int {
    return base64Size / 4 * 3
}

fun getDeBase64Length(base64Size: Long): Long {
    return base64Size / 4 * 3
}

@JvmOverloads
fun CharSequence.base64(charset: Charset = DEFAULT_CHARSET): String {
    return this.byteArray(charset).base64().to8BitString()
}

fun ByteArray.base64(): ByteArray {
    return Base64.getEncoder().encode(this)
}

fun ByteBuffer.base64(): ByteBuffer {
    return Base64.getEncoder().encode(this)
}

@JvmOverloads
fun ByteArray.base64(offset: Int, length: Int = remainingLength(this.size, offset)): ByteArray {
    return this.asInputStream(offset, length).base64()
}

fun InputStream.base64(): ByteArray {
    val output = BytesAppender()
    val encOut = Base64.getEncoder().wrap(output)
    this.copyTo(encOut)
    encOut.close()
    return output.toBytes()
}

fun InputStream.base64(output: OutputStream): Long {
    val encOut = Base64.getEncoder().wrap(output)
    return this.copyTo(encOut)
}

@JvmOverloads
fun CharSequence.deBase64(charset: Charset = DEFAULT_CHARSET): String {
    return this.to8BitBytes().deBase64().string(charset)
}

fun ByteArray.deBase64(): ByteArray {
    return Base64.getDecoder().decode(this)
}

fun ByteBuffer.deBase64(): ByteBuffer {
    return Base64.getDecoder().decode(this)
}

@JvmOverloads
fun ByteArray.deBase64(offset: Int, length: Int = remainingLength(this.size, offset)): ByteArray {
    return this.asInputStream(offset, length).deBase64()
}

fun InputStream.deBase64(): ByteArray {
    val encIn = Base64.getDecoder().wrap(this)
    val output = BytesAppender()
    encIn.copyTo(output)
    return output.toBytes()
}

fun InputStream.deBase64(output: OutputStream): Long {
    val encIn = Base64.getDecoder().wrap(this)
    return encIn.copyTo(output)
}