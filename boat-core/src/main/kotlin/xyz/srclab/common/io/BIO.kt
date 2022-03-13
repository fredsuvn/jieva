@file:JvmName("BIO")

package xyz.srclab.common.io

import org.apache.commons.io.input.ReaderInputStream
import org.apache.commons.io.output.WriterOutputStream
import xyz.srclab.common.base.defaultBufferSize
import xyz.srclab.common.base.defaultCharset
import xyz.srclab.common.base.remainingLength
import java.io.*
import java.net.URL
import java.nio.charset.Charset
import kotlin.io.readBytes as readBytesKt

/**
 * Reads all bytes and returns.
 * @param close whether close this stream after reading
 */
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

/**
 * Reads all bytes and returns as string.
 * @param close whether close this stream after reading
 */
@JvmOverloads
fun InputStream.readString(charset: Charset = defaultCharset(), close: Boolean = false): String {
    return String(readBytes(close), charset)
}

/**
 * Reads all bytes and returns as string with [defaultCharset].
 * @param close whether close this stream after reading
 */
fun InputStream.readString(close: Boolean): String {
    return this.readString(defaultCharset(), close)
}

/**
 * Reads all bytes and returns as string in lines.
 * @param close whether close this stream after reading
 */
@JvmOverloads
fun InputStream.readLines(charset: Charset = defaultCharset(), close: Boolean = false): List<String> {
    return readString(charset, close).lines()
}

/**
 * Reads all bytes and returns as string with [defaultCharset] in lines.
 * @param close whether close this stream after reading
 */
fun InputStream.readLines(close: Boolean): List<String> {
    return this.readLines(defaultCharset(), close)
}

/**
 * Reads available bytes and returns.
 */
fun InputStream.availableBytes(): ByteArray {
    val available = this.available()
    if (available == 0) {
        return byteArrayOf()
    }
    val bytes = ByteArray(available)
    val count = this.read(bytes)
    return if (count == bytes.size) {
        bytes
    } else {
        bytes.copyOfRange(0, count)
    }
}

/**
 * Reads available bytes and returns as string.
 */
@JvmOverloads
fun InputStream.availableString(charset: Charset = defaultCharset()): String {
    return String(availableBytes(), charset)
}

/**
 * Reads available bytes and returns as string in lines.
 */
@JvmOverloads
fun InputStream.availableLines(charset: Charset = defaultCharset()): List<String> {
    return availableString(charset).lines()
}

/**
 * Reads all chars and returns.
 */
@JvmOverloads
fun Reader.readString(close: Boolean = false): String {
    return this.readText().let {
        if (close) {
            this.close()
        }
        it
    }
}

/**
 * Reads all chars and returns in lines.
 */
@JvmOverloads
fun Reader.readLines(close: Boolean = false): List<String> {
    return readString(close).lines()
}

/**
 * Reads bytes to [dest], returning the number of bytes read.
 */
@JvmOverloads
fun InputStream.readTo(dest: OutputStream, bufferSize: Int = defaultBufferSize()): Long {
    return this.copyTo(dest, bufferSize)
}

/**
 * Reads chars to [dest], returning the number of bytes read.
 */
@JvmOverloads
fun Reader.readTo(dest: Writer, bufferSize: Int = defaultBufferSize()): Long {
    return this.copyTo(dest, bufferSize)
}

/**
 * Returns a [Reader] of which content from [this] [InputStream].
 */
@JvmOverloads
fun InputStream.asReader(charset: Charset = defaultCharset()): Reader {
    return InputStreamReader(this, charset)
}

/**
 * Returns a [Writer] of which content from [this] [OutputStream].
 */
@JvmOverloads
fun OutputStream.asWriter(charset: Charset = defaultCharset()): Writer {
    return OutputStreamWriter(this, charset)
}

/**
 * Returns a [InputStream] of which content from [this] [Reader].
 */
@JvmOverloads
fun Reader.asInputStream(charset: Charset = defaultCharset()): InputStream {
    return ReaderInputStream(this, charset)
}

/**
 * Returns a [OutputStream] of which content from [this] [Writer].
 */
@JvmOverloads
fun Writer.asOutputStream(charset: Charset = defaultCharset()): OutputStream {
    return WriterOutputStream(this, charset)
}

/**
 * Returns a [BytesInputStream] of which content is [this] [ByteArray].
 */
@JvmOverloads
fun ByteArray.asInputStream(offset: Int = 0, length: Int = remainingLength(this.size, offset)): BytesInputStream {
    return BytesInputStream(this, offset, length)
}

/**
 * Returns a [BytesOutputStream] of which content is [this] [ByteArray].
 */
@JvmOverloads
fun ByteArray.asOutputStream(offset: Int = 0, length: Int = remainingLength(this.size, offset)): BytesOutputStream {
    return BytesOutputStream(this, offset, length)
}

/**
 * Returns a [CharsReader] of which content is [this] [CharArray].
 */
@JvmOverloads
fun CharArray.asReader(offset: Int = 0, length: Int = remainingLength(this.size, offset)): CharsReader {
    return CharsReader(this, offset, length)
}

/**
 * Returns a [CharsWriter] of which content is [this] [CharArray].
 */
@JvmOverloads
fun CharArray.asWriter(offset: Int = 0, length: Int = remainingLength(this.size, offset)): CharsWriter {
    return CharsWriter(this, offset, length)
}

/**
 * Returns a [CharSeqReader] of which content is [this] [T].
 */
@JvmOverloads
fun <T : CharSequence> T.asReader(
    offset: Int = 0,
    length: Int = remainingLength(this.length, offset)
): CharSeqReader<T> {
    return CharSeqReader(this, offset, length)
}

/**
 * Returns a [AppendableWriter] of which content is [this] [T].
 */
fun <T : Appendable> T.asWriter(): AppendableWriter<T> {
    return AppendableWriter(this)
}

/**
 * Returns a [RandomInputStream] of which content from [this] [RandomAccessFile].
 */
@JvmOverloads
fun RandomAccessFile.asInputStream(
    offset: Long = 0, length: Long = remainingLength(this.length(), offset)
): RandomInputStream {
    return RandomInputStream(this, offset, length)
}

/**
 * Returns a [RandomInputStream] of which content from [this] [RandomAccessFile].
 */
@JvmOverloads
fun RandomAccessFile.asOutputStream(
    offset: Long = 0, length: Long = remainingLength(this.length(), offset)
): RandomOutputStream {
    return RandomOutputStream(this, offset, length)
}

/**
 * Returns a [InputStream] of which content from [this] [URL].
 */
fun URL.asInputStream(): InputStream {
    return this.openStream()
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