package xyz.srclab.common.run

import java.time.Duration
import java.util.concurrent.RejectedExecutionException
import java.util.concurrent.SynchronousQueue

interface Runner {

    @Throws(RejectedExecutionException::class)
    fun <V> run(block: () -> V): Running<V>

    companion object {

        private val newThreadRunner: Runner by lazy {
            threadPoolRunnerBuilder()
                .corePoolSize(0)
                .maximumPoolSize(Int.MAX_VALUE)
                .keepAliveTime(Duration.ZERO)
                .workQueue(SynchronousQueue())
                .threadFactory { r -> Thread(r) }
                .build()
        }

        @JvmStatic
        fun <V> runWithNewThread(block: () -> V): Running<V> {
            return newThreadRunner.run(block)
        }

        @JvmStatic
        fun threadPoolRunnerBuilder(): ThreadPoolRunnerBuilder {
            return ThreadPoolRunnerBuilder()
        }
    }
}