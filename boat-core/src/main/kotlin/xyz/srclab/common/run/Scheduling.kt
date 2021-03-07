package xyz.srclab.common.run

import java.util.concurrent.ScheduledFuture

/**
 * Scheduled run processing.
 */
interface Scheduling<V> : Running<V>, ScheduledFuture<V>