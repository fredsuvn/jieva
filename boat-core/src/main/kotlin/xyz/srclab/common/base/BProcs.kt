@file:JvmName("BProcs")

package xyz.srclab.common.base

import xyz.srclab.common.io.availableString
import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.Charset
import java.time.Duration
import java.util.concurrent.TimeUnit

fun CharSequence.startProcess(): BProcessing {
    return Runtime.getRuntime().exec(this.toString()).toProcessing()
}

fun startProcess(vararg cmd: String): BProcessing {
    return Runtime.getRuntime().exec(cmd).toProcessing()
}

fun ProcessBuilder.startProcess(): BProcessing {
    return this.start().toProcessing()
}

fun Process.toProcessing(): BProcessing {
    return ProcessingImpl(this)
}

/**
 * Represents processing of [Process].
 *
 * Note [inputStream] is from [Process.getOutputStream], [outputStream] is from [Process.getInputStream].
 *
 * @see [Process]
 */
interface BProcessing {

    val process: Process

    val outputStream: OutputStream?
        get() {
            return process.outputStream
        }

    val inputStream: InputStream?
        get() {
            return process.inputStream
        }

    val errorStream: InputStream?
        get() {
            return process.errorStream
        }

    val isAlive: Boolean
        get() {
            return process.isAlive
        }

    val exitValue: Int
        get() {
            return process.exitValue()
        }

    /**
     * @throws InterruptedException
     */
    fun await(): Int {
        return process.waitFor()
    }

    /**
     * @throws InterruptedException
     */
    fun await(timeout: Duration): Boolean {
        return process.waitFor(timeout.toNanos(), TimeUnit.NANOSECONDS)
    }

    fun destroy() {
        destroy(false)
    }

    fun destroy(force: Boolean) {
        if (force)
            process.destroy()
        else
            process.destroyForcibly()
    }

    /**
     * Returns all output stream as `String`.
     */
    fun outputString(): String? {
        return inputStream?.readBytes()?.encodeToString()
    }

    /**
     * Returns all output stream as `String`.
     */
    fun outputString(charset: Charset): String? {
        return inputStream?.readBytes()?.encodeToString(charset)
    }

    /**
     *  Returns available output stream as `String`.
     *
     *  This method will return immediately after read available bytes, rather than all bytes like [outputString].
     */
    fun availableOutputString(): String? {
        return inputStream?.availableString()
    }

    /**
     *  Returns available output stream as `String`.
     *
     *  This method will return immediately after read available bytes, rather than all bytes like [outputString].
     */
    fun availableOutputString(charset: Charset): String? {
        return inputStream?.availableString(charset)
    }

    /**
     * Returns all error stream as `String`.
     */
    fun errorString(): String? {
        return errorStream?.readBytes()?.encodeToString()
    }

    /**
     * Returns all error stream as `String`.
     */
    fun errorString(charset: Charset): String? {
        return errorStream?.readBytes()?.encodeToString(charset)
    }

    /**
     *  Returns available output stream as `String`.
     *
     *  This method will return immediately after read available bytes, rather than all bytes like [errorString].
     */
    fun availableErrorString(): String? {
        return errorStream?.availableString()
    }

    /**
     *  Returns available output stream as `String`.
     *
     *  This method will return immediately after read available bytes, rather than all bytes like [errorString].
     */
    fun availableErrorString(charset: Charset): String? {
        return errorStream?.availableString(charset)
    }
}

private class ProcessingImpl(override val process: Process) : BProcessing {

    override fun equals(other: Any?): Boolean {
        if (other is BProcessing) {
            return this.process == other.process
        }
        return false
    }

    override fun hashCode(): Int {
        return this.process.hashCode()
    }

    override fun toString(): String {
        return this.process.toString()
    }
}