@file:JvmName("BIO")

package xyz.srclab.common.io

import org.apache.commons.io.input.ReaderInputStream
import org.apache.commons.io.output.WriterOutputStream
import xyz.srclab.common.base.asTyped
import xyz.srclab.common.base.defaultBufferSize
import xyz.srclab.common.base.defaultCharset
import xyz.srclab.common.base.remainingLength
import java.io.*
import java.net.URL
import java.nio.ByteBuffer
import java.nio.charset.Charset
import kotlin.io.readBytes as readBytesKt

//Read write:

@JvmOverloads
fun InputStream.readBytes(close: Boolean = false): ByteArray {
    return this.let {
        val bytes = readBytesKt()
        if (close) {
            it.close()
        }
        bytes
    }
}

@JvmOverloads
fun InputStream.readString(charset: Charset = defaultCharset(), close: Boolean = false): String {
    return String(readBytes(close), charset)
}

@JvmOverloads
fun InputStream.readLines(charset: Charset = defaultCharset(), close: Boolean = false): List<String> {
    return readString(charset, close).lines()
}

@JvmOverloads
fun InputStream.availableBytes(close: Boolean = false): ByteArray {
    val available = this.available()
    if (available == 0) {
        return byteArrayOf()
    }
    val bytes = ByteArray(available)
    this.read(bytes)
    if (close) {
        this.close()
    }
    return bytes
}

@JvmOverloads
fun InputStream.availableString(charset: Charset = defaultCharset(), close: Boolean = false): String {
    return String(availableBytes(close), charset)
}

@JvmOverloads
fun InputStream.availableLines(charset: Charset = defaultCharset(), close: Boolean = false): List<String> {
    return availableString(charset, close).lines()
}

@JvmOverloads
fun Reader.readString(close: Boolean = false): String {
    return this.readText().let {
        if (close) {
            this.close()
        }
        it
    }
}

@JvmOverloads
fun Reader.readLines(close: Boolean = false): List<String> {
    return readString(close).lines()
}

@JvmOverloads
fun InputStream.readTo(output: OutputStream, bufferSize: Int = defaultBufferSize()): Long {
    return this.copyTo(output, bufferSize)
}

@JvmOverloads
fun Reader.readTo(output: Writer, bufferSize: Int = defaultBufferSize()): Long {
    return this.copyTo(output, bufferSize)
}

//Convert:

@JvmOverloads
fun InputStream.asBuffered(bufferSize: Int = defaultBufferSize()): BufferedInputStream {
    return this.buffered(bufferSize)
}

@JvmOverloads
fun OutputStream.asBuffered(bufferSize: Int = defaultBufferSize()): BufferedOutputStream {
    return this.buffered(bufferSize)
}

@JvmOverloads
fun InputStream.asReader(charset: Charset = defaultCharset()): Reader {
    return InputStreamReader(this, charset)
}

@JvmOverloads
fun OutputStream.asWriter(charset: Charset = defaultCharset()): Writer {
    return OutputStreamWriter(this, charset)
}

@JvmOverloads
fun InputStream.asBufferedReader(
    charset: Charset = defaultCharset(),
    bufferSize: Int = defaultBufferSize()
): BufferedReader {
    return this.reader(charset).buffered(bufferSize)
}

@JvmOverloads
fun OutputStream.asBufferedWriter(
    charset: Charset = defaultCharset(),
    bufferSize: Int = defaultBufferSize()
): BufferedWriter {
    return this.writer(charset).buffered(bufferSize)
}

@JvmOverloads
fun Reader.asInputStream(charset: Charset = defaultCharset()): InputStream {
    return ReaderInputStream(this, charset)
}

@JvmOverloads
fun Writer.asOutputStream(charset: Charset = defaultCharset()): OutputStream {
    return WriterOutputStream(this, charset)
}

@JvmOverloads
fun ByteArray.asInputStream(offset: Int = 0, length: Int = remainingLength(this.size, offset)): BytesInputStream {
    return BytesInputStream(this, offset, length)
}

@JvmOverloads
fun ByteArray.asOutputStream(offset: Int = 0, length: Int = remainingLength(this.size, offset)): BytesOutputStream {
    return BytesOutputStream(this, offset, length)
}

@JvmOverloads
fun CharArray.asReader(offset: Int = 0, length: Int = remainingLength(this.size, offset)): CharsReader {
    return CharsReader(this, offset, length)
}

@JvmOverloads
fun CharArray.asWriter(offset: Int = 0, length: Int = remainingLength(this.size, offset)): CharsWriter {
    return CharsWriter(this, offset, length)
}

@JvmOverloads
fun <T : CharSequence> T.asReader(
    offset: Int = 0,
    length: Int = remainingLength(this.length, offset)
): CharSeqReader<T> {
    return CharSeqReader(this, offset, length)
}

fun <T : Appendable> T.asWriter(): AppendableWriter<T> {
    return AppendableWriter(this)
}

fun ByteBuffer.asInputStream(): ByteBufferInputStream {
    return ByteBufferInputStream(this)
}

fun ByteBuffer.asOutputStream(): ByteBufferOutputStream {
    return ByteBufferOutputStream(this)
}

fun RandomAccessFile.asInputStream(
    offset: Long = 0, length: Long = remainingLength(this.length(), offset)
): RandomInputStream {
    return RandomInputStream(this, offset, length)
}

fun RandomAccessFile.asOutputStream(
    offset: Long = 0, length: Long = remainingLength(this.length(), offset)
): RandomOutputStream {
    return RandomOutputStream(this, offset, length)
}

fun URL.toInputStream(): InputStream {
    return this.openStream()
}

@JvmOverloads
fun Any.writeObject(output: OutputStream, close: Boolean = false) {
    val oop = ObjectOutputStream(output)
    oop.writeObject(this)
    if (close) {
        oop.close()
    }
}

@JvmOverloads
fun Any.writeObject(outputFile: CharSequence, close: Boolean = false) {
    return writeObject(outputFile.openFileOutputStream(), close)
}

@JvmOverloads
fun Any.writeObject(outputFile: File, close: Boolean = false) {
    return writeObject(outputFile.openOutputStream(), close)
}

@JvmOverloads
fun <T> CharSequence.readObject(close: Boolean = false): T {
    return this.openFileInputStream().readObject(close)
}

@JvmOverloads
fun <T> InputStream.readObject(close: Boolean = false): T {
    val ooi = ObjectInputStream(this)
    return ooi.readObject().asTyped()
}

@JvmOverloads
fun <T> File.readObject(close: Boolean = false): T {
    return this.openInputStream().readObject(close)
}

/**
 * Returns a [InputStream] which wraps all method of given stream,
 * but prevents the `close` method, and count the read bytes.
 *
 * @see UncloseInputStream
 */
fun <T : InputStream> T.unclose(): UncloseInputStream<T> {
    return UncloseInputStream(this)
}

/**
 * Returns a [OutputStream] which wraps all method of given stream,
 * but prevents the `close` method, and count the written bytes.
 *
 * @see UncloseOutputStream
 */
fun <T : OutputStream> T.unclose(): UncloseOutputStream<T> {
    return UncloseOutputStream(this)
}