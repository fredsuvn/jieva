package xyz.srclab.common.run

import xyz.srclab.common.base.Check
import xyz.srclab.common.base.asAny
import xyz.srclab.common.base.asNotNull
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.*

open class ScheduledExecutorServiceRunner(
    private val scheduledExecutorService: ScheduledExecutorService
) : ExecutorServiceRunner(scheduledExecutorService), ScheduledRunner {

    override fun <V> schedule(task: () -> V, delay: Duration): ScheduledRunning<V> {
        return ScheduledExecutorServiceRunning(scheduledExecutorService, task, delay)
    }

    override fun <V> scheduleAtFixedRate(
        task: () -> V,
        initialDelay: Duration,
        period: Duration
    ): ScheduledRunning<V> {
        return RepeatableScheduledExecutorServiceRunningAtFixedRate(
            scheduledExecutorService,
            task,
            initialDelay,
            period
        )
    }

    override fun <V> scheduleWithFixedDelay(
        task: () -> V,
        initialDelay: Duration,
        period: Duration
    ): ScheduledRunning<V> {
        return RepeatableScheduledExecutorServiceRunningWithFixedDelay(
            scheduledExecutorService,
            task,
            initialDelay,
            period
        )
    }

    private open class ScheduledExecutorServiceRunning<V>(
        private val scheduledExecutorService: ScheduledExecutorService,
        private val task: () -> V,
        private val delay: Duration
    ) : ScheduledRunning<V> {

        protected val runningTask: RunningTask<V>
        protected val scheduledFuture: ScheduledFuture<V>

        init {
            runningTask = createRunningTask()
            scheduledFuture = createScheduledFuture()
        }

        override fun isStart(): Boolean {
            return runningTask.startTime != null
        }

        override fun startTime(): LocalDateTime {
            Check.checkState(runningTask.startTime != null, "Task was not started.")
            return runningTask.startTime.asNotNull()
        }

        override fun endTime(): LocalDateTime {
            Check.checkState(runningTask.endTime != null, "Task was not done.")
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

        protected open fun createRunningTask(): RunningTask<V> {
            return RunningTask(task)
        }

        protected open fun createScheduledFuture(): ScheduledFuture<V> {
            return scheduledExecutorService.schedule(runningTask, delay.toNanos(), TimeUnit.NANOSECONDS)
        }
    }

    private open class RepeatableScheduledExecutorServiceRunning<V>(
        private val scheduledExecutorService: ScheduledExecutorService,
        private val task: () -> V,
        private val initialDelay: Duration,
        private val period: Duration
    ) : ScheduledExecutorServiceRunning<V>(scheduledExecutorService, task, initialDelay) {

        override fun get(): V {
            scheduledFuture.get()
            return (runningTask as RepeatableRunningTask).result.asAny()
        }

        override fun get(timeout: Long, unit: TimeUnit): V {
            scheduledFuture.get(timeout, unit)
            return (runningTask as RepeatableRunningTask).result.asAny()
        }

        override fun createRunningTask(): RunningTask<V> {
            return RepeatableRunningTask(task)
        }
    }

    private open class RepeatableScheduledExecutorServiceRunningAtFixedRate<V>(
        private val scheduledExecutorService: ScheduledExecutorService,
        private val task: () -> V,
        private val initialDelay: Duration,
        private val period: Duration
    ) : ScheduledExecutorServiceRunning<V>(scheduledExecutorService, task, initialDelay) {

        override fun createScheduledFuture(): ScheduledFuture<V> {
            return scheduledExecutorService.scheduleAtFixedRate(
                { runningTask.call() },
                initialDelay.toNanos(),
                period.toNanos(),
                TimeUnit.NANOSECONDS
            ).asAny()
        }
    }

    private open class RepeatableScheduledExecutorServiceRunningWithFixedDelay<V>(
        private val scheduledExecutorService: ScheduledExecutorService,
        private val task: () -> V,
        private val initialDelay: Duration,
        private val period: Duration
    ) : ScheduledExecutorServiceRunning<V>(scheduledExecutorService, task, initialDelay) {

        override fun createScheduledFuture(): ScheduledFuture<V> {
            return scheduledExecutorService.scheduleWithFixedDelay(
                { runningTask.call() },
                initialDelay.toNanos(),
                period.toNanos(),
                TimeUnit.NANOSECONDS
            ).asAny()
        }
    }

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

    private class RepeatableRunningTask<V>(private val task: () -> V) : RunningTask<V>(task) {

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