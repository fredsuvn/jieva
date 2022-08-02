package xyz.srclab.common.base

import xyz.srclab.common.Boat
import xyz.srclab.common.base.DatePattern.Companion.toDatePattern
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField
import java.util.*

/**
 * Base and default properties for Boat.
 */
object BtProps {

    /**
     * Returns default charset: UTF-8.
     */
    @JvmStatic
    fun charset(): Charset = StandardCharsets.UTF_8

    /**
     * Returns default [String] for `null`.
     */
    @JvmStatic
    fun nullString(): String = "null"

    /**
     * Returns default radix: 10.
     */
    @JvmStatic
    fun radix(): Int = 10

    /**
     * Returns default locale: [Locale.ENGLISH].
     */
    @JvmStatic
    fun locale(): Locale = Locale.ENGLISH

    /**
     * Returns default concurrency level: [availableProcessors] * 4.
     */
    @JvmStatic
    fun concurrencyLevel(): Int = availableProcessors() * 4

    /**
     * Returns default timestamp pattern: yyyyMMddHHmmssSSS.
     */
    @JvmStatic
    fun timestampPattern(): DatePattern = BtPropsHolder.timestampPattern

    /**
     * Returns default IO buffer size: 8 * 1024.
     */
    @JvmStatic
    fun ioBufferSize(): Int = 8 * 1024

    /**
     * Returns [ClassLoader.getSystemClassLoader].
     */
    @JvmStatic
    fun classLoader(): ClassLoader {
        return ClassLoader.getSystemClassLoader()
    }

    /**
     * Returns default serial version for current boat version.
     */
    @JvmStatic
    fun serialVersion(): Long = Boat.serialVersion()

    private object BtPropsHolder {
        val timestampPattern: DatePattern = run {
            val pattern = "yyyyMMddHHmmssSSS"
            // JDK8 bug:
            // Error for "yyyyMMddHHmmssSSS".toDatePattern()
            if (isJdk9OrHigher()) {
                return@run pattern.toDatePattern()
            }
            val formatter: DateTimeFormatter = DateTimeFormatterBuilder() // date/time
                .appendPattern("yyyyMMddHHmmss") // milliseconds
                .appendValue(ChronoField.MILLI_OF_SECOND, 3) // create formatter
                .toFormatter()
            DatePattern.of(pattern, formatter)
        }
    }
}