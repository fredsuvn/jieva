package xyz.srclab.common.base

import xyz.srclab.common.run.RunContext
import java.lang.reflect.Method
import java.time.Duration
import java.time.LocalDateTime

/**
 * Represents `current` runtime, context and environment.
 *
 * @author sunqian
 */
object Current {

    /**
     * [Thread.currentThread].
     */
    @JvmStatic
    val thread: Thread
        get() {
            return Thread.currentThread()
        }

    /**
     * [ClassLoader] of current thread.
     */
    @JvmStatic
    val classLoader: ClassLoader
        get() {
            return thread.contextClassLoader
        }

    /**
     * Context of current runner (maybe thread).
     */
    @JvmStatic
    val runContext: RunContext by lazy {
        RunContext.current()
    }

    /**
     * Returns current milliseconds.
     */
    @JvmStatic
    fun milliseconds(): Long {
        return System.currentTimeMillis()
    }

    /**
     * Returns current nanoseconds.
     */
    @JvmStatic
    fun nanoseconds(): Long {
        return System.nanoTime()
    }

    /**
     * Returns current timestamp with pattern [timestampPattern].
     */
    @JvmStatic
    fun timestamp(): String {
        return Defaults.timestampFormatter.format(LocalDateTime.now())
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
     * Gets from [runContext].
     */
    @JvmStatic
    fun <T : Any> get(key: Any): T {
        return runContext.get(key)
    }

    /**
     * Gets or null from [runContext].
     */
    @JvmStatic
    fun <T : Any> getOrNull(key: Any): T? {
        return runContext.getOrNull(key)
    }

    /**
     * Gets or else from [runContext].
     */
    @JvmStatic
    fun <T : Any> getOrElse(key: Any, value: T): T {
        return runContext.getOrElse(key, value)
    }

    /**
     * Gets or else from [runContext].
     */
    @JvmStatic
    fun <T : Any> getOrElse(key: Any, supplier: (key: Any) -> T): T {
        return runContext.getOrElse(key, supplier)
    }

    /**
     * Gets or throw from [runContext].
     */
    @JvmStatic
    fun <T : Any> getOrThrow(key: Any, supplier: (key: Any) -> Throwable): T {
        return runContext.getOrThrow(key, supplier)
    }

    /**
     * Sets into [runContext].
     */
    @JvmStatic
    fun set(key: Any, value: Any?) {
        runContext.set(key, value)
    }

    /**
     * Clears [runContext].
     */
    @JvmStatic
    fun clear() {
        runContext.clear()
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