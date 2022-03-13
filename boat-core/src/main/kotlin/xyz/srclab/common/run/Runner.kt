package xyz.srclab.common.run

import java.util.concurrent.Callable
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.RejectedExecutionException

/**
 * Runner is used to run tasks, generally used with thread pool.
 *
 * @see RunWork
 * @see SyncRunner
 * @see AsyncRunner
 * @see ExecutorServiceRunner
 * @see ThreadPoolRunner
 * @see Scheduler
 */
interface Runner : Executor {

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
        return submit(Callable { task.run() })
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
        return run(Runnable { task.run() })
    }

    override fun execute(command: Runnable) {
        run(command)
    }

    companion object {

        /**
         * Returns an [Runner] of [this] [ExecutorService].
         */
        @JvmName("of")
        @JvmStatic
        fun <T : ExecutorService> T.toRunner(): Runner {
            return ExecutorServiceRunner(this)
        }
    }
}