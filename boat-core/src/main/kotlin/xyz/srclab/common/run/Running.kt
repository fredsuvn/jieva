package xyz.srclab.common.run

import java.time.Duration
import java.util.concurrent.*

/**
 * Run processing.
 *
 * @see Runner
 */
interface Running<V> : RunningStatistics, Future<V> {

    /**
     * Waits if necessary for the computation to complete, and then retrieves its result.
     *
     * @throws CancellationException if the computation was cancelled
     * @throws ExecutionException if the computation threw an
     * exception
     * @throws InterruptedException if the current thread was interrupted
     */
    override fun get(): V

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
    override fun get(timeout: Long, unit: TimeUnit): V

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
        return get(duration.nano.toLong(), TimeUnit.NANOSECONDS)
    }
}