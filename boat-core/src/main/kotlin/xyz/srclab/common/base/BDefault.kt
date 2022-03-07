@file:JvmName("BDefault")

package xyz.srclab.common.base

import xyz.srclab.common.Boat
import xyz.srclab.common.base.DatePattern.Companion.toDatePattern
import java.nio.charset.Charset
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField
import java.util.*

private const val defaultTimestampPatternString: String = "yyyyMMddHHmmssSSS"

private val defaultTimestampPattern: DatePattern = run {
    // JDK8 bug:
    // Error for "yyyyMMddHHmmssSSS".toDatePattern()
    if (isJdk9OrHigher()) {
        return@run defaultTimestampPatternString.toDatePattern()
    }
    val formatter: DateTimeFormatter = DateTimeFormatterBuilder() // date/time
        .appendPattern("yyyyMMddHHmmss") // milliseconds
        .appendValue(ChronoField.MILLI_OF_SECOND, 3) // create formatter
        .toFormatter()
    DatePattern.of(defaultTimestampPatternString, formatter)
}

/**
 * Returns default charset: UTF-8.
 */
@JvmName("charset")
fun defaultCharset(): Charset = utf8()

/**
 * Returns default buffer size: 8 * 1024.
 */
@JvmName("bufferSize")
fun defaultBufferSize(): Int = 8 * 1024

/**
 * Returns default serial version for current boat version.
 */
@JvmName("serialVersion")
fun defaultSerialVersion(): Long = Boat.serialVersion()

/**
 * Returns default radix: 10.
 */
@JvmName("radix")
fun defaultRadix(): Int = 10

/**
 * Returns default locale: [Locale.ENGLISH].
 */
@JvmName("locale")
fun defaultLocale(): Locale = Locale.ENGLISH

/**
 * Returns default timestamp pattern: yyyyMMddHHmmssSSS.
 */
@JvmName("timestampPattern")
fun defaultTimestampPattern(): DatePattern = defaultTimestampPattern