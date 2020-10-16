package xyz.srclab.common.run

import xyz.srclab.common.base.Environment
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.RejectedExecutionException
import java.util.concurrent.ThreadPoolExecutor

interface Runner {

    @Throws(RejectedExecutionException::class)
    fun <V> run(task: () -> V): Running<V>

    companion object {

        @JvmField
        val SYNC_RUNNER: Runner = SyncRunner

        @JvmField
        val ASYNC_RUNNER: Runner = AsyncRunner

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
        fun workStealingPool(parallelism: Int = Environment.availableProcessors): ExecutorServiceRunner {
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
        fun <V> runAsync(task: () -> V): Running<V> {
            return ASYNC_RUNNER.run(task)
        }
    }
}

fun <V> runSync(task: () -> V): Running<V> {
    return Runner.SYNC_RUNNER.run(task)
}

fun <V> runAsync(task: () -> V): Running<V> {
    return Runner.ASYNC_RUNNER.run(task)
}