package xyz.srclab.common.base

import com.google.common.base.CharMatcher
import java.io.File
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Default values.
 */
object Defaults {

    /**
     * Default charset: UTF-8.
     */
    @get:JvmName("charset")
    @JvmStatic
    val charset: Charset = StandardCharsets.UTF_8

    /**
     * Default locale: [Locale.getDefault].
     */
    @get:JvmName("locale")
    @JvmStatic
    val locale: Locale
        get() {
            return Locale.getDefault()
        }

    /**
     * Default time unit: [TimeUnit.SECONDS].
     */
    @get:JvmName("timeUnit")
    @JvmStatic
    val timeUnit: TimeUnit = TimeUnit.SECONDS

    /**
     * Default concurrent level: Math.min([Environments.availableProcessors] * 2, 16).
     */
    @get:JvmName("concurrencyLevel")
    @JvmStatic
    val concurrencyLevel: Int
        get() {
            return (Environments.availableProcessors * 2).coerceAtMost(16)
        }

    /**
     * Default file separator: [File.separator].
     * "/" on Unix, "\\" on Windows.
     */
    @get:JvmName("fileSeparator")
    @JvmStatic
    val fileSeparator: String = File.separator

    /**
     * Default path separator: [File.pathSeparator].
     * ":" on Unix, ";" on Windows.
     */
    @get:JvmName("pathSeparator")
    @JvmStatic
    val pathSeparator: String = File.pathSeparator

    /**
     * Default line separator: [System.lineSeparator].
     * "\n" on Unix, "\r\n" on Windows, "\r" on Mac.
     */
    @get:JvmName("lineSeparator")
    @JvmStatic
    val lineSeparator: String
        get() {
            return System.lineSeparator()
        }

    /**
     * Default timestamp pattern: `yyyyMMddHHmmssSSS`.
     */
    @get:JvmName("timestampPattern")
    @JvmStatic
    val timestampPattern: String = "yyyyMMddHHmmssSSS"

    /**
     * Default date time pattern: `yyyy-MM-dd HH-mm-ss`.
     */
    @get:JvmName("dateTimePattern")
    @JvmStatic
    val dateTimePattern: String = "yyyy-MM-dd HH-mm-ss"

    /**
     * Default timestamp formatter: [timestampPattern].
     */
    @get:JvmName("timestampFormatter")
    @JvmStatic
    val timestampFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern(timestampPattern)

    /**
     * Default date time formatter: [dateTimePattern].
     */
    @get:JvmName("dateTimeFormatter")
    @JvmStatic
    val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern(dateTimePattern)

    /**
     * Default timestamp format: [timestampPattern].
     */
    @get:JvmName("timestampFormat")
    @JvmStatic
    val timestampFormat: DateFormat = SimpleDateFormat(timestampPattern)

    /**
     * Default date time format: [dateTimePattern].
     */
    @get:JvmName("dateTimeFormat")
    @JvmStatic
    val dateTimeFormat: DateFormat = SimpleDateFormat(dateTimePattern)

    /**
     * Default radix: 10.
     */
    @get:JvmName("radix")
    @JvmStatic
    val radix: Int = 10

    /**
     * [CharMatcher] of pattern dot: ".".
     */
    @get:JvmName("dotMatcher")
    @JvmStatic
    val dotMatcher: CharMatcher = CharMatcher.`is`('.')

    /**
     * [CharMatcher] of pattern hyphen: "-".
     */
    @get:JvmName("hyphenMatcher")
    @JvmStatic
    val hyphenMatcher: CharMatcher = CharMatcher.`is`('-')

    /**
     * [CharMatcher] of pattern plus sign: "+".
     */
    @get:JvmName("plusMatcher")
    @JvmStatic
    val plusMatcher: CharMatcher = CharMatcher.`is`('+')
}