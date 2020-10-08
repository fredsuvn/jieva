package xyz.srclab.common.run

import java.time.LocalDateTime
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit

object SyncRunner : Runner {

    override fun <V> run(task: () -> V): Running<V> {
        val startTime = LocalDateTime.now()
        return try {
            Success(task(), startTime)
        } catch (e: Throwable) {
            Failure(e, startTime)
        }
    }

    private abstract class Result<V>(
        private val startTime: LocalDateTime
    ) : Running<V> {

        private val endTime: LocalDateTime = LocalDateTime.now()

        override fun isStart(): Boolean {
            return true
        }

        override fun startTime(): LocalDateTime {
            return startTime
        }

        override fun endTime(): LocalDateTime {
            return endTime
        }

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