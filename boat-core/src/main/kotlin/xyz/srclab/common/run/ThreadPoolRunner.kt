package xyz.srclab.common.run

import xyz.srclab.common.base.Environment
import xyz.srclab.common.base.asNotNull
import java.time.Duration
import java.util.concurrent.*

open class ThreadPoolRunner(
    private val threadPoolExecutor: ThreadPoolExecutor
) : ExecutorServiceRunner(threadPoolExecutor) {

    val corePoolSize: Int
        @JvmName("corePoolSize") get() {
            return threadPoolExecutor.corePoolSize
        }

    val allowCoreThreadTimeOut: Boolean
        @JvmName("allowCoreThreadTimeOut") get() {
            return threadPoolExecutor.allowsCoreThreadTimeOut()
        }

    val maximumPoolSize: Int
        @JvmName("maximumPoolSize") get() {
            return threadPoolExecutor.maximumPoolSize
        }

    val keepAliveTime: Duration
        @JvmName("keepAliveTime") get() {
            return Duration.ofNanos(threadPoolExecutor.getKeepAliveTime(TimeUnit.NANOSECONDS))
        }

    val queue: BlockingQueue<Runnable>
        @JvmName("queue") get() {
            return threadPoolExecutor.queue
        }

    val poolSize: Int
        @JvmName("poolSize") get() {
            return threadPoolExecutor.poolSize
        }

    val activeCount: Int
        @JvmName("activeCount") get() {
            return threadPoolExecutor.activeCount
        }

    val largestPoolSize: Int
        @JvmName("largestPoolSize") get() {
            return threadPoolExecutor.largestPoolSize
        }

    val taskCount: Long
        @JvmName("taskCount") get() {
            return threadPoolExecutor.taskCount
        }

    val completedTaskCount: Long
        @JvmName("completedTaskCount") get() {
            return threadPoolExecutor.completedTaskCount
        }

    fun prestartCoreThread(): Boolean {
        return threadPoolExecutor.prestartCoreThread()
    }

    fun prestartAllCoreThreads(): Int {
        return threadPoolExecutor.prestartAllCoreThreads()
    }

    fun remove(task: Runnable?): Boolean {
        return threadPoolExecutor.remove(task)
    }

    fun purge() {
        threadPoolExecutor.purge()
    }

    override fun toString(): String {
        return threadPoolExecutor.toString()
    }

    class Builder {

        private var corePoolSize = Environment.availableProcessors * 2
        private var maximumPoolSize = Environment.availableProcessors * 8
        private var workQueueCapacity = maximumPoolSize * 32
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
            if (keepAliveTime === null) {
                keepTime = 0
                keepUnit = TimeUnit.MILLISECONDS
            } else {
                keepTime = keepAliveTime.asNotNull().toNanos()
                keepUnit = TimeUnit.NANOSECONDS
            }
            if (workQueue === null) {
                workQueue = LinkedBlockingQueue(workQueueCapacity)
            }
            if (threadFactory === null) {
                threadFactory = Executors.defaultThreadFactory()
            }
            val threadPoolExecutor = if (rejectedExecutionHandler === null) ThreadPoolExecutor(
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

    companion object {

        @JvmStatic
        fun newBuilder(): Builder {
            return Builder()
        }
    }
}