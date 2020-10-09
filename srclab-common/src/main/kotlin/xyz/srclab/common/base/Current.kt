package xyz.srclab.common.base

/**
 * @author sunqian
 */
object Current {

    private val localContext: ThreadLocal<MutableMap<Any, Any?>> by lazy {
        ThreadLocal.withInitial { mutableMapOf() }
    }

    @JvmStatic
    val milliseconds: Long
        @JvmName("milliseconds") get() {
            return System.currentTimeMillis()
        }

    @JvmStatic
    val nanoseconds: Long
        @JvmName("nanoseconds") get() {
            return System.nanoTime()
        }

    @JvmStatic
    val thread: Thread
        @JvmName("thread") get() {
            return Thread.currentThread()
        }

    @JvmStatic
    val classLoader: ClassLoader
        @JvmName("classLoader") get() {
            return thread.contextClassLoader
        }

    @JvmStatic
    val context: MutableMap<Any, Any?>
        @JvmName("context") get() {
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
    fun clearContext() {
        localContext.get().clear()
    }
}