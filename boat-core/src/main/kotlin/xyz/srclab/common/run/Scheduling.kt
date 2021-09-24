package xyz.srclab.common.run

import java.util.concurrent.ScheduledFuture

/**
 * Scheduled run processing.
 *
 * Note [startTime] and [endTime] are set by current or last execution, and will be refreshed for each execution.
 */
interface Scheduling<V> : Running<V>, SchedulingStatistics, ScheduledFuture<V>