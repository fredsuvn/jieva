package xyz.srclab.common.run

import xyz.srclab.common.base.asType
import java.util.*
import java.util.concurrent.*

/**
 * [ExecutorService] implementation which always executes in current thread.
 *
 * Note any timeout for this [ExecutorService] is invalid.
 */
open class SyncExecutorService : ExecutorService {

    private var isShutdown: Boolean = false

    override fun execute(command: Runnable) {
        command.run()
    }

    override fun shutdown() {
        isShutdown = true
    }

    override fun shutdownNow(): MutableList<Runnable> {
        shutdown()
        return mutableListOf()
    }

    override fun isShutdown(): Boolean {
        return isShutdown
    }

    override fun isTerminated(): Boolean {
        return isShutdown()
    }

    override fun awaitTermination(timeout: Long, unit: TimeUnit): Boolean {
        return isShutdown()
    }

    override fun <T : Any?> submit(task: Callable<T>): Future<T> {
        return try {
            SyncFuture(task.call())
        } catch (e: Throwable) {
            SyncFuture(null, e)
        }
    }

    override fun <T : Any?> submit(task: Runnable, result: T): Future<T> {
        return try {
            task.run()
            SyncFuture(result)
        } catch (e: Throwable) {
            SyncFuture(null, e)
        }
    }

    override fun submit(task: Runnable): Future<*> {
        return try {
            task.run()
            SyncFuture(null)
        } catch (e: Throwable) {
            SyncFuture(null, e)
        }
    }

    override fun <T : Any?> invokeAll(tasks: MutableCollection<out Callable<T>>): MutableList<Future<T>> {
        val result = LinkedList<Future<T>>()
        for (task in tasks) {
            result.add(submit(task))
        }
        return result
    }

    override fun <T : Any?> invokeAll(tasks: MutableCollection<out Callable<T>>, timeout: Long, unit: TimeUnit): MutableList<Future<T>> {
        return invokeAll(tasks)
    }

    override fun <T : Any?> invokeAny(tasks: MutableCollection<out Callable<T>>): T {
        for (task in tasks) {
            try {
                return task.call()
            } catch (e: Throwable) {
                continue
            }
        }
        throw NoTaskSuccessfullyException()
    }

    override fun <T : Any?> invokeAny(tasks: MutableCollection<out Callable<T>>, timeout: Long, unit: TimeUnit): T {
        TODO("Not yet implemented")
    }

    private class SyncFuture<T>(
        val value: T?,
        val exception: Throwable? = null,
    ) : Future<T> {

        private var isCancelled: Boolean = false

        override fun cancel(mayInterruptIfRunning: Boolean): Boolean {
            isCancelled = true
            return true
        }

        override fun isCancelled(): Boolean {
            return isCancelled
        }

        override fun isDone(): Boolean {
            return true
        }

        override fun get(): T {
            if (isCancelled()) {
                throw CancellationException()
            }
            if (exception !== null) {
                throw ExecutionException(exception)
            }
            return value.asType()
        }

        override fun get(timeout: Long, unit: TimeUnit): T {
            return get()
        }
    }

    private class NoTaskSuccessfullyException : ExecutionException("No Task Successfully Completes!")
}