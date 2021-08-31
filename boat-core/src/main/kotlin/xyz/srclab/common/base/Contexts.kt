@file:JvmName("Contexts")
@file:JvmMultifileClass

package xyz.srclab.common.base

private val threadLocal: ThreadLocal<MutableMap<Any, Any?>> = ThreadLocal.withInitial { HashMap() }

fun currentThread(): Thread {
    return Thread.currentThread()
}

@Throws(NullPointerException::class)
fun <T : Any> getContext(key: Any): T {
    return getContextOrNull(key) ?: throw NullPointerException("getContext($key)")
}

fun <T : Any> getContextOrElse(key: Any, value: T): T {
    return getContextOrNull(key) ?: value
}

fun <T : Any> getContextOrElse(key: Any, supplier: (key: Any) -> T): T {
    return getContextOrNull(key) ?: supplier(key)
}

fun <T : Any> getContextOrThrow(key: Any, supplier: (key: Any) -> Throwable): T {
    return getContextOrNull(key) ?: throw supplier(key)
}

fun <T : Any> getContextOrNull(key: Any): T? {
    return threadLocal.get()[key].asAny()
}

fun setContext(key: Any, value: Any?) {
    threadLocal.get()[key] = value
}

fun getContext(): MutableMap<Any, Any?> {
    return threadLocal.get()
}