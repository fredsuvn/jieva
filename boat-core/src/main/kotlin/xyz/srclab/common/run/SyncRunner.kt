package xyz.srclab.common.run

import xyz.srclab.common.base.asTyped
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executor
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

/**
 * A type of [Runner] always use current thread.
 */
object SyncRunner : Runner, Executor {

    override fun <V> run(task: () -> V): Running<V> {
        return RunningImpl(task)
    }

    override fun run(task: Runnable): Running<*> {
        return RunningImpl<Any?>(task)
    }

    override fun <V> execute(task: () -> V) {
        try {
            task()
        } catch (e: Exception) {
            //Dropped
        }
    }

    override fun execute(task: Runnable) {
        try {
            task.run()
        } catch (e: Exception) {
            //Dropped
        }
    }

    override fun asExecutor(): Executor = this

    private class RunningImpl<V> : Running<V> {

        private val future: SyncFuture<V>

        override val isStart: Boolean = true

        constructor(task: () -> V) {
            future = SyncFuture(task)
        }

        constructor(task: Runnable) {
            future = SyncFuture(task)
        }

        override fun asFuture(): Future<V> {
            return future
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
            return result.asTyped()
        }

        override fun get(timeout: Long, unit: TimeUnit): V {
            return get()
        }
    }
}