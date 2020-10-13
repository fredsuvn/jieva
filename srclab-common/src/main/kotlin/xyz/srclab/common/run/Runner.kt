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

        @JvmStatic
        fun syncRunner(): Runner {
            return SyncRunner
        }

        @JvmStatic
        fun asyncRunner(): Runner {
            return AsyncRunner
        }

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

        @JvmOverloads
        @JvmStatic
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
            return syncRunner().run(task)
        }

        @JvmStatic
        fun <V> runAsync(task: () -> V): Running<V> {
            return asyncRunner().run(task)
        }
    }
}

fun <V> (() -> V).runSync(): Running<V> {
    return Runner.runSync(this)
}

fun <V> (() -> V).runAsync(): Running<V> {
    return Runner.runAsync(this)
}