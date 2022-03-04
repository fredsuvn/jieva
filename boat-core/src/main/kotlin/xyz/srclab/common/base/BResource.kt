@file:JvmName("BResource")

package xyz.srclab.common.base

import xyz.srclab.common.io.readString
import xyz.srclab.common.io.toFile
import xyz.srclab.common.io.toFileOrNull
import xyz.srclab.common.reflect.defaultClassLoader
import java.io.File
import java.io.InputStream
import java.io.Serializable
import java.net.URL
import java.nio.charset.Charset
import java.util.*

@JvmName("load")
@JvmOverloads
@Throws(NoSuchResourceException::class)
fun CharSequence.loadResource(classLoader: ClassLoader = defaultClassLoader()): URL {
    return loadResourceOrNull(classLoader) ?: throw NoSuchResourceException("$this")
}

@JvmName("loadOrNull")
@JvmOverloads
fun CharSequence.loadResourceOrNull(classLoader: ClassLoader = defaultClassLoader()): URL? {
    return classLoader.getResource(this.removeAbsolute())
}

@JvmName("loadStream")
@JvmOverloads
@Throws(NoSuchResourceException::class)
fun CharSequence.loadResourceStream(classLoader: ClassLoader = defaultClassLoader()): InputStream {
    return loadResource(classLoader).openStream()
}

@JvmName("loadStreamOrNull")
@JvmOverloads
fun CharSequence.loadResourceStreamOrNull(classLoader: ClassLoader = defaultClassLoader()): InputStream? {
    return loadResourceOrNull(classLoader)?.openStream()
}

@JvmName("loadFile")
@JvmOverloads
@Throws(NoSuchResourceException::class)
fun CharSequence.loadResourceFile(classLoader: ClassLoader = defaultClassLoader()): File {
    return loadResource(classLoader).toFile()
}

@JvmName("loadFileOrNull")
@JvmOverloads
fun CharSequence.loadResourceFileOrNull(classLoader: ClassLoader = defaultClassLoader()): File? {
    return loadResourceOrNull(classLoader)?.toFileOrNull()
}

@JvmName("loadString")
@JvmOverloads
@Throws(NoSuchResourceException::class)
fun CharSequence.loadResourceString(
    charset: Charset = defaultCharset(), classLoader: ClassLoader = defaultClassLoader()
): String {
    return loadResourceStream(classLoader).readString(charset, true)
}

@JvmName("loadStringOrNull")
@JvmOverloads
fun CharSequence.loadResourceStringOrNull(
    charset: Charset = defaultCharset(), classLoader: ClassLoader = defaultClassLoader()
): String? {
    return loadResourceStreamOrNull(classLoader)?.readString(charset, true)
}

@JvmName("loadProperties")
@JvmOverloads
fun CharSequence.loadResourceProperties(
    charset: Charset = defaultCharset(), classLoader: ClassLoader = defaultClassLoader()
): Map<String, String> {
    return loadResource(classLoader).readProperties(charset)
}

@JvmName("loadPropertiesOrNull")
@JvmOverloads
fun CharSequence.loadResourcePropertiesOrNull(
    charset: Charset = defaultCharset(), classLoader: ClassLoader = defaultClassLoader()
): Map<String, String>? {
    return loadResourceOrNull(classLoader)?.readProperties(charset)
}

@JvmName("loadAll")
@JvmOverloads
fun CharSequence.loadResources(classLoader: ClassLoader = defaultClassLoader()): List<URL> {
    val enumeration = loadAll0(classLoader)
    val result = mutableListOf<URL>()
    while (enumeration.hasMoreElements()) {
        result.add(enumeration.nextElement())
    }
    return result
}

@JvmName("loadStreams")
@JvmOverloads
fun CharSequence.loadResourceStreams(classLoader: ClassLoader = defaultClassLoader()): List<InputStream> {
    val enumeration = loadAll0(classLoader)
    val result = mutableListOf<InputStream>()
    while (enumeration.hasMoreElements()) {
        result.add(enumeration.nextElement().openStream())
    }
    return result
}

@JvmName("loadFiles")
@JvmOverloads
fun CharSequence.loadResourceFiles(classLoader: ClassLoader = defaultClassLoader()): List<File> {
    val enumeration = loadAll0(classLoader)
    val result = mutableListOf<File>()
    while (enumeration.hasMoreElements()) {
        result.add(enumeration.nextElement().toFile())
    }
    return result
}

@JvmName("loadStrings")
@JvmOverloads
fun CharSequence.loadResourceStrings(
    charset: Charset = defaultCharset(), classLoader: ClassLoader = defaultClassLoader()
): List<String> {
    val enumeration = loadAll0(classLoader)
    val result = mutableListOf<String>()
    while (enumeration.hasMoreElements()) {
        result.add(enumeration.nextElement().openStream().readString(charset, true))
    }
    return result
}

@JvmName("loadPropertiesList")
@JvmOverloads
fun CharSequence.loadResourcePropertiesList(
    charset: Charset = defaultCharset(), classLoader: ClassLoader = defaultClassLoader()
): List<Map<String, String>> {
    val enumeration = loadAll0(classLoader)
    val result = mutableListOf<Map<String, String>>()
    while (enumeration.hasMoreElements()) {
        result.add(enumeration.nextElement().readProperties(charset))
    }
    return result
}

private fun CharSequence.loadAll0(classLoader: ClassLoader = defaultClassLoader()): Enumeration<URL> {
    return classLoader.getResources(this.removeAbsolute())
}

private fun CharSequence.removeAbsolute(): String {
    return this.removeIfStartWith("/")
}

open class NoSuchResourceException(message: String?) : RuntimeException(message), Serializable {
    companion object {
        private val serialVersionUID: Long = defaultSerialVersion()
    }
}