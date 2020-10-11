package xyz.srclab.common.base

import java.io.File
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.concurrent.TimeUnit

object Defaults {

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
    val concurrencyLevel: Int = 16

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
}