package xyz.srclab.common.lang

import xyz.srclab.common.collect.arrayToStringArray
import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.Charset
import java.time.Duration
import java.util.concurrent.TimeUnit

/**
 * Represents processing of [Process].
 *
 * Note [inputStream] is [Process.getOutputStream], [outputStream] is [Process.getInputStream].
 *
 * @see [Process]
 */
interface Processing {

    @JvmDefault
    @Suppress(INAPPLICABLE_JVM_NAME)
    @get:JvmName("inputStream")
    val outputStream: OutputStream?
        get() {
            return toProcess().outputStream
        }

    @JvmDefault
    @Suppress(INAPPLICABLE_JVM_NAME)
    @get:JvmName("outputStream")
    val inputStream: InputStream?
        get() {
            return toProcess().inputStream
        }

    @JvmDefault
    @Suppress(INAPPLICABLE_JVM_NAME)
    @get:JvmName("errorStream")
    val errorStream: InputStream?
        get() {
            return toProcess().errorStream
        }

    @JvmDefault
    @Suppress(INAPPLICABLE_JVM_NAME)
    @get:JvmName("isAlive")
    val isAlive: Boolean
        get() {
            return toProcess().isAlive
        }

    @JvmDefault
    @Suppress(INAPPLICABLE_JVM_NAME)
    @get:JvmName("exitValue")
    val exitValue: Int
        get() {
            return toProcess().exitValue()
        }

    /**
     * @throws InterruptedException
     */
    @JvmDefault
    fun waitForTermination(): Int {
        return toProcess().waitFor()
    }

    /**
     * @throws InterruptedException
     */
    @JvmDefault
    fun waitForTermination(timeout: Duration): Boolean {
        return toProcess().waitFor(timeout.toNanos(), TimeUnit.NANOSECONDS)
    }

    @JvmDefault
    fun destroy() {
        destroy(false)
    }

    @JvmDefault
    fun destroy(force: Boolean) {
        val process = toProcess()
        if (force)
            process.destroy()
        else
            process.destroyForcibly()
    }

    @JvmDefault
    fun outputString(): String? {
        return inputStream?.readBytes()?.toChars()
    }

    @JvmDefault
    fun outputString(charset: Charset): String? {
        return inputStream?.readBytes()?.toChars(charset)
    }

    @JvmDefault
    fun errorString(): String? {
        return errorStream?.readBytes()?.toChars()
    }

    @JvmDefault
    fun errorString(charset: Charset): String? {
        return errorStream?.readBytes()?.toChars(charset)
    }

    fun toProcess(): Process

    companion object {

        @JvmStatic
        fun CharSequence.newProcessing(): Processing {
            return Runtime.getRuntime().exec(this.toString()).toProcessing()
        }

        @JvmStatic
        fun newProcessing(vararg command: CharSequence): Processing {
            return ProcessBuilder(*(command.arrayToStringArray())).toProcessing()
        }

        @JvmStatic
        fun newProcessing(commands: List<CharSequence>): Processing {
            return ProcessBuilder(commands.map { it.toString() }).toProcessing()
        }

        @JvmName("newProcessing")
        @JvmStatic
        fun ProcessBuilder.toProcessing(): Processing {
            return this.start().toProcessing()
        }

        @JvmName("newProcessing")
        @JvmStatic
        fun Process.toProcessing(): Processing {
            return object : Processing {

                override fun toProcess(): Process {
                    return this@toProcessing
                }

                override fun equals(other: Any?): Boolean {
                    if (other is Processing) {
                        return this@toProcessing == other.toProcess()
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