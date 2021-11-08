@file:JvmName("Loaders")

package xyz.srclab.common.base

import xyz.srclab.common.collect.map
import xyz.srclab.common.reflect.toClass
import xyz.srclab.common.reflect.toClassOrNull
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
fun <T> CharSequence.loadClass(classLoader: ClassLoader = currentClassLoader()): Class<T> {
    return this.toClass(classLoader)
}

@JvmOverloads
fun <T> CharSequence.loadClassOrNull(classLoader: ClassLoader = currentClassLoader()): Class<T>? {
    return this.toClassOrNull(classLoader)
}

@Throws(ResourceNotFoundException::class)
@JvmOverloads
fun CharSequence.loadResource(classLoader: ClassLoader = currentClassLoader()): URL {
    return loadResourceOrNull(classLoader) ?: throw ResourceNotFoundException(this)
}

@JvmOverloads
fun CharSequence.loadResourceOrNull(classLoader: ClassLoader = currentClassLoader()): URL? {
    return classLoader.getResource(this.toString())
}

@Throws(ResourceNotFoundException::class)
@JvmOverloads
fun CharSequence.loadResourceAsBytes(classLoader: ClassLoader = currentClassLoader()): ByteArray {
    return loadResourceAsBytesOrNull(classLoader) ?: throw ResourceNotFoundException(this)
}

@JvmOverloads
fun CharSequence.loadResourceAsBytesOrNull(classLoader: ClassLoader = currentClassLoader()): ByteArray? {
    return loadResourceOrNull(classLoader)?.readBytes()
}

@Throws(ResourceNotFoundException::class)
@JvmOverloads
fun CharSequence.loadResourceAsStream(classLoader: ClassLoader = currentClassLoader()): InputStream {
    return loadResourceAsStreamOrNull(classLoader) ?: throw ResourceNotFoundException(this)
}

@JvmOverloads
fun CharSequence.loadResourceAsStreamOrNull(classLoader: ClassLoader = currentClassLoader()): InputStream? {
    return loadResourceOrNull(classLoader)?.openStream()
}

@Throws(ResourceNotFoundException::class)
@JvmOverloads
fun CharSequence.loadResourceAsReader(
    classLoader: ClassLoader = currentClassLoader(),
    charset: Charset = DEFAULT_CHARSET
): Reader {
    return loadResourceAsReaderOrNull(classLoader, charset) ?: throw ResourceNotFoundException(this)
}

@JvmOverloads
fun CharSequence.loadResourceAsReaderOrNull(
    classLoader: ClassLoader = currentClassLoader(),
    charset: Charset = DEFAULT_CHARSET
): Reader? {
    return loadResourceOrNull(classLoader)?.openStream()?.reader(charset)
}

@Throws(ResourceNotFoundException::class)
@JvmOverloads
fun CharSequence.loadResourceAsString(
    classLoader: ClassLoader = currentClassLoader(), charset: Charset = DEFAULT_CHARSET
): String {
    return loadResourceAsStringOrNull(classLoader, charset) ?: throw ResourceNotFoundException(this)
}

@JvmOverloads
fun CharSequence.loadResourceAsStringOrNull(
    classLoader: ClassLoader = currentClassLoader(), charset: Charset = DEFAULT_CHARSET
): String? {
    return loadResourceAsBytesOrNull(classLoader)?.toChars(charset)
}

@Throws(ResourceNotFoundException::class)
@JvmOverloads
fun CharSequence.loadResourceAsProperties(
    classLoader: ClassLoader = currentClassLoader(), charset: Charset = DEFAULT_CHARSET
): Map<String, String> {
    return loadResourceAsPropertiesOrNull(classLoader, charset) ?: throw ResourceNotFoundException(this)
}

@JvmOverloads
fun CharSequence.loadResourceAsPropertiesOrNull(
    classLoader: ClassLoader = currentClassLoader(), charset: Charset = DEFAULT_CHARSET
): Map<String, String>? {
    return loadResourceAsStreamOrNull(classLoader)?.loadProperties(charset)
}

/**
 * @throws IOException
 */
@JvmOverloads
fun CharSequence.loadAllResources(classLoader: ClassLoader = currentClassLoader()): List<URL> {
    return classLoader.getResources(this.toString()).toList()
}

/**
 * @throws IOException
 */
@JvmOverloads
fun CharSequence.loadAllResourcesAsBytes(classLoader: ClassLoader = currentClassLoader()): List<ByteArray> {
    return loadAllResources(classLoader).map { url -> url.readBytes() }
}

/**
 * @throws IOException
 */
@JvmOverloads
fun CharSequence.loadAllResourcesAsStreams(classLoader: ClassLoader = currentClassLoader()): List<InputStream> {
    return loadAllResources(classLoader).map { url -> url.openStream() }
}

/**
 * @throws IOException
 */
@JvmOverloads
fun CharSequence.loadAllResourcesAsReaders(
    classLoader: ClassLoader = currentClassLoader(),
    charset: Charset = DEFAULT_CHARSET
): List<Reader> {
    return loadAllResources(classLoader).map { url -> url.openStream().reader(charset) }
}

/**
 * @throws IOException
 */
@JvmOverloads
fun CharSequence.loadAllResourcesAsStrings(
    classLoader: ClassLoader = currentClassLoader(), charset: Charset = DEFAULT_CHARSET
): List<String> {
    return loadAllResourcesAsBytes(classLoader).map { bytes -> bytes.toChars(charset) }
}

/**
 * @throws IOException
 */
@JvmOverloads
fun CharSequence.loadAllResourcesAsProperties(
    classLoader: ClassLoader = currentClassLoader(), charset: Charset = DEFAULT_CHARSET
): List<Map<String, String>> {
    return loadAllResourcesAsBytes(classLoader).map { stream -> stream.loadProperties(charset = charset) }
}

@JvmOverloads
fun ByteArray.loadProperties(
    offset: Int = 0,
    length: Int = this.size - offset,
    charset: Charset = DEFAULT_CHARSET
): Map<String, String> {
    return ByteArrayInputStream(this, offset, length).loadProperties(charset)
}

@JvmOverloads
fun InputStream.loadProperties(charset: Charset = DEFAULT_CHARSET): Map<String, String> {
    return this.reader(charset).loadProperties()
}

fun Reader.loadProperties(): Map<String, String> {
    val properties = Properties()
    properties.load(this)
    val result = LinkedHashMap<String, String>()
    properties.forEach {
        result[it.key.toString()] = it.value.toString()
    }
    return result
}

@JvmOverloads
fun <T> ByteArray.loadClass(offset: Int = 0, length: Int = this.size - offset): Class<T> {
    return BoatClassLoader.loadClass(this, offset, length).asTyped()
}

fun <T> InputStream.loadClass(): Class<T> {
    return BoatClassLoader.loadClass(this).asTyped()
}

fun <T> ByteBuffer.loadClass(): Class<T> {
    return BoatClassLoader.loadClass(this).asTyped()
}

open class ResourceNotFoundException : RuntimeException {
    constructor() : super()
    constructor(message: CharSequence?) : super(message?.toString())
    constructor(message: CharSequence?, cause: Throwable?) : super(message?.toString(), cause)
    constructor(cause: Throwable?) : super(cause)
}