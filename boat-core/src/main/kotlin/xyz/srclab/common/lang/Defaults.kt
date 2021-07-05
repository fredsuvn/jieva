package xyz.srclab.common.lang

import com.google.common.base.CharMatcher
import java.io.File
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Default settings and arguments.
 */
object Defaults {

    /**
     * A [String] constant of which value is `NULL`.
     */
    const val NULL: String = "NULL"

    /**
     * A [String] constant of which value is `ABSENT`.
     */
    const val ABSENT: String = "ABSENT"

    @JvmField
    val DOT_MATCHER: CharMatcher = CharMatcher.`is`('.')

    @JvmField
    val HYPHEN_MATCHER: CharMatcher = CharMatcher.`is`('-')

    @JvmField
    val PLUS_SIGN_MATCHER: CharMatcher = CharMatcher.`is`('+')

    /**
     * UTF-8.
     */
    @JvmStatic
    @get:JvmName("charset")
    val charset: Charset = StandardCharsets.UTF_8

    /**
     * [Locale.getDefault].
     */
    @JvmStatic
    val locale: Locale
        @JvmName("locale") get() {
            return Locale.getDefault()
        }

    /**
     * [TimeUnit.SECONDS].
     */
    @JvmStatic
    @get:JvmName("timeUnit")
    val timeUnit: TimeUnit = TimeUnit.SECONDS

    /**
     * [Environment.availableProcessors] * 2.
     */
    @JvmStatic
    @get:JvmName("concurrencyLevel")
    val concurrencyLevel: Int = Environment.availableProcessors * 2

    /**
     * [File.separator].
     */
    @JvmStatic
    @get:JvmName("fileSeparator")
    val fileSeparator: String = File.separator

    /**
     * [File.pathSeparator].
     */
    @JvmStatic
    @get:JvmName("pathSeparator")
    val pathSeparator: String = File.pathSeparator

    /**
     * [System.lineSeparator].
     */
    @JvmStatic
    val lineSeparator: String
        @JvmName("lineSeparator") get() {
            return System.lineSeparator()
        }

    /**
     * yyyyMMddHHmmssSSS.
     */
    @JvmStatic
    @get:JvmName("timestampPattern")
    val timestampPattern: String = "yyyyMMddHHmmssSSS"

    /**
     * yyyyMMddHHmmssSSS.
     */
    @JvmStatic
    @get:JvmName("timestampFormatter")
    val timestampFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern(timestampPattern, locale)

    /**
     * 10.
     */
    @JvmStatic
    @get:JvmName("radix")
    val radix: Int = 10
}