package xyz.srclab.common.base

import xyz.srclab.common.Boat
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
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
    fun timestampPattern(): String = "yyyyMMddHHmmssSSS"

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
}