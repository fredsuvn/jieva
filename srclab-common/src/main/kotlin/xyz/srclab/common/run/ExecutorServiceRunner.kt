package xyz.srclab.common.run

import xyz.srclab.common.base.Check
import xyz.srclab.common.base.asNotNull
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.*

open class ExecutorServiceRunner(
    private val executorService: ExecutorService
) : Runner {

    override fun <V> run(block: () -> V): Running<V> {
        return ThreadPoolRunning(executorService, block)
    }

    private class ThreadPoolRunning<V>(
        executorService: ExecutorService,
        block: () -> V
    ) : Running<V> {

        private val runningBlock = RunningBlock(block)
        private val future: Future<V>

        init {
            future = executorService.submit(runningBlock)
        }

        override fun isStart(): Boolean {
            return runningBlock.startTime != null
        }

        override fun startTime(): LocalDateTime {
            Check.checkState(runningBlock.startTime != null, "Task was not started.")
            return runningBlock.startTime.asNotNull()
        }

        override fun endTime(): LocalDateTime {
            Check.checkState(runningBlock.endTime != null, "Task was not done.")
            return runningBlock.endTime.asNotNull()
        }

        override fun cancel(mayInterruptIfRunning: Boolean): Boolean {
            val canceled = future.cancel(mayInterruptIfRunning)
            if (canceled) {
                runningBlock.endTime = LocalDateTime.now()
            }
            return canceled
        }

        override fun isCancelled(): Boolean {
            return future.isCancelled
        }

        override fun isDone(): Boolean {
            return future.isDone
        }

        override fun get(): V {
            return future.get()
        }

        override fun get(timeout: Long, unit: TimeUnit): V {
            return future.get(timeout, unit)
        }

        private class RunningBlock<V>(private val block: () -> V) : Callable<V> {

            var startTime: LocalDateTime? = null
            var endTime: LocalDateTime? = null

            override fun call(): V {
                try {
                    startTime = LocalDateTime.now()
                    return block()
                } finally {
                    endTime = LocalDateTime.now()
                }
            }
        }
    }
}

class ExecutorServiceRunnerBuilder {

    private var corePoolSize = 1
    private var maximumPoolSize = 1
    private var workQueueCapacity = Int.MAX_VALUE
    private var keepAliveTime: Duration? = null
    private var workQueue: BlockingQueue<Runnable>? = null
    private var threadFactory: ThreadFactory? = null
    private var rejectedExecutionHandler: RejectedExecutionHandler? = null

    fun corePoolSize(corePoolSize: Int) = apply {
        this.corePoolSize = corePoolSize
    }

    fun maximumPoolSize(maximumPoolSize: Int) = apply {
        this.maximumPoolSize = maximumPoolSize
        return this
    }

    fun workQueueCapacity(workQueueCapacity: Int) = apply {
        this.workQueueCapacity = workQueueCapacity
        return this
    }

    fun keepAliveTime(keepAliveTime: Duration) = apply {
        this.keepAliveTime = keepAliveTime
        return this
    }

    fun workQueue(workQueue: BlockingQueue<Runnable>) = apply {
        this.workQueue = workQueue
        return this
    }

    fun threadFactory(threadFactory: ThreadFactory) = apply {
        this.threadFactory = threadFactory
        return this
    }

    fun rejectedExecutionHandler(rejectedExecutionHandler: RejectedExecutionHandler) = apply {
        this.rejectedExecutionHandler = rejectedExecutionHandler
        return this
    }

    fun build(): ExecutorServiceRunner {
        return ExecutorServiceRunner(createExecutorService())
    }

    private fun createExecutorService(): ExecutorService {
        val keepTime: Long
        val keepUnit: TimeUnit
        if (keepAliveTime == null) {
            keepTime = 0
            keepUnit = TimeUnit.MILLISECONDS
        } else {
            keepTime = keepAliveTime!!.toNanos()
            keepUnit = TimeUnit.NANOSECONDS
        }
        if (workQueue == null) {
            workQueue = LinkedBlockingQueue(workQueueCapacity)
        }
        if (threadFactory == null) {
            threadFactory = Executors.defaultThreadFactory()
        }
        return if (rejectedExecutionHandler == null) ThreadPoolExecutor(
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
    }
}