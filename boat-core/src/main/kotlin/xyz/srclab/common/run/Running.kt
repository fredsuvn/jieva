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
     * Returns underlying [Future].
     */
    val future: Future<V>

    /**
     * Returns statistics info, or null if statistics is not enabled.
     */
    val statistics: RunningStatistics?

    /**
     * Waits if necessary for the computation to complete, and then retrieves its result.
     *
     * @throws CancellationException if the computation was cancelled
     * @throws ExecutionException if the computation threw an
     * exception
     * @throws InterruptedException if the current thread was interrupted
     */
    fun get(): V {
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
    fun get(duration: Duration): V {
        return future.get(duration.nano.toLong(), TimeUnit.NANOSECONDS)
    }
}