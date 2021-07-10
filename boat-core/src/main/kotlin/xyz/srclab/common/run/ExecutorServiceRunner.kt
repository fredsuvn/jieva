package xyz.srclab.common.run

import java.time.Duration
import java.time.LocalDateTime
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
        return RunningImpl<Any>(task)
    }

    override fun <V> fastRun(task: () -> V) {
        executorService.execute { task() }
    }

    override fun fastRun(task: Runnable) {
        executorService.execute(task)
    }

    override fun execute(command: Runnable) {
        fastRun(command)
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

    private inner class RunningImpl<V> : Running<V> {

        private val future: Future<V>

        override var startTime: LocalDateTime? = null
        override var endTime: LocalDateTime? = null

        constructor(task: () -> V) {
            future = executorService.submit(CallableTask(task))
        }

        constructor(task: Runnable) {
            future = executorService.submit(RunnableTask(task), null)
        }

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

        private inner class CallableTask<V>(private val task: () -> V) : Callable<V> {
            override fun call(): V {
                startTime = LocalDateTime.now()
                try {
                    return task()
                } catch (e: Exception) {
                    throw e
                } finally {
                    endTime = LocalDateTime.now()
                }
            }
        }

        private inner class RunnableTask(private val task: Runnable) : Runnable {
            override fun run() {
                startTime = LocalDateTime.now()
                try {
                    task.run()
                } catch (e: Exception) {
                    throw e
                } finally {
                    endTime = LocalDateTime.now()
                }
            }
        }
    }
}