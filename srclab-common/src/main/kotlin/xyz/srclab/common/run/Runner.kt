package xyz.srclab.common.run

import xyz.srclab.common.base.Environment
import java.time.Duration
import java.util.concurrent.*

interface Runner {

    @Throws(RejectedExecutionException::class)
    fun <V> run(task: () -> V): Running<V>

    companion object {

        private val AsyncRunner: Runner by lazy {
            ThreadPoolRunner.Builder()
                .corePoolSize(0)
                .maximumPoolSize(Int.MAX_VALUE)
                .keepAliveTime(Duration.ZERO)
                .workQueue(SynchronousQueue())
                .threadFactory { r -> Thread(r) }
                .build()
        }

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
    }
}

fun <V> runSync(task: () -> V): Running<V> {
    return Runner.syncRunner().run(task)
}

fun <V> runAsync(task: () -> V): Running<V> {
    return Runner.asyncRunner().run(task)
}
