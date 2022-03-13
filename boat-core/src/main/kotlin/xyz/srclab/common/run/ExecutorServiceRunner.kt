package xyz.srclab.common.run

import xyz.srclab.common.run.RunWork.Companion.toRunWork
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService

/**
 * [Runner] implemented by [ExecutorService].
 */
open class ExecutorServiceRunner(
    /**
     * Returns the executor service.
     */
    val executorService: ExecutorService
) : Runner {

    override fun <V> submit(task: Callable<V>): RunWork<V> {
        return executorService.submit(task).toRunWork()
    }

    override fun submit(task: Runnable): RunWork<*> {
        return executorService.submit(task).toRunWork()
    }

    override fun run(task: Runnable) {
        executorService.execute(task)
    }
}