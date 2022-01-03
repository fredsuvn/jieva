package xyz.srclab.common.run

import java.time.Duration
import java.util.concurrent.*

/**
 * Represents run-work submitted by [Runner].
 *
 * @see Runner
 */
interface RunWork<V> {

    /**
     * [Future] associated with this work.
     */
    val future: Future<V>

    /**
     * Returns whether started.
     */
    val isStart: Boolean

    /**
     * Returns whether ended.
     */
    val isEnd: Boolean
        get() {
            return future.isDone
        }

    /**
     * Returns whether cancelled.
     */
    val isCancelled: Boolean
        get() {
            return future.isCancelled
        }

    /**
     * Waits if necessary for the computation to complete, and then retrieves its result.
     *
     * @throws CancellationException if the computation was cancelled
     * @throws ExecutionException if the computation threw an
     * exception
     * @throws InterruptedException if the current thread was interrupted
     */
    fun getResult(): V {
        return future.get()
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
    fun getResult(duration: Duration): V {
        return future.get(duration.nano.toLong(), TimeUnit.NANOSECONDS)
    }

    /**
     * Cancels or interrupts the task associated by this [RunWork]. This method is equivalent to:
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
     * Cancel the task associated by this [RunWork].
     *
     * @see Future.cancel
     */
    fun cancel(mayInterruptIfRunning: Boolean): Boolean {
        return future.cancel(mayInterruptIfRunning)
    }
}