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

    override fun run(statistics: Boolean, task: Runnable): Running<Any?> {
        return if (statistics) StatisticsRunning(task) else SimpleRunning(task)
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

    private inner class SimpleRunning<V> : Running<V> {

        override val future: Future<V>
        override val statistics: RunningStatistics? = null

        constructor(task: () -> V) {
            future = executorService.submit(task)
        }

        constructor(task: Runnable) {
            future = executorService.submit(task, null)
        }
    }

    private inner class StatisticsRunning<V> : Running<V> {

        override val future: Future<V>
        override val statistics: SimpleRunningStatistics = SimpleRunningStatistics()

        constructor(task: () -> V) {
            future = executorService.submit(CallableTask(task))
        }

        constructor(task: Runnable) {
            future = executorService.submit(RunnableTask(task), null)
        }

        private inner class CallableTask<V>(private val task: () -> V) : Callable<V> {
            override fun call(): V {
                statistics.startTime = Instant.now()
                try {
                    return task()
                } finally {
                    statistics.endTime = Instant.now()
                }
            }
        }

        private inner class RunnableTask(private val task: Runnable) : Runnable {
            override fun run() {
                statistics.startTime = Instant.now()
                try {
                    task.run()
                } finally {
                    statistics.endTime = Instant.now()
                }
            }
        }
    }
}