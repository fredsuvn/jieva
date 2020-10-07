package xyz.srclab.common.run

import xyz.srclab.common.base.Check
import xyz.srclab.common.base.asAny
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.*

class ScheduledThreadPoolRunner(
    private val scheduledThreadPoolExecutor: ScheduledThreadPoolExecutor
) : ThreadPoolRunner(scheduledThreadPoolExecutor), ScheduledRunner {

    override fun <V> schedule(block: () -> V, delay: Duration): ScheduledRunning<V> {
        return ScheduledThreadPoolRunning(scheduledThreadPoolExecutor, block, delay)
    }

    override fun <V> scheduleAtFixedRate(
        block: () -> V,
        initialDelay: Duration,
        period: Duration
    ): ScheduledRunning<V> {
        return RepeatableScheduledThreadPoolRunningAtFixedRate(scheduledThreadPoolExecutor, block, initialDelay, period)
    }

    override fun <V> scheduleWithFixedDelay(
        block: () -> V,
        initialDelay: Duration,
        period: Duration
    ): ScheduledRunning<V> {
        return RepeatableScheduledThreadPoolRunningWithFixedDelay(
            scheduledThreadPoolExecutor,
            block,
            initialDelay,
            period
        )
    }

    private open class ScheduledThreadPoolRunning<V>(
        private val scheduledThreadPoolExecutor: ScheduledThreadPoolExecutor,
        private val block: () -> V,
        private val delay: Duration
    ) : ScheduledRunning<V> {

        protected val runningBlock: RunningBlock<V>
        protected val scheduledFuture: ScheduledFuture<V>

        init {
            runningBlock = createRunningBlock()
            scheduledFuture = createScheduledFuture()
        }

        override fun isStart(): Boolean {
            return runningBlock.startTime != null
        }

        override fun startTime(): LocalDateTime {
            Check.checkState(runningBlock.startTime != null, "Task was not started.")
            return runningBlock.startTime!!
        }

        override fun endTime(): LocalDateTime {
            Check.checkState(runningBlock.endTime != null, "Task was not done.")
            return runningBlock.endTime!!
        }

        override fun cancel(mayInterruptIfRunning: Boolean): Boolean {
            val canceled = scheduledFuture.cancel(mayInterruptIfRunning)
            if (canceled) {
                runningBlock.endTime = LocalDateTime.now()
            }
            return canceled
        }

        override fun isCancelled(): Boolean {
            return scheduledFuture.isCancelled
        }

        override fun isDone(): Boolean {
            return scheduledFuture.isDone
        }

        override fun get(): V {
            return scheduledFuture.get()
        }

        override fun get(timeout: Long, unit: TimeUnit): V {
            return scheduledFuture.get(timeout, unit)
        }

        override fun compareTo(other: Delayed): Int {
            return scheduledFuture.compareTo(other)
        }

        override fun getDelay(unit: TimeUnit): Long {
            return scheduledFuture.getDelay(unit)
        }

        protected open fun createRunningBlock(): RunningBlock<V> {
            return RunningBlock(block)
        }

        protected open fun createScheduledFuture(): ScheduledFuture<V> {
            return scheduledThreadPoolExecutor.schedule(runningBlock, delay.toNanos(), TimeUnit.NANOSECONDS)
        }
    }

    private open class RepeatableScheduledThreadPoolRunning<V>(
        private val scheduledThreadPoolExecutor: ScheduledThreadPoolExecutor,
        private val block: () -> V,
        private val initialDelay: Duration,
        private val period: Duration
    ) : ScheduledThreadPoolRunning<V>(scheduledThreadPoolExecutor, block, initialDelay) {

        override fun get(): V {
            scheduledFuture.get()
            return (runningBlock as RepeatableRunningBlock).result!!
        }

        override fun get(timeout: Long, unit: TimeUnit): V {
            scheduledFuture.get(timeout, unit)
            return (runningBlock as RepeatableRunningBlock).result!!
        }

        override fun createRunningBlock(): RunningBlock<V> {
            return RepeatableRunningBlock(block)
        }
    }

    private open class RepeatableScheduledThreadPoolRunningAtFixedRate<V>(
        private val scheduledThreadPoolExecutor: ScheduledThreadPoolExecutor,
        private val block: () -> V,
        private val initialDelay: Duration,
        private val period: Duration
    ) : ScheduledThreadPoolRunning<V>(scheduledThreadPoolExecutor, block, initialDelay) {

        override fun createScheduledFuture(): ScheduledFuture<V> {
            return scheduledThreadPoolExecutor.scheduleAtFixedRate(
                { runningBlock.call() },
                initialDelay.toNanos(),
                period.toNanos(),
                TimeUnit.NANOSECONDS
            ).asAny()
        }
    }

    private open class RepeatableScheduledThreadPoolRunningWithFixedDelay<V>(
        private val scheduledThreadPoolExecutor: ScheduledThreadPoolExecutor,
        private val block: () -> V,
        private val initialDelay: Duration,
        private val period: Duration
    ) : ScheduledThreadPoolRunning<V>(scheduledThreadPoolExecutor, block, initialDelay) {

        override fun createScheduledFuture(): ScheduledFuture<V> {
            return scheduledThreadPoolExecutor.scheduleWithFixedDelay(
                { runningBlock.call() },
                initialDelay.toNanos(),
                period.toNanos(),
                TimeUnit.NANOSECONDS
            ).asAny()
        }
    }

    private open class RunningBlock<V>(private val block: () -> V) : Callable<V> {

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

    private class RepeatableRunningBlock<V>(private val block: () -> V) : RunningBlock<V>(block) {

        var result: V? = null

        override fun call(): V {
            reset()
            val superResult = super.call()
            result = superResult
            return superResult
        }

        private fun reset() {
            startTime = null
            endTime = null
        }
    }
}

class ScheduledThreadPoolRunnerBuilder {

    private var corePoolSize = 1
    private var threadFactory: ThreadFactory? = null
    private var rejectedExecutionHandler: RejectedExecutionHandler? = null

    fun corePoolSize(corePoolSize: Int): ScheduledThreadPoolRunnerBuilder {
        this.corePoolSize = corePoolSize
        return this
    }

    fun threadFactory(threadFactory: ThreadFactory): ScheduledThreadPoolRunnerBuilder {
        this.threadFactory = threadFactory
        return this
    }

    fun rejectedExecutionHandler(rejectedExecutionHandler: RejectedExecutionHandler): ScheduledThreadPoolRunnerBuilder {
        this.rejectedExecutionHandler = rejectedExecutionHandler
        return this
    }

    fun build(): ScheduledThreadPoolRunner {
        return ScheduledThreadPoolRunner(createScheduledThreadPoolExecutor())
    }

    private fun createScheduledThreadPoolExecutor(): ScheduledThreadPoolExecutor {
        if (threadFactory == null) {
            threadFactory = Executors.defaultThreadFactory()
        }
        return if (rejectedExecutionHandler == null) ScheduledThreadPoolExecutor(
            corePoolSize,
            threadFactory
        ) else ScheduledThreadPoolExecutor(corePoolSize, threadFactory, rejectedExecutionHandler)
    }
}