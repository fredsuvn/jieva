package xyz.srclab.common.run

import xyz.srclab.common.base.asCallable
import xyz.srclab.common.base.asRunnable
import java.util.concurrent.Callable
import java.util.concurrent.Executor
import java.util.concurrent.RejectedExecutionException

/**
 * Runner is used to run tasks, with thread, coroutine, or others.
 *
 * @see RunWork
 * @see SyncRunner
 * @see AsyncRunner
 * @see ExecutorServiceRunner
 * @see ThreadPoolRunner
 * @see Scheduler
 */
interface Runner {

    /**
     * Runs and returns [RunWork].
     */
    @Throws(RejectedExecutionException::class)
    @JvmSynthetic
    fun <V> submit(task: () -> V): RunWork<V> {
        return submit(task.asCallable())
    }

    /**
     * Runs and returns [RunWork].
     */
    @Throws(RejectedExecutionException::class)
    fun <V> submit(task: Callable<V>): RunWork<V>

    /**
     * Runs and returns [RunWork].
     */
    @Throws(RejectedExecutionException::class)
    fun submit(task: Runnable): RunWork<*>

    /**
     * Runs and returns [RunWork].
     */
    @Throws(RejectedExecutionException::class)
    fun <V> submit(task: RunTask<V>): RunWork<V> {
        task.prepare()
        return submit(task.toCallable())
    }

    /**
     * Runs and no return.
     *
     * @see Executor.execute
     */
    @Throws(RejectedExecutionException::class)
    @JvmSynthetic
    fun run(task: () -> Any?) {
        return run(task.asRunnable())
    }

    /**
     * Runs and no return.
     *
     * @see Executor.execute
     */
    @Throws(RejectedExecutionException::class)
    fun run(task: Runnable)

    /**
     * Runs and no return.
     *
     * @see Executor.execute
     */
    @Throws(RejectedExecutionException::class)
    fun run(task: RunTask<*>) {
        task.prepare()
        return run(task.toRunnable())
    }

    fun asExecutor(): Executor
}