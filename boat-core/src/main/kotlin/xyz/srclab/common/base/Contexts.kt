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
fun classLoader(): ClassLoader {
    return currentThread().contextClassLoader
}

@JvmName("get")
@Throws(NullPointerException::class)
fun <T : Any> getContext(key: Any): T {
    return getContextOrNull(key) ?: throw NullPointerException("getContext($key)")
}

@JvmName("getOrElse")
fun <T : Any> getContextOrElse(key: Any, value: T): T {
    return getContextOrNull(key) ?: value
}

@JvmName("getOrElse")
fun <T : Any> getContextOrElse(key: Any, supplier: (key: Any) -> T): T {
    return getContextOrNull(key) ?: supplier(key)
}

@JvmName("getOrThrow")
fun <T : Any> getContextOrThrow(key: Any, supplier: (key: Any) -> Throwable): T {
    return getContextOrNull(key) ?: throw supplier(key)
}

@JvmName("getOrNull")
fun <T : Any> getContextOrNull(key: Any): T? {
    return threadLocal.get()[key].asAny()
}

@JvmName("set")
fun setContext(key: Any, value: Any?) {
    threadLocal.get()[key] = value
}

@JvmName("clear")
fun clearContext() {
    threadLocal.get().clear()
}

/**
 * Adds [context] into current context.
 */
@JvmName("attach")
fun attachContext(context: Map<Any, Any?>) {
    getContextAsMap().putAll(context)
}

/**
 * Returns copy of current context.
 */
@JvmName("detach")
fun detachContext(): Map<Any, Any?> {
    return getContextAsMap().toMap()
}

/**
 * Returns current context as [MutableMap].
 * Note any operation for the return map will reflect the context map, and vice versa.
 */
@JvmName("asMap")
fun getContextAsMap(): MutableMap<Any, Any?> {
    return threadLocal.get()
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