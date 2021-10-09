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
 * [ClassLoader] of current context.
 */
fun currentClassLoader(): ClassLoader {
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