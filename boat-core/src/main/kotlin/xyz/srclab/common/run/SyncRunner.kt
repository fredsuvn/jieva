package xyz.srclab.common.run

import xyz.srclab.common.base.asAny
import java.time.Instant
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit

/**
 * A type of [Runner] always use current thread.
 */
object SyncRunner : Runner {

    override fun <V> run(statistics: Boolean, task: () -> V): Running<V> {
        return if (statistics) StatisticsRunning(task) else SimpleRunning(task)
    }

    override fun run(statistics: Boolean, task: Runnable): Running<*> {
        return if (statistics) StatisticsRunning<Any?>(task) else SimpleRunning<Any?>(task)
    }

    override fun <V> execute(task: () -> V) {
        task()
    }

    override fun execute(command: Runnable) {
        command.run()
    }

    private class SimpleRunning<V> : AbstractRunning<V> {

        override val startTime: Instant? = null
        override val endTime: Instant? = null

        constructor(task: () -> V) {
            run(task)
        }

        constructor(task: Runnable) {
            run(task)
        }
    }

    private class StatisticsRunning<V> : AbstractRunning<V> {

        override var startTime: Instant? = null
        override var endTime: Instant? = null

        constructor(task: () -> V) {
            startTime = Instant.now()
            run(task)
            endTime = Instant.now()
        }

        constructor(task: Runnable) {
            startTime = Instant.now()
            run(task)
            endTime = Instant.now()
        }
    }

    private abstract class AbstractRunning<V> : Running<V> {

        protected var result: V? = null
        protected var exception: Exception? = null

        protected fun run(task: () -> V) {
            try {
                result = task()
            } catch (e: Exception) {
                exception = e
            }
        }

        protected fun run(task: Runnable) {
            try {
                task.run()
            } catch (e: Exception) {
                exception = e
            }
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

        override fun cancel(mayInterruptIfRunning: Boolean): Boolean {
            return false
        }

        override fun isCancelled(): Boolean {
            return false
        }

        override fun isDone(): Boolean {
            return isEnd
        }
    }
}