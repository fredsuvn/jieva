package xyz.srclab.common.run

import xyz.srclab.common.base.asTyped
import java.time.Duration
import java.util.concurrent.Callable
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

/**
 * A type of [Scheduler] use [ScheduledExecutorService].
 */
open class ScheduledExecutorServiceScheduler(
    private val scheduledExecutorService: ScheduledExecutorService
) : ExecutorServiceRunner(scheduledExecutorService), Scheduler {

    override fun <V> schedule(delay: Duration, task: Callable<V>): ScheduleWork<V> {
        return DelayScheduling(delay, task)
    }

    override fun schedule(delay: Duration, task: Runnable): ScheduleWork<*> {
        return DelayScheduling<Any?>(delay, task)
    }

    override fun scheduleFixedRate(initialDelay: Duration, period: Duration, task: Runnable): ScheduleWork<*> {
        return FixedRateScheduling<Any?>(initialDelay, period, task)
    }

    override fun scheduleFixedDelay(initialDelay: Duration, period: Duration, task: Runnable): ScheduleWork<*> {
        return FixedDelayScheduling<Any?>(initialDelay, period, task)
    }

    override fun asExecutor(): ScheduledExecutorService = scheduledExecutorService

    private inner class DelayScheduling<V> : AbstractScheduleWork<V> {

        override val future: ScheduledFuture<V>

        constructor(delay: Duration, task: Callable<V>) {
            this.future = scheduledExecutorService.schedule(
                Callable {
                    this.isStart = true
                    this.executionCount++
                    task.call()
                }, delay.toNanos(), TimeUnit.NANOSECONDS
            )
        }

        constructor(delay: Duration, task: Runnable) {
            this.future = scheduledExecutorService.schedule({
                this.isStart = true
                this.executionCount++
                task.run()
            }, delay.toNanos(), TimeUnit.NANOSECONDS).asTyped()
        }
    }

    private inner class FixedRateScheduling<V>(
        initialDelay: Duration, period: Duration, task: Runnable
    ) : AbstractScheduleWork<V>() {

        override val future: ScheduledFuture<V>

        init {
            this.future = scheduledExecutorService.scheduleAtFixedRate(
                {
                    this.isStart = true
                    this.executionCount++
                    task.run()
                }, initialDelay.toNanos(), period.toNanos(), TimeUnit.NANOSECONDS
            ).asTyped()
        }
    }

    private inner class FixedDelayScheduling<V>(
        initialDelay: Duration, period: Duration, task: Runnable
    ) : AbstractScheduleWork<V>() {

        override val future: ScheduledFuture<V>

        init {
            this.future = scheduledExecutorService.scheduleWithFixedDelay(
                {
                    this.isStart = true
                    this.executionCount++
                    task.run()
                }, initialDelay.toNanos(), period.toNanos(), TimeUnit.NANOSECONDS
            ).asTyped()
        }
    }

    private abstract class AbstractScheduleWork<V> : ScheduleWork<V> {
        override var executionCount: Long = 0
        override var isStart: Boolean = false
    }
}