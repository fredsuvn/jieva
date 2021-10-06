package xyz.srclab.common.run

import xyz.srclab.common.base.availableProcessors
import java.util.concurrent.*

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
        task.initialize()
        return run { task.run() }
    }

    /**
     * Runs and no return.
     *
     * @see Executor.execute
     */
    @Throws(RejectedExecutionException::class)
    fun <V> execute(task: () -> V)

    /**
     * Runs and no return.
     *
     * @see Executor.execute
     */
    @Throws(RejectedExecutionException::class)
    fun execute(task: Runnable)

    fun asExecutor(): Executor

    companion object {

        @JvmField
        val SYNC_RUNNER: SyncRunner = SyncRunner

        @JvmField
        val ASYNC_RUNNER: AsyncRunner = AsyncRunner

        @JvmStatic
        fun singleThreadRunner(): ExecutorServiceRunner {
            return executorServiceRunner(Executors.newSingleThreadExecutor())
        }

        @JvmStatic
        fun cachedThreadPoolRunner(): ExecutorServiceRunner {
            return executorServiceRunner(Executors.newCachedThreadPool())
        }

        @JvmStatic
        fun fixedThreadPoolRunner(threadNumber: Int): ExecutorServiceRunner {
            return executorServiceRunner(Executors.newFixedThreadPool(threadNumber))
        }

        @JvmStatic
        @JvmOverloads
        fun workStealingPool(parallelism: Int = availableProcessors()): ExecutorServiceRunner {
            return executorServiceRunner(Executors.newWorkStealingPool(parallelism))
        }

        @JvmStatic
        fun executorServiceRunner(executorService: ExecutorService): ExecutorServiceRunner {
            return ExecutorServiceRunner(executorService)
        }

        @JvmStatic
        fun threadPoolRunner(threadPoolExecutor: ThreadPoolExecutor): ThreadPoolRunner {
            return ThreadPoolRunner(threadPoolExecutor)
        }

        @JvmStatic
        fun threadPoolRunnerBuilder(): ThreadPoolRunner.Builder {
            return ThreadPoolRunner.Builder()
        }

        @JvmStatic
        fun <V> runSync(task: () -> V): Running<V> {
            return SYNC_RUNNER.run(task)
        }

        @JvmStatic
        fun runSync(task: Runnable): Running<*> {
            return SYNC_RUNNER.run(task)
        }

        @JvmStatic
        fun <V> runSync(task: RunTask<V>): Running<V> {
            return SYNC_RUNNER.run(task)
        }

        @JvmStatic
        fun <V> runAsync(task: () -> V): Running<V> {
            return ASYNC_RUNNER.run(task)
        }

        @JvmStatic
        fun runAsync(task: Runnable): Running<*> {
            return ASYNC_RUNNER.run(task)
        }

        @JvmStatic
        fun <V> runAsync(task: RunTask<V>): Running<V> {
            return ASYNC_RUNNER.run(task)
        }

        @JvmStatic
        fun <V> executeSync(task: () -> V) {
            SYNC_RUNNER.execute(task)
        }

        @JvmStatic
        fun executeSync(task: Runnable) {
            SYNC_RUNNER.execute(task)
        }

        @JvmStatic
        fun <V> executeAsync(task: () -> V) {
            ASYNC_RUNNER.execute(task)
        }

        @JvmStatic
        fun executeAsync(task: Runnable) {
            ASYNC_RUNNER.execute(task)
        }
    }
}