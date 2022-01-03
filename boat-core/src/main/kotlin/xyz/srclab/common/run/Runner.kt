package xyz.srclab.common.run

import java.util.concurrent.Executor
import java.util.concurrent.RejectedExecutionException

/**
 * Runner is used to run tasks, with thread, coroutine, or others.
 *
 * @see Running
 * @see SyncRunner
 * @see AsyncRunner
 * @see ExecutorServiceRunner
 * @see ThreadPoolRunner
 * @see Scheduler
 */
interface Runner {

    /**
     * Runs and returns [Running].
     */
    @Throws(RejectedExecutionException::class)
    fun <V> run(task: () -> V): Running<V>

    /**
     * Runs and returns [Running].
     */
    @Throws(RejectedExecutionException::class)
    fun run(task: Runnable): Running<*>

    /**
     * Runs and returns [Running].
     */
    @Throws(RejectedExecutionException::class)
    fun <V> run(task: RunTask<V>): Running<V> {
        task.prepare()
        return run { task.run() }
    }

    /**
     * Runs and no return.
     *
     * @see Executor.execute
     */
    @Throws(RejectedExecutionException::class)
    fun execute(task: () -> Any?)

    /**
     * Runs and no return.
     *
     * @see Executor.execute
     */
    @Throws(RejectedExecutionException::class)
    fun execute(task: Runnable)

    /**
     * Runs and no return.
     *
     * @see Executor.execute
     */
    @Throws(RejectedExecutionException::class)
    fun execute(task: RunTask<*>) {
        task.prepare()
        execute { task.run() }
    }

    fun asExecutor(): Executor
}