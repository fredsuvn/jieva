package xyz.srclab.common.run

/**
 * A type of [Scheduler] with single thread.
 */
object SingleThreadScheduler : Scheduler by singleThreadScheduler()