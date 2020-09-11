package xyz.srclab.common.base

import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.concurrent.TimeUnit

object Defaults {

    @JvmStatic
    fun charset(): Charset = StandardCharsets.UTF_8

    @JvmStatic
    fun locale(): Locale = Locale.getDefault()

    @JvmStatic
    fun timeUnit(): TimeUnit = TimeUnit.SECONDS

    @JvmStatic
    fun concurrencyLevel(): Int = 16
}