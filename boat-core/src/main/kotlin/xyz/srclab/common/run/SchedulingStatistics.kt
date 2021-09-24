package xyz.srclab.common.run

import java.util.concurrent.Future

/**
 * Statistics for a running [Future].
 */
interface SchedulingStatistics : RunningStatistics {

    /**
     * Execution times, starts from `0`, increases after each execution.
     */
    val executionTimes: Long
}