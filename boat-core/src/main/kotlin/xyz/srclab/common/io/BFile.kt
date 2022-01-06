@file:JvmName("BFile")

package xyz.srclab.common.io

import java.io.File
import java.io.FileInputStream
import java.io.RandomAccessFile
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.nio.file.Files
import java.nio.file.OpenOption

fun CharSequence.openFileInputStream(): FileInputStream {
    return FileInputStream(this.toString())
}

fun File.openStream(): FileInputStream {
    return this.inputStream()
}

@JvmOverloads
fun CharSequence.openRandomAccessFile(mode: CharSequence = "r"): RandomAccessFile {
    return RandomAccessFile(this.toString(), mode.toString())
}

@JvmOverloads
fun File.openRandomAccess(mode: CharSequence = "r"): RandomAccessFile {
    return RandomAccessFile(this, mode.toString())
}

//fun File.openMappedByteBuffer(vararg openOptions: OpenOption):MappedByteBuffer {
//    val channel = FileChannel.open(this.toPath(),*openOptions)
//    return channel.map()
//}