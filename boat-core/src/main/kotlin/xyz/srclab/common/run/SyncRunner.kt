package xyz.srclab.common.run

import xyz.srclab.common.base.asAny
import java.time.Instant
import java.util.concurrent.ExecutionException
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

/**
 * A type of [Runner] always use current thread.
 */
object SyncRunner : Runner {

    override fun <V> run(statistics: Boolean, task: () -> V): Running<V> {
        return if (statistics) StatisticsRunning(task) else SimpleRunning(task)
    }

    override fun run(statistics: Boolean, task: Runnable): Running<Any?> {
        return if (statistics) StatisticsRunning(task) else SimpleRunning(task)
    }

    override fun <V> execute(task: () -> V) {
        task()
    }

    override fun execute(command: Runnable) {
        command.run()
    }

    private class SimpleRunning<V> : Running<V> {

        override val future: Future<V>
        override val statistics: RunningStatistics? = null

        constructor(task: () -> V) {
            future = SyncFuture(task)
        }

        constructor(task: Runnable) {
            future = SyncFuture(task)
        }
    }

    private class StatisticsRunning<V> : Running<V> {

        override val future: Future<V>
        override val statistics: SimpleRunningStatistics = SimpleRunningStatistics()

        constructor(task: () -> V) {
            statistics.startTime = Instant.now()
            future = SyncFuture(task)
            statistics.endTime = Instant.now()
        }

        constructor(task: Runnable) {
            statistics.startTime = Instant.now()
            future = SyncFuture(task)
            statistics.endTime = Instant.now()
        }
    }

    private class SyncFuture<V> : Future<V> {

        private var result: V? = null
        private var exception: Exception? = null

        constructor(task: () -> V) {
            try {
                result = task()
            } catch (e: Exception) {
                exception = e
            }
        }

        constructor(task: Runnable) {
            try {
                task.run()
            } catch (e: Exception) {
                exception = e
            }
        }

        override fun cancel(mayInterruptIfRunning: Boolean): Boolean {
            return false
        }

        override fun isCancelled(): Boolean {
            return false
        }

        override fun isDone(): Boolean {
            return true
        }

        override fun get(): V {
            if (exception !== null) {
                throw ExecutionException(exception)
            }
            return result.asAny()
        }

        override fun get(timeout: Long, unit: TimeUnit): V {
            return get()
        }
    }
}