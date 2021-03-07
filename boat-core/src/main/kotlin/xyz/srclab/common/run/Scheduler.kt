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
    fun <V> scheduleFixedRate(initialDelay: Duration, period: Duration, task: () -> V): Scheduling<V>

    @Throws(RejectedExecutionException::class)
    fun <V> scheduleFixedDelay(initialDelay: Duration, period: Duration, task: () -> V): Scheduling<V>

    companion object {

        @JvmField
        val DEFAULT_THREAD_SCHEDULER: Scheduler = newSingleThreadScheduler()

        @JvmStatic
        fun newSingleThreadScheduler(): ScheduledExecutorServiceScheduler {
            return newScheduledExecutorServiceScheduler(Executors.newSingleThreadScheduledExecutor())
        }

        @JvmStatic
        fun newThreadPoolScheduler(corePoolSize: Int): ScheduledExecutorServiceScheduler {
            return newScheduledExecutorServiceScheduler(Executors.newScheduledThreadPool(corePoolSize))
        }

        @JvmStatic
        fun newScheduledExecutorServiceScheduler(
            scheduledExecutorService: ScheduledExecutorService
        ): ScheduledExecutorServiceScheduler {
            return ScheduledExecutorServiceScheduler(scheduledExecutorService)
        }

        @JvmStatic
        fun newScheduledThreadPoolScheduler(
            scheduledThreadPoolExecutor: ScheduledThreadPoolExecutor
        ): ScheduledThreadPoolScheduler {
            return ScheduledThreadPoolScheduler(scheduledThreadPoolExecutor)
        }

        @JvmStatic
        fun newScheduledThreadPoolSchedulerBuilder(): ScheduledThreadPoolScheduler.Builder {
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

fun <V> scheduleDefaultThread(delay: Duration, task: () -> V): Scheduling<V> {
    return Scheduler.scheduleDefaultThread(delay, task)
}

fun <V> scheduleDefaultThreadFixedRate(initialDelay: Duration, period: Duration, task: () -> V): Scheduling<V> {
    return Scheduler.scheduleDefaultThreadFixedRate(initialDelay, period, task)
}

fun <V> scheduleDefaultThreadFixedDelay(
    initialDelay: Duration,
    period: Duration,
    task: () -> V,
): Scheduling<V> {
    return Scheduler.scheduleDefaultThreadFixedDelay(initialDelay, period, task)
}