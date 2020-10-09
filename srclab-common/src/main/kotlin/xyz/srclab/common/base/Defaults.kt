package xyz.srclab.common.base

import java.io.File
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.concurrent.TimeUnit

object Defaults {

    @JvmStatic
    val charset: Charset
        @JvmName("charset") get() = StandardCharsets.UTF_8

    @JvmStatic
    val locale: Locale
        @JvmName("locale") get() = Locale.getDefault()

    @JvmStatic
    val timeUnit: TimeUnit
        @JvmName("timeUnit") get() = TimeUnit.SECONDS

    @JvmStatic
    val concurrencyLevel: Int
        @JvmName("concurrencyLevel") get() = 16

    @JvmStatic
    val fileSeparator: String
        @JvmName("fileSeparator") get() = File.separator

    @JvmStatic
    val pathSeparator: String
        @JvmName("pathSeparator") get() = File.pathSeparator

    @JvmStatic
    val lineSeparator: String
        @JvmName("lineSeparator") get() = System.lineSeparator()
}