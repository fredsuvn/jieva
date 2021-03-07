package xyz.srclab.common.run

import xyz.srclab.common.base.asAny
import xyz.srclab.common.base.asNotNull
import xyz.srclab.common.base.checkState
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.*

/**
 * A type of [Scheduler] use [ScheduledExecutorService].
 */
open class ScheduledExecutorServiceScheduler(
    private val scheduledExecutorService: ScheduledExecutorService
) : ExecutorServiceRunner(scheduledExecutorService), Scheduler {

    override fun <V> schedule(delay: Duration, task: () -> V): Scheduling<V> {
        return ScheduledExecutorServiceScheduling(delay, task)
    }

    override fun <V> scheduleFixedRate(
        initialDelay: Duration,
        period: Duration,
        task: () -> V
    ): Scheduling<V> {
        return FixedRateRepeatableScheduledExecutorServiceScheduling(
            initialDelay,
            period,
            task
        )
    }

    override fun <V> scheduleFixedDelay(
        initialDelay: Duration,
        period: Duration,
        task: () -> V,
    ): Scheduling<V> {
        return FixedDelayRepeatableScheduledExecutorServiceScheduling(
            initialDelay,
            period,
            task
        )
    }

    private abstract inner class AbstractScheduledExecutorServiceScheduling<V> : Scheduling<V> {

        abstract val schedulingTask: SchedulingTask<V>
        abstract val scheduledFuture: ScheduledFuture<V>

        override val isStart: Boolean
            get() {
                return schedulingTask.startTime !== null
            }

        override val isEnd: Boolean
            get() {
                return schedulingTask.endTime !== null
            }

        override val startTime: LocalDateTime
            get() {
                checkState(schedulingTask.startTime !== null, "Task was not started.")
                return schedulingTask.startTime.asNotNull()
            }

        override val endTime: LocalDateTime
            get() {
                checkState(schedulingTask.endTime !== null, "Task was not done.")
                return schedulingTask.endTime.asNotNull()
            }

        override fun cancel(mayInterruptIfRunning: Boolean): Boolean {
            val canceled = scheduledFuture.cancel(mayInterruptIfRunning)
            if (canceled) {
                schedulingTask.endTime = LocalDateTime.now()
            }
            return canceled
        }

        override fun isCancelled(): Boolean {
            return scheduledFuture.isCancelled
        }

        override fun isDone(): Boolean {
            return scheduledFuture.isDone
        }

        override fun get(): V {
            return scheduledFuture.get()
        }

        override fun get(timeout: Long, unit: TimeUnit): V {
            return scheduledFuture.get(timeout, unit)
        }

        override fun compareTo(other: Delayed): Int {
            return scheduledFuture.compareTo(other)
        }

        override fun getDelay(unit: TimeUnit): Long {
            return scheduledFuture.getDelay(unit)
        }
    }

    private inner class ScheduledExecutorServiceScheduling<V>(
        delay: Duration,
        task: () -> V,
        override val schedulingTask: SchedulingTask<V> = SchedulingTask(task),
        override val scheduledFuture: ScheduledFuture<V> = scheduledExecutorService.schedule(
            schedulingTask,
            delay.toNanos(),
            TimeUnit.NANOSECONDS
        )
    ) : AbstractScheduledExecutorServiceScheduling<V>()

    private abstract inner class RepeatableScheduledExecutorServiceScheduling<V> :
        AbstractScheduledExecutorServiceScheduling<V>() {

        abstract override val schedulingTask: RepeatableSchedulingTask<V>

        override fun get(): V {
            scheduledFuture.get()
            return schedulingTask.result.asAny()
        }

        override fun get(timeout: Long, unit: TimeUnit): V {
            scheduledFuture.get(timeout, unit)
            return schedulingTask.result.asAny()
        }
    }

    private inner class FixedRateRepeatableScheduledExecutorServiceScheduling<V>(
        initialDelay: Duration,
        period: Duration,
        task: () -> V,
        override val schedulingTask: RepeatableSchedulingTask<V> = RepeatableSchedulingTask(task),
        override val scheduledFuture: ScheduledFuture<V> = scheduledExecutorService.scheduleAtFixedRate(
            { schedulingTask.call() },
            initialDelay.toNanos(),
            period.toNanos(),
            TimeUnit.NANOSECONDS
        ).asAny()
    ) : RepeatableScheduledExecutorServiceScheduling<V>()

    private inner class FixedDelayRepeatableScheduledExecutorServiceScheduling<V>(
        initialDelay: Duration,
        period: Duration,
        task: () -> V,
        override val schedulingTask: RepeatableSchedulingTask<V> = RepeatableSchedulingTask(task),
        override val scheduledFuture: ScheduledFuture<V> = scheduledExecutorService.scheduleWithFixedDelay(
            { schedulingTask.call() },
            initialDelay.toNanos(),
            period.toNanos(),
            TimeUnit.NANOSECONDS
        ).asAny()
    ) : RepeatableScheduledExecutorServiceScheduling<V>()

    private open class SchedulingTask<V>(private val task: () -> V) : Callable<V> {

        var startTime: LocalDateTime? = null
        var endTime: LocalDateTime? = null

        override fun call(): V {
            try {
                startTime = LocalDateTime.now()
                return task()
            } finally {
                endTime = LocalDateTime.now()
            }
        }
    }

    private class RepeatableSchedulingTask<V>(task: () -> V) : SchedulingTask<V>(task) {

        var result: V? = null

        override fun call(): V {
            reset()
            val superResult = super.call()
            result = superResult
            return superResult
        }

        private fun reset() {
            startTime = null
            endTime = null
        }
    }
}