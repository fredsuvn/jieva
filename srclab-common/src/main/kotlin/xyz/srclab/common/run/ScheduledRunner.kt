package xyz.srclab.common.run

import java.time.Duration
import java.util.concurrent.RejectedExecutionException
import java.util.concurrent.ScheduledExecutorService

interface ScheduledRunner : Runner {

    @Throws(RejectedExecutionException::class)
    fun <V> schedule(block: () -> V, delay: Duration): ScheduledRunning<V>

    @Throws(RejectedExecutionException::class)
    fun <V> scheduleAtFixedRate(block: () -> V, initialDelay: Duration, period: Duration): ScheduledRunning<V>

    @Throws(RejectedExecutionException::class)
    fun <V> scheduleWithFixedDelay(block: () -> V, initialDelay: Duration, period: Duration): ScheduledRunning<V>

    companion object {

        private val newThreadScheduledRunner: ScheduledRunner by lazy {
            scheduledExecutorServiceRunnerBuilder()
                .corePoolSize(0)
                .threadFactory { r -> Thread(r) }
                .build()
        }

        @JvmStatic
        fun newThreadScheduledRunner(): ScheduledRunner {
            return newThreadScheduledRunner
        }

        @JvmStatic
        fun scheduledExecutorServiceRunner(scheduledExecutorService: ScheduledExecutorService): Runner {
            return ScheduledExecutorServiceRunner(scheduledExecutorService)
        }

        @JvmStatic
        fun scheduledExecutorServiceRunnerBuilder(): ScheduledExecutorServiceRunnerBuilder {
            return ScheduledExecutorServiceRunnerBuilder()
        }

        @JvmStatic
        fun <V> scheduleNew(block: () -> V, delay: Duration): ScheduledRunning<V> {
            return scheduleNewThread(block, delay)
        }

        @JvmStatic
        fun <V> scheduleNewAtFixedRate(block: () -> V, initialDelay: Duration, period: Duration): ScheduledRunning<V> {
            return scheduleNewThreadAtFixedRate(block, initialDelay, period)
        }

        @JvmStatic
        fun <V> scheduleNewWithFixedDelay(
            block: () -> V,
            initialDelay: Duration,
            period: Duration
        ): ScheduledRunning<V> {
            return scheduleNewThreadWithFixedDelay(block, initialDelay, period)
        }

        @JvmStatic
        fun <V> scheduleNewThread(block: () -> V, delay: Duration): ScheduledRunning<V> {
            return newThreadScheduledRunner().schedule(block, delay)
        }

        @JvmStatic
        fun <V> scheduleNewThreadAtFixedRate(
            block: () -> V,
            initialDelay: Duration,
            period: Duration
        ): ScheduledRunning<V> {
            return newThreadScheduledRunner().scheduleAtFixedRate(block, initialDelay, period)
        }

        @JvmStatic
        fun <V> scheduleNewThreadWithFixedDelay(
            block: () -> V,
            initialDelay: Duration,
            period: Duration
        ): ScheduledRunning<V> {
            return newThreadScheduledRunner().scheduleAtFixedRate(block, initialDelay, period)
        }
    }
}