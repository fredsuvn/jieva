package xyz.srclab.common.base

import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

/**
 * @author sunqian
 */
object Current {

    private val localContext: ThreadLocal<MutableMap<Any, Any?>> by lazy {
        ThreadLocal.withInitial { mutableMapOf() }
    }

    private val unlimitedExecutor: ExecutorService by lazy {
        Executors.newCachedThreadPool()
    }

    private val singleExecutor: ExecutorService by lazy {
        Executors.newSingleThreadExecutor()
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
    fun <T : Any> get(key: Any): T {
        return As.notNull(Require.notNullElement(localContext.get()[key]))
    }

    @JvmStatic
    fun <T : Any> getOrNull(key: Any): T? {
        return As.nullable(localContext.get()[key])
    }

    @JvmStatic
    fun set(key: Any, value: Any?) {
        localContext.get()[key] = value
    }

    @JvmStatic
    fun clear() {
        localContext.get().clear()
    }

    @JvmStatic
    fun contextMap(): Map<Any, Any?> {
        return localContext.get()
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
    fun run(task: Runnable) {
        unlimitedExecutor.submit(task)
    }

    @JvmStatic
    fun <T> run(task: Callable<T>): Future<T> {
        return unlimitedExecutor.submit(task)
    }

    @JvmStatic
    fun submit(task: Runnable) {
        singleExecutor.submit(task)
    }

    @JvmStatic
    fun <T> submit(task: Callable<T>): Future<T> {
        return singleExecutor.submit(task)
    }
}