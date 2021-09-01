@file:JvmName("Contexts")
@file:JvmMultifileClass

package xyz.srclab.common.base

import xyz.srclab.common.lang.isIndexInBounds

private val threadLocal: ThreadLocal<MutableMap<Any, Any?>> = ThreadLocal.withInitial { HashMap() }

/**
 * Returns current thread.
 */
fun thread(): Thread {
    return Thread.currentThread()
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
 * Returns current context as [MutableMap].
 * Note any operation for the return map will reflect the context map, and vice versa.
 */
@JvmName("asMap")
fun getContextAsMap(): MutableMap<Any, Any?> {
    return threadLocal.get()
}

/**
 * Finds the index of stack trace element of which class name is [className], method name is [methodName],
 * computes  next stack trace element which passes the [filter].
 *
 * Searching will start from first stack trace element specified by [className] and [methodName],
 * if an element's className and methodName both
 */
@JvmStatic
fun nextStackTraceElement(
    find: (StackTraceElement) -> Boolean,
    offset: Int,
    filter: (StackTraceElement) -> Boolean
): StackTraceElement? {
    val stackTrace = Current.thread.stackTrace
    if (stackTrace.isNullOrEmpty()) {
        return null
    }
    var index = 0
    for (i in stackTrace.indices) {
        if (stackTrace[i].className == className && stackTrace[i].methodName == methodName) {
            index = i
            break
        }
    }
    for (i in index + 1 until stackTrace.size) {
        if (stackTrace[i].className != className && stackTrace[i].methodName != methodName) {
            index = i
            break;
        }
    }
    val lastIndex = index + offset
    return if (isIndexInBounds(lastIndex, 0, stackTrace.size)) stackTrace[lastIndex] else null
}