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
fun InputStream.readProperties(charset: Charset = defaultCharset(), close: Boolean = false): Map<String, String> {
    return this.reader(charset).readProperties(close)
}

/**
 * Reads and parses properties.
 */
@JvmOverloads
fun Reader.readProperties(close: Boolean = false): Map<String, String> {
    val props = Properties()
    props.load(this)
    val map = props.toStringMap()
    if (close) {
        this.close()
    }
    return map
}

/**
 * Reads and parses properties.
 */
@JvmOverloads
fun File.readProperties(charset: Charset = defaultCharset()): Map<String, String> {
    return this.reader(charset).readProperties(true)
}