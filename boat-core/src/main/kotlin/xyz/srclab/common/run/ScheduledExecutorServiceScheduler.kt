package xyz.srclab.common.run

import xyz.srclab.common.base.asAny
import java.time.Duration
import java.time.Instant
import java.util.concurrent.*

/**
 * A type of [Scheduler] use [ScheduledExecutorService].
 */
open class ScheduledExecutorServiceScheduler(
    /**
     * Underlying scheduled executor service, all functions of [ScheduledExecutorServiceScheduler] are based on it.
     */
    val scheduledExecutorService: ScheduledExecutorService
) : ExecutorServiceRunner(scheduledExecutorService), Scheduler {

    override fun <V> schedule(statistics: Boolean, delay: Duration, task: () -> V): Scheduling<V> {
        return DelayScheduling(statistics, delay, task)
    }

    override fun schedule(statistics: Boolean, delay: Duration, task: Runnable): Scheduling<*> {
        return DelayScheduling<Any?>(statistics, delay, task)
    }

    override fun <V> scheduleFixedRate(
        statistics: Boolean,
        initialDelay: Duration,
        period: Duration,
        task: () -> V
    ): Scheduling<V> {
        return FixedRateScheduling(statistics, initialDelay, period, task)
    }

    override fun scheduleFixedRate(
        statistics: Boolean,
        initialDelay: Duration,
        period: Duration,
        task: Runnable
    ): Scheduling<*> {
        return FixedRateScheduling<Any?>(statistics, initialDelay, period, task)
    }

    override fun <V> scheduleFixedDelay(
        statistics: Boolean,
        initialDelay: Duration,
        period: Duration,
        task: () -> V
    ): Scheduling<V> {
        return FixedDelayScheduling(statistics, initialDelay, period, task)
    }

    override fun scheduleFixedDelay(
        statistics: Boolean,
        initialDelay: Duration,
        period: Duration,
        task: Runnable
    ): Scheduling<*> {
        return FixedDelayScheduling<Any?>(statistics, initialDelay, period, task)
    }

    private inner class DelayScheduling<V> : AbstractScheduling<V> {

        constructor(statistics: Boolean, delay: Duration, task: () -> V) {
            if (statistics) {
                this.future =
                    scheduledExecutorService.schedule(
                        CallableTask(this, task), delay.toNanos(), TimeUnit.NANOSECONDS
                    )
            } else {
                this.future = scheduledExecutorService.schedule(task, delay.toNanos(), TimeUnit.NANOSECONDS)
            }
        }

        constructor(statistics: Boolean, delay: Duration, task: Runnable) {
            if (statistics) {
                this.future =
                    scheduledExecutorService.schedule(
                        RunnableTask(this, task), delay.toNanos(), TimeUnit.NANOSECONDS
                    ).asAny()
            } else {
                this.future = scheduledExecutorService.schedule(task, delay.toNanos(), TimeUnit.NANOSECONDS).asAny()
            }
        }
    }

    private inner class FixedRateScheduling<V> : AbstractScheduling<V> {

        constructor(statistics: Boolean, initialDelay: Duration, period: Duration, task: () -> V) {
            if (statistics) {
                this.future =
                    scheduledExecutorService.scheduleAtFixedRate(
                        RunnableTask(this, task.toRunnable()),
                        initialDelay.toNanos(),
                        period.toNanos(),
                        TimeUnit.NANOSECONDS
                    ).asAny()
            } else {
                this.future =
                    scheduledExecutorService.scheduleAtFixedRate(
                        task.toRunnable(), initialDelay.toNanos(), period.toNanos(), TimeUnit.NANOSECONDS
                    ).asAny()
            }
        }

        constructor(statistics: Boolean, initialDelay: Duration, period: Duration, task: Runnable) {
            if (statistics) {
                this.future =
                    scheduledExecutorService.scheduleAtFixedRate(
                        RunnableTask(this, task),
                        initialDelay.toNanos(),
                        period.toNanos(),
                        TimeUnit.NANOSECONDS
                    ).asAny()
            } else {
                this.future =
                    scheduledExecutorService.scheduleAtFixedRate(
                        task, initialDelay.toNanos(), period.toNanos(), TimeUnit.NANOSECONDS
                    ).asAny()
            }
        }
    }

    private inner class FixedDelayScheduling<V> : AbstractScheduling<V> {

        constructor(statistics: Boolean, initialDelay: Duration, period: Duration, task: () -> V) {
            if (statistics) {
                this.future =
                    scheduledExecutorService.scheduleWithFixedDelay(
                        RunnableTask(this, task.toRunnable()),
                        initialDelay.toNanos(),
                        period.toNanos(),
                        TimeUnit.NANOSECONDS
                    ).asAny()
            } else {
                this.future =
                    scheduledExecutorService.scheduleWithFixedDelay(
                        task.toRunnable(), initialDelay.toNanos(), period.toNanos(), TimeUnit.NANOSECONDS
                    ).asAny()
            }
        }

        constructor(statistics: Boolean, initialDelay: Duration, period: Duration, task: Runnable) {
            if (statistics) {
                this.future =
                    scheduledExecutorService.scheduleWithFixedDelay(
                        RunnableTask(this, task),
                        initialDelay.toNanos(),
                        period.toNanos(),
                        TimeUnit.NANOSECONDS
                    ).asAny()
            } else {
                this.future =
                    scheduledExecutorService.scheduleWithFixedDelay(
                        task, initialDelay.toNanos(), period.toNanos(), TimeUnit.NANOSECONDS
                    ).asAny()
            }
        }
    }

    private abstract class AbstractScheduling<V> : Scheduling<V> {

        protected lateinit var future: ScheduledFuture<V>

        override var startTime: Instant? = null
        override var endTime: Instant? = null
        override var executionTimes: Long = 0

        override val isEnd: Boolean
            get() = future.isDone

        override fun get(): V {
            return future.get()
        }

        override fun get(timeout: Long, unit: TimeUnit): V {
            return future.get(timeout, unit)
        }

        override fun cancel(mayInterruptIfRunning: Boolean): Boolean {
            val result = future.cancel(mayInterruptIfRunning)
            if (result) {
                endTime = Instant.now()
            }
            return result
        }

        override fun isCancelled(): Boolean {
            return future.isCancelled
        }

        override fun isDone(): Boolean {
            return future.isDone
        }

        override fun compareTo(other: Delayed?): Int {
            return future.compareTo(other)
        }

        override fun getDelay(unit: TimeUnit): Long {
            return future.getDelay(unit)
        }
    }

    private class CallableTask<V>(
        private val scheduling: AbstractScheduling<V>,
        private val task: () -> V
    ) : Callable<V> {
        override fun call(): V {
            scheduling.endTime = null
            scheduling.startTime = Instant.now()
            try {
                return task()
            } catch (e: Exception) {
                throw e
            } finally {
                scheduling.endTime = Instant.now()
                scheduling.executionTimes++
            }
        }
    }

    private class RunnableTask(
        private val scheduling: AbstractScheduling<*>,
        private val task: Runnable
    ) : Runnable {
        override fun run() {
            scheduling.endTime = null
            scheduling.startTime = Instant.now()
            try {
                task.run()
            } catch (e: Exception) {
                throw e
            } finally {
                scheduling.endTime = Instant.now()
                scheduling.executionTimes++
            }
        }
    }
}