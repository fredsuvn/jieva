package xyz.srclab.common.run

import java.time.Duration
import java.util.concurrent.ExecutorService
import java.util.concurrent.RejectedExecutionException
import java.util.concurrent.SynchronousQueue

interface Runner {

    @Throws(RejectedExecutionException::class)
    fun <V> run(block: () -> V): Running<V>

    companion object {

        private val newThreadRunner: Runner by lazy {
            executorServiceRunnerBuilder()
                .corePoolSize(0)
                .maximumPoolSize(Int.MAX_VALUE)
                .keepAliveTime(Duration.ZERO)
                .workQueue(SynchronousQueue())
                .threadFactory { r -> Thread(r) }
                .build()
        }

        @JvmStatic
        fun currentRunner(): Runner {
            return CurrentRunner
        }

        @JvmStatic
        fun newThreadRunner(): Runner {
            return newThreadRunner
        }

        @JvmStatic
        fun executorServiceRunner(executorService: ExecutorService): Runner {
            return ExecutorServiceRunner(executorService)
        }

        @JvmStatic
        fun executorServiceRunnerBuilder(): ExecutorServiceRunnerBuilder {
            return ExecutorServiceRunnerBuilder()
        }

        @JvmStatic
        fun <V> runCurrent(block: () -> V): Running<V> {
            return currentRunner().run(block)
        }

        @JvmStatic
        fun <V> runNew(block: () -> V): Running<V> {
            return runNewThread(block)
        }

        @JvmStatic
        fun <V> runNewThread(block: () -> V): Running<V> {
            return newThreadRunner().run(block)
        }
    }
}