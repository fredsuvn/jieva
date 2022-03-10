package xyz.srclab.common.run

import xyz.srclab.common.base.defaultSerialVersion
import java.io.Serializable
import java.time.Duration
import java.util.concurrent.TimeoutException

/**
 * Work represents the future or promise for an asynchronous task.
 */
interface Work<T> {

    /**
     * Returns whether this work is done.
     */
    fun isDone(): Boolean

    /**
     * Returns whether this work is cancelled.
     */
    fun isCancelled(): Boolean

    /**
     * Awaits util the computation has completed, returns the result.
     */
    @Throws(WorkException::class)
    fun getResult(): T

    /**
     * Awaits util the computation has completed or timeout for [millis],
     * returns the result or throws [WorkException] caused by [TimeoutException].
     */
    @Throws(WorkException::class)
    fun getResult(millis: Long): T

    /**
     * Awaits util the computation has completed or timeout for [duration],
     * returns the result or throws [WorkException] caused by [TimeoutException].
     */
    @Throws(WorkException::class)
    fun getResult(duration: Duration): T

    /**
     * Cancels or interrupts the task associated by this [Work].
     * It is equivalent to `cancel(true)`.
     *
     * @return true if success
     */
    @Throws(WorkException::class)
    fun cancel(): Boolean {
        return cancel(true)
    }

    /**
     * Cancels or interrupts the task associated by this [Work].
     *
     * @return true if success
     */
    @Throws(WorkException::class)
    fun cancel(mayInterruptIfRunning: Boolean): Boolean
}

/**
 * Work exception, a global exception for [Work].
 */
open class WorkException @JvmOverloads constructor(
    message: String? = null, cause: Throwable? = null
) : RuntimeException(message, cause), Serializable {

    constructor(cause: Throwable?) : this(null, cause)

    companion object {
        private val serialVersionUID: Long = defaultSerialVersion()
    }
}