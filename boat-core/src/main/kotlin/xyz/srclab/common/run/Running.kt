package xyz.srclab.common.run

import java.time.Duration
import java.util.concurrent.*

/**
 * Represents run processing.
 *
 * @see Runner
 */
interface Running<V> {

    /**
     * Returns whether started.
     */
    val isStart: Boolean

    /**
     * Returns whether ended.
     */
    val isEnd: Boolean
        get() {
            return asFuture().isDone
        }

    /**
     * Returns whether cancelled.
     */
    val isCancelled: Boolean
        get() {
            return asFuture().isCancelled
        }

    /**
     * Waits if necessary for the computation to complete, and then retrieves its result.
     *
     * @throws CancellationException if the computation was cancelled
     * @throws ExecutionException if the computation threw an
     * exception
     * @throws InterruptedException if the current thread was interrupted
     */
    fun get(): V {
        return asFuture().get()
    }

    /**
     * Waits if necessary for at most the given time for the computation to complete,
     * and then retrieves its result, if available.
     *
     * @throws CancellationException if the computation was cancelled
     * @throws ExecutionException if the computation threw an
     * exception
     * @throws InterruptedException if the current thread was interrupted
     * while waiting
     * @throws TimeoutException if the wait timed out
     */
    fun get(duration: Duration): V {
        return asFuture().get(duration.nano.toLong(), TimeUnit.NANOSECONDS)
    }

    /**
     * Cancels or interrupts the task associated by this [Running]. This method is equivalent to:
     *
     * ```
     * cancel(true)
     * ```
     *
     * @see Future.cancel
     */
    fun cancel(): Boolean {
        return cancel(true)
    }

    /**
     * Cancel the task associated by this [Running].
     *
     * @see Future.cancel
     */
    fun cancel(mayInterruptIfRunning: Boolean): Boolean {
        return asFuture().cancel(mayInterruptIfRunning)
    }

    /**
     * Returns this running as [Future].
     */
    fun asFuture(): Future<V>
}