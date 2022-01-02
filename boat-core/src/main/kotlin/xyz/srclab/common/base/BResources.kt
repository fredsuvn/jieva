@file:JvmName("BResources")

package xyz.srclab.common.base

import xyz.srclab.common.io.readString
import xyz.srclab.common.io.toFile
import xyz.srclab.common.io.toFileOrNull
import java.io.File
import java.io.InputStream
import java.net.URL
import java.nio.charset.Charset

@JvmName("load")
fun CharSequence.loadResource(): URL {
    return loadResourceOrNull() ?: throw ResourceNotFoundException("Resource not found: $this")
}

@JvmName("loadOrNull")
fun CharSequence.loadResourceOrNull(): URL? {
    return Any::class.java.getResource(this.addAbsolute())
}

@JvmName("loadStream")
fun CharSequence.loadResourceStream(): InputStream {
    return loadResource().openStream()
}

@JvmName("loadStreamOrNull")
fun CharSequence.loadResourceStreamOrNull(): InputStream? {
    return loadResourceOrNull()?.openStream()
}

@JvmName("loadFile")
fun CharSequence.loadResourceFile(): File {
    return loadResource().toFile()
}

@JvmName("loadFileOrNull")
fun CharSequence.loadResourceFileOrNull(): File? {
    return loadResourceOrNull()?.toFileOrNull()
}

@JvmName("loadString")
@JvmOverloads
fun CharSequence.loadResourceString(charset: Charset = DEFAULT_CHARSET): String {
    return loadResourceStream().readString(charset, true)
}

@JvmName("loadStringOrNull")
@JvmOverloads
fun CharSequence.loadResourceStringOrNull(charset: Charset = DEFAULT_CHARSET): String? {
    return loadResourceStreamOrNull()?.readString(charset, true)
}

@JvmName("loadProperties")
@JvmOverloads
fun CharSequence.loadResourceProperties(charset: Charset = DEFAULT_CHARSET): Map<String, String> {
    return loadResource().readProperties(charset)
}

@JvmName("loadPropertiesOrNull")
@JvmOverloads
fun CharSequence.loadResourcePropertiesOrNull(charset: Charset = DEFAULT_CHARSET): Map<String, String>? {
    return loadResourceOrNull()?.readProperties(charset)
}

@JvmName("loadAll")
fun CharSequence.loadResources(): Set<URL> {
    val enumeration = BytesClassLoader.getResources(this.removeAbsolute())
    val result = mutableSetOf<URL>()
    while (enumeration.hasMoreElements()) {
        result.add(enumeration.nextElement())
    }
    return result
}

@JvmName("loadStreams")
fun CharSequence.loadResourceStreams(): Set<InputStream> {
    val enumeration = BytesClassLoader.getResources(this.removeAbsolute())
    val result = mutableSetOf<InputStream>()
    while (enumeration.hasMoreElements()) {
        result.add(enumeration.nextElement().openStream())
    }
    return result
}

@JvmName("loadFiles")
fun CharSequence.loadResourceFiles(): Set<File> {
    val enumeration = BytesClassLoader.getResources(this.removeAbsolute())
    val result = mutableSetOf<File>()
    while (enumeration.hasMoreElements()) {
        result.add(enumeration.nextElement().toFile())
    }
    return result
}

@JvmName("loadStrings")
@JvmOverloads
fun CharSequence.loadResourceStrings(charset: Charset = DEFAULT_CHARSET): Set<String> {
    val enumeration = BytesClassLoader.getResources(this.removeAbsolute())
    val result = mutableSetOf<String>()
    while (enumeration.hasMoreElements()) {
        result.add(enumeration.nextElement().openStream().readString(charset, true))
    }
    return result
}

@JvmName("loadPropertiesSet")
@JvmOverloads
fun CharSequence.loadResourcePropertiesSet(charset: Charset = DEFAULT_CHARSET): Set<Map<String, String>> {
    val enumeration = BytesClassLoader.getResources(this.removeAbsolute())
    val result = mutableSetOf<Map<String, String>>()
    while (enumeration.hasMoreElements()) {
        result.add(enumeration.nextElement().readProperties(charset))
    }
    return result
}

private fun CharSequence.addAbsolute(): String {
    return if (this.startsWith('/')) {
        this.toString()
    } else {
        "/$this"
    }
}

private fun CharSequence.removeAbsolute(): String {
    return if (this.startsWith('/')) {
        this.substring(1)
    } else {
        this.toString()
    }
}

open class ResourceNotFoundException(message: String?) : RuntimeException(message)