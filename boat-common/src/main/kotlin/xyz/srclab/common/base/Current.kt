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
    fun <T> getOrElse(key: Any, value: T): T {
        return getOrNull(key) ?: value
    }

    @JvmStatic
    fun <T> getOrElse(key: Any, supplier: (key: Any) -> T): T {
        return getOrNull(key) ?: supplier(key)
    }

    @JvmStatic
    fun <T> getOrThrow(key: Any, supplier: (key: Any) -> Throwable): T {
        return getOrNull(key) ?: throw supplier(key)
    }

    @JvmStatic
    fun set(key: Any, value: Any?) {
        context[key] = value
    }

    @JvmStatic
    fun clearContext() {
        context.clear()
    }

    @JvmStatic
    fun Class<*>.callerFrame(): StackTraceElement? {
        //Throwable().stackTrace
        val stackTrace = thread.stackTrace
        if (stackTrace.isNullOrEmpty()) {
            return null
        }
        val calledClassName = this.name
        var calledIndex = 0
        for (i in stackTrace.indices) {
            if (stackTrace[i].className == calledClassName) {
                calledIndex = i
                break
            }
        }
        for (i in calledIndex until stackTrace.size) {
            if (stackTrace[i].className != calledClassName) {
                return stackTrace[i]
            }
        }
        return null
    }
}