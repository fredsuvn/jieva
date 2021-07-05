@file:JvmName("Loaders")

package xyz.srclab.common.lang

import xyz.srclab.common.collect.map
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.io.Reader
import java.net.URL
import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.util.*

/**
 * @throws ClassNotFoundException
 */
@JvmOverloads
fun <T> CharSequence.loadClass(classLoader: ClassLoader = Current.classLoader): Class<T> {
    return Class.forName(this.toString(), true, classLoader).asAny()
}

@JvmOverloads
fun <T> CharSequence.loadClassOrNull(classLoader: ClassLoader = Current.classLoader): Class<T>? {
    return try {
        Class.forName(this.toString(), true, classLoader)
    } catch (e: ClassNotFoundException) {
        null
    }.asAny()
}

@Throws(ResourceNotFoundException::class)
@JvmOverloads
fun CharSequence.loadResource(classLoader: ClassLoader = Current.classLoader): URL {
    return loadResourceOrNull(classLoader) ?: throw ResourceNotFoundException(this)
}

@JvmOverloads
fun CharSequence.loadResourceOrNull(classLoader: ClassLoader = Current.classLoader): URL? {
    return classLoader.getResource(this.toString())
}

@JvmName("loadBytes")
@Throws(ResourceNotFoundException::class)
@JvmOverloads
fun CharSequence.loadBytesResource(classLoader: ClassLoader = Current.classLoader): ByteArray {
    return loadBytesResourceOrNull(classLoader) ?: throw ResourceNotFoundException(this)
}

@JvmName("loadBytesOrNull")
@JvmOverloads
fun CharSequence.loadBytesResourceOrNull(classLoader: ClassLoader = Current.classLoader): ByteArray? {
    return loadResourceOrNull(classLoader)?.readBytes()
}

@JvmName("loadStream")
@Throws(ResourceNotFoundException::class)
@JvmOverloads
fun CharSequence.loadStreamResource(classLoader: ClassLoader = Current.classLoader): InputStream {
    return loadStreamResourceOrNull(classLoader) ?: throw ResourceNotFoundException(this)
}

@JvmName("loadStreamOrNull")
@JvmOverloads
fun CharSequence.loadStreamResourceOrNull(classLoader: ClassLoader = Current.classLoader): InputStream? {
    return loadResourceOrNull(classLoader)?.openStream()
}

@JvmName("loadString")
@Throws(ResourceNotFoundException::class)
@JvmOverloads
fun CharSequence.loadStringResource(
    classLoader: ClassLoader = Current.classLoader, charset: Charset = Defaults.charset
): String {
    return loadStringResourceOrNull(classLoader, charset) ?: throw ResourceNotFoundException(this)
}

@JvmName("loadStringOrNull")
@JvmOverloads
fun CharSequence.loadStringResourceOrNull(
    classLoader: ClassLoader = Current.classLoader, charset: Charset = Defaults.charset
): String? {
    return loadBytesResourceOrNull(classLoader)?.toChars(charset)
}

@JvmName("loadProperties")
@Throws(ResourceNotFoundException::class)
@JvmOverloads
fun CharSequence.loadPropertiesResource(
    classLoader: ClassLoader = Current.classLoader, charset: Charset = Defaults.charset
): Map<String, String> {
    return loadPropertiesResourceOrNull(classLoader, charset) ?: throw ResourceNotFoundException(this)
}

@JvmName("loadPropertiesOrNull")
@JvmOverloads
fun CharSequence.loadPropertiesResourceOrNull(
    classLoader: ClassLoader = Current.classLoader, charset: Charset = Defaults.charset
): Map<String, String>? {
    return loadResourceOrNull(classLoader)?.openStream()?.loadProperties(charset)
}

/**
 * @throws IOException
 */
@JvmOverloads
fun CharSequence.loadAllResources(classLoader: ClassLoader = Current.classLoader): List<URL> {
    val urlEnumeration = classLoader.getResources(this.toString())
    return urlEnumeration.toList()
}

/**
 * @throws IOException
 */
@JvmName("loadAllBytes")
@JvmOverloads
fun CharSequence.loadAllBytesResources(classLoader: ClassLoader = Current.classLoader): List<ByteArray> {
    return loadAllResources(classLoader).map { url -> url.readBytes() }
}

/**
 * @throws IOException
 */
@JvmName("loadAllStreams")
@JvmOverloads
fun CharSequence.loadAllStreamResources(classLoader: ClassLoader = Current.classLoader): List<InputStream> {
    return loadAllResources(classLoader).map { url -> url.openStream() }
}

/**
 * @throws IOException
 */
@JvmName("loadAllStrings")
@JvmOverloads
fun CharSequence.loadAllStringResources(
    classLoader: ClassLoader = Current.classLoader, charset: Charset = Defaults.charset
): List<String> {
    return loadAllBytesResources(classLoader).map { bytes -> bytes.toChars(charset) }
}

/**
 * @throws IOException
 */
@JvmName("loadAllProperties")
@JvmOverloads
fun CharSequence.loadAllPropertiesResources(
    classLoader: ClassLoader = Current.classLoader, charset: Charset = Defaults.charset
): List<Map<String, String>> {
    return loadAllStreamResources(classLoader).map { stream -> stream.loadProperties(charset) }
}

@JvmOverloads
fun <T> ByteArray.loadClass(offset: Int = 0, length: Int = this.size - offset): Class<T> {
    return BytesClassLoader.loadClass(this, offset, length).asAny()
}

fun <T> InputStream.loadClass(): Class<T> {
    return BytesClassLoader.loadClass(this).asAny()
}

@JvmOverloads
fun ByteArray.loadProperties(
    offset: Int = 0,
    length: Int = this.size - offset,
    charset: Charset = Defaults.charset
): Map<String, String> {
    return ByteArrayInputStream(this, offset, length).loadProperties(charset)
}

fun InputStream.loadProperties(charset: Charset = Defaults.charset): Map<String, String> {
    if (charset == Defaults.charset) {
        val properties = Properties()
        properties.load(this)
        return properties.map { k, v -> k.toString() to v.toString() }
    }
    return this.bufferedReader(charset).loadProperties()
}

fun Reader.loadProperties(): Map<String, String> {
    val properties = Properties()
    properties.load(this)
    return properties.map { k, v -> k.toString() to v.toString() }
}

object BytesClassLoader : ClassLoader() {

    @JvmOverloads
    fun loadClass(bytes: ByteArray, offset: Int = 0, length: Int = bytes.size): Class<*> {
        return super.defineClass(null, bytes, offset, length)
    }

    fun loadClass(inputStream: InputStream): Class<*> {
        return loadClass(inputStream.readBytes())
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