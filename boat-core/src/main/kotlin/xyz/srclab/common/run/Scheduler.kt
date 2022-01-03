package xyz.srclab.common.run

import xyz.srclab.common.base.toCallable
import xyz.srclab.common.base.toRunnable
import java.time.Duration
import java.util.concurrent.Callable
import java.util.concurrent.RejectedExecutionException

/**
 * Scheduler is used to run or schedule tasks, with thread, coroutine, or others.
 *
 * @see ScheduleWork
 * @see SingleThreadScheduler
 * @see ScheduledExecutorServiceScheduler
 * @see ScheduledThreadPoolScheduler
 * @see Runner
 */
interface Scheduler : Runner {

    /**
     * Schedules and returns [ScheduleWork].
     */
    @Throws(RejectedExecutionException::class)
    @JvmSynthetic
    fun <V> schedule(delay: Duration, task: () -> V): ScheduleWork<V> {
        return schedule(delay, task.toCallable())
    }

    /**
     * Schedules and returns [ScheduleWork].
     */
    @Throws(RejectedExecutionException::class)
    fun <V> schedule(delay: Duration, task: Callable<V>): ScheduleWork<V>

    /**
     * Schedules and returns [ScheduleWork].
     */
    @Throws(RejectedExecutionException::class)
    fun schedule(delay: Duration, task: Runnable): ScheduleWork<*>

    /**
     * Schedules and returns [ScheduleWork].
     */
    @Throws(RejectedExecutionException::class)
    fun <V> schedule(delay: Duration, task: RunTask<V>): ScheduleWork<V> {
        task.prepare()
        return schedule(delay, task.toCallable())
    }

    /**
     * Schedules and returns [ScheduleWork].
     */
    @Throws(RejectedExecutionException::class)
    @JvmSynthetic
    fun scheduleFixedRate(initialDelay: Duration, period: Duration, task: () -> Any?): ScheduleWork<*> {
        return scheduleFixedRate(initialDelay, period, task.toRunnable())
    }

    /**
     * Schedules and returns [ScheduleWork].
     */
    @Throws(RejectedExecutionException::class)
    fun scheduleFixedRate(initialDelay: Duration, period: Duration, task: Runnable): ScheduleWork<*>

    /**
     * Schedules and returns [ScheduleWork].
     */
    @Throws(RejectedExecutionException::class)
    fun scheduleFixedRate(initialDelay: Duration, period: Duration, task: RunTask<*>): ScheduleWork<*> {
        task.prepare()
        return scheduleFixedRate(initialDelay, period, task.toRunnable())
    }

    /**
     * Schedules and returns [ScheduleWork].
     */
    @Throws(RejectedExecutionException::class)
    @JvmSynthetic
    fun scheduleFixedDelay(initialDelay: Duration, period: Duration, task: () -> Any?): ScheduleWork<*> {
        return scheduleFixedDelay(initialDelay, period, task.toRunnable())
    }

    /**
     * Schedules and returns [ScheduleWork].
     */
    @Throws(RejectedExecutionException::class)
    fun scheduleFixedDelay(initialDelay: Duration, period: Duration, task: Runnable): ScheduleWork<*>

    /**
     * Schedules and returns [ScheduleWork].
     */
    @Throws(RejectedExecutionException::class)
    fun scheduleFixedDelay(initialDelay: Duration, period: Duration, task: RunTask<*>): ScheduleWork<*> {
        task.prepare()
        return scheduleFixedDelay(initialDelay, period, task.toRunnable())
    }
}