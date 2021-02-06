package xyz.srclab.common.run

import xyz.srclab.common.base.Environment
import java.util.concurrent.*

interface Runner : Executor {

    @Throws(RejectedExecutionException::class)
    fun <V> run(task: () -> V): Running<V>

    companion object {

        @JvmField
        val SYNC_RUNNER: SyncRunner = SyncRunner

        @JvmField
        val ASYNC_RUNNER: AsyncRunner = AsyncRunner

        @JvmStatic
        fun newSingleThreadRunner(): ExecutorServiceRunner {
            return newExecutorServiceRunner(Executors.newSingleThreadExecutor())
        }

        @JvmStatic
        fun newCachedThreadPoolRunner(): ExecutorServiceRunner {
            return newExecutorServiceRunner(Executors.newCachedThreadPool())
        }

        @JvmStatic
        fun newFixedThreadPoolRunner(threadNumber: Int): ExecutorServiceRunner {
            return newExecutorServiceRunner(Executors.newFixedThreadPool(threadNumber))
        }

        @JvmStatic
        @JvmOverloads
        fun newWorkStealingPool(parallelism: Int = Environment.availableProcessors): ExecutorServiceRunner {
            return newExecutorServiceRunner(Executors.newWorkStealingPool(parallelism))
        }

        @JvmStatic
        fun newExecutorServiceRunner(executorService: ExecutorService): ExecutorServiceRunner {
            return ExecutorServiceRunner(executorService)
        }

        @JvmStatic
        fun newThreadPoolRunner(threadPoolExecutor: ThreadPoolExecutor): ThreadPoolRunner {
            return ThreadPoolRunner(threadPoolExecutor)
        }

        @JvmStatic
        fun newThreadPoolRunnerBuilder(): ThreadPoolRunner.Builder {
            return ThreadPoolRunner.Builder()
        }

        @JvmStatic
        fun <V> runSync(task: () -> V): Running<V> {
            return SYNC_RUNNER.run(task)
        }

        @JvmStatic
        fun <V> runAsync(task: () -> V): Running<V> {
            return ASYNC_RUNNER.run(task)
        }
    }
}

fun <V> runSync(task: () -> V): Running<V> {
    return Runner.runSync(task)
}

fun <V> runAsync(task: () -> V): Running<V> {
    return Runner.runAsync(task)
}