@file:JvmName("Loader")
@file:JvmMultifileClass

package xyz.srclab.common.base

import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.nio.ByteBuffer

fun <T> findClass(className: String): Class<T>? {
    return try {
        Class.forName(className).asAny()
    } catch (e: ClassNotFoundException) {
        null
    }
}

fun <T> findClass(className: String, classLoader: ClassLoader): Class<T>? {
    return try {
        Class.forName(className, true, classLoader).asAny()
    } catch (e: ClassNotFoundException) {
        null
    }
}

@JvmOverloads
fun <T> loadClass(bytes: ByteArray, offset: Int = 0, length: Int = bytes.size - offset): Class<T> {
    return BytesClassLoader.loadClass(bytes, offset, length).asAny()
}

fun <T> loadClass(inputStream: InputStream): Class<T> {
    return BytesClassLoader.loadClass(inputStream).asAny()
}

fun <T> loadClass(byteBuffer: ByteBuffer): Class<T> {
    return BytesClassLoader.loadClass(byteBuffer).asAny()
}

fun findResource(resourceName: String): URL? {
    return findResource(resourceName, Current.classLoader)
}

fun findResource(resourceName: String, classLoader: ClassLoader): URL? {
    return classLoader.getResource(resourceName)
}

fun findResources(resourceName: String): List<URL> {
    return findResources(resourceName, Current.classLoader)
}

fun findResources(resourceName: String?, classLoader: ClassLoader): List<URL> {
    return try {
        val urlEnumeration = classLoader.getResources(resourceName)
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