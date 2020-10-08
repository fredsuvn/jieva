package xyz.srclab.common.run

import xyz.srclab.common.base.asNotNull
import java.time.Duration
import java.util.concurrent.*

class ScheduledThreadPoolRunner(
    private val scheduledThreadPoolExecutor: ScheduledThreadPoolExecutor
) : ScheduledExecutorServiceRunner(scheduledThreadPoolExecutor) {

    fun corePoolSize(): Int {
        return scheduledThreadPoolExecutor.corePoolSize
    }

    fun prestartCoreThread(): Boolean {
        return scheduledThreadPoolExecutor.prestartCoreThread()
    }

    fun prestartAllCoreThreads(): Int {
        return scheduledThreadPoolExecutor.prestartAllCoreThreads()
    }

    fun allowCoreThreadTimeOut(): Boolean {
        return scheduledThreadPoolExecutor.allowsCoreThreadTimeOut()
    }

    fun maximumPoolSize(): Int {
        return scheduledThreadPoolExecutor.maximumPoolSize
    }

    fun keepAliveTime(): Duration {
        return Duration.ofNanos(scheduledThreadPoolExecutor.getKeepAliveTime(TimeUnit.NANOSECONDS))
    }

    fun queue(): BlockingQueue<Runnable?>? {
        return scheduledThreadPoolExecutor.queue
    }

    fun remove(task: Runnable?): Boolean {
        return scheduledThreadPoolExecutor.remove(task)
    }

    fun purge() {
        scheduledThreadPoolExecutor.purge()
    }

    fun poolSize(): Int {
        return scheduledThreadPoolExecutor.poolSize
    }

    fun activeCount(): Int {
        return scheduledThreadPoolExecutor.activeCount
    }

    fun largestPoolSize(): Int {
        return scheduledThreadPoolExecutor.largestPoolSize
    }

    fun taskCount(): Long {
        return scheduledThreadPoolExecutor.taskCount
    }

    fun completedTaskCount(): Long {
        return scheduledThreadPoolExecutor.completedTaskCount
    }

    fun continueExistingPeriodicTasksAfterShutdownPolicy(): Boolean {
        return scheduledThreadPoolExecutor.continueExistingPeriodicTasksAfterShutdownPolicy
    }

    fun executeExistingDelayedTasksAfterShutdownPolicy(): Boolean {
        return scheduledThreadPoolExecutor.executeExistingDelayedTasksAfterShutdownPolicy
    }

    fun removeOnCancelPolicy(): Boolean {
        return scheduledThreadPoolExecutor.removeOnCancelPolicy
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
            if (threadFactory == null) {
                threadFactory = Executors.defaultThreadFactory()
            }
            val scheduledThreadPoolExecutor = if (rejectedExecutionHandler == null) ScheduledThreadPoolExecutor(
                corePoolSize,
                threadFactory
            ) else ScheduledThreadPoolExecutor(corePoolSize, threadFactory, rejectedExecutionHandler)
            if (keepAliveTime != null) {
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
}