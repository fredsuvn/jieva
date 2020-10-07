package xyz.srclab.common.run

import java.time.Duration
import java.util.concurrent.RejectedExecutionException

interface ScheduledRunner : Runner {

    @Throws(RejectedExecutionException::class)
    fun <V> schedule(block: () -> V, delay: Duration): ScheduledRunning<V>

    @Throws(RejectedExecutionException::class)
    fun <V> scheduleAtFixedRate(block: () -> V, initialDelay: Duration, period: Duration): ScheduledRunning<V>

    @Throws(RejectedExecutionException::class)
    fun <V> scheduleWithFixedDelay(block: () -> V, initialDelay: Duration, period: Duration): ScheduledRunning<V>

    companion object {

        private val newThreadScheduledRunner: ScheduledRunner by lazy {
            scheduledThreadPoolRunnerBuilder()
                .corePoolSize(0)
                .threadFactory { r -> Thread(r) }
                .build()
        }

        @JvmStatic
        fun <V> scheduleWithNewThread(block: () -> V, delay: Duration): ScheduledRunning<V> {
            return newThreadScheduledRunner.schedule(block, delay)
        }

        @JvmStatic
        fun <V> scheduleAtFixedRateWithNewThread(
            block: () -> V,
            initialDelay: Duration,
            period: Duration
        ): ScheduledRunning<V> {
            return newThreadScheduledRunner.scheduleAtFixedRate(block, initialDelay, period)
        }

        @JvmStatic
        fun <V> scheduleWithFixedDelayWithNewThread(
            block: () -> V,
            initialDelay: Duration,
            period: Duration
        ): ScheduledRunning<V> {
            return newThreadScheduledRunner.scheduleAtFixedRate(block, initialDelay, period)
        }

        @JvmStatic
        fun scheduledThreadPoolRunnerBuilder(): ScheduledThreadPoolRunnerBuilder {
            return ScheduledThreadPoolRunnerBuilder()
        }
    }
}