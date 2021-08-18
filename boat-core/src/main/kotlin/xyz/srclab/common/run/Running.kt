package xyz.srclab.common.run

import xyz.srclab.common.lang.INAPPLICABLE_JVM_NAME
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneOffset
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
    @get:JvmName("startTime")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val startTime: LocalDateTime?

    /**
     * Return end time, or null if not finished.
     */
    @get:JvmName("endTime")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val endTime: LocalDateTime?

    @get:JvmName("isStart")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val isStart: Boolean
        get() {
            return startTime !== null
        }

    @get:JvmName("isEnd")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val isEnd: Boolean
        get() {
            return endTime !== null
        }

    /**
     * Return start time in milliseconds, or -1 if not finished.
     */
    @get:JvmName("startMilliseconds")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val startMilliseconds: Long
        get() {
            return startTime?.toInstant(ZoneOffset.UTC)?.toEpochMilli() ?: -1
        }

    /**
     * Return end time in milliseconds, or -1 if not finished.
     */
    @get:JvmName("endMilliseconds")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val endMilliseconds: Long
        get() {
            return endTime?.toInstant(ZoneOffset.UTC)?.toEpochMilli() ?: -1
        }

    /**
     * Return cost in milliseconds, or null if not finished.
     */
    @get:JvmName("cost")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val cost: Duration?
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
    @get:JvmName("costMillis")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val costMillis: Long
        get() {
            return cost?.toMillis() ?: -1
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