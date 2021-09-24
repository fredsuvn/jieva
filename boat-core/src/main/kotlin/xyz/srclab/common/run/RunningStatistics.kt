package xyz.srclab.common.run

import java.time.Duration
import java.time.Instant
import java.util.concurrent.Future

/**
 * Statistics for a running [Future].
 */
interface RunningStatistics {

    /**
     * Start time.
     */
    val startTime: Instant?

    /**
     * End time.
     */
    val endTime: Instant?

    /**
     * Whether started.
     */
    val isStart: Boolean
        get() {
            return startTime !== null
        }

    /**
     * Whether ended.
     */
    val isEnd: Boolean
        get() {
            return endTime !== null
        }

    /**
     * Return start time in milliseconds, or -1 if not finished or unsupported.
     */
    val startTimeMillis: Long
        get() {
            return startTime?.toEpochMilli() ?: -1
        }

    /**
     * Return end time in milliseconds, or -1 if not finished or unsupported.
     */
    val endTimeMillis: Long
        get() {
            return endTime?.toEpochMilli() ?: -1
        }

    /**
     * Return cost in milliseconds, or null if not finished or unsupported.
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
     * Return cost in milliseconds, or -1 if not finished or unsupported.
     */
    val costTimeMillis: Long
        get() {
            return costTime?.toMillis() ?: -1
        }
}