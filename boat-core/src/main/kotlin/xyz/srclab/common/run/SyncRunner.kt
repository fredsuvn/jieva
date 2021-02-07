package xyz.srclab.common.run

import java.time.LocalDateTime
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit

/**
 * A type of [Runner] always use new thread.
 */
object SyncRunner : Runner {

    override fun <V> run(task: () -> V): Running<V> {
        val startTime = LocalDateTime.now()
        return try {
            Success(task(), startTime)
        } catch (e: Throwable) {
            Failure(e, startTime)
        }
    }

    override fun execute(command: Runnable) {
        command.run()
    }

    private abstract class Result<V>(
        override val startTime: LocalDateTime
    ) : Running<V> {

        override val isStart: Boolean = true
        override val isEnd: Boolean = true
        override val endTime: LocalDateTime = LocalDateTime.now()

        override fun get(timeout: Long, unit: TimeUnit): V {
            return get()
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
    }

    private class Success<V>(
        private val result: V,
        startTime: LocalDateTime
    ) : Result<V>(startTime) {
        override fun get(): V {
            return result
        }
    }

    private class Failure<V>(
        private val cause: Throwable,
        startTime: LocalDateTime
    ) : Result<V>(startTime) {
        override fun get(): V {
            throw ExecutionException(cause)
        }
    }
}