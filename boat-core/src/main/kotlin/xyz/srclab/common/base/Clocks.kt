@file:JvmName("Clocks")
@file:JvmMultifileClass

package xyz.srclab.common.base

import java.time.LocalDateTime
import java.time.ZoneOffset

/**
 * Returns current time as milliseconds.
 */
fun nowMillis(): Long {
    return System.currentTimeMillis()
}

/**
 * Returns the current running time in nanoseconds.
 */
fun nowNanos(): Long {
    return System.nanoTime();
}

/**
 * Returns current timestamp of [ZoneOffset.UTC] by [TIMESTAMP_PATTERN].
 */
fun nowTimestamp(): String {
    return TIMESTAMP_FORMATTER.format(LocalDateTime.now(ZoneOffset.UTC))
}