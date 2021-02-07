package xyz.srclab.common.run

import xyz.srclab.common.base.asNotNull
import java.time.Duration
import java.util.concurrent.*

/**
 * A type of [ScheduledRunner] use [ScheduledThreadPoolExecutor].
 */
class ScheduledThreadPoolRunner(
    private val scheduledThreadPoolExecutor: ScheduledThreadPoolExecutor
) : ScheduledExecutorServiceRunner(scheduledThreadPoolExecutor) {

    val corePoolSize: Int
        @JvmName("corePoolSize") get() {
            return scheduledThreadPoolExecutor.corePoolSize
        }

    val allowCoreThreadTimeOut: Boolean
        @JvmName("allowCoreThreadTimeOut") get() {
            return scheduledThreadPoolExecutor.allowsCoreThreadTimeOut()
        }

    val maximumPoolSize: Int
        @JvmName("maximumPoolSize") get() {
            return scheduledThreadPoolExecutor.maximumPoolSize
        }

    val keepAliveTime: Duration
        @JvmName("keepAliveTime") get() {
            return Duration.ofNanos(scheduledThreadPoolExecutor.getKeepAliveTime(TimeUnit.NANOSECONDS))
        }

    val queue: BlockingQueue<Runnable>
        @JvmName("queue") get() {
            return scheduledThreadPoolExecutor.queue
        }

    val poolSize: Int
        @JvmName("poolSize") get() {
            return scheduledThreadPoolExecutor.poolSize
        }

    val activeCount: Int
        @JvmName("activeCount") get() {
            return scheduledThreadPoolExecutor.activeCount
        }

    val largestPoolSize: Int
        @JvmName("largestPoolSize") get() {
            return scheduledThreadPoolExecutor.largestPoolSize
        }

    val taskCount: Long
        @JvmName("taskCount") get() {
            return scheduledThreadPoolExecutor.taskCount
        }

    val completedTaskCount: Long
        @JvmName("completedTaskCount") get() {
            return scheduledThreadPoolExecutor.completedTaskCount
        }

    val continueExistingPeriodicTasksAfterShutdownPolicy: Boolean
        @JvmName("continueExistingPeriodicTasksAfterShutdownPolicy") get() {
            return scheduledThreadPoolExecutor.continueExistingPeriodicTasksAfterShutdownPolicy
        }

    val executeExistingDelayedTasksAfterShutdownPolicy: Boolean
        @JvmName("executeExistingDelayedTasksAfterShutdownPolicy") get() {
            return scheduledThreadPoolExecutor.executeExistingDelayedTasksAfterShutdownPolicy
        }

    val removeOnCancelPolicy: Boolean
        @JvmName("removeOnCancelPolicy") get() {
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

        fun build(): ScheduledThreadPoolRunner {
            return ScheduledThreadPoolRunner(createScheduledExecutorService())
        }

        private fun createScheduledExecutorService(): ScheduledThreadPoolExecutor {
            if (threadFactory === null) {
                threadFactory = Executors.defaultThreadFactory()
            }
            val scheduledThreadPoolExecutor = if (rejectedExecutionHandler === null) ScheduledThreadPoolExecutor(
                corePoolSize,
                threadFactory
            ) else ScheduledThreadPoolExecutor(corePoolSize, threadFactory, rejectedExecutionHandler)
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