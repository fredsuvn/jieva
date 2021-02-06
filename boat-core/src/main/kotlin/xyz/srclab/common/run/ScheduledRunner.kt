package xyz.srclab.common.run

import java.time.Duration
import java.util.concurrent.Executors
import java.util.concurrent.RejectedExecutionException
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledThreadPoolExecutor

interface ScheduledRunner : Runner {

    @Throws(RejectedExecutionException::class)
    fun <V> schedule(delay: Duration, task: () -> V): ScheduledRunning<V>

    @Throws(RejectedExecutionException::class)
    fun <V> scheduleAtFixedRate(initialDelay: Duration, period: Duration, task: () -> V): ScheduledRunning<V>

    @Throws(RejectedExecutionException::class)
    fun <V> scheduleWithFixedDelay(initialDelay: Duration, period: Duration, task: () -> V): ScheduledRunning<V>

    companion object {

        @JvmField
        val NEW_THREAD_RUNNER: NewThreadScheduledRunner = NewThreadScheduledRunner

        @JvmStatic
        fun newSingleThreadRunner(): ScheduledExecutorServiceRunner {
            return newScheduledExecutorServiceRunner(Executors.newSingleThreadScheduledExecutor())
        }

        @JvmStatic
        fun newThreadPoolRunner(corePoolSize: Int): ScheduledExecutorServiceRunner {
            return newScheduledExecutorServiceRunner(Executors.newScheduledThreadPool(corePoolSize))
        }

        @JvmStatic
        fun newScheduledExecutorServiceRunner(
            scheduledExecutorService: ScheduledExecutorService
        ): ScheduledExecutorServiceRunner {
            return ScheduledExecutorServiceRunner(scheduledExecutorService)
        }

        @JvmStatic
        fun newScheduledThreadPoolRunner(
            scheduledThreadPoolExecutor: ScheduledThreadPoolExecutor
        ): ScheduledThreadPoolRunner {
            return ScheduledThreadPoolRunner(scheduledThreadPoolExecutor)
        }

        @JvmStatic
        fun newScheduledThreadPoolRunnerBuilder(): ScheduledThreadPoolRunner.Builder {
            return ScheduledThreadPoolRunner.Builder()
        }

        @JvmStatic
        fun <V> scheduleNewThread(delay: Duration, task: () -> V): ScheduledRunning<V> {
            return NEW_THREAD_RUNNER.schedule(delay, task)
        }

        @JvmStatic
        fun <V> scheduleNewThreadAtFixedRate(
            initialDelay: Duration,
            period: Duration,
            task: () -> V,
        ): ScheduledRunning<V> {
            return NEW_THREAD_RUNNER.scheduleAtFixedRate(initialDelay, period, task)
        }

        @JvmStatic
        fun <V> scheduleNewThreadWithFixedDelay(
            initialDelay: Duration,
            period: Duration,
            task: () -> V
        ): ScheduledRunning<V> {
            return NEW_THREAD_RUNNER.scheduleWithFixedDelay(initialDelay, period, task)
        }
    }
}

fun <V> scheduleNewThread(delay: Duration, task: () -> V): ScheduledRunning<V> {
    return ScheduledRunner.scheduleNewThread(delay, task)
}

fun <V> scheduleNewThreadAtFixedRate(initialDelay: Duration, period: Duration, task: () -> V): ScheduledRunning<V> {
    return ScheduledRunner.scheduleNewThreadAtFixedRate(initialDelay, period, task)
}

fun <V> scheduleNewThreadWithFixedDelay(
    initialDelay: Duration,
    period: Duration,
    task: () -> V,
): ScheduledRunning<V> {
    return ScheduledRunner.scheduleNewThreadWithFixedDelay(initialDelay, period, task)
}