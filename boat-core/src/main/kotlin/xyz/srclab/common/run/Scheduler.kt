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
     * Schedules and returns [Scheduling] with statistics. It is equivalent to:
     *
     * ```
     * schedule(true, delay, task)
     * ```
     */
    @Throws(RejectedExecutionException::class)
    fun <V> schedule(delay: Duration, task: () -> V): Scheduling<V> {
        return schedule(true, delay, task)
    }

    /**
     * Schedules and returns [Scheduling] with statistics. It is equivalent to:
     *
     * ```
     * schedule(true, delay, task)
     * ```
     */
    @Throws(RejectedExecutionException::class)
    fun schedule(delay: Duration, task: Runnable): Scheduling<*> {
        return schedule(true, delay, task)
    }

    /**
     * Schedules and returns [Scheduling] with statistics. It is equivalent to:
     *
     * ```
     * scheduleFixedRate(true, initialDelay, period, task)
     * ```
     */
    @Throws(RejectedExecutionException::class)
    fun <V> scheduleFixedRate(initialDelay: Duration, period: Duration, task: () -> V): Scheduling<V> {
        return scheduleFixedRate(true, initialDelay, period, task)
    }

    /**
     * Schedules and returns [Scheduling] with statistics. It is equivalent to:
     *
     * ```
     * scheduleFixedRate(true, initialDelay, period, task)
     * ```
     */
    @Throws(RejectedExecutionException::class)
    fun scheduleFixedRate(initialDelay: Duration, period: Duration, task: Runnable): Scheduling<*> {
        return scheduleFixedRate(true, initialDelay, period, task)
    }

    /**
     * Schedules and returns [Scheduling] with statistics. It is equivalent to:
     *
     * ```
     * scheduleFixedDelay(true, initialDelay, period, task)
     * ```
     */
    @Throws(RejectedExecutionException::class)
    fun <V> scheduleFixedDelay(initialDelay: Duration, period: Duration, task: () -> V): Scheduling<V> {
        return scheduleFixedDelay(true, initialDelay, period, task)
    }

    /**
     * Schedules and returns [Scheduling] with statistics. It is equivalent to:
     *
     * ```
     * scheduleFixedDelay(true, initialDelay, period, task)
     * ```
     */
    @Throws(RejectedExecutionException::class)
    fun scheduleFixedDelay(initialDelay: Duration, period: Duration, task: Runnable): Scheduling<*> {
        return scheduleFixedDelay(true, initialDelay, period, task)
    }

    /**
     * Schedules and returns [Scheduling].
     *
     * @param statistics specifies whether enable statistics
     */
    @Throws(RejectedExecutionException::class)
    fun <V> schedule(statistics: Boolean, delay: Duration, task: () -> V): Scheduling<V>

    /**
     * Schedules and returns [Scheduling].
     *
     * @param statistics specifies whether enable statistics
     */
    @Throws(RejectedExecutionException::class)
    fun schedule(statistics: Boolean, delay: Duration, task: Runnable): Scheduling<*>

    /**
     * Schedules and returns [Scheduling].
     *
     * @param statistics specifies whether enable statistics
     */
    @Throws(RejectedExecutionException::class)
    fun <V> scheduleFixedRate(
        statistics: Boolean, initialDelay: Duration, period: Duration, task: () -> V
    ): Scheduling<V>

    /**
     * Schedules and returns [Scheduling].
     *
     * @param statistics specifies whether enable statistics
     */
    @Throws(RejectedExecutionException::class)
    fun scheduleFixedRate(statistics: Boolean, initialDelay: Duration, period: Duration, task: Runnable): Scheduling<*>

    /**
     * Schedules and returns [Scheduling].
     *
     * @param statistics specifies whether enable statistics
     */
    @Throws(RejectedExecutionException::class)
    fun <V> scheduleFixedDelay(
        statistics: Boolean,
        initialDelay: Duration,
        period: Duration,
        task: () -> V
    ): Scheduling<V>

    /**
     * Schedules and returns [Scheduling].
     *
     * @param statistics specifies whether enable statistics
     */
    @Throws(RejectedExecutionException::class)
    fun scheduleFixedDelay(
        statistics: Boolean,
        initialDelay: Duration,
        period: Duration,
        task: Runnable
    ): Scheduling<*>

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
        fun <V> scheduleWithSingleThread(statistics: Boolean, delay: Duration, task: () -> V): Scheduling<V> {
            return SingleThreadScheduler.schedule(statistics, delay, task)
        }

        /**
         * Schedules with [SingleThreadScheduler].
         */
        @JvmStatic
        fun <V> scheduleFixedRateWithSingleThread(
            statistics: Boolean,
            initialDelay: Duration,
            period: Duration,
            task: () -> V,
        ): Scheduling<V> {
            return SingleThreadScheduler.scheduleFixedRate(statistics, initialDelay, period, task)
        }

        /**
         * Schedules with [SingleThreadScheduler].
         */
        @JvmStatic
        fun <V> scheduleFixedDelayWithSingleThread(
            statistics: Boolean,
            initialDelay: Duration,
            period: Duration,
            task: () -> V
        ): Scheduling<V> {
            return SingleThreadScheduler.scheduleFixedDelay(statistics, initialDelay, period, task)
        }
    }
}