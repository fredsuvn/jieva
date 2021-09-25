package xyz.srclab.common.run

import java.time.Duration
import java.time.Instant
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

/**
 * A type of [Runner] use [ExecutorService].
 */
open class ExecutorServiceRunner(
    /**
     * Underlying executor service, all functions of [ExecutorServiceRunner] are based on it.
     */
    val executorService: ExecutorService
) : Runner {

    override fun <V> run(statistics: Boolean, task: () -> V): Running<V> {
        return if (statistics) StatisticsRunning(task) else SimpleRunning(task)
    }

    override fun run(statistics: Boolean, task: Runnable): Running<*> {
        return if (statistics) StatisticsRunning<Any?>(task) else SimpleRunning<Any?>(task)
    }

    override fun <V> execute(task: () -> V) {
        executorService.execute { task() }
    }

    override fun execute(command: Runnable) {
        executorService.execute(command)
    }

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

    private inner class SimpleRunning<V> : AbstractRunning<V> {

        override var startTime: Instant? = null
        override var endTime: Instant? = null

        constructor(task: () -> V) {
            future = executorService.submit(task)
        }

        constructor(task: Runnable) {
            future = executorService.submit(task, null)
        }
    }

    private inner class StatisticsRunning<V> : AbstractRunning<V> {

        override var startTime: Instant? = null
        override var endTime: Instant? = null

        constructor(task: () -> V) {
            future = executorService.submit(CallableTask(task))
        }

        constructor(task: Runnable) {
            future = executorService.submit(RunnableTask(task), null)
        }

        private inner class CallableTask<V>(private val task: () -> V) : Callable<V> {
            override fun call(): V {
                startTime = Instant.now()
                try {
                    return task()
                } catch (e: Exception) {
                    throw e
                } finally {
                    endTime = Instant.now()
                }
            }
        }

        private inner class RunnableTask(private val task: Runnable) : Runnable {
            override fun run() {
                startTime = Instant.now()
                try {
                    task.run()
                } catch (e: Exception) {
                    throw e
                } finally {
                    endTime = Instant.now()
                }
            }
        }
    }

    private abstract class AbstractRunning<V> : Running<V> {

        protected lateinit var future: Future<V>

        override fun get(): V {
            return future.get()
        }

        override fun get(timeout: Long, unit: TimeUnit): V {
            return future.get(timeout, unit)
        }

        override fun cancel(mayInterruptIfRunning: Boolean): Boolean {
            return future.cancel(mayInterruptIfRunning)
        }

        override fun isCancelled(): Boolean {
            return future.isCancelled
        }

        override fun isDone(): Boolean {
            return future.isDone
        }
    }
}