package xyz.srclab.common.base

import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.concurrent.TimeUnit

interface Defaults {

    val charset: Charset

    val locale: Locale

    val timeUnit: TimeUnit

    val concurrencyLevel: Int

    companion object {

        @JvmStatic
        fun charset() = DefaultsImpl.charset

        @JvmStatic
        fun locale() = DefaultsImpl.locale

        @JvmStatic
        fun timeUnit() = DefaultsImpl.timeUnit

        @JvmStatic
        fun concurrencyLevel() = DefaultsImpl.concurrencyLevel

        private object DefaultsImpl : Defaults {
            override val charset: Charset
                get() = StandardCharsets.UTF_8
            override val locale: Locale
                get() = Locale.getDefault()
            override val timeUnit: TimeUnit
                get() = TimeUnit.SECONDS
            override val concurrencyLevel: Int
                get() = 16
        }
    }
}