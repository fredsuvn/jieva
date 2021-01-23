@file:JvmName("Loaders")
@file:JvmMultifileClass

package xyz.srclab.common.base

import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.nio.ByteBuffer
import java.nio.charset.Charset

@JvmOverloads
fun <T> CharSequence.loadClassOrNull(classLoader: ClassLoader = Current.classLoader): Class<T>? {
    return try {
        Class.forName(this.toString(), true, classLoader)
    } catch (e: ClassNotFoundException) {
        null
    }.asAny()
}

/**
 * @throws ClassNotFoundException
 */
@JvmOverloads
fun <T> CharSequence.loadClass(classLoader: ClassLoader = Current.classLoader): Class<T> {
    return try {
        Class.forName(this.toString(), true, classLoader)
    } catch (e: ClassNotFoundException) {
        throw e
    }.asAny()
}

@JvmOverloads
fun CharSequence.loadResourceOrNull(classLoader: ClassLoader = Current.classLoader): URL? {
    return classLoader.getResource(this.toString())
}

/**
 * @throws ResourceNotFoundException
 */
@JvmOverloads
fun CharSequence.loadResource(classLoader: ClassLoader = Current.classLoader): URL {
    return this.loadResourceOrNull(classLoader) ?: throw ResourceNotFoundException(this)
}

@JvmOverloads
fun CharSequence.loadBytesResourceOrNull(classLoader: ClassLoader = Current.classLoader): ByteArray? {
    return loadResourceOrNull(classLoader)?.openStream()?.readBytes()
}

/**
 * @throws ResourceNotFoundException
 */
@JvmOverloads
fun CharSequence.loadBytesResource(classLoader: ClassLoader = Current.classLoader): ByteArray {
    return this.loadBytesResourceOrNull(classLoader) ?: throw ResourceNotFoundException(this)
}

@JvmOverloads
fun CharSequence.loadStringResourceOrNull(
    classLoader: ClassLoader = Current.classLoader, charset: Charset = Default.charset
): String? {
    return loadBytesResourceOrNull(classLoader)?.toChars(charset)
}

/**
 * @throws ResourceNotFoundException
 */
@JvmOverloads
fun CharSequence.loadStringResource(
    classLoader: ClassLoader = Current.classLoader, charset: Charset = Default.charset
): String {
    return this.loadStringResourceOrNull(classLoader, charset) ?: throw ResourceNotFoundException(this)
}

@JvmOverloads
fun CharSequence.loadResources(classLoader: ClassLoader = Current.classLoader): List<URL> {
    return try {
        val urlEnumeration = classLoader.getResources(this.toString())
        urlEnumeration.toList()
    } catch (e: Exception) {
        throw IllegalStateException(e)
    }
}

@JvmOverloads
fun CharSequence.loadBytesResources(classLoader: ClassLoader = Current.classLoader): List<ByteArray> {
    return loadResources(classLoader).map { url -> url.readBytes() }
}

@JvmOverloads
fun CharSequence.loadStringResources(
    classLoader: ClassLoader = Current.classLoader, charset: Charset = Default.charset
): List<String> {
    return loadBytesResources(classLoader).map { bytes -> bytes.toChars(charset) }
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

open class ResourceNotFoundException : RuntimeException {
    constructor() : super()
    constructor(message: CharSequence?) : super(message?.toString())
    constructor(message: CharSequence?, cause: Throwable?) : super(message?.toString(), cause)
    constructor(cause: Throwable?) : super(cause)
}