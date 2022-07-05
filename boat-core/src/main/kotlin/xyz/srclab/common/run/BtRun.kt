/**
 * Execution utilities.
 */
@file:JvmName("BtRun")

package xyz.srclab.common.run

import xyz.srclab.common.base.JavaFunction
import java.time.Duration
import java.util.concurrent.*
import java.util.function.Consumer

/**
 * Returns [ExecutorService] implementation which always executes in current thread.
 *
 * Note any timeout for this ExecutorService is invalid.
 *
 * @see SyncExecutorService
 */
fun newSyncExecutor(): ExecutorService {
    return SyncExecutorService()
}

/**
 * Returns [ExecutorService] implementation which always executes **not** in current thread.
 */
fun newAsyncExecutor(): ExecutorService {
    return Executors.newCachedThreadPool()
}

/**
 * Executes [runnable] with given [context].
 */
@Throws(RejectedExecutionException::class, NullPointerException::class)
fun <C> Executor.execute(context: C, runnable: Consumer<C>) {
    this.execute {
        runnable.accept(context)
    }
}

/**
 * Executes [runnable] with given [context].
 */
@Throws(RejectedExecutionException::class, NullPointerException::class)
fun <C> ExecutorService.execute(context: C, runnable: Consumer<C>) {
    this.submit {
        runnable.accept(context)
    }
}

/**
 * Executes [callable] with given [context].
 */
@Throws(RejectedExecutionException::class, NullPointerException::class)
fun <C, V> ExecutorService.execute(context: C, callable: JavaFunction<C, V>): Future<V> {
    return this.submit(Callable {
        callable.apply(context)
    })
}

/**
 * Gets result of [Future] without checked exception.
 *
 * @throws [CancellationException] if the computation was cancelled
 * @throws [ExecutionException] if the computation threw an exception
 * @throws [InterruptedException] if the current thread was interrupted while waiting
 */
fun <V> Future<V>.getResult(): V {
    return this.get()
}

/**
 * Gets result of [Future] without checked exception in [timeout].
 *
 * @throws [CancellationException] if the computation was cancelled
 * @throws [ExecutionException] if the computation threw an exception
 * @throws [InterruptedException] if the current thread was interrupted while waiting
 * @throws [TimeoutException] – if the wait timed out
 */
fun <V> Future<V>.getResult(timeout: Duration): V {
    return this.get(timeout.toNanos(), TimeUnit.NANOSECONDS)
}

/**
 * Gets result of [Future] without checked exception in [timeout].
 *
 * @throws [CancellationException] if the computation was cancelled
 * @throws [ExecutionException] if the computation threw an exception
 * @throws [InterruptedException] if the current thread was interrupted while waiting
 * @throws [TimeoutException] – if the wait timed out
 */
fun <V> Future<V>.getResult(timeout: Long, timeUnit: TimeUnit): V {
    return this.get(timeout, timeUnit)
}

/**
 * Gets result of [Future] without checked exception in [timeoutMillis] millis.
 *
 * @throws [CancellationException] if the computation was cancelled
 * @throws [ExecutionException] if the computation threw an exception
 * @throws [InterruptedException] if the current thread was interrupted while waiting
 * @throws [TimeoutException] – if the wait timed out
 */
fun <V> Future<V>.getResult(timeoutMillis: Long): V {
    return this.get(timeoutMillis, TimeUnit.MILLISECONDS)
}