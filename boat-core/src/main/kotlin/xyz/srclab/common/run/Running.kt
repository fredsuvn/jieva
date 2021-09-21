package xyz.srclab.common.run

import java.time.Duration
import java.time.Instant
import java.util.concurrent.*

/**
 * Run processing.
 *
 * @see Runner
 */
interface Running<V> : Future<V> {

    /**
     * Return start time, or null if not finished.
     */
    val startTime: Instant?

    /**
     * Return end time, or null if not finished.
     */
    val endTime: Instant?

    val isStart: Boolean
        get() {
            return startTime !== null
        }

    val isEnd: Boolean
        get() {
            return endTime !== null
        }

    /**
     * Return start time in milliseconds, or -1 if not finished.
     */
    val startTimeMillis: Long
        get() {
            return startTime?.toEpochMilli() ?: -1
        }

    /**
     * Return end time in milliseconds, or -1 if not finished.
     */
    val endTimeMillis: Long
        get() {
            return endTime?.toEpochMilli() ?: -1
        }

    /**
     * Return cost in milliseconds, or null if not finished.
     */
    val costTime: Duration?
        get() {
            val start = startTime
            val end = endTime
            if (start === null || end === null) {
                return null
            }
            return Duration.between(start, end)
        }

    /**
     * Return cost in milliseconds, or -1 if not finished.
     */
    val costTimeMillis: Long
        get() {
            return costTime?.toMillis() ?: -1
        }

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