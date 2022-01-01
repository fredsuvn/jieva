@file:JvmName("BConfigs")

package xyz.srclab.common.base

import java.io.File
import java.io.InputStream
import java.io.Reader
import java.util.*

fun InputStream.readProperties(): Map<String, String> {
    val props = Properties()
    props.load(this)
    return props.toMap()
}

fun Reader.readProperties(): Map<String, String> {
    val props = Properties()
    props.load(this)
    return props.toMap()
}

fun File.readProperties(): Map<String, String> {
    val props = Properties()
    props.load(this.inputStream())
    return props.toMap()
}

fun Properties.toMap(): Map<String, String> {
    val result = mutableMapOf<String, String>()
    for (mutableEntry in this) {
        if (mutableEntry.key !== null || mutableEntry.value !== null) {
            result[mutableEntry.key.toString()] = mutableEntry.value.toString()
        }
    }
    return result
}