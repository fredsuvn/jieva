package xyz.srclab.common.lang

import xyz.srclab.common.collect.toImmutableMap
import xyz.srclab.common.lang.Defaults.timestampPattern
import java.lang.reflect.Method
import java.time.Duration

/**
 * Represents `current` runtime, context and environment.
 *
 * @author sunqian
 */
object Current {

    private val localContext: ThreadLocal<MutableMap<Any, Any?>> by lazy {
        ThreadLocal.withInitial { mutableMapOf() }
    }

    /**
     * [milliseconds].
     */
    @JvmStatic
    val millis: Long
        @JvmName("millis") get() {
            return milliseconds()
        }

    /**
     * [nanoseconds].
     */
    @JvmStatic
    val nanos: Long
        @JvmName("nanos") get() {
            return nanoseconds()
        }

    /**
     * With [timestampPattern].
     */
    @JvmStatic
    val timestamp: String
        @JvmName("timestamp") get() {
            return timestamp()
        }

    /**
     * [Thread.currentThread].
     */
    @JvmStatic
    val thread: Thread
        @JvmName("thread") get() {
            return Thread.currentThread()
        }

    /**
     * [ClassLoader] of current thread.
     */
    @JvmStatic
    val classLoader: ClassLoader
        @JvmName("classLoader") get() {
            return thread.contextClassLoader
        }

    /**
     * Sleeps for [millis] and [nanos].
     */
    @JvmStatic
    @JvmOverloads
    fun sleep(millis: Long, nanos: Int = 0) {
        Thread.sleep(millis, nanos)
    }

    /**
     * Sleeps for [duration].
     */
    @JvmStatic
    fun sleep(duration: Duration) {
        sleep(duration.toMillis(), duration.nano)
    }

    /**
     * Context of current runner (maybe thread).
     */
    @JvmStatic
    val context: MutableMap<Any, Any?>
        @JvmName("context") get() {
            return localContext.get()
        }

    /**
     * Gets from [context].
     */
    @JvmStatic
    fun <T : Any> get(key: Any): T {
        return getOrNull(key)!!
    }

    /**
     * Gets or null from [context].
     */
    @JvmStatic
    fun <T : Any> getOrNull(key: Any): T? {
        return context[key].asAny()
    }

    /**
     * Gets or else from [context].
     */
    @JvmStatic
    fun <T : Any> getOrElse(key: Any, value: T): T {
        return context.getOrDefault(key, value).asAny()
    }

    /**
     * Gets or else from [context].
     */
    @JvmStatic
    fun <T : Any> getOrElse(key: Any, supplier: (key: Any) -> T): T {
        return getOrNull(key) ?: supplier(key)
    }

    /**
     * Gets or throw from [context].
     */
    @JvmStatic
    fun <T : Any> getOrThrow(key: Any, supplier: (key: Any) -> Throwable): T {
        return getOrNull(key) ?: throw supplier(key)
    }

    /**
     * Sets into [context].
     */
    @JvmStatic
    fun set(key: Any, value: Any?) {
        context[key] = value
    }

    /**
     * Attach [context], current context as previously is returned
     */
    @JvmStatic
    fun attach(): Map<Any, Any?> {
        return context.toImmutableMap()
    }

    /**
     * Reverse an [attach], restoring the previous [context].
     */
    @JvmStatic
    fun detach(previous: Map<Any, Any?>) {
        context.clear()
        context.putAll(previous)
    }

    /**
     * Clears [context].
     */
    @JvmStatic
    fun clear() {
        context.clear()
    }

    /**
     * Returns last stack frame on [this] class from current runner (thread), or null if failed.
     */
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

    /**
     * Returns last stack frame on [this] method from current runner (thread), or null if failed.
     */
    @JvmStatic
    fun Method.callerFrameOrNull(): StackTraceElement? {
        val calledClassName = this.declaringClass.name
        val calledMethodName = this.name
        return callerFrameOrNull(calledClassName, calledMethodName)
    }

    /**
     * Returns last stack frame on [calledClassName] and [calledMethodName] from current runner (thread),
     * or null if failed.
     */
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