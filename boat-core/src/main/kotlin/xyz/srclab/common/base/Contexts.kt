@file:JvmName("Contexts")
@file:JvmMultifileClass

package xyz.srclab.common.base

import java.time.Duration

private val threadLocal: ThreadLocal<MutableMap<Any, Any?>> = ThreadLocal.withInitial { HashMap() }

/**
 * Returns current thread.
 */
fun currentThread(): Thread {
    return Thread.currentThread()
}

/**
 * [ClassLoader] of current thread.
 */
fun contextClassLoader(): ClassLoader {
    return currentThread().contextClassLoader
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
    return threadLocal.get()[key].asAny()
}

@JvmName("setProperty")
fun setContextProperty(key: Any, value: Any?) {
    threadLocal.get()[key] = value
}

/**
 * Returns current context as [MutableMap].
 * Note any operation for the return map will reflect the context map, and vice versa.
 */
@JvmName("getPropertiesAsMap")
fun getContextPropertiesAsMap(): MutableMap<Any, Any?> {
    return threadLocal.get()
}

@JvmName("clearProperties")
fun clearContextProperties() {
    threadLocal.get().clear()
}

/**
 * Adds [context] into current context.
 */
@JvmName("attachProperties")
fun attachContextProperties(context: Map<Any, Any?>) {
    getContextPropertiesAsMap().putAll(context)
}

/**
 * Returns copy of current context.
 */
@JvmName("detachProperties")
fun detachContextProperties(): Map<Any, Any?> {
    return getContextPropertiesAsMap().toMap()
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
 * Try to find caller stack trace element.
 *
 * This function calls [filter] for each stack trace element of current thread,
 * first try to find the `called` element of which return value is `0`,
 * then continue (from ``called``'s index + 1) to find the `caller` element of which return value is `1`,
 * returns the element of which index is ``caller``'s index + [offset].
 *
 * Note if stack trace elements of current thread is null,
 * or ``caller``'s index + [offset] is out of bounds, return null.
 */
@JvmOverloads
fun callerStackTrace(
    offset: Int = 0,
    filter: (StackTraceElement, findCalled: Boolean) -> Int
): StackTraceElement? {
    val stackTrace = currentThread().stackTrace
    if (stackTrace.isNullOrEmpty()) {
        return null
    }
    for (i in stackTrace.indices) {
        val ir = filter(stackTrace[i], false)
        if (ir == 0) {
            for (j in i + 1 until stackTrace.size) {
                val jr = filter(stackTrace[j], true)
                if (jr == 1) {
                    val r = jr + offset
                    if (isIndexInBounds(r, 0, stackTrace.size)) {
                        return stackTrace[r]
                    }
                    return null
                }
            }
        }
    }
    return null
}