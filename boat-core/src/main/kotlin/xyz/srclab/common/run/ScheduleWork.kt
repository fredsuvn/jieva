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

    /**
     * [ScheduledFuture] associated with this work.
     */
    override val future: ScheduledFuture<V>

    companion object {

        /**
         * Returns [ScheduleWork] from [ScheduledFuture].
         */
        @JvmName("of")
        @JvmStatic
        fun <V> ScheduledFuture<V>.toScheduleWork(): ScheduleWork<V> {
            return object : ScheduleWork<V> {
                override val future: ScheduledFuture<V> = this@toScheduleWork
            }
        }
    }
}