package xyz.srclab.common.run

import java.util.concurrent.ScheduledFuture

/**
 * Represents a scheduled run processing.
 *
 * @see Scheduler
 * @see Runner
 * @see Running
 */
interface Scheduling<V> : Running<V> {

    /**
     * Execution count, starts from `0`, increases after each execution start.
     */
    val executionCount: Long

    override fun asFuture(): ScheduledFuture<V>
}