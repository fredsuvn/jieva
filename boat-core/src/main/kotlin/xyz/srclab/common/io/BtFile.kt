@file:JvmName("BtFile")

package xyz.srclab.common.io

import xyz.srclab.common.base.defaultBufferSize
import xyz.srclab.common.base.defaultCharset
import xyz.srclab.common.base.isEmpty
import java.io.*
import java.net.URL
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path

/**
 * Opens [InputStream] for [this] file.
 *
 * @param bufferSize may be 0 if the buffer is not required
 */
@JvmOverloads
fun File.openInputStream(bufferSize: Int = defaultBufferSize()): InputStream {
    return this.toPath().openInputStream(bufferSize)
}

/**
 * Opens [InputStream] for [this] path.
 *
 * @param bufferSize may be 0 if the buffer is not required
 */
@JvmOverloads
fun Path.openInputStream(bufferSize: Int = defaultBufferSize()): InputStream {
    if (bufferSize < 0) {
        throw IllegalArgumentException("Illegal buffer size: $bufferSize")
    }
    val input = Files.newInputStream(this)
    return if (bufferSize == 0) input else input.buffered(bufferSize)
}

/**
 * Opens [Reader] for [this] file.
 *
 * @param bufferSize may be 0 if the buffer is not required
 */
@JvmOverloads
fun File.openReader(charset: Charset = defaultCharset(), bufferSize: Int = defaultBufferSize()): Reader {
    return this.toPath().openReader(charset, bufferSize)
}

/**
 * Opens [Reader] for [this] path.
 *
 * @param bufferSize may be 0 if the buffer is not required
 */
@JvmOverloads
fun Path.openReader(charset: Charset = defaultCharset(), bufferSize: Int = defaultBufferSize()): Reader {
    return this.openInputStream(bufferSize).asReader(charset)
}

/**
 * Opens [OutputStream] for [this] file.
 *
 * @param bufferSize may be 0 if the buffer is not required
 */
@JvmOverloads
fun File.openOutputStream(bufferSize: Int = defaultBufferSize()): OutputStream {
    return this.toPath().openOutputStream(bufferSize)
}

/**
 * Opens [OutputStream] for [this] path.
 *
 * @param bufferSize may be 0 if the buffer is not required
 */
@JvmOverloads
fun Path.openOutputStream(bufferSize: Int = defaultBufferSize()): OutputStream {
    if (bufferSize < 0) {
        throw IllegalArgumentException("Illegal buffer size: $bufferSize")
    }
    if (bufferSize == 0) {
        return Files.newOutputStream(this)
    }
    return Files.newOutputStream(this).buffered(bufferSize)
}

/**
 * Opens [Writer] for [this] file.
 *
 * @param bufferSize may be 0 if the buffer is not required
 */
@JvmOverloads
fun File.openWriter(charset: Charset = defaultCharset(), bufferSize: Int = defaultBufferSize()): Writer {
    return this.toPath().openWriter(charset, bufferSize)
}

/**
 * Opens [Writer] for [this] path.
 *
 * @param bufferSize may be 0 if the buffer is not required
 */
@JvmOverloads
fun Path.openWriter(charset: Charset = defaultCharset(), bufferSize: Int = defaultBufferSize()): Writer {
    return this.openOutputStream(bufferSize).asWriter(charset)
}

/**
 * Opens [RandomAccessFile] for [this] file.
 *
 * @param mode open mode: `r`, `rw`, `rws`, `rwd`
 */
@JvmOverloads
fun File.openRandomAccessFile(mode: CharSequence = "rw"): RandomAccessFile {
    return RandomAccessFile(this, mode.toString())
}

/**
 * Opens [RandomAccessFile] for [this] path.
 *
 * @param mode open mode: `r`, `rw`, `rws`, `rwd`
 */
@JvmOverloads
fun Path.openRandomAccessFile(mode: CharSequence = "rw"): RandomAccessFile {
    return this.toFile().openRandomAccessFile(mode)
}

/**
 * Returns a [File] specified by [this] url.
 *
 * @throws FileNotFoundException if file not found for this url
 */
fun URL.toFile(): File {
    return this.file.let {
        if (it.isEmpty()) {
            throw FileNotFoundException(it)
        } else {
            File(it)
        }
    }
}

/**
 * Returns a [File] specified by [this] url, or null if failed.
 */
fun URL.toFileOrNull(): File? {
    return try {
        this.file.let {
            if (it.isEmpty()) {
                null
            } else {
                File(it)
            }
        }
    } catch (e: IOException) {
        null
    }
}

/**
 * Returns a [Path] specified by [this] url.
 */
fun URL.toPath(): Path {
    return toFile().toPath()
}

/**
 * Returns a [Path] specified by [this] url, or null if failed.
 */
fun URL.toPathOrNull(): Path? {
    return toFileOrNull()?.toPath()
}