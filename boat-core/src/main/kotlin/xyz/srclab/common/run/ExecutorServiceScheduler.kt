package xyz.srclab.common.run

import xyz.srclab.common.run.RunWork.Companion.toRunWork
import xyz.srclab.common.run.ScheduleWork.Companion.toScheduleWork
import java.time.Duration
import java.util.concurrent.Callable
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

/**
 * [Scheduler] implemented by [ScheduledExecutorService].
 */
open class ExecutorServiceScheduler<T : ScheduledExecutorService>(
    /**
     * Returns the executor service.
     */
    val executorService: T
) : Scheduler {

    override fun <V> schedule(delay: Duration, task: Callable<V>): ScheduleWork<V> {
        return executorService.schedule(task, delay.toNanos(), TimeUnit.NANOSECONDS).toScheduleWork()
    }

    override fun schedule(delay: Duration, task: Runnable): ScheduleWork<*> {
        return executorService.schedule(task, delay.toNanos(), TimeUnit.NANOSECONDS).toScheduleWork()
    }

    override fun scheduleFixedRate(initialDelay: Duration, period: Duration, task: Runnable): ScheduleWork<*> {
        return executorService.scheduleAtFixedRate(
            task,
            initialDelay.toNanos(),
            period.toNanos(),
            TimeUnit.NANOSECONDS
        ).toScheduleWork()
    }

    override fun scheduleFixedDelay(initialDelay: Duration, period: Duration, task: Runnable): ScheduleWork<*> {
        return executorService.scheduleWithFixedDelay(
            task,
            initialDelay.toNanos(),
            period.toNanos(),
            TimeUnit.NANOSECONDS
        ).toScheduleWork()
    }

    override fun <V> submit(task: Callable<V>): RunWork<V> {
        return executorService.submit(task).toRunWork()
    }

    override fun submit(task: Runnable): RunWork<*> {
        return executorService.submit(task).toRunWork()
    }

    override fun run(task: Runnable) {
        return executorService.execute(task)
    }
}