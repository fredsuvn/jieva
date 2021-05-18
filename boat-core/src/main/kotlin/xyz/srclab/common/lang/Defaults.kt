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
     * Represents null value.
     */
    @JvmField
    val NULL: Any = "NULL_VALUE"

    /**
     * Represents absent value.
     */
    @JvmField
    val ABSENT: Any = "ABSENT_VALUE"

    @JvmField
    val DOT_MATCHER: CharMatcher = CharMatcher.`is`('.')

    @JvmField
    val HYPHEN_MATCHER: CharMatcher = CharMatcher.`is`('-')

    @JvmField
    val PLUS_SIGN_MATCHER: CharMatcher = CharMatcher.`is`('+')

    @JvmStatic
    @get:JvmName("charset")
    val charset: Charset = StandardCharsets.UTF_8

    @JvmStatic
    val locale: Locale
        @JvmName("locale") get() {
            return Locale.getDefault()
        }

    @JvmStatic
    @get:JvmName("timeUnit")
    val timeUnit: TimeUnit = TimeUnit.SECONDS

    @JvmStatic
    @get:JvmName("concurrencyLevel")
    val concurrencyLevel: Int = Environment.availableProcessors * 2

    @JvmStatic
    @get:JvmName("fileSeparator")
    val fileSeparator: String = File.separator

    @JvmStatic
    @get:JvmName("pathSeparator")
    val pathSeparator: String = File.pathSeparator

    @JvmStatic
    val lineSeparator: String
        @JvmName("lineSeparator") get() {
            return System.lineSeparator()
        }

    @JvmStatic
    @get:JvmName("timestampPattern")
    val timestampPattern: String = "yyyyMMddHHmmssSSS"

    @JvmStatic
    @get:JvmName("timestampFormatter")
    val timestampFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern(timestampPattern, locale)

    @JvmStatic
    @get:JvmName("radix")
    val radix: Int = 10
}