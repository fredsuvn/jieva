package xyz.srclab.common.reflect

import xyz.srclab.common.base.remainingLength
import java.io.InputStream
import java.nio.ByteBuffer

/**
 * [ClassLoader] used to help load class from bytes.
 */
object BytesClassLoader : ClassLoader() {

    @JvmOverloads
    fun loadClass(bytes: ByteArray, offset: Int = 0, length: Int = remainingLength(bytes.size, offset)): Class<*> {
        return super.defineClass(null, bytes, offset, length)
    }

    @JvmOverloads
    fun loadClass(
        name: String, bytes: ByteArray, offset: Int = 0, length: Int = remainingLength(bytes.size, offset)
    ): Class<*> {
        return super.defineClass(name, bytes, offset, length)
    }

    fun loadClass(inputStream: InputStream): Class<*> {
        return loadClass(inputStream.readBytes())
    }

    fun loadClass(name: String, inputStream: InputStream): Class<*> {
        return loadClass(name, inputStream.readBytes())
    }

    fun loadClass(byteBuffer: ByteBuffer): Class<*> {
        return super.defineClass(null, byteBuffer, null)
    }

    fun loadClass(name: String, byteBuffer: ByteBuffer): Class<*> {
        return super.defineClass(name, byteBuffer, null)
    }
}