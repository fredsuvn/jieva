package xyz.srclab.common.base

/**
 * @author sunqian
 */
object Current {

    private val localContext: ThreadLocal<MutableMap<Any, Any?>> by lazy {
        ThreadLocal.withInitial { mutableMapOf() }
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
    fun <T> get(key: Any): T {
        return getOrNull(key)!!
    }

    @JvmStatic
    fun <T> getOrNull(key: Any): T? {
        return context[key].asAny()
    }

    @JvmStatic
    fun set(key: Any, value: Any?) {
        context[key] = value
    }

    @JvmStatic
    fun clearContext() {
        context.clear()
    }
}