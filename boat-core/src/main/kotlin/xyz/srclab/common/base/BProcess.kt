@file:JvmName("BProcess")

package xyz.srclab.common.base

import xyz.srclab.common.base.ProcessWork.Companion.toProcessWork
import xyz.srclab.common.io.availableString
import xyz.srclab.common.io.readString
import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.Charset
import java.time.Duration
import java.util.concurrent.TimeUnit

fun CharSequence.startProcess(): ProcessWork {
    return Runtime.getRuntime().exec(this.toString()).toProcessWork()
}

fun startProcess(vararg cmd: CharSequence): ProcessWork {
    return Runtime.getRuntime().exec(cmd.map { it.toString() }.toTypedArray()).toProcessWork()
}

/**
 * Represents process-work associated with [Process].
 */
interface ProcessWork {

    val process: Process

    val outputStream: OutputStream?
        get() {
            return process.outputStream
        }

    val inputStream: InputStream?
        get() {
            kotlin.run { }
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
    fun await() = apply {
        process.waitFor()
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
        return inputStream?.readString()
    }

    /**
     * Returns all output stream as `String`.
     */
    fun outputString(charset: Charset): String? {
        return inputStream?.readString(charset)
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
        return errorStream?.readString()
    }

    /**
     * Returns all error stream as `String`.
     */
    fun errorString(charset: Charset): String? {
        return errorStream?.readString(charset)
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

    companion object {

        @JvmName("of")
        @JvmStatic
        fun Process.toProcessWork(): ProcessWork {
            return ProcessWorkImpl(this)
        }

        private class ProcessWorkImpl(override val process: Process) : ProcessWork {

            override fun equals(other: Any?): Boolean {
                if (other is ProcessWork) {
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
    }
}