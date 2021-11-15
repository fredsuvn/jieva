@file:JvmName("BLoaders")

package xyz.srclab.common.base

import xyz.srclab.common.collect.map
import xyz.srclab.common.reflect.toClass
import xyz.srclab.common.reflect.toClassOrNull
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
fun CharSequence.loadResourceAsString(
    classLoader: ClassLoader = currentClassLoader(), charset: Charset = DEFAULT_CHARSET
): String {
    return loadResourceAsStringOrNull(classLoader, charset) ?: throw ResourceNotFoundException(this)
}

@JvmOverloads
fun CharSequence.loadResourceAsStringOrNull(
    classLoader: ClassLoader = currentClassLoader(), charset: Charset = DEFAULT_CHARSET
): String? {
    return loadResourceOrNull(classLoader)?.openStream()?.reader(charset)?.readText()
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
    return loadResourceOrNull(classLoader)?.openStream()?.loadProperties(charset)
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
fun CharSequence.loadAllResourcesAsStrings(
    classLoader: ClassLoader = currentClassLoader(), charset: Charset = DEFAULT_CHARSET
): List<String> {
    return loadAllResources(classLoader).map { url -> url.openStream().reader(charset).readText() }
}

/**
 * @throws IOException
 */
@JvmOverloads
fun CharSequence.loadAllResourcesAsProperties(
    classLoader: ClassLoader = currentClassLoader(), charset: Charset = DEFAULT_CHARSET
): List<Map<String, String>> {
    return loadAllResources(classLoader).map { url -> url.openStream().loadProperties(charset = charset) }
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
    return BClassLoader.loadClass(this, offset, length).asTyped()
}

fun <T> InputStream.loadClass(): Class<T> {
    return BClassLoader.loadClass(this).asTyped()
}

fun <T> ByteBuffer.loadClass(): Class<T> {
    return BClassLoader.loadClass(this).asTyped()
}

/**
 * Base class loader.
 */
object BClassLoader : ClassLoader() {

    @JvmOverloads
    fun loadClass(bytes: ByteArray, offset: Int = 0, length: Int = bytes.size - offset): Class<*> {
        return super.defineClass(null, bytes, offset, length)
    }

    fun loadClass(inputStream: InputStream): Class<*> {
        return loadClass(inputStream.readBytes())
    }

    fun loadClass(byteBuffer: ByteBuffer): Class<*> {
        return super.defineClass(null, byteBuffer, null)
    }
}

open class ResourceNotFoundException(message: CharSequence?) : RuntimeException(message?.toString())