package xyz.srclab.common.run

import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.concurrent.*

interface Running<V> : Future<V> {

    fun isStart(): Boolean

    fun startTime(): LocalDateTime

    fun endTime(): LocalDateTime

    fun startMilliseconds(): Long {
        return startTime().toInstant(ZoneOffset.UTC).toEpochMilli()
    }

    fun endMilliseconds(): Long {
        return endTime().toInstant(ZoneOffset.UTC).toEpochMilli()
    }

    fun cost(): Duration {
        return Duration.between(startTime(), endTime())
    }

    fun costMillis(): Long {
        return cost().toMillis()
    }

    /**
     * @throws CancellationException if the computation was cancelled
     * @throws ExecutionException if the computation threw an
     * exception
     * @throws InterruptedException if the current thread was interrupted
     */
    override fun get(): V

    /**
     * @throws CancellationException if the computation was cancelled
     * @throws ExecutionException if the computation threw an
     * exception
     * @throws InterruptedException if the current thread was interrupted
     * while waiting
     * @throws TimeoutException if the wait timed out
     */
    override fun get(timeout: Long, unit: TimeUnit): V

    /**
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