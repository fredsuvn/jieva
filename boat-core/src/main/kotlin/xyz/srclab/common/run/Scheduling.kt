package xyz.srclab.common.run

import java.util.concurrent.ScheduledFuture

/**
 * Represents a scheduled run processing.
 *
 * Note [startTime] and [endTime] are set by current or last execution, and will be refreshed for each execution.
 *
 * @see Runner
 * @see Running
 */
interface Scheduling<V> : Running<V>, SchedulingStatistics, ScheduledFuture<V>