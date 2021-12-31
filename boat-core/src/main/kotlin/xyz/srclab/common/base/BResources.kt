@file:JvmName("BResources")

package xyz.srclab.common.base

import java.io.File
import java.io.InputStream
import java.net.URL
import java.nio.charset.Charset
import java.util.*

fun CharSequence.loadResource(): URL {
    return loadResourceOrNull() ?: throw ResourceNotFoundException("Resource not found: $this")
}

fun CharSequence.loadResourceOrNull(): URL? {
    return Any::class.java.getResource(this.addAbsolute())
}

fun CharSequence.loadResourceStream(): InputStream {
    return loadResource().openStream()
}

fun CharSequence.loadResourceStreamOrNull(): InputStream? {
    return loadResourceOrNull()?.openStream()
}

fun CharSequence.loadResourceFile(): File {
    return loadResourceFileOrNull() ?: throw ResourceNotFoundException("Resource file not found: $this")
}

fun CharSequence.loadResourceFileOrNull(): File? {
    val path = loadResourceOrNull()?.file
    return if (path === null) {
        null
    } else {
        File(path)
    }
}

@JvmOverloads
fun CharSequence.loadResourceString(charset: Charset = DEFAULT_CHARSET): String {
    return loadResourceStream().reader(charset).readText()
}

@JvmOverloads
fun CharSequence.loadResourceStringOrNull(charset: Charset = DEFAULT_CHARSET): String? {
    return loadResourceStreamOrNull()?.reader(charset)?.readText()
}

fun CharSequence.loadResourceProperties(): Map<String, String> {
    val input = loadResourceStreamOrNull()
    if (input === null) {
        return emptyMap()
    }
    val prop = Properties()
    prop.load(input)
    return prop.toMap()
}

fun CharSequence.loadResources(): Set<URL> {
    val enumeration = BytesClassLoader.getResources(this.removeAbsolute())
    val result = mutableSetOf<URL>()
    while (enumeration.hasMoreElements()) {
        result.add(enumeration.nextElement())
    }
    return result
}

fun CharSequence.loadResourceStreams(): Set<InputStream> {
    val enumeration = BytesClassLoader.getResources(this.removeAbsolute())
    val result = mutableSetOf<InputStream>()
    while (enumeration.hasMoreElements()) {
        result.add(enumeration.nextElement().openStream())
    }
    return result
}

fun CharSequence.loadResourceFiles(): Set<File> {
    val enumeration = BytesClassLoader.getResources(this.removeAbsolute())
    val result = mutableSetOf<File>()
    while (enumeration.hasMoreElements()) {
        val path = enumeration.nextElement()?.file
        if (path !== null) {
            result.add(File(path))
        }
    }
    return result
}

@JvmOverloads
fun CharSequence.loadResourceStrings(charset: Charset = DEFAULT_CHARSET): Set<String> {
    val enumeration = BytesClassLoader.getResources(this.removeAbsolute())
    val result = mutableSetOf<String>()
    while (enumeration.hasMoreElements()) {
        val text = enumeration.nextElement().openStream().reader(charset).readText()
        result.add(text)
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

private fun Properties.toMap(): Map<String, String> {
    val result = mutableMapOf<String, String>()
    for (mutableEntry in this) {
        if (mutableEntry.key !== null || mutableEntry.value !== null) {
            result[mutableEntry.key.toString()] = mutableEntry.value.toString()
        }
    }
    return result
}

open class ResourceNotFoundException(message: String?) : RuntimeException(message)