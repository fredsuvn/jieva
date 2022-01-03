package xyz.srclab.common.run

import java.time.Duration
import java.util.concurrent.RejectedExecutionException

/**
 * Scheduler is used to run or schedule tasks, with thread, coroutine, or others.
 *
 * @see Scheduling
 * @see SingleThreadScheduler
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
        task.prepare()
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
        task.prepare()
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
        task.prepare()
        return scheduleFixedDelay(initialDelay, period) { task.run() }
    }
}