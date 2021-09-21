package xyz.srclab.common.run

import xyz.srclab.common.base.asNotNull
import java.time.Duration
import java.util.concurrent.*

/**
 * A type of [Scheduler] use [ScheduledThreadPoolExecutor].
 */
class ScheduledThreadPoolScheduler(
    private val scheduledThreadPoolExecutor: ScheduledThreadPoolExecutor
) : ScheduledExecutorServiceScheduler(scheduledThreadPoolExecutor) {

    val corePoolSize: Int
        get() {
            return scheduledThreadPoolExecutor.corePoolSize
        }

    val allowCoreThreadTimeOut: Boolean
        get() {
            return scheduledThreadPoolExecutor.allowsCoreThreadTimeOut()
        }

    val maximumPoolSize: Int
        get() {
            return scheduledThreadPoolExecutor.maximumPoolSize
        }

    val keepAliveTime: Duration
        get() {
            return Duration.ofNanos(scheduledThreadPoolExecutor.getKeepAliveTime(TimeUnit.NANOSECONDS))
        }

    val queue: BlockingQueue<Runnable>
        get() {
            return scheduledThreadPoolExecutor.queue
        }

    val poolSize: Int
        get() {
            return scheduledThreadPoolExecutor.poolSize
        }

    val activeCount: Int
        get() {
            return scheduledThreadPoolExecutor.activeCount
        }

    val largestPoolSize: Int
        get() {
            return scheduledThreadPoolExecutor.largestPoolSize
        }

    val taskCount: Long
        get() {
            return scheduledThreadPoolExecutor.taskCount
        }

    val completedTaskCount: Long
        get() {
            return scheduledThreadPoolExecutor.completedTaskCount
        }

    val continueExistingPeriodicTasksAfterShutdownPolicy: Boolean
        get() {
            return scheduledThreadPoolExecutor.continueExistingPeriodicTasksAfterShutdownPolicy
        }

    val executeExistingDelayedTasksAfterShutdownPolicy: Boolean
        get() {
            return scheduledThreadPoolExecutor.executeExistingDelayedTasksAfterShutdownPolicy
        }

    val removeOnCancelPolicy: Boolean
        get() {
            return scheduledThreadPoolExecutor.removeOnCancelPolicy
        }

    fun prestartCoreThread(): Boolean {
        return scheduledThreadPoolExecutor.prestartCoreThread()
    }

    fun prestartAllCoreThreads(): Int {
        return scheduledThreadPoolExecutor.prestartAllCoreThreads()
    }

    fun remove(task: Runnable?): Boolean {
        return scheduledThreadPoolExecutor.remove(task)
    }

    fun purge() {
        scheduledThreadPoolExecutor.purge()
    }

    override fun toString(): String {
        return scheduledThreadPoolExecutor.toString()
    }

    class Builder {

        private var corePoolSize = 1
        private var threadFactory: ThreadFactory? = null
        private var rejectedExecutionHandler: RejectedExecutionHandler? = null

        // Set after newing
        private var keepAliveTime: Duration? = null
        private var allowCoreThreadTimeOut = false
        private var continueExistingPeriodicTasksAfterShutdownPolicy = false
        private var executeExistingDelayedTasksAfterShutdownPolicy = true
        private var removeOnCancelPolicy = false

        fun corePoolSize(corePoolSize: Int) = apply {
            this.corePoolSize = corePoolSize
        }

        fun threadFactory(threadFactory: ThreadFactory) = apply {
            this.threadFactory = threadFactory
        }

        fun rejectedExecutionHandler(rejectedExecutionHandler: RejectedExecutionHandler) = apply {
            this.rejectedExecutionHandler = rejectedExecutionHandler
        }

        fun keepAliveTime(keepAliveTime: Duration) = apply {
            this.keepAliveTime = keepAliveTime
        }

        fun allowCoreThreadTimeOut(allowCoreThreadTimeOut: Boolean) = apply {
            this.allowCoreThreadTimeOut = allowCoreThreadTimeOut
        }

        fun continueExistingPeriodicTasksAfterShutdownPolicy(
            continueExistingPeriodicTasksAfterShutdownPolicy: Boolean
        ) = apply {
            this.continueExistingPeriodicTasksAfterShutdownPolicy = continueExistingPeriodicTasksAfterShutdownPolicy
        }

        fun executeExistingDelayedTasksAfterShutdownPolicy(
            executeExistingDelayedTasksAfterShutdownPolicy: Boolean
        ) = apply {
            this.executeExistingDelayedTasksAfterShutdownPolicy = executeExistingDelayedTasksAfterShutdownPolicy
        }

        fun removeOnCancelPolicy(removeOnCancelPolicy: Boolean) = apply {
            this.removeOnCancelPolicy = removeOnCancelPolicy
        }

        fun build(): ScheduledThreadPoolScheduler {
            return ScheduledThreadPoolScheduler(createScheduledExecutorService())
        }

        private fun createScheduledExecutorService(): ScheduledThreadPoolExecutor {
            if (threadFactory === null) {
                threadFactory = Executors.defaultThreadFactory()
            }
            val scheduledThreadPoolExecutor = ScheduledThreadPoolExecutor(
                corePoolSize,
                threadFactory ?: Executors.defaultThreadFactory(),
                rejectedExecutionHandler ?: ThreadPoolExecutor.AbortPolicy()
            )
            if (keepAliveTime !== null) {
                scheduledThreadPoolExecutor.setKeepAliveTime(keepAliveTime.asNotNull().toNanos(), TimeUnit.NANOSECONDS)
            }
            scheduledThreadPoolExecutor.allowCoreThreadTimeOut(allowCoreThreadTimeOut)
            scheduledThreadPoolExecutor.continueExistingPeriodicTasksAfterShutdownPolicy =
                continueExistingPeriodicTasksAfterShutdownPolicy
            scheduledThreadPoolExecutor.executeExistingDelayedTasksAfterShutdownPolicy =
                executeExistingDelayedTasksAfterShutdownPolicy
            scheduledThreadPoolExecutor.removeOnCancelPolicy = removeOnCancelPolicy
            return scheduledThreadPoolExecutor
        }
    }

    companion object {

        @JvmStatic
        fun newBuilder(): Builder {
            return Builder()
        }
    }
}