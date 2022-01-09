@file:JvmName("BFile")

package xyz.srclab.common.io

import xyz.srclab.common.base.isEmpty
import xyz.srclab.common.collect.toTypedArray
import java.io.*
import java.net.URL
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.nio.file.Files
import java.nio.file.OpenOption
import java.nio.file.Path
import java.nio.file.Paths
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

fun File.fileInputStream(): FileInputStream {
    return FileInputStream(this)
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

fun File.fileOutputStream(): FileOutputStream {
    return FileOutputStream(this)
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

fun Path.readByteBuffer(): MappedByteBuffer {
    val file = this.toFile()
    val fis = file.fileInputStream()
    val channel = fis.channel
    return channel.map(FileChannel.MapMode.READ_ONLY, 0, file.length())
}

fun Path.mappedByteBuffer(): MappedByteBuffer {
    val randomAccess = this.toFile().openRandomAccessFile("rw")
    val channel = randomAccess.channel
    return channel.map(FileChannel.MapMode.READ_WRITE, 0, randomAccess.length())
}

fun Path.mappedByteBuffer(size: Long): MappedByteBuffer {
    val channel = this.toRandomAccessFileChannel("rw")
    return channel.map(FileChannel.MapMode.READ_WRITE, 0, size)
}

fun FileChannel.mappedByteBuffer(
    mode: FileChannel.MapMode,
    position: Long,
    size: Long
): MappedByteBuffer {
    return this.map(mode, position, size)
}

fun Path.toFileChannel(vararg openOptions: OpenOption): FileChannel {
    return FileChannel.open(this, *openOptions)
}

fun Path.toFileChannel(openOptions: Iterable<OpenOption>, fileAttributes: Iterable<FileAttribute<*>>): FileChannel {
    return FileChannel.open(this, openOptions.toSet(), *fileAttributes.toTypedArray())
}

fun Path.toRandomAccessFileChannel(mode: CharSequence): FileChannel {
    return this.toFile().openRandomAccessFile(mode.toString()).channel
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