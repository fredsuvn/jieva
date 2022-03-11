package xyz.srclab.common.reflect

import xyz.srclab.common.base.remainingLength
import java.io.InputStream
import java.nio.ByteBuffer

/**
 * [ClassLoader] used to load class from bytes.
 */
object BytesClassLoader : ClassLoader() {

    /**
     * Loads class from bytes.
     */
    @JvmOverloads
    fun loadClass(bytes: ByteArray, offset: Int = 0, length: Int = remainingLength(bytes.size, offset)): Class<*> {
        return super.defineClass(null, bytes, offset, length)
    }

    /**
     * Loads class from bytes.
     */
    @JvmOverloads
    fun loadClass(
        name: String, bytes: ByteArray, offset: Int = 0, length: Int = remainingLength(bytes.size, offset)
    ): Class<*> {
        return super.defineClass(name, bytes, offset, length)
    }

    /**
     * Loads class from input stream.
     */
    fun loadClass(inputStream: InputStream): Class<*> {
        return loadClass(inputStream.readBytes())
    }

    /**
     * Loads class from input stream.
     */
    fun loadClass(name: String, inputStream: InputStream): Class<*> {
        return loadClass(name, inputStream.readBytes())
    }

    /**
     * Loads class from bytes buffer.
     */
    fun loadClass(byteBuffer: ByteBuffer): Class<*> {
        return super.defineClass(null, byteBuffer, null)
    }

    /**
     * Loads class from bytes buffer.
     */
    fun loadClass(name: String, byteBuffer: ByteBuffer): Class<*> {
        return super.defineClass(name, byteBuffer, null)
    }
}