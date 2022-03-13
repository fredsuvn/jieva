package xyz.srclab.common.run

import xyz.srclab.common.run.Scheduler.Companion.toScheduler
import java.util.concurrent.Executors

/**
 * [Scheduler] with single thread.
 */
object SingleThreadScheduler : Scheduler by Executors.newSingleThreadScheduledExecutor().toScheduler()