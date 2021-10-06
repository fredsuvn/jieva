package xyz.srclab.common.run

import xyz.srclab.common.base.asAny
import java.time.Duration
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

/**
 * A type of [Scheduler] use [ScheduledExecutorService].
 */
open class ScheduledExecutorServiceScheduler(
    private val scheduledExecutorService: ScheduledExecutorService
) : ExecutorServiceRunner(scheduledExecutorService), Scheduler {

    override fun <V> schedule(delay: Duration, task: () -> V): Scheduling<V> {
        return DelayScheduling(delay, task)
    }

    override fun schedule(delay: Duration, task: Runnable): Scheduling<*> {
        return DelayScheduling<Any?>(delay, task)
    }

    override fun <V> scheduleFixedRate(initialDelay: Duration, period: Duration, task: () -> V): Scheduling<V> {
        return FixedRateScheduling(initialDelay, period, task)
    }

    override fun scheduleFixedRate(initialDelay: Duration, period: Duration, task: Runnable): Scheduling<*> {
        return FixedRateScheduling<Any?>(initialDelay, period, task)
    }

    override fun <V> scheduleFixedDelay(initialDelay: Duration, period: Duration, task: () -> V): Scheduling<V> {
        return FixedDelayScheduling(initialDelay, period, task)
    }

    override fun scheduleFixedDelay(initialDelay: Duration, period: Duration, task: Runnable): Scheduling<*> {
        return FixedDelayScheduling<Any?>(initialDelay, period, task)
    }

    override fun asExecutor(): ScheduledExecutorService = scheduledExecutorService

    private inner class DelayScheduling<V> : AbstractScheduling<V> {

        override val future: ScheduledFuture<V>

        constructor(delay: Duration, task: () -> V) {
            this.future = scheduledExecutorService.schedule(task, delay.toNanos(), TimeUnit.NANOSECONDS)
        }

        constructor(delay: Duration, task: Runnable) {
            this.future = scheduledExecutorService.schedule(task, delay.toNanos(), TimeUnit.NANOSECONDS).asAny()
        }
    }

    private inner class FixedRateScheduling<V> : AbstractScheduling<V> {

        override val future: ScheduledFuture<V>

        constructor(initialDelay: Duration, period: Duration, task: () -> V) {
            this.future = scheduledExecutorService.scheduleAtFixedRate(
                task.toRunnable(), initialDelay.toNanos(), period.toNanos(), TimeUnit.NANOSECONDS
            ).asAny()
        }

        constructor(initialDelay: Duration, period: Duration, task: Runnable) {
            this.future = scheduledExecutorService.scheduleAtFixedRate(
                task, initialDelay.toNanos(), period.toNanos(), TimeUnit.NANOSECONDS
            ).asAny()
        }
    }

    private inner class FixedDelayScheduling<V> : AbstractScheduling<V> {

        override val future: ScheduledFuture<V>

        constructor(initialDelay: Duration, period: Duration, task: () -> V) {
            this.future = scheduledExecutorService.scheduleWithFixedDelay(
                task.toRunnable(), initialDelay.toNanos(), period.toNanos(), TimeUnit.NANOSECONDS
            ).asAny()
        }

        constructor(initialDelay: Duration, period: Duration, task: Runnable) {
            this.future = scheduledExecutorService.scheduleWithFixedDelay(
                task, initialDelay.toNanos(), period.toNanos(), TimeUnit.NANOSECONDS
            ).asAny()
        }
    }

    private abstract class AbstractScheduling<V> : Scheduling<V> {

        protected abstract val future: ScheduledFuture<V>

        override var executionCount: Long = 0
        override var isStart: Boolean = true

        override fun asFuture(): ScheduledFuture<V> {
            return future
        }
    }
}