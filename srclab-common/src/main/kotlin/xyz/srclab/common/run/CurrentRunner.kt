package xyz.srclab.common.run

import xyz.srclab.common.base.asAny
import java.time.LocalDateTime
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit

object CurrentRunner : Runner {

    override fun <V> run(block: () -> V): Running<V> {
        return CurrentRunning(block)
    }

    private class CurrentRunning<V>(block: () -> V) : Running<V> {

        private val startTime: LocalDateTime = LocalDateTime.now()
        private val endTime: LocalDateTime
        private var result: V?
        private var cause: Throwable?

        init {
            try {
                result = block()
                cause = null
            } catch (e: Throwable) {
                result = null
                cause = e
            } finally {
                endTime = LocalDateTime.now()
            }
        }

        override fun isStart(): Boolean {
            return true
        }

        override fun startTime(): LocalDateTime {
            return startTime
        }

        override fun endTime(): LocalDateTime {
            return endTime
        }

        override fun get(): V {
            if (cause != null) {
                throw ExecutionException(cause)
            }
            return result.asAny()
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
}