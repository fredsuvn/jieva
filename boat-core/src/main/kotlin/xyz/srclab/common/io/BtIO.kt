/**
 * IO utilities.
 */
@file:JvmName("BtIO")

package xyz.srclab.common.io

import org.apache.commons.io.input.ReaderInputStream
import org.apache.commons.io.output.WriterOutputStream
import xyz.srclab.common.asType
import xyz.srclab.common.base.*
import java.io.*
import java.net.URI
import java.net.URL
import java.nio.ByteBuffer
import java.nio.MappedByteBuffer
import java.nio.charset.Charset
import kotlin.text.lines
import kotlin.io.readBytes as readBytesKt

/**
 * Reads all bytes and returns.
 * @param close whether close this stream after reading
 */
@JvmOverloads
fun InputStream.readBytes(close: Boolean = true): ByteArray {
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
fun InputStream.readString(charset: Charset = defaultCharset(), close: Boolean = true): String {
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
fun InputStream.readLines(charset: Charset = defaultCharset(), close: Boolean = true): List<String> {
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
fun Reader.readString(close: Boolean = true): String {
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
fun Reader.readLines(close: Boolean = true): List<String> {
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
fun ByteArray.asInputStream(offset: Int = 0, length: Int = remLength(this.size, offset)): BytesInputStream {
    return BytesInputStream(this, offset, length)
}

/**
 * Returns a [BytesOutputStream] of which content is [this] [ByteArray].
 */
@JvmOverloads
fun ByteArray.asOutputStream(offset: Int = 0, length: Int = remLength(this.size, offset)): BytesOutputStream {
    return BytesOutputStream(this, offset, length)
}

/**
 * Returns a [CharsReader] of which content is [this] [CharArray].
 */
@JvmOverloads
fun CharArray.asReader(offset: Int = 0, length: Int = remLength(this.size, offset)): CharsReader {
    return CharsReader(this, offset, length)
}

/**
 * Returns a [CharsWriter] of which content is [this] [CharArray].
 */
@JvmOverloads
fun CharArray.asWriter(offset: Int = 0, length: Int = remLength(this.size, offset)): CharsWriter {
    return CharsWriter(this, offset, length)
}

/**
 * Returns a [CharSeqReader] of which content is [this] [T].
 */
@JvmOverloads
fun <T : CharSequence> T.asReader(
    offset: Int = 0,
    length: Int = remLength(this.length, offset)
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
    offset: Long = 0, length: Long = remLength(this.length(), offset)
): RandomInputStream {
    return RandomInputStream(this, offset, length)
}

/**
 * Returns a [RandomInputStream] of which content from [this] [RandomAccessFile].
 */
@JvmOverloads
fun RandomAccessFile.asOutputStream(
    offset: Long = 0, length: Long = remLength(this.length(), offset)
): RandomOutputStream {
    return RandomOutputStream(this, offset, length)
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
 * Returns a [InputStream] of which content from [this] [URL].
 */
fun URL.asInputStream(): InputStream {
    return this.openStream()
}

/**
 * Returns a [InputStream] of which content from [this] [URI].
 */
fun URI.asInputStream(): InputStream {
    return this.toURL().openStream()
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
 * @param useWrappedArray try return wrapped byte array directly if this buffer has a wrapped byte array
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
        val result = array.getString(charset, arrayOffset, length)
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
        length: Int = remLength(this.size, offset),
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

// Serialization:

/**
 * Writes [this] serializable object into [dest].
 *
 * @param close whether close the stream after writing
 */
@JvmOverloads
fun Any.writeObject(dest: OutputStream, close: Boolean = false) {
    val oop = ObjectOutputStream(dest)
    oop.writeObject(this)
    if (close) {
        oop.close()
    }
}

/**
 * Writes [this] serializable object into [file].
 *
 * @param close whether close the stream after writing
 */
@JvmOverloads
fun Any.writeObject(file: File, close: Boolean = false) {
    return this.writeObject(FileOutputStream(file), close)
}

/**
 * Writes [this] serializable object into file [fileName].
 *
 * @param close whether close the stream after writing
 */
@JvmOverloads
fun Any.writeObject(fileName: CharSequence, close: Boolean = false) {
    return this.writeObject(FileOutputStream(fileName.toString()), close)
}

/**
 * Reads serializable object from [this] input stream.
 *
 * @param close whether close the stream after reading
 */
@JvmOverloads
fun <T> InputStream.readObject(close: Boolean = false): T {
    val ooi = ObjectInputStream(this)
    return ooi.readObject().asType<T>().let {
        if (close) {
            this.close()
        }
        it
    }
}

/**
 * Reads serializable object from [this] file.
 *
 * @param close whether close the stream after reading
 */
@JvmOverloads
fun <T> File.readObject(close: Boolean = false): T {
    return FileInputStream(this).readObject(close)
}

/**
 * Reads serializable object from file [fileName].
 *
 * @param close whether close the stream after reading
 */
@JvmOverloads
fun <T> readObject(fileName: CharSequence, close: Boolean = false): T {
    return FileInputStream(fileName.toString()).readObject(close)
}