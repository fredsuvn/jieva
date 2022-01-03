package xyz.srclab.common.run

import xyz.srclab.common.base.asNotNull
import xyz.srclab.common.base.availableProcessors
import java.time.Duration
import java.util.concurrent.*

/**
 * A type of [Runner] use [ThreadPoolExecutor].
 */
open class ThreadPoolRunner(
    private val threadPoolExecutor: ThreadPoolExecutor
) : ExecutorServiceRunner(threadPoolExecutor) {

    val corePoolSize: Int
        get() {
            return threadPoolExecutor.corePoolSize
        }

    val allowCoreThreadTimeOut: Boolean
        get() {
            return threadPoolExecutor.allowsCoreThreadTimeOut()
        }

    val maximumPoolSize: Int
        get() {
            return threadPoolExecutor.maximumPoolSize
        }

    val keepAliveTime: Duration
        get() {
            return Duration.ofNanos(threadPoolExecutor.getKeepAliveTime(TimeUnit.NANOSECONDS))
        }

    val queue: BlockingQueue<Runnable>
        get() {
            return threadPoolExecutor.queue
        }

    val poolSize: Int
        get() {
            return threadPoolExecutor.poolSize
        }

    val activeCount: Int
        get() {
            return threadPoolExecutor.activeCount
        }

    val largestPoolSize: Int
        get() {
            return threadPoolExecutor.largestPoolSize
        }

    val taskCount: Long
        get() {
            return threadPoolExecutor.taskCount
        }

    val completedTaskCount: Long
        get() {
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

    override fun asExecutor(): ThreadPoolExecutor = threadPoolExecutor

    override fun toString(): String {
        return threadPoolExecutor.toString()
    }

    class Builder {

        private var corePoolSize = 1
        private var maximumPoolSize = availableProcessors() * 4
        private var workQueueCapacity = maximumPoolSize * 8
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
            val threadPoolExecutor = ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                keepTime,
                keepUnit,
                workQueue ?: LinkedBlockingQueue(workQueueCapacity),
                threadFactory ?: Executors.defaultThreadFactory(),
                rejectedExecutionHandler ?: ThreadPoolExecutor.AbortPolicy()
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