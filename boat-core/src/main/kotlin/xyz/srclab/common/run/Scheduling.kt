package xyz.srclab.common.run

import java.util.concurrent.ScheduledFuture

/**
 * Scheduled run processing.
 *
 * Note [startTime] and [endTime] will be refreshed for each scheduling.
 */
interface Scheduling<V> : Running<V>, ScheduledFuture<V>