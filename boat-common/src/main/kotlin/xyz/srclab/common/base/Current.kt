package xyz.srclab.common.base

import java.lang.reflect.Method

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
        return context.getOrDefault(key, value).asAny()
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
    fun clear() {
        context.clear()
    }

    @JvmStatic
    fun Class<*>.callerFrameOrNull(): StackTraceElement? {
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
        for (i in calledIndex + 1 until stackTrace.size) {
            if (stackTrace[i].className != calledClassName) {
                return stackTrace[i]
            }
        }
        return null
    }

    @JvmStatic
    fun Method.callerFrameOrNull(): StackTraceElement? {
        val calledClassName = this.declaringClass.name
        val calledMethodName = this.name
        return callerFrameOrNull(calledClassName, calledMethodName)
    }

    @JvmStatic
    fun callerFrameOrNull(calledClassName: String, calledMethodName: String): StackTraceElement? {
        val stackTrace = thread.stackTrace
        if (stackTrace.isNullOrEmpty()) {
            return null
        }
        var calledIndex = 0
        for (i in stackTrace.indices) {
            if (stackTrace[i].className == calledClassName && stackTrace[i].methodName == calledMethodName) {
                calledIndex = i
                break
            }
        }
        for (i in calledIndex + 1 until stackTrace.size) {
            if (stackTrace[i].className != calledClassName && stackTrace[i].methodName != calledMethodName) {
                return stackTrace[i]
            }
        }
        return null
    }
}