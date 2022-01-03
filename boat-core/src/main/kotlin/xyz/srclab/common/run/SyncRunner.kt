package xyz.srclab.common.run

import xyz.srclab.common.base.asTyped
import java.util.concurrent.*

/**
 * A type of [Runner] always use current thread.
 */
object SyncRunner : Runner, Executor {

    override fun <V> submit(task: Callable<V>): RunWork<V> {
        return RunWorkImpl(task)
    }

    override fun submit(task: Runnable): RunWork<*> {
        return RunWorkImpl<Any?>(task)
    }

    override fun run(task: Runnable) {
        try {
            task.run()
        } catch (e: Exception) {
            //Dropped
        }
    }

    override fun execute(command: Runnable) {
        run(command)
    }

    override fun asExecutor(): Executor = this

    private class RunWorkImpl<V> : RunWork<V> {

        override val future: SyncFuture<V>

        constructor(task: Callable<V>) {
            future = SyncFuture(task)
        }

        constructor(task: Runnable) {
            future = SyncFuture(task)
        }
    }

    private class SyncFuture<V> : Future<V> {

        private var result: V? = null
        private var exception: Exception? = null

        constructor(task: Callable<V>) {
            try {
                result = task.call()
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