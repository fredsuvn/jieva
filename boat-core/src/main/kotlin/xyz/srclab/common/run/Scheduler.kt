package xyz.srclab.common.run

import java.time.Duration
import java.util.concurrent.Callable
import java.util.concurrent.RejectedExecutionException
import java.util.concurrent.ScheduledExecutorService

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
        return schedule(delay, Callable { task.run() })
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
        return scheduleFixedRate(initialDelay, period, Runnable { task.run() })
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
        return scheduleFixedDelay(initialDelay, period, Runnable { task.run() })
    }

    companion object {

        /**
         * Returns an [Scheduler] of [this] [ScheduledExecutorService].
         */
        @JvmName("of")
        @JvmStatic
        fun <T : ScheduledExecutorService> T.toScheduler(): Scheduler {
            return ExecutorServiceScheduler(this)
        }
    }
}