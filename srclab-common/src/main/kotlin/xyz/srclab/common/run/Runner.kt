package xyz.srclab.common.run

import java.time.Duration
import java.util.concurrent.ExecutorService
import java.util.concurrent.RejectedExecutionException
import java.util.concurrent.SynchronousQueue
import java.util.concurrent.ThreadPoolExecutor

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