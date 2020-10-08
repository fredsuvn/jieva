package xyz.srclab.common.base

/**
 * @author sunqian
 */
object Current {

    private val localContext: ThreadLocal<MutableMap<Any, Any?>> by lazy {
        ThreadLocal.withInitial { mutableMapOf() }
    }

    @JvmStatic
    fun milliseconds(): Long {
        return System.currentTimeMillis()
    }

    @JvmStatic
    fun nanoseconds(): Long {
        return System.nanoTime()
    }

    @JvmStatic
    fun thread(): Thread {
        return Thread.currentThread()
    }

    @JvmStatic
    fun classLoader(): ClassLoader {
        return thread().contextClassLoader
    }

    @JvmStatic
    fun context(): MutableMap<Any, Any?> {
        return localContext.get()
    }

    @JvmStatic
    fun <T : Any> get(key: Any): T {
        return localContext.get()[key].notNull().asAny()
    }

    @JvmStatic
    fun <T : Any> getOrNull(key: Any): T? {
        return localContext.get()[key].notNull().asAny()
    }

    @JvmStatic
    fun set(key: Any, value: Any?) {
        localContext.get()[key] = value
    }

    @JvmStatic
    fun clear() {
        localContext.get().clear()
    }
}