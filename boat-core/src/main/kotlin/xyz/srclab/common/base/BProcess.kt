@file:JvmName("BProcess")

package xyz.srclab.common.base

import xyz.srclab.common.io.availableString
import xyz.srclab.common.io.readString
import xyz.srclab.common.run.Work
import xyz.srclab.common.run.WorkException
import java.nio.charset.Charset
import java.time.Duration
import java.util.concurrent.TimeUnit

/**
 * Returns [this] process as [ProcWork].
 */
fun Process.asWork(): ProcWork {
    return ProcWork(this)
}

/**
 * Starts a process as [ProcWork].
 */
fun CharSequence.startProcess(): ProcWork {
    return Runtime.getRuntime().exec(this.toString()).asWork()
}

/**
 * Starts a process as [ProcWork].
 */
fun startProcess(vararg cmd: CharSequence): ProcWork {
    return Runtime.getRuntime().exec(cmd.map { it.toString() }.toTypedArray()).asWork()
}

/**
 * [Work] for [Process].
 */
open class ProcWork(
    /**
     * The process.
     */
    val process: Process
) : Work<Process> {

    override fun isDone(): Boolean {
        return !process.isAlive
    }

    override fun isCancelled(): Boolean = false

    override fun get(): Process {
        try {
            process.waitFor()
            return process
        } catch (e: Exception) {
            throw WorkException(e)
        }
    }

    override fun get(millis: Long): Process {
        try {
            process.waitFor(millis, TimeUnit.MILLISECONDS)
            return process
        } catch (e: Exception) {
            throw WorkException(e)
        }
    }

    override fun get(duration: Duration): Process {
        try {
            process.waitFor(duration.toNanos(), TimeUnit.NANOSECONDS)
            return process
        } catch (e: Exception) {
            throw WorkException(e)
        }
    }

    /**
     * Kills the process.
     *
     * Note the killing may not block the current thread, so the result is inaccurate.
     */
    override fun cancel(mayInterruptIfRunning: Boolean): Boolean {
        try {
            val p = process.destroyForcibly()
            return !p.isAlive
        } catch (e: Exception) {
            throw WorkException(e)
        }
    }

    /**
     * Returns all output stream as `String`.
     */
    fun outputString(): String? {
        return process.inputStream?.readString(Charset.defaultCharset())
    }

    /**
     * Returns all output stream as `String`.
     */
    fun outputString(charset: Charset): String? {
        return process.inputStream?.readString(charset)
    }

    /**
     *  Returns available output stream as `String`.
     *
     *  This method will return immediately after read available bytes, rather than all bytes like [outputString].
     */
    fun availableOutputString(): String? {
        return process.inputStream?.availableString(charset("GBK"))
    }

    /**
     *  Returns available output stream as `String`.
     *
     *  This method will return immediately after read available bytes, rather than all bytes like [outputString].
     */
    fun availableOutputString(charset: Charset): String? {
        return process.inputStream?.availableString(charset)
    }

    /**
     * Returns all error stream as `String`.
     */
    fun errorString(): String? {
        return process.errorStream?.readString(Charset.defaultCharset())
    }

    /**
     * Returns all error stream as `String`.
     */
    fun errorString(charset: Charset): String? {
        return process.errorStream?.readString(charset)
    }

    /**
     *  Returns available output stream as `String`.
     *
     *  This method will return immediately after read available bytes, rather than all bytes like [errorString].
     */
    fun availableErrorString(): String? {
        return process.errorStream?.availableString(Charset.defaultCharset())
    }

    /**
     *  Returns available output stream as `String`.
     *
     *  This method will return immediately after read available bytes, rather than all bytes like [errorString].
     */
    fun availableErrorString(charset: Charset): String? {
        return process.errorStream?.availableString(charset)
    }
}