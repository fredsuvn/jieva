@file:JvmName("Clocks")
@file:JvmMultifileClass

package xyz.srclab.common.lang

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