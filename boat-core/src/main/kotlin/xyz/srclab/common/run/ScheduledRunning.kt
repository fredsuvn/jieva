package xyz.srclab.common.run

import java.util.concurrent.ScheduledFuture

/**
 * Scheduled run processing.
 */
interface ScheduledRunning<V> : Running<V>, ScheduledFuture<V>