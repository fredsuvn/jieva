@file:JvmName("BConfig")

package xyz.srclab.common.base

import java.io.File
import java.io.InputStream
import java.io.Reader
import java.net.URL
import java.nio.charset.Charset
import java.util.*

@JvmOverloads
fun InputStream.readProperties(charset: Charset = DEFAULT_CHARSET): Map<String, String> {
    return this.reader(charset).readProperties()
}

fun Reader.readProperties(): Map<String, String> {
    val props = Properties()
    props.load(this)
    val map = props.toStringMap()
    this.close()
    return map
}

@JvmOverloads
fun File.readProperties(charset: Charset = DEFAULT_CHARSET): Map<String, String> {
    return this.reader(charset).readProperties()
}

@JvmOverloads
fun URL.readProperties(charset: Charset = DEFAULT_CHARSET): Map<String, String> {
    return this.openStream().readProperties(charset)
}

fun Properties.toStringMap(): Map<String, String> {
    val result = mutableMapOf<String, String>()
    for (mutableEntry in this) {
        if (mutableEntry.key !== null || mutableEntry.value !== null) {
            result[mutableEntry.key.toString()] = mutableEntry.value.toString()
        }
    }
    return result
}