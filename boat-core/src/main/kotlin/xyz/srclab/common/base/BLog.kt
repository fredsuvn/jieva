@file:JvmName("BLog")

package xyz.srclab.common.base

import java.io.OutputStream
import java.nio.charset.Charset
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private var defaultLogger: LogPrinter = LogPrinter.simpleLogger(callerOffset = 1)

/**
 * Sets the default logger for `BLog`.
 */
fun setDefaultLogLogger(logger: LogPrinter) {
    defaultLogger = logger
}

/**
 * Sets the default logger for `BLog`.
 */
fun getDefaultLogLogger(): LogPrinter {
    return defaultLogger
}

/**
 * Logs trace level.
 */
fun trace(message: String?, vararg args: Any?) {
    defaultLogger.trace(message, *args)
}

/**
 * Logs debug level.
 */
fun debug(message: String?, vararg args: Any?) {
    defaultLogger.debug(message, *args)
}

/**
 * Logs info level.
 */
fun info(message: String?, vararg args: Any?) {
    defaultLogger.info(message, *args)
}

/**
 * Logs warn level.
 */
fun warn(message: String?, vararg args: Any?) {
    defaultLogger.warn(message, *args)
}

/**
 * Logs error level.
 */
fun error(message: String?, vararg args: Any?) {
    defaultLogger.error(message, *args)
}

/**
 * Logs with specified message level.
 */
fun log(messageLevel: Int, message: String?, vararg args: Any?) {
    defaultLogger.log(messageLevel, message, *args)
}

/**
 * Simple logger interface (and default implementation), used for demo and simple test codes.
 * It is named `LogPrinter` rather than `Logger` to avoid polluting the name `Logger`,
 * which is used by other logging system.
 *
 * This logger and its default implementation is simple and light,
 * better to use mature logging system such `SLF4j` and `common-logging` if you need richer functions.
 */
interface LogPrinter {

    val level: Int

    /**
     * Returns whether level of this logger can output [TRACE_LEVEL] level message.
     */
    fun isTraceEnabled(): Boolean {
        return level <= TRACE_LEVEL
    }

    /**
     * Returns whether level of this logger can output [DEBUG_LEVEL] level message.
     */
    fun isDebugEnabled(): Boolean {
        return level <= DEBUG_LEVEL
    }

    /**
     * Returns whether level of this logger can output [INFO_LEVEL] level message.
     */
    fun isInfoEnabled(): Boolean {
        return level <= INFO_LEVEL
    }

    /**
     * Returns whether level of this logger can output [WARN_LEVEL] level message.
     */
    fun isWarnEnabled(): Boolean {
        return level <= WARN_LEVEL
    }

    /**
     * Returns whether level of this logger can output [ERROR_LEVEL] level message.
     */
    fun isErrorEnabled(): Boolean {
        return level <= ERROR_LEVEL
    }

    /**
     * Logs trace level.
     */
    fun trace(message: String?, vararg args: Any?) {
        log(TRACE_LEVEL, message, *args)
    }

    /**
     * Logs debug level.
     */
    fun debug(message: String?, vararg args: Any?) {
        log(DEBUG_LEVEL, message, *args)
    }

    /**
     * Logs info level.
     */
    fun info(message: String?, vararg args: Any?) {
        log(INFO_LEVEL, message, *args)
    }

    /**
     * Logs warn level.
     */
    fun warn(message: String?, vararg args: Any?) {
        log(WARN_LEVEL, message, *args)
    }

    /**
     * Logs error level.
     */
    fun error(message: String?, vararg args: Any?) {
        log(ERROR_LEVEL, message, *args)
    }

    /**
     * Logs with specified message level.
     */
    fun log(messageLevel: Int, message: String?, vararg args: Any?)

    companion object {

        const val TRACE_LEVEL = 0
        const val DEBUG_LEVEL = 1000
        const val INFO_LEVEL = 2000
        const val WARN_LEVEL = 3000
        const val ERROR_LEVEL = 4000

        /**
         * Creates a simple implementation of [LogPrinter].
         */
        @JvmOverloads
        @JvmStatic
        fun simpleLogger(
            level: Int = DEBUG_LEVEL,
            output: OutputStream = System.out,
            callerOffset: Int = 0,
            charset: Charset = defaultCharset(),
        ): LogPrinter {
            return SimpleLogPrinter(level, output, callerOffset, charset)
        }

        private class SimpleLogPrinter(
            override val level: Int,
            private val output: OutputStream,
            private val callerOffset: Int,
            private val charset: Charset,
        ) : LogPrinter {

            override fun log(messageLevel: Int, message: String?, vararg args: Any?) {
                if (messageLevel < this.level) {
                    return
                }

                //Configures args.
                val arguments: Array<Any?> = args.asTyped()
                val length = arguments.size
                if (arguments.isNotEmpty()) {
                    val lastArgument = arguments[length - 1]
                    if (lastArgument is Throwable) {
                        arguments[length - 1] = lastArgument.stackTraceToString()
                    }
                }

                //Builds message.
                val formattedMessage = message?.fastFormat(*arguments) ?: "null"
                val timestamp = timeFormatter.format(LocalDateTime.now())

                //Computes caller stack trace.
                val callerTrace = callerStackTrace(callerOffset) { trace, findLogger ->
                    if (!findLogger) {
                        if (trace.className == SimpleLogPrinter::class.java.name
                            || trace.className == LogPrinter::class.java.name
                        ) {
                            return@callerStackTrace true
                        }
                    }
                    if (findLogger) {
                        if (trace.className != SimpleLogPrinter::class.java.name
                            && trace.className != LogPrinter::class.java.name
                        ) {
                            return@callerStackTrace true
                        }
                    }
                    false
                }

                //Level string
                val levelString: ByteArray = run {
                    when (messageLevel) {
                        TRACE_LEVEL -> "TRACE".toByteArray()
                        DEBUG_LEVEL -> "DEBUG".toByteArray()
                        INFO_LEVEL -> "INFO".toByteArray()
                        WARN_LEVEL -> "WARN".toByteArray()
                        ERROR_LEVEL -> "ERROR".toByteArray()
                        else -> "LOG".toByteArray()
                    }
                }

                if (callerTrace === null) {
                    writeBytes(
                        levelString, timestamp.toByteArray(), formattedMessage.charsToBytes(charset),
                        false,
                        EMPTY_BYTES, EMPTY_BYTES, EMPTY_BYTES
                    )
                } else {
                    writeBytes(
                        levelString, timestamp.toByteArray(), formattedMessage.charsToBytes(charset),
                        true,
                        callerTrace.className.charsToBytes(charset),
                        callerTrace.methodName.charsToBytes(charset),
                        "${callerTrace.lineNumber}".toByteArray()
                    )
                }
            }

            private fun writeBytes(
                levelString: ByteArray,
                timestamp: ByteArray,
                message: ByteArray,
                detail: Boolean,
                className: ByteArray,
                methodName: ByteArray,
                lineNumber: ByteArray,
            ) {
                output.write('['.code)
                output.write(levelString)
                output.write(']'.code)
                output.write('['.code)
                output.write(timestamp)
                output.write(']'.code)
                if (detail) {
                    output.write('['.code)
                    output.write(className)
                    output.write('.'.code)
                    output.write(methodName)
                    output.write('.'.code)
                    output.write(lineNumber)
                    output.write(']'.code)
                }
                output.write(':'.code)
                output.write(' '.code)
                output.write(message)
                output.write(LINE_SEPARATOR)
            }

            companion object {
                private val EMPTY_BYTES: ByteArray = byteArrayOf()
                private val LINE_SEPARATOR: ByteArray = System.lineSeparator().toByteArray()
                private val timeFormatter: DateTimeFormatter =
                    DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss.SSS")
            }
        }
    }
}