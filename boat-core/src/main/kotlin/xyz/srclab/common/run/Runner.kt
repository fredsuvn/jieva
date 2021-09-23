package xyz.srclab.common.run

import xyz.srclab.common.base.availableProcessors
import java.util.concurrent.*

/**
 * For run a processing, may based on a thread, a coroutine, or others.
 *
 * @see Running
 * @see SyncRunner
 * @see AsyncRunner
 * @see ExecutorServiceRunner
 * @see ThreadPoolRunner
 * @see Scheduler
 */
interface Runner : Executor {

    /**
     * Run and returns [Running].
     */
    @Throws(RejectedExecutionException::class)
    fun <V> run(task: () -> V): Running<V>

    /**
     * Run and returns [Running].
     */
    @Throws(RejectedExecutionException::class)
    fun run(task: Runnable): Running<*>

    /**
     * Run and no return.
     */
    @Throws(RejectedExecutionException::class)
    fun <V> fastRun(task: () -> V)

    /**
     * Run and no return.
     */
    @Throws(RejectedExecutionException::class)
    fun fastRun(task: Runnable) {
        execute(task)
    }

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
        fun <V> runAsync(task: () -> V): Running<V> {
            return ASYNC_RUNNER.run(task)
        }

        @JvmStatic
        fun runAsync(task: Runnable): Running<*> {
            return ASYNC_RUNNER.run(task)
        }

        @JvmStatic
        fun <V> fastRunSync(task: () -> V) {
            SYNC_RUNNER.fastRun(task)
        }

        @JvmStatic
        fun fastRunSync(task: Runnable) {
            SYNC_RUNNER.fastRun(task)
        }

        @JvmStatic
        fun <V> fastRunAsync(task: () -> V) {
            ASYNC_RUNNER.fastRun(task)
        }

        @JvmStatic
        fun fastRunAsync(task: Runnable) {
            ASYNC_RUNNER.fastRun(task)
        }
    }
}