package xyz.srclab.common.run

import java.util.concurrent.Callable

/**
 * Task to run by [Runner].
 *
 * [run] method is run body of the task, it will be run by specified [Runner].
 * [prepare] method is used to do prepared works in current thread and context.
 */
interface RunTask<V> {

    /**
     * Prepared works before running, will be called in current thread and context
     */
    fun prepare()

    /**
     * Run the task, will be called by specified [Runner].
     */
    fun run(): V
}

fun <V> RunTask<V>.toCallable(): Callable<V> {
    return Callable { this.run() }
}

fun RunTask<*>.toRunnable(): Runnable {
    return Runnable { this.run() }
}