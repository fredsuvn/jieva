package xyz.srclab.common.run

import java.time.Duration
import java.util.concurrent.Executors
import java.util.concurrent.RejectedExecutionException
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledThreadPoolExecutor

/**
 * For run a scheduled processing, may based on a thread, a coroutine, or others.
 *
 * @see Scheduling
 * @see ScheduledExecutorServiceScheduler
 * @see ScheduledThreadPoolScheduler
 * @see Runner
 */
interface Scheduler : Runner {

    @Throws(RejectedExecutionException::class)
    fun <V> schedule(delay: Duration, task: () -> V): Scheduling<V>

    @Throws(RejectedExecutionException::class)
    fun schedule(delay: Duration, task: Runnable): Scheduling<*> {
        return schedule(delay) {
            task.run()
            null
        }
    }

    @Throws(RejectedExecutionException::class)
    fun <V> scheduleFixedRate(initialDelay: Duration, period: Duration, task: () -> V): Scheduling<V>

    @Throws(RejectedExecutionException::class)
    fun scheduleFixedRate(initialDelay: Duration, period: Duration, task: Runnable): Scheduling<*> {
        return scheduleFixedRate(initialDelay, period) {
            task.run()
            null
        }
    }

    @Throws(RejectedExecutionException::class)
    fun <V> scheduleFixedDelay(initialDelay: Duration, period: Duration, task: () -> V): Scheduling<V>

    @Throws(RejectedExecutionException::class)
    fun scheduleFixedDelay(initialDelay: Duration, period: Duration, task: Runnable): Scheduling<*> {
        return scheduleFixedDelay(initialDelay, period) {
            task.run()
            null
        }
    }

    companion object {

        @JvmField
        val DEFAULT_THREAD_SCHEDULER: Scheduler = singleThreadScheduler()

        @JvmStatic
        fun singleThreadScheduler(): ScheduledExecutorServiceScheduler {
            return scheduledExecutorServiceScheduler(Executors.newSingleThreadScheduledExecutor())
        }

        @JvmStatic
        fun threadPoolScheduler(corePoolSize: Int): ScheduledExecutorServiceScheduler {
            return scheduledExecutorServiceScheduler(Executors.newScheduledThreadPool(corePoolSize))
        }

        @JvmStatic
        fun scheduledExecutorServiceScheduler(
            scheduledExecutorService: ScheduledExecutorService
        ): ScheduledExecutorServiceScheduler {
            return ScheduledExecutorServiceScheduler(scheduledExecutorService)
        }

        @JvmStatic
        fun scheduledThreadPoolScheduler(
            scheduledThreadPoolExecutor: ScheduledThreadPoolExecutor
        ): ScheduledThreadPoolScheduler {
            return ScheduledThreadPoolScheduler(scheduledThreadPoolExecutor)
        }

        @JvmStatic
        fun scheduledThreadPoolSchedulerBuilder(): ScheduledThreadPoolScheduler.Builder {
            return ScheduledThreadPoolScheduler.Builder()
        }

        @JvmStatic
        fun <V> scheduleDefaultThread(delay: Duration, task: () -> V): Scheduling<V> {
            return DEFAULT_THREAD_SCHEDULER.schedule(delay, task)
        }

        @JvmStatic
        fun <V> scheduleDefaultThreadFixedRate(
            initialDelay: Duration,
            period: Duration,
            task: () -> V,
        ): Scheduling<V> {
            return DEFAULT_THREAD_SCHEDULER.scheduleFixedRate(initialDelay, period, task)
        }

        @JvmStatic
        fun <V> scheduleDefaultThreadFixedDelay(
            initialDelay: Duration,
            period: Duration,
            task: () -> V
        ): Scheduling<V> {
            return DEFAULT_THREAD_SCHEDULER.scheduleFixedDelay(initialDelay, period, task)
        }
    }
}