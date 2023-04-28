/**
 * Thread utilities.
 */
@file:JvmName("BtThread")

package xyz.srclab.common.base

import xyz.srclab.common.asType
import java.time.Duration
import java.util.function.BiPredicate

private val local: ThreadLocal<MutableMap<Any, Any?>> = ThreadLocal.withInitial { HashMap() }

/**
 * Returns current [Thread].
 */
fun currentThread(): Thread {
    return Thread.currentThread()
}

/**
 * Sleeps current thread for [millis] and [nanos].
 */
@JvmOverloads
fun sleep(millis: Long, nanos: Int = 0) {
    Thread.sleep(millis, nanos)
}

/**
 * Sleeps current thread for [duration].
 */
fun sleep(duration: Duration) {
    sleep(duration.toMillis(), duration.nano)
}

/**
 * Returns thread local value of [key].
 */
@JvmName("get")
fun <T> getLocal(key: Any): T {
    return local.get()[key].asType()
}

/**
 * Sets thread local value of [key] with [value], returns old value.
 */
@JvmName("set")
fun <T> setLocal(key: Any, value: Any?): T {
    return local.get().put(key, value).asType()
}

/**
 * Returns container of all thread local values managed by `BtThread`.
 *
 * Note any change for returned map will reflect [getLocal] and [setLocal], and vice versa.
 */
@JvmName("getAll")
fun getAllLocals(): MutableMap<Any, Any?> {
    return local.get()
}

/**
 * Try to find caller stack trace element, usually used in logging.
 *
 * This function invokes [predicate] for each stack trace element of current [Thread.getStackTrace]:
 *
 * * Invokes `predicate.test(element, false)` for each stack trace element util the `true` has returned,
 * let the index of this element be `loggerIndex`;
 * * Invokes `predicate.test(element, true)` for each stack trace element
 * from the `loggerIndex` exclusive util the `true` has returned
 * let the index of this element be `callerIndex`;
 * * Return element of which index is `callerIndex`.
 *
 * Note if stack trace elements of current thread is null,
 * or index of returned element is out of bounds, return null.
 */
fun callerStackTrace(
    predicate: BiPredicate<StackTraceElement, Boolean>
): StackTraceElement? {
    return callerStackTrace(0, predicate)
}

/**
 * Try to find caller stack trace element, usually used in logging.
 *
 * This function invokes [predicate] for each stack trace element of current [Thread.getStackTrace]:
 *
 * * Invokes `predicate.test(element, false)` for each stack trace element util the `true` has returned,
 * let the index of this element be `loggerIndex`;
 * * Invokes `predicate.test(element, true)` for each stack trace element
 * from the `loggerIndex` exclusive util the `true` has returned
 * let the index of this element be `callerIndex`;
 * * Return element of which index is ([offset] + `callerIndex`).
 *
 * Note if stack trace elements of current thread is null,
 * or index of returned element is out of bounds, return null.
 */
fun callerStackTrace(
    offset: Int,
    predicate: BiPredicate<StackTraceElement, Boolean>
): StackTraceElement? {
    val stackTrace = currentThread().stackTrace
    if (stackTrace.isNullOrEmpty()) {
        return null
    }
    for (i in stackTrace.indices) {
        var result = predicate.test(stackTrace[i], false)
        if (result) {
            for (j in i + 1 until stackTrace.size) {
                result = predicate.test(stackTrace[j], true)
                if (result) {
                    val index = j + offset
                    if (index.isInBounds(0, stackTrace.size)) {
                        return stackTrace[index]
                    }
                    return null
                }
            }
        }
    }
    return null
}