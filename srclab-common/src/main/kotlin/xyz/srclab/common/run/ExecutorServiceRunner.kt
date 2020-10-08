package xyz.srclab.common.run

import xyz.srclab.common.base.Check
import xyz.srclab.common.base.asNotNull
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

open class ExecutorServiceRunner(
    private val executorService: ExecutorService
) : Runner {

    override fun <V> run(task: () -> V): Running<V> {
        return ExecutorServiceRunning(executorService, task)
    }

    fun shutdown() {
        executorService.shutdown()
    }

    fun shutdownNow(): List<Runnable> {
        return executorService.shutdownNow()
    }

    fun isShutdown(): Boolean {
        return executorService.isShutdown
    }

    fun isTerminated(): Boolean {
        return executorService.isTerminated
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

    private class ExecutorServiceRunning<V>(
        executorService: ExecutorService,
        task: () -> V
    ) : Running<V> {

        private val runningTask = RunningTask(task)
        private val future: Future<V>

        init {
            future = executorService.submit(runningTask)
        }

        override fun isStart(): Boolean {
            return runningTask.startTime != null
        }

        override fun startTime(): LocalDateTime {
            Check.checkState(runningTask.startTime != null, "Task was not started.")
            return runningTask.startTime.asNotNull()
        }

        override fun endTime(): LocalDateTime {
            Check.checkState(runningTask.endTime != null, "Task was not done.")
            return runningTask.endTime.asNotNull()
        }

        override fun cancel(mayInterruptIfRunning: Boolean): Boolean {
            val canceled = future.cancel(mayInterruptIfRunning)
            if (canceled) {
                runningTask.endTime = LocalDateTime.now()
            }
            return canceled
        }

        override fun isCancelled(): Boolean {
            return future.isCancelled
        }

        override fun isDone(): Boolean {
            return future.isDone
        }

        override fun get(): V {
            return future.get()
        }

        override fun get(timeout: Long, unit: TimeUnit): V {
            return future.get(timeout, unit)
        }

        private class RunningTask<V>(private val task: () -> V) : Callable<V> {

            var startTime: LocalDateTime? = null
            var endTime: LocalDateTime? = null

            override fun call(): V {
                try {
                    startTime = LocalDateTime.now()
                    return task()
                } finally {
                    endTime = LocalDateTime.now()
                }
            }
        }
    }
}