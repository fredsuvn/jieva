@file:JvmName("Loader")
@file:JvmMultifileClass

package xyz.srclab.common.base

import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.nio.ByteBuffer

@JvmOverloads
fun <T> String.findClass(classLoader: ClassLoader = Current.classLoader): Class<T>? {
    return try {
        Class.forName(this, true, classLoader).asAny()
    } catch (e: ClassNotFoundException) {
        null
    }
}

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
fun String.findResource(classLoader: ClassLoader = Current.classLoader): URL? {
    return classLoader.getResource(this)
}

@JvmOverloads
fun String.findResources(classLoader: ClassLoader = Current.classLoader): List<URL> {
    return try {
        val urlEnumeration = classLoader.getResources(this)
        urlEnumeration.toList()
    } catch (e: Exception) {
        throw IllegalStateException(e)
    }
}

object BytesClassLoader : ClassLoader() {

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