@file:JvmName("BFile")

package xyz.srclab.common.io

import xyz.srclab.common.base.isEmpty
import xyz.srclab.common.collect.toTypedArray
import java.io.*
import java.net.URL
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.nio.file.*
import java.nio.file.attribute.FileAttribute

fun CharSequence.toPath(): Path {
    return Paths.get(this.toString())
}

fun CharSequence.openInputStream(): InputStream {
    return this.toPath().openInputStream()
}

fun File.openInputStream(): InputStream {
    return this.toPath().openInputStream()
}

fun Path.openInputStream(): InputStream {
    return Files.newInputStream(this)
}

fun CharSequence.openOutputStream(): OutputStream {
    return this.toPath().openOutputStream()
}

fun File.openOutputStream(): OutputStream {
    return this.toPath().openOutputStream()
}

fun Path.openOutputStream(): OutputStream {
    return Files.newOutputStream(this)
}

@JvmOverloads
fun CharSequence.openRandomAccessFile(mode: CharSequence = "r"): RandomAccessFile {
    return RandomAccessFile(this.toString(), mode.toString())
}

@JvmOverloads
fun File.openRandomAccessFile(mode: CharSequence = "r"): RandomAccessFile {
    return RandomAccessFile(this, mode.toString())
}

@JvmOverloads
fun Path.openRandomAccessFile(mode: CharSequence = "r"): RandomAccessFile {
    return this.toFile().openRandomAccessFile(mode)
}

@JvmOverloads
fun Path.readByteBuffer(
    vararg openOptions: OpenOption = arrayOf(StandardOpenOption.READ)
): MappedByteBuffer {
    val channel = FileChannel.open(this, *openOptions)
    return channel.map(FileChannel.MapMode.READ_ONLY, 0, Integer.MAX_VALUE.toLong())
}

@JvmOverloads
fun Path.mappedByteBuffer(
    vararg openOptions: OpenOption = arrayOf(
        StandardOpenOption.READ,
        StandardOpenOption.WRITE,
        StandardOpenOption.APPEND
    )
): MappedByteBuffer {
    val channel = FileChannel.open(this, *openOptions)
    return channel.map(FileChannel.MapMode.READ_ONLY, 0, Integer.MAX_VALUE.toLong())
}

fun Path.mappedByteBuffer(
    mode: FileChannel.MapMode,
    position: Long,
    size: Long,
    vararg openOptions: OpenOption
): MappedByteBuffer {
    val channel = FileChannel.open(this, *openOptions)
    return channel.map(mode, position, size)
}

fun Path.mappedByteBuffer(
    mode: FileChannel.MapMode,
    position: Long,
    size: Long,
    openOptions: Iterable<OpenOption>,
    fileAttributes: Iterable<FileAttribute<*>>
): MappedByteBuffer {
    val channel = FileChannel.open(this, openOptions.toSet(), *fileAttributes.toTypedArray())
    return channel.map(mode, position, size)
}

fun URL.toFile(): File {
    return this.file.let {
        if (it.isEmpty()) {
            throw FileNotFoundException(it)
        } else {
            File(it)
        }
    }
}

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

fun URL.toPath(): Path {
    return toFile().toPath()
}

fun URL.toPathOrNull(): Path? {
    return toFileOrNull()?.toPath()
}