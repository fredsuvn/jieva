package xyz.srclab.common.run

import java.time.Duration
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

/**
 * Represents a type of [Work] submitted by [Runner].
 *
 * @see Runner
 */
interface RunWork<V> : Work<V> {

    /**
     * [Future] associated with this work.
     */
    val future: Future<V>

    override fun isDone(): Boolean {
        return future.isDone
    }

    override fun isCancelled(): Boolean {
        return future.isCancelled
    }

    override fun getResult(): V {
        try {
            return future.get()
        } catch (e: Exception) {
            throw WorkException(e)
        }
    }

    override fun getResult(millis: Long): V {
        try {
            return future.get(millis, TimeUnit.MILLISECONDS)
        } catch (e: Exception) {
            throw WorkException(e)
        }
    }

    override fun getResult(duration: Duration): V {
        try {
            return future.get(duration.toNanos(), TimeUnit.NANOSECONDS)
        } catch (e: Exception) {
            throw WorkException(e)
        }
    }

    override fun cancel(mayInterruptIfRunning: Boolean): Boolean {
        return future.cancel(mayInterruptIfRunning)
    }

    companion object {

        /**
         * Returns [RunWork] from [this].
         */
        @JvmName("of")
        @JvmStatic
        fun <V> Future<V>.toRunWork(): RunWork<V> {
            return object : RunWork<V> {
                override val future: Future<V> = this@toRunWork
            }
        }
    }
}