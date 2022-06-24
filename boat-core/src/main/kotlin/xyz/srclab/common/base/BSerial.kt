@file:JvmName("BSerial")

package xyz.srclab.common.base

import java.io.*

/**
 * Writes [this] object into [dest].
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
 * Writes [this] object into [file].
 * @param close whether close the stream after writing
 */
@JvmOverloads
fun Any.writeObject(file: File, close: Boolean = false) {
    return this.writeObject(FileOutputStream(file), close)
}

/**
 * Writes [this] object into file [fileName].
 * @param close whether close the stream after writing
 */
@JvmOverloads
fun Any.writeObject(fileName: CharSequence, close: Boolean = false) {
    return this.writeObject(FileOutputStream(fileName.toString()), close)
}

/**
 * Reads object from [this] input stream.
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
 * Reads object from [this] file.
 * @param close whether close the stream after reading
 */
@JvmOverloads
fun <T> File.readObject(close: Boolean = false): T {
    return FileInputStream(this).readObject(close)
}

/**
 * Reads object from file [fileName].
 * @param close whether close the stream after reading
 */
@JvmOverloads
fun <T> readObject(fileName: CharSequence, close: Boolean = false): T {
    return FileInputStream(fileName.toString()).readObject(close)
}