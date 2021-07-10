package xyz.srclab.common.run

import xyz.srclab.common.lang.asAny
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

/**
 * A type of [Runner] always use new thread.
 */
object SyncRunner : Runner {

    override fun <V> run(task: () -> V): Running<V> {
        return RunningImpl(task)
    }

    override fun run(task: Runnable): Running<*> {
        return RunningImpl<Any>(task)
    }

    override fun <V> fastRun(task: () -> V) {
        task()
    }

    override fun fastRun(task: Runnable) {
        task.run()
    }

    override fun execute(command: Runnable) {
        fastRun(command)
    }

    private class RunningImpl<V> : Running<V> {

        private var result: V? = null

        override var startTime: LocalDateTime? = null

        override var endTime: LocalDateTime? = null

        constructor(task: () -> V) {
            startTime = LocalDateTime.now()
            result = task()
            endTime = LocalDateTime.now()
        }

        constructor(task: Runnable) {
            startTime = LocalDateTime.now()
            task.run()
            endTime = LocalDateTime.now()
        }

        override fun get(): V {
            return result.asAny()
        }

        override fun get(timeout: Long, unit: TimeUnit): V {
            return get()
        }

        override fun cancel(mayInterruptIfRunning: Boolean): Boolean {
            return false
        }

        override fun isCancelled(): Boolean {
            return false
        }

        override fun isDone(): Boolean {
            return isEnd
        }
    }
}