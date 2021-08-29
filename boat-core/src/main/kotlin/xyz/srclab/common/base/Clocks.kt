@file:JvmName("Clocks")
@file:JvmMultifileClass

package xyz.srclab.common.base

import java.time.LocalDateTime

/**
 * Returns current time as milliseconds.
 */
fun currentMillis(): Long {
    return System.currentTimeMillis()
}

/**
 * Returns the current running time in nanoseconds.
 */
fun currentNanos(): Long {
    return System.nanoTime();
}

/**
 * Returns current timestamp by [Defaults.timestampPattern].
 */
fun timestamp(): String {
    return Defaults.timestampFormatter.format(LocalDateTime.now())
}