package xyz.srclab.common.run

import xyz.srclab.common.base.asNotNull
import java.time.Duration
import java.util.concurrent.*

open class ThreadPoolRunner(
    private val threadPoolExecutor: ThreadPoolExecutor
) : ExecutorServiceRunner(threadPoolExecutor) {

    fun corePoolSize(): Int {
        return threadPoolExecutor.corePoolSize
    }

    fun prestartCoreThread(): Boolean {
        return threadPoolExecutor.prestartCoreThread()
    }

    fun prestartAllCoreThreads(): Int {
        return threadPoolExecutor.prestartAllCoreThreads()
    }

    fun allowCoreThreadTimeOut(): Boolean {
        return threadPoolExecutor.allowsCoreThreadTimeOut()
    }

    fun maximumPoolSize(): Int {
        return threadPoolExecutor.maximumPoolSize
    }

    fun keepAliveTime(): Duration {
        return Duration.ofNanos(threadPoolExecutor.getKeepAliveTime(TimeUnit.NANOSECONDS))
    }

    fun queue(): BlockingQueue<Runnable?>? {
        return threadPoolExecutor.queue
    }

    fun remove(task: Runnable?): Boolean {
        return threadPoolExecutor.remove(task)
    }

    fun purge() {
        threadPoolExecutor.purge()
    }

    fun poolSize(): Int {
        return threadPoolExecutor.poolSize
    }

    fun activeCount(): Int {
        return threadPoolExecutor.activeCount
    }

    fun largestPoolSize(): Int {
        return threadPoolExecutor.largestPoolSize
    }

    fun taskCount(): Long {
        return threadPoolExecutor.taskCount
    }

    fun completedTaskCount(): Long {
        return threadPoolExecutor.completedTaskCount
    }

    override fun toString(): String {
        return threadPoolExecutor.toString()
    }

    class Builder {

        private var corePoolSize = 1
        private var maximumPoolSize = 1
        private var workQueueCapacity = Int.MAX_VALUE
        private var keepAliveTime: Duration? = null
        private var workQueue: BlockingQueue<Runnable>? = null
        private var threadFactory: ThreadFactory? = null
        private var rejectedExecutionHandler: RejectedExecutionHandler? = null

        // Set after newing
        private var allowCoreThreadTimeOut = false

        fun corePoolSize(corePoolSize: Int) = apply {
            this.corePoolSize = corePoolSize
        }

        fun maximumPoolSize(maximumPoolSize: Int) = apply {
            this.maximumPoolSize = maximumPoolSize
        }

        fun workQueueCapacity(workQueueCapacity: Int) = apply {
            this.workQueueCapacity = workQueueCapacity
        }

        fun keepAliveTime(keepAliveTime: Duration) = apply {
            this.keepAliveTime = keepAliveTime
        }

        fun workQueue(workQueue: BlockingQueue<Runnable>) = apply {
            this.workQueue = workQueue
        }

        fun threadFactory(threadFactory: ThreadFactory) = apply {
            this.threadFactory = threadFactory
        }

        fun rejectedExecutionHandler(rejectedExecutionHandler: RejectedExecutionHandler) = apply {
            this.rejectedExecutionHandler = rejectedExecutionHandler
        }

        fun allowCoreThreadTimeOut(allowCoreThreadTimeOut: Boolean) = apply {
            this.allowCoreThreadTimeOut = allowCoreThreadTimeOut
        }

        fun build(): ThreadPoolRunner {
            return ThreadPoolRunner(createThreadPoolExecutor())
        }

        private fun createThreadPoolExecutor(): ThreadPoolExecutor {
            val keepTime: Long
            val keepUnit: TimeUnit
            if (keepAliveTime == null) {
                keepTime = 0
                keepUnit = TimeUnit.MILLISECONDS
            } else {
                keepTime = keepAliveTime.asNotNull().toNanos()
                keepUnit = TimeUnit.NANOSECONDS
            }
            if (workQueue == null) {
                workQueue = LinkedBlockingQueue(workQueueCapacity)
            }
            if (threadFactory == null) {
                threadFactory = Executors.defaultThreadFactory()
            }
            val threadPoolExecutor = if (rejectedExecutionHandler == null) ThreadPoolExecutor(
                corePoolSize, maximumPoolSize, keepTime, keepUnit, workQueue, threadFactory
            ) else ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                keepTime,
                keepUnit,
                workQueue,
                threadFactory,
                rejectedExecutionHandler
            )
            threadPoolExecutor.allowCoreThreadTimeOut(allowCoreThreadTimeOut)
            return threadPoolExecutor
        }
    }
}