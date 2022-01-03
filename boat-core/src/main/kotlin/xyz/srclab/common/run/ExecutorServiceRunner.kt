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

    override fun <V> run(task: () -> V): Running<V> {
        return RunningImpl(task)
    }

    override fun run(task: Runnable): Running<*> {
        return RunningImpl<Any?>(task)
    }

    override fun execute(task: () -> Any?) {
        executorService.execute { task() }
    }

    override fun execute(task: Runnable) {
        executorService.execute(task)
    }

    override fun asExecutor(): ExecutorService = executorService

    val isShutdown: Boolean
        @JvmName("isShutdown") get() {
            return executorService.isShutdown
        }

    val isTerminated: Boolean
        @JvmName("isTerminated") get() {
            return executorService.isTerminated
        }

    fun shutdown() {
        executorService.shutdown()
    }

    fun shutdownNow(): List<Runnable> {
        return executorService.shutdownNow()
    }

    /**
     * @throws InterruptedException
     */
    fun awaitTermination(duration: Duration): Boolean {
        return executorService.awaitTermination(duration.toNanos(), TimeUnit.NANOSECONDS)
    }

    override fun toString(): String {
        return executorService.toString()
    }

    private inner class RunningImpl<V> : Running<V> {

        private val future: Future<V>

        constructor(task: () -> V) {
            future = executorService.submit(Callable {
                isStart = true
                task()
            })
        }

        constructor(task: Runnable) {
            future = executorService.submit({
                isStart = true
                task.run()
            }, null)
        }

        override var isStart: Boolean = false

        override fun asFuture(): Future<V> {
            return future
        }
    }
}