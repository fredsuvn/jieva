package xyz.srclab.common.run

import xyz.srclab.common.base.asAny
import xyz.srclab.common.base.asNotNull
import xyz.srclab.common.base.checkState
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.*

open class ScheduledExecutorServiceRunner(
    private val scheduledExecutorService: ScheduledExecutorService
) : ExecutorServiceRunner(scheduledExecutorService), ScheduledRunner {

    override fun <V> schedule(delay: Duration, task: () -> V): ScheduledRunning<V> {
        return ScheduledExecutorServiceRunning(delay, task)
    }

    override fun <V> scheduleAtFixedRate(
        initialDelay: Duration,
        period: Duration,
        task: () -> V
    ): ScheduledRunning<V> {
        return RepeatableScheduledExecutorServiceRunningAtFixedRate(
            initialDelay,
            period,
            task
        )
    }

    override fun <V> scheduleWithFixedDelay(
        initialDelay: Duration,
        period: Duration,
        task: () -> V,
    ): ScheduledRunning<V> {
        return RepeatableScheduledExecutorServiceRunningWithFixedDelay(
            initialDelay,
            period,
            task
        )
    }

    private abstract inner class AbstractScheduledExecutorServiceRunning<V> : ScheduledRunning<V> {

        abstract val runningTask: RunningTask<V>
        abstract val scheduledFuture: ScheduledFuture<V>

        override val isStart: Boolean
            get() {
                return runningTask.startTime !== null
            }

        override val isEnd: Boolean
            get() {
                return runningTask.endTime !== null
            }

        override val startTime: LocalDateTime
            get() {
                checkState(runningTask.startTime !== null, "Task was not started.")
                return runningTask.startTime.asNotNull()
            }

        override val endTime: LocalDateTime
            get() {
                checkState(runningTask.endTime !== null, "Task was not done.")
                return runningTask.endTime.asNotNull()
            }

        override fun cancel(mayInterruptIfRunning: Boolean): Boolean {
            val canceled = scheduledFuture.cancel(mayInterruptIfRunning)
            if (canceled) {
                runningTask.endTime = LocalDateTime.now()
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

    private inner class ScheduledExecutorServiceRunning<V>(
        delay: Duration,
        task: () -> V,
        override val runningTask: RunningTask<V> = RunningTask(task),
        override val scheduledFuture: ScheduledFuture<V> = scheduledExecutorService.schedule(
            runningTask,
            delay.toNanos(),
            TimeUnit.NANOSECONDS
        )
    ) : AbstractScheduledExecutorServiceRunning<V>()

    private abstract inner class RepeatableScheduledExecutorServiceRunning<V> :
        AbstractScheduledExecutorServiceRunning<V>() {

        abstract override val runningTask: RepeatableRunningTask<V>

        override fun get(): V {
            scheduledFuture.get()
            return runningTask.result.asAny()
        }

        override fun get(timeout: Long, unit: TimeUnit): V {
            scheduledFuture.get(timeout, unit)
            return runningTask.result.asAny()
        }
    }

    private inner class RepeatableScheduledExecutorServiceRunningAtFixedRate<V>(
        initialDelay: Duration,
        period: Duration,
        task: () -> V,
        override val runningTask: RepeatableRunningTask<V> = RepeatableRunningTask(task),
        override val scheduledFuture: ScheduledFuture<V> = scheduledExecutorService.scheduleAtFixedRate(
            { runningTask.call() },
            initialDelay.toNanos(),
            period.toNanos(),
            TimeUnit.NANOSECONDS
        ).asAny()
    ) : RepeatableScheduledExecutorServiceRunning<V>()

    private inner class RepeatableScheduledExecutorServiceRunningWithFixedDelay<V>(
        initialDelay: Duration,
        period: Duration,
        task: () -> V,
        override val runningTask: RepeatableRunningTask<V> = RepeatableRunningTask(task),
        override val scheduledFuture: ScheduledFuture<V> = scheduledExecutorService.scheduleWithFixedDelay(
            { runningTask.call() },
            initialDelay.toNanos(),
            period.toNanos(),
            TimeUnit.NANOSECONDS
        ).asAny()
    ) : RepeatableScheduledExecutorServiceRunning<V>()

    private open class RunningTask<V>(private val task: () -> V) : Callable<V> {

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

    private class RepeatableRunningTask<V>(task: () -> V) : RunningTask<V>(task) {

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