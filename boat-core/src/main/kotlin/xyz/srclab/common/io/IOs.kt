@file:JvmName("IOs")

package xyz.srclab.common.io

import org.apache.commons.io.IOUtils
import org.apache.commons.io.input.CharSequenceReader
import org.apache.commons.io.output.AppendableWriter
import xyz.srclab.common.base.DEFAULT_CHARSET
import xyz.srclab.common.base.toChars
import java.io.*
import java.nio.ByteBuffer
import java.nio.charset.Charset
import kotlin.io.readBytes as readBytesKt

fun InputStream.readBytes(): ByteArray {
    return this.readBytesKt()
}

@JvmOverloads
fun InputStream.readString(charset: Charset = DEFAULT_CHARSET): String {
    return toReader(charset).readText()
}

@JvmOverloads
fun InputStream.readLines(charset: Charset = DEFAULT_CHARSET): List<String> {
    return IOUtils.readLines(this, charset)
}

fun InputStream.availableBytes(): ByteArray {
    val available = this.available()
    if (available == 0) {
        return byteArrayOf()
    }
    val bytes = ByteArray(available)
    this.read(bytes)
    return bytes
}

@JvmOverloads
fun InputStream.availableString(charset: Charset = DEFAULT_CHARSET): String {
    return availableBytes().toChars(charset)
}

fun Reader.readString(): String {
    return this.readText()
}

fun Reader.readLines(): List<String> {
    return IOUtils.readLines(this)
}

@JvmOverloads
fun InputStream.toReader(charset: Charset = DEFAULT_CHARSET): Reader {
    return InputStreamReader(this, charset)
}

@JvmOverloads
fun OutputStream.toWriter(charset: Charset = DEFAULT_CHARSET): Writer {
    return OutputStreamWriter(this, charset)
}

@JvmOverloads
fun InputStream.toBuffered(bufferSize: Int = DEFAULT_BUFFER_SIZE): BufferedInputStream {
    return BufferedInputStream(this, bufferSize)
}

@JvmOverloads
fun OutputStream.toBuffered(bufferSize: Int = DEFAULT_BUFFER_SIZE): BufferedOutputStream {
    return BufferedOutputStream(this, bufferSize)
}

@JvmOverloads
fun InputStream.readTo(bytes: ByteArray, offset: Int = 0, length: Int = bytes.size - offset): ByteArray {
    IOUtils.readFully(this, bytes, offset, length)
    return bytes
}

@JvmOverloads
fun InputStream.readTo(output: OutputStream, bufferSize: Int = DEFAULT_BUFFER_SIZE): Long {
    return this.copyTo(output, bufferSize)
}

@JvmOverloads
fun Reader.readTo(chars: CharArray, offset: Int = 0, length: Int = chars.size - offset): CharArray {
    IOUtils.readFully(this, chars, offset, length)
    return chars
}

@JvmOverloads
fun Reader.readTo(output: Writer, bufferSize: Int = DEFAULT_BUFFER_SIZE): Long {
    return this.copyTo(output, bufferSize)
}

@JvmOverloads
fun ByteArray.toInputStream(offset: Int = 0, length: Int = this.size - offset): ByteArrayInputStream {
    return ByteArrayInputStream(this, offset, length)
}

@JvmOverloads
fun ByteArray.toOutputStream(offset: Int = 0, length: Int = this.size - offset): BytesOutputStream {
    return BytesOutputStream(this, offset, length)
}

@JvmOverloads
fun CharSequence.toReader(offset: Int = 0, length: Int = this.length - offset): CharSequenceReader {
    return CharSequenceReader(this, offset, offset + length)
}

fun <T : Appendable> T.toWriter(): AppendableWriter<T> {
    return AppendableWriter(this)
}

@JvmOverloads
fun ByteBuffer.toBytes(useBackedArray: Boolean = false): ByteArray {
    if (this.hasArray()) {
        return if (useBackedArray) {
            this.array()
        } else {
            this.array().clone()
        }
    }
    val array = ByteArray(this.remaining())
    this.get(array)
    return array
}

fun ByteBuffer.toInputStream(): InputStream {
    return ByteBufferInputStream(this)
}

fun ByteBuffer.toOutputStream(): OutputStream {
    return ByteBufferOutputStream(this)
}