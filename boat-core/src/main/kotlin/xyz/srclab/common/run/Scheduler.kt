package xyz.srclab.common.run

import java.time.Duration
import java.util.concurrent.Executors
import java.util.concurrent.RejectedExecutionException
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledThreadPoolExecutor

/**
 * Scheduler is used to run or schedule tasks, with thread, coroutine, or others.
 *
 * @see Scheduling
 * @see ScheduledExecutorServiceScheduler
 * @see ScheduledThreadPoolScheduler
 * @see Runner
 */
interface Scheduler : Runner {

    /**
     * Schedules and returns [Scheduling].
     */
    @Throws(RejectedExecutionException::class)
    fun <V> schedule(delay: Duration, task: () -> V): Scheduling<V>

    /**
     * Schedules and returns [Scheduling].
     */
    @Throws(RejectedExecutionException::class)
    fun schedule(delay: Duration, task: Runnable): Scheduling<*>

    /**
     * Schedules and returns [Scheduling].
     */
    @Throws(RejectedExecutionException::class)
    fun <V> schedule(delay: Duration, task: RunTask<V>): Scheduling<V> {
        task.initialize()
        return schedule(delay) { task.run() }
    }

    /**
     * Schedules and returns [Scheduling].
     */
    @Throws(RejectedExecutionException::class)
    fun <V> scheduleFixedRate(initialDelay: Duration, period: Duration, task: () -> V): Scheduling<V>

    /**
     * Schedules and returns [Scheduling].
     */
    @Throws(RejectedExecutionException::class)
    fun scheduleFixedRate(initialDelay: Duration, period: Duration, task: Runnable): Scheduling<*>

    /**
     * Schedules and returns [Scheduling].
     */
    @Throws(RejectedExecutionException::class)
    fun <V> scheduleFixedRate(initialDelay: Duration, period: Duration, task: RunTask<V>): Scheduling<V> {
        task.initialize()
        return scheduleFixedRate(initialDelay, period) { task.run() }
    }

    /**
     * Schedules and returns [Scheduling].
     */
    @Throws(RejectedExecutionException::class)
    fun <V> scheduleFixedDelay(initialDelay: Duration, period: Duration, task: () -> V): Scheduling<V>

    /**
     * Schedules and returns [Scheduling].
     */
    @Throws(RejectedExecutionException::class)
    fun scheduleFixedDelay(initialDelay: Duration, period: Duration, task: Runnable): Scheduling<*>

    /**
     * Schedules and returns [Scheduling].
     */
    @Throws(RejectedExecutionException::class)
    fun <V> scheduleFixedDelay(initialDelay: Duration, period: Duration, task: RunTask<V>): Scheduling<V> {
        task.initialize()
        return scheduleFixedDelay(initialDelay, period) { task.run() }
    }

    companion object {

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

        /**
         * Schedules with [SingleThreadScheduler].
         */
        @JvmStatic
        fun <V> scheduleWithSingleThread(delay: Duration, task: () -> V): Scheduling<V> {
            return SingleThreadScheduler.schedule(delay, task)
        }

        /**
         * Schedules with [SingleThreadScheduler].
         */
        @JvmStatic
        fun scheduleWithSingleThread(delay: Duration, task: Runnable): Scheduling<*> {
            return SingleThreadScheduler.schedule(delay, task)
        }

        /**
         * Schedules with [SingleThreadScheduler].
         */
        @JvmStatic
        fun <V> scheduleWithSingleThread(delay: Duration, task: RunTask<V>): Scheduling<V> {
            return SingleThreadScheduler.schedule(delay, task)
        }

        /**
         * Schedules with [SingleThreadScheduler].
         */
        @JvmStatic
        fun <V> scheduleFixedRateWithSingleThread(
            initialDelay: Duration,
            period: Duration,
            task: () -> V,
        ): Scheduling<V> {
            return SingleThreadScheduler.scheduleFixedRate(initialDelay, period, task)
        }

        /**
         * Schedules with [SingleThreadScheduler].
         */
        @JvmStatic
        fun scheduleFixedRateWithSingleThread(
            initialDelay: Duration,
            period: Duration,
            task: Runnable,
        ): Scheduling<*> {
            return SingleThreadScheduler.scheduleFixedRate(initialDelay, period, task)
        }

        /**
         * Schedules with [SingleThreadScheduler].
         */
        @JvmStatic
        fun <V> scheduleFixedRateWithSingleThread(
            initialDelay: Duration,
            period: Duration,
            task: RunTask<V>,
        ): Scheduling<V> {
            return SingleThreadScheduler.scheduleFixedRate(initialDelay, period, task)
        }

        /**
         * Schedules with [SingleThreadScheduler].
         */
        @JvmStatic
        fun <V> scheduleFixedDelayWithSingleThread(
            initialDelay: Duration,
            period: Duration,
            task: () -> V
        ): Scheduling<V> {
            return SingleThreadScheduler.scheduleFixedDelay(initialDelay, period, task)
        }

        /**
         * Schedules with [SingleThreadScheduler].
         */
        @JvmStatic
        fun scheduleFixedDelayWithSingleThread(
            initialDelay: Duration,
            period: Duration,
            task: Runnable
        ): Scheduling<*> {
            return SingleThreadScheduler.scheduleFixedDelay(initialDelay, period, task)
        }

        /**
         * Schedules with [SingleThreadScheduler].
         */
        @JvmStatic
        fun <V> scheduleFixedDelayWithSingleThread(
            initialDelay: Duration,
            period: Duration,
            task: RunTask<V>
        ): Scheduling<V> {
            return SingleThreadScheduler.scheduleFixedDelay(initialDelay, period, task)
        }
    }
}