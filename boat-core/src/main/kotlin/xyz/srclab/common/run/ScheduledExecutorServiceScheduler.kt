package xyz.srclab.common.run

import xyz.srclab.common.lang.asAny
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
        return ScheduleScheduling(delay, task)
    }

    override fun <V> scheduleFixedRate(
        initialDelay: Duration,
        period: Duration,
        task: () -> V
    ): Scheduling<V> {
        return FixedRateScheduling(initialDelay, period, task)
    }

    override fun <V> scheduleFixedDelay(
        initialDelay: Duration,
        period: Duration,
        task: () -> V,
    ): Scheduling<V> {
        return FixedDelayScheduling(initialDelay, period, task)
    }

    private inner class ScheduleScheduling<V>(delay: Duration, task: () -> V) : SchedulingImpl<V>() {
        init {
            val callable = CallableTask(this, task)
            future = scheduledExecutorService.schedule(callable, delay.toNanos(), TimeUnit.NANOSECONDS)
        }
    }

    private inner class FixedRateScheduling<V>(
        initialDelay: Duration, period: Duration, task: () -> V
    ) : SchedulingImpl<V>() {
        init {
            val runnable = RunnableTask(this) { task() }
            val future = scheduledExecutorService.scheduleAtFixedRate(
                runnable,
                initialDelay.toNanos(),
                period.toNanos(),
                TimeUnit.NANOSECONDS
            )
            this.future = future.asAny()
        }
    }

    private inner class FixedDelayScheduling<V>(
        initialDelay: Duration, period: Duration, task: () -> V
    ) : SchedulingImpl<V>() {
        init {
            val runnable = RunnableTask(this) { task() }
            val future = scheduledExecutorService.scheduleWithFixedDelay(
                runnable,
                initialDelay.toNanos(),
                period.toNanos(),
                TimeUnit.NANOSECONDS
            )
            this.future = future.asAny()
        }
    }

    private abstract class SchedulingImpl<V> : Scheduling<V> {

        protected lateinit var future: ScheduledFuture<V>

        override var startTime: LocalDateTime? = null
        override var endTime: LocalDateTime? = null

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
                endTime = LocalDateTime.now()
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
        private val schedulingImpl: SchedulingImpl<V>,
        private val task: () -> V
    ) : Callable<V> {
        override fun call(): V {
            schedulingImpl.endTime = null
            schedulingImpl.startTime = LocalDateTime.now()
            try {
                return task()
            } catch (e: Exception) {
                throw e
            } finally {
                schedulingImpl.endTime = LocalDateTime.now()
            }
        }
    }

    private class RunnableTask(
        private val schedulingImpl: SchedulingImpl<*>,
        private val task: Runnable
    ) : Runnable {
        override fun run() {
            schedulingImpl.endTime = null
            schedulingImpl.startTime = LocalDateTime.now()
            try {
                task.run()
            } catch (e: Exception) {
                throw e
            } finally {
                schedulingImpl.endTime = LocalDateTime.now()
            }
        }
    }
}