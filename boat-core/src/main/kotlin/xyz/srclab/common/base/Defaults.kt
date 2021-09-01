package xyz.srclab.common.base

import com.google.common.base.CharMatcher
import java.io.File
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Default values.
 */
object Defaults {

    /**
     * UTF-8.
     */
    @get:JvmName("charset")
    @JvmStatic
    val charset: Charset = StandardCharsets.UTF_8

    /**
     * [Locale.getDefault].
     */
    @get:JvmName("locale")
    @JvmStatic
    val locale: Locale
        get() {
            return Locale.getDefault()
        }

    /**
     * [TimeUnit.SECONDS].
     */
    @get:JvmName("timeUnit")
    @JvmStatic
    val timeUnit: TimeUnit = TimeUnit.SECONDS

    /**
     * [Environments.availableProcessors] * 2.
     */
    @get:JvmName("concurrencyLevel")
    @JvmStatic
    val concurrencyLevel: Int
        get() {
            return Environments.availableProcessors * 2
        }

    /**
     * [File.separator].
     * "/" on Unix, "\\" on Windows.
     */
    @get:JvmName("fileSeparator")
    @JvmStatic
    val fileSeparator: String = File.separator

    /**
     * [File.pathSeparator].
     * ":" on Unix, ";" on Windows.
     */
    @get:JvmName("pathSeparator")
    @JvmStatic
    val pathSeparator: String = File.pathSeparator

    /**
     * [System.lineSeparator].
     * "\n" on Unix, "\r\n" on Windows, "\r" on Mac.
     */
    @get:JvmName("lineSeparator")
    @JvmStatic
    val lineSeparator: String
        get() {
            return System.lineSeparator()
        }

    /**
     * yyyyMMddHHmmssSSS.
     */
    @get:JvmName("timestampPattern")
    @JvmStatic
    val timestampPattern: String = "yyyyMMddHHmmssSSS"

    /**
     * yyyyMMddHHmmssSSS.
     */
    @get:JvmName("timestampFormatter")
    @JvmStatic
    val timestampFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern(timestampPattern, locale)

    /**
     * 10.
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