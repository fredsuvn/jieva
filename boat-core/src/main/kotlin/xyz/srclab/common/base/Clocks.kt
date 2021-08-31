@file:JvmName("Clocks")
@file:JvmMultifileClass

package xyz.srclab.common.base

import java.time.LocalDateTime

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
 * Returns current timestamp by [Defaults.timestampPattern].
 */
fun nowTimestamp(): String {
    return Defaults.timestampFormatter.format(LocalDateTime.now())
}