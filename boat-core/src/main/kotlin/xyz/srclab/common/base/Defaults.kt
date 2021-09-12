@file:JvmName("Defaults")

package xyz.srclab.common.base

import java.io.File
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

/**
 * Returns default charset: UTF-8.
 */
fun defaultCharset(): Charset = StandardCharsets.UTF_8

/**
 * Default radix: 10.
 */
const val RADIX: Int = 10

/**
 * Default concurrent level: Math.min([Systems.availableProcessors] * 2, 16).
 */
fun default: Int
    get() {
        return (Systems.availableProcessors * 2).coerceAtMost(16)
    }

/**
 * Default file separator: [File.separator].
 * "/" on Unix, "\\" on Windows.
 */
@JvmField
val FILE_SEPARATOR: String = File.separator

/**
 * Default path separator: [File.pathSeparator].
 * ":" on Unix, ";" on Windows.
 */
@JvmField
val PATH_SEPARATOR: String = File.pathSeparator

/**
 * Default line separator: [System.lineSeparator].
 * "\n" on Unix, "\r\n" on Windows, "\r" on Mac.
 */
@JvmField
val LINE_SEPARATOR: String = System.lineSeparator()