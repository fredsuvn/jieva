package xyz.srclab.common.lang

import xyz.srclab.common.io.availableString
import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.Charset
import java.time.Duration
import java.util.concurrent.TimeUnit

/**
 * Represents processing of [Process].
 *
 * Note [inputStream] is from [Process.getOutputStream], [outputStream] is from [Process.getInputStream].
 *
 * @see [Process]
 */
interface Processing {

    @get:JvmName("process")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val process: Process

    @get:JvmName("inputStream")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val outputStream: OutputStream?
        get() {
            return process.outputStream
        }

    @get:JvmName("outputStream")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val inputStream: InputStream?
        get() {
            return process.inputStream
        }

    @get:JvmName("errorStream")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val errorStream: InputStream?
        get() {
            return process.errorStream
        }

    @get:JvmName("isAlive")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val isAlive: Boolean
        get() {
            return process.isAlive
        }

    @get:JvmName("exitValue")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val exitValue: Int
        get() {
            return process.exitValue()
        }

    /**
     * @throws InterruptedException
     */
    fun waitForTermination(): Int {
        return process.waitFor()
    }

    /**
     * @throws InterruptedException
     */
    fun waitForTermination(timeout: Duration): Boolean {
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
        return inputStream?.readBytes()?.toChars()
    }

    /**
     * Returns all output stream as `String`.
     */
    fun outputString(charset: Charset): String? {
        return inputStream?.readBytes()?.toChars(charset)
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
        return errorStream?.readBytes()?.toChars()
    }

    /**
     * Returns all error stream as `String`.
     */
    fun errorString(charset: Charset): String? {
        return errorStream?.readBytes()?.toChars(charset)
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

        @JvmName("start")
        @JvmStatic
        fun CharSequence.startProcessing(): Processing {
            return Runtime.getRuntime().exec(this.toString()).toProcessing()
        }

        @JvmStatic
        fun start(vararg command: CharSequence): Processing {
            return ProcessBuilder(*(command.toStringArray())).toProcessing()
        }

        @JvmStatic
        fun start(commands: List<CharSequence>): Processing {
            return ProcessBuilder(commands.map { it.toString() }).toProcessing()
        }

        @JvmName("of")
        @JvmStatic
        fun ProcessBuilder.toProcessing(): Processing {
            return this.start().toProcessing()
        }

        @JvmName("of")
        @JvmStatic
        fun Process.toProcessing(): Processing {
            return object : Processing {

                override val process: Process = this@toProcessing

                override fun equals(other: Any?): Boolean {
                    if (other is Processing) {
                        return this@toProcessing == other.process
                    }
                    return false
                }

                override fun hashCode(): Int {
                    return this@toProcessing.hashCode()
                }

                override fun toString(): String {
                    return this@toProcessing.toString()
                }
            }
        }
    }
}