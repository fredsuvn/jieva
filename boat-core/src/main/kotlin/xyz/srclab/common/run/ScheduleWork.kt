package xyz.srclab.common.run

import java.util.concurrent.ScheduledFuture

/**
 * Represents a schedule-work submitted by [Scheduler].
 *
 * @see Scheduler
 * @see Runner
 * @see RunWork
 */
interface ScheduleWork<V> : RunWork<V> {

    override val future: ScheduledFuture<V>
}