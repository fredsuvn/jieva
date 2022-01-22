package xyz.srclab.common.run

import java.time.Duration
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

/**
 * A type of [Runner] use [ExecutorService].
 */
open class ExecutorServiceRunner(
    private val executorService: ExecutorService
) : Runner {

    override fun <V> submit(task: Callable<V>): RunWork<V> {
        return RunWorkImpl(task)
    }

    override fun submit(task: Runnable): RunWork<*> {
        return RunWorkImpl<Any?>(task)
    }

    override fun run(task: Runnable) {
        executorService.execute(task)
    }

    override fun asExecutor(): ExecutorService = executorService

    open val isShutdown: Boolean
        get() {
            return executorService.isShutdown
        }

    open val isTerminated: Boolean
        get() {
            return executorService.isTerminated
        }

    open fun shutdown() {
        executorService.shutdown()
    }

    open fun shutdownNow(): List<Runnable> {
        return executorService.shutdownNow()
    }

    /**
     * @throws InterruptedException
     */
    open fun awaitTermination(duration: Duration): Boolean {
        return executorService.awaitTermination(duration.toNanos(), TimeUnit.NANOSECONDS)
    }

    override fun toString(): String {
        return executorService.toString()
    }

    private inner class RunWorkImpl<V> : RunWork<V> {

        override val future: Future<V>

        constructor(task: Callable<V>) {
            future = executorService.submit(task)
        }

        constructor(task: Runnable) {
            future = executorService.submit(task, null)
        }
    }
}