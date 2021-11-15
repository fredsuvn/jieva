@file:JvmName("Contexts")
@file:JvmMultifileClass

package xyz.srclab.common.base

import xyz.srclab.common.run.RunContext
import java.time.Duration

/**
 * [Thread] of current context.
 */
fun currentThread(): Thread {
    return Thread.currentThread()
}

/**
 * [ClassLoader] of current context, if it is null, returns [BClassLoader].
 */
fun currentClassLoader(): ClassLoader {
    return currentThread().contextClassLoader ?: BClassLoader
}

@JvmName("getProperty")
@Throws(NullPointerException::class)
fun <T : Any> getContextProperty(key: Any): T {
    return getContextPropertyOrNull(key) ?: throw NullPointerException("getContext($key)")
}

@JvmName("getPropertyOrElse")
fun <T : Any> getContextPropertyOrElse(key: Any, value: T): T {
    return getContextPropertyOrNull(key) ?: value
}

@JvmName("getPropertyOrElse")
fun <T : Any> getContextPropertyOrElse(key: Any, supplier: (key: Any) -> T): T {
    return getContextPropertyOrNull(key) ?: supplier(key)
}

@JvmName("getPropertyOrThrow")
fun <T : Any> getContextPropertyOrThrow(key: Any, supplier: (key: Any) -> Throwable): T {
    return getContextPropertyOrNull(key) ?: throw supplier(key)
}

@JvmName("getPropertyOrNull")
fun <T : Any> getContextPropertyOrNull(key: Any): T? {
    return RunContext.current().getOrNull(key)
}

@JvmName("setProperty")
fun setContextProperty(key: Any, value: Any?) {
    RunContext.current().set(key, value)
}

/**
 * Returns current context as [MutableMap].
 * Note any operation for the return map will reflect the context map, and vice versa.
 */
@JvmName("getPropertiesAsMap")
fun getContextPropertiesAsMap(): MutableMap<Any, Any?> {
    return RunContext.current().asMap()
}

@JvmName("clearProperties")
fun clearContextProperties() {
    RunContext.current().clear()
}

/**
 * Returns copy of current context.
 */
@JvmName("attachProperties")
fun attachContextProperties(): Map<Any, Any?> {
    return RunContext.current().attach()
}

/**
 * Adds [contents] into current context.
 */
@JvmName("detachProperties")
fun detachContextProperties(contents: Map<Any, Any?>) {
    RunContext.current().detach(contents)
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
 * This function calls [filter] for each stack trace element of current thread,
 * first try to find the `called` element of which return value is `0`,
 * then continue (from ``called``'s index + 1) to find the `caller` element of which return value is `1`,
 * returns the element of which index is ``caller``'s index.
 *
 * Note if stack trace elements of current thread is null,
 * or ``caller``'s index is out of bounds, return null.
 */
fun callerStackTrace(
    filter: (StackTraceElement, findCalled: Boolean) -> Int
): StackTraceElement? {
    return callerStackTrace(0, filter)
}

/**
 * Try to find caller stack trace element, usually used in logging.
 *
 * This function calls [filter] for each stack trace element of current thread,
 * first try to find the `called` element of which return value is `0`,
 * then continue (from ``called``'s index + 1) to find the `caller` element of which return value is `1`,
 * returns the element of which index is ``caller``'s index + [offset].
 *
 * Note if stack trace elements of current thread is null,
 * or ``caller``'s index + [offset] is out of bounds, return null.
 */
fun callerStackTrace(
    offset: Int,
    filter: (StackTraceElement, findCalled: Boolean) -> Int
): StackTraceElement? {
    val stackTrace = currentThread().stackTrace
    if (stackTrace.isNullOrEmpty()) {
        return null
    }
    for (i in stackTrace.indices) {
        var filterResult = filter(stackTrace[i], false)
        if (filterResult == 0) {
            for (j in i + 1 until stackTrace.size) {
                filterResult = filter(stackTrace[j], true)
                if (filterResult == 1) {
                    val callerIndex = j + offset
                    if (isIndexInBounds(callerIndex, 0, stackTrace.size)) {
                        return stackTrace[callerIndex]
                    }
                    return null
                }
            }
        }
    }
    return null
}