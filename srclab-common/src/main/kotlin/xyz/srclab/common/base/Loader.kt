@file:JvmName("Loader")
@file:JvmMultifileClass

package xyz.srclab.common.base

import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.nio.ByteBuffer

@JvmOverloads
fun <T> ByteArray.loadClass(offset: Int = 0, length: Int = this.size - offset): Class<T> {
    return BytesClassLoader.loadClass(this, offset, length).asAny()
}

fun <T> InputStream.loadClass(): Class<T> {
    return BytesClassLoader.loadClass(this).asAny()
}

fun <T> ByteBuffer.loadClass(): Class<T> {
    return BytesClassLoader.loadClass(this).asAny()
}

@JvmOverloads
fun CharSequence.findResource(classLoader: ClassLoader = currentClassLoader()): URL? {
    return classLoader.getResource(this.toString())
}

@JvmOverloads
fun CharSequence.findResources(classLoader: ClassLoader = currentClassLoader()): List<URL> {
    return try {
        val urlEnumeration = classLoader.getResources(this.toString())
        urlEnumeration.toList()
    } catch (e: Exception) {
        throw IllegalStateException(e)
    }
}

object BytesClassLoader : ClassLoader() {

    @JvmOverloads
    fun loadClass(bytes: ByteArray, offset: Int = 0, length: Int = bytes.size): Class<*> {
        return super.defineClass(null, bytes, offset, length)
    }

    fun loadClass(inputStream: InputStream): Class<*> {
        return try {
            loadClass(inputStream.readBytes())
        } catch (e: IOException) {
            throw IllegalStateException(e)
        }
    }

    fun loadClass(byteBuffer: ByteBuffer): Class<*> {
        return super.defineClass(null, byteBuffer, null)
    }
}