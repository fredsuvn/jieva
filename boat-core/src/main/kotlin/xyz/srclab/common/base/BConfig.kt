@file:JvmName("BConfig")

package xyz.srclab.common.base

import xyz.srclab.common.collect.toStringMap
import java.io.File
import java.io.InputStream
import java.io.Reader
import java.nio.charset.Charset
import java.util.*

/**
 * Reads and parses properties.
 */
@JvmOverloads
fun InputStream.readProperties(charset: Charset = defaultCharset()): Map<String, String> {
    return this.reader(charset).readProperties()
}

/**
 * Reads and parses properties.
 */
fun Reader.readProperties(): Map<String, String> {
    val props = Properties()
    props.load(this)
    val map = props.toStringMap()
    this.close()
    return map
}

/**
 * Reads and parses properties.
 */
@JvmOverloads
fun File.readProperties(charset: Charset = defaultCharset()): Map<String, String> {
    return this.reader(charset).readProperties()
}