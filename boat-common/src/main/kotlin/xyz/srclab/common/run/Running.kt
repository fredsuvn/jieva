package xyz.srclab.common.run

import xyz.srclab.common.base.INAPPLICABLE_JVM_NAME
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.concurrent.*

interface Running<V> : Future<V> {

    @Suppress(INAPPLICABLE_JVM_NAME)
    val isStart: Boolean
        @JvmName("isStart") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    val startTime: LocalDateTime
        @JvmName("startTime") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    val endTime: LocalDateTime
        @JvmName("endTime") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    val startMilliseconds: Long
        @JvmName("startMilliseconds") get() {
            return startTime.toInstant(ZoneOffset.UTC).toEpochMilli()
        }

    @Suppress(INAPPLICABLE_JVM_NAME)
    val endMilliseconds: Long
        @JvmName("endMilliseconds") get() {
            return endTime.toInstant(ZoneOffset.UTC).toEpochMilli()
        }

    @Suppress(INAPPLICABLE_JVM_NAME)
    val cost: Duration
        @JvmName("cost") get() {
            return Duration.between(startTime, endTime)
        }

    @Suppress(INAPPLICABLE_JVM_NAME)
    val costMillis: Long
        @JvmName("costMillis") get() {
            return cost.toMillis()
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