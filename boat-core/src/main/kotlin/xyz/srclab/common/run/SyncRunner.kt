package xyz.srclab.common.run

import xyz.srclab.common.base.asAny
import java.time.Instant
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit

/**
 * A type of [Runner] always use current thread.
 */
object SyncRunner : Runner {

    override fun <V> run(task: () -> V): Running<V> {
        return RunningImpl(task)
    }

    override fun run(task: Runnable): Running<*> {
        return RunningImpl<Any>(task)
    }

    override fun <V> fastRun(task: () -> V) {
        task()
    }

    override fun execute(command: Runnable) {
        command.run()
    }

    private class RunningImpl<V> : Running<V> {

        private var result: V? = null
        override var startTime: Instant? = null
        override var endTime: Instant? = null

        private var exception: Exception? = null

        constructor(task: () -> V) {
            try {
                startTime = Instant.now()
                result = task()
            } catch (e: Exception) {
                exception = e
            } finally {
                endTime = Instant.now()
            }
        }

        constructor(task: Runnable) {
            try {
                startTime = Instant.now()
                task.run()
            } catch (e: Exception) {
                exception = e
            } finally {
                endTime = Instant.now()
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