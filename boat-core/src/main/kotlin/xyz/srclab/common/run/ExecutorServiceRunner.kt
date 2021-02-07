package xyz.srclab.common.run

import xyz.srclab.common.base.asNotNull
import xyz.srclab.common.base.checkState
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
        return ExecutorServiceRunning(task)
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

    private inner class ExecutorServiceRunning<V>(
        task: () -> V
    ) : Running<V> {

        private val runningTask = RunningTask(task)
        private val future: Future<V>

        init {
            future = executorService.submit(runningTask)
        }

        override val isStart: Boolean
            get() {
                return runningTask.startTime !== null
            }

        override val isEnd: Boolean
            get() {
                return runningTask.endTime !== null
            }

        override val startTime: LocalDateTime
            get() {
                checkState(runningTask.startTime !== null, "Task was not started.")
                return runningTask.startTime.asNotNull()
            }

        override val endTime: LocalDateTime
            get() {
                checkState(runningTask.endTime !== null, "Task was not done.")
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

        private inner class RunningTask<V>(private val task: () -> V) : Callable<V> {

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