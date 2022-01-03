@file:JvmName("BThread")

package xyz.srclab.common.base

import java.time.Duration
import java.util.function.BiFunction

/**
 * [Thread] of current context.
 */
fun currentThread(): Thread {
    return Thread.currentThread()
}

/**
 * Sleeps for [millis] and [nanos].
 */
@JvmOverloads
fun sleep(millis: Long, nanos: Int = 0) {
    Thread.sleep(millis, nanos)
}

/**
 * Sleeps for [duration].
 */
fun sleep(duration: Duration) {
    sleep(duration.toMillis(), duration.nano)
}

/**
 * Try to find caller stack trace element, usually used in logging.
 *
 * This function invokes [filter] for each stack trace element of current [Thread.getStackTrace], and:
 *
 * * Find first element as `called` of which invoking result is `0`;
 * * Then from next element continue to find first element as `caller` of which invoking result is `1`;
 * * Return `caller` element.
 *
 * Note if stack trace elements of current thread is null,
 * or index of `caller` is out of bounds, return null.
 */
fun callerStackTraceOrNull(
    filter: (StackTraceElement, findCalled: Boolean) -> Int
): StackTraceElement? {
    return callerStackTraceOrNull(0, filter)
}

/**
 * Try to find caller stack trace element, usually used in logging.
 *
 * This function invokes [filter] for each stack trace element of current [Thread.getStackTrace], and:
 *
 * * Find first element as `called` of which invoking result is `0`;
 * * Then from next element continue to find first element as `caller` of which invoking result is `1`;
 * * Return `caller` element.
 *
 * Note second parameter (boolean type) indicates whether `called` element has found.
 *
 * Note if stack trace elements of current thread is null,
 * or index of `caller` is out of bounds, return null.
 */
fun callerStackTraceOrNull(
    filter: BiFunction<StackTraceElement, Boolean, Int>
): StackTraceElement? {
    return callerStackTraceOrNull(0, filter)
}

/**
 * Try to find caller stack trace element, usually used in logging.
 *
 * This function invokes [filter] for each stack trace element of current [Thread.getStackTrace], and:
 *
 * * Find first element as `called` of which invoking result is `0`;
 * * Then from next element continue to find first element as `caller` of which invoking result is `1`;
 * * Return element of which index is ([offset] + index of `caller` element).
 *
 * Note if stack trace elements of current thread is null,
 * or index of `caller` is out of bounds, return null.
 */
fun callerStackTraceOrNull(
    offset: Int,
    filter: (StackTraceElement, findCalled: Boolean) -> Int
): StackTraceElement? {
    return callerStackTraceOrNull(offset, filter.toFunction())
}

/**
 * Try to find caller stack trace element, usually used in logging.
 *
 * This function invokes [filter] for each stack trace element of current [Thread.getStackTrace], and:
 *
 * * Find first element as `called` of which invoking result is `0`;
 * * Then from next element continue to find first element as `caller` of which invoking result is `1`;
 * * Return element of which index is ([offset] + index of `caller` element).
 *
 * Note second parameter (boolean type) indicates whether `called` element has found.
 *
 * Note if stack trace elements of current thread is null,
 * or index of `caller` is out of bounds, return null.
 */
fun callerStackTraceOrNull(
    offset: Int,
    filter: BiFunction<StackTraceElement, Boolean, Int>
): StackTraceElement? {
    val stackTrace = currentThread().stackTrace
    if (stackTrace.isNullOrEmpty()) {
        return null
    }
    for (i in stackTrace.indices) {
        var result = filter.apply(stackTrace[i], false)
        if (result == 0) {
            for (j in i + 1 until stackTrace.size) {
                result = filter.apply(stackTrace[j], true)
                if (result == 1) {
                    val callerIndex = j + offset
                    if (callerIndex.isIndexInBounds(0, stackTrace.size)) {
                        return stackTrace[callerIndex]
                    }
                    return null
                }
            }
        }
    }
    return null
}