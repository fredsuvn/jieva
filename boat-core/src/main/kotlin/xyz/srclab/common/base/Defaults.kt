@file:JvmName("Defaults")

package xyz.srclab.common.base

import java.io.File
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets


/**
 * Default charset: UTF-8.
 */
@JvmField
val CHARSET: Charset = StandardCharsets.UTF_8

/**
 * Default concurrent level: Math.min([Environments.availableProcessors] * 2, 16).
 */
val CONCURRENCY_LEVEL: Int
    get() {
        return (Environments.availableProcessors * 2).coerceAtMost(16)
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

/**
 * Default radix: 10.
 */
const val RADIX: Int = 10