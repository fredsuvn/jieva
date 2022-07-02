/**
 * Log utilities.
 */
@file:JvmName("LogBt")

package xyz.srclab.common.base

import java.io.OutputStream
import java.nio.charset.Charset
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.ConcurrentHashMap

private var defaultLogger: Logger = Logger.newLogger(callerOffset = 1)

/**
 * Sets default logger.
 */
fun setDefaultLogLogger(logger: Logger) {
    defaultLogger = logger
}

/**
 * Gets default logger for.
 */
fun getDefaultLogLogger(): Logger {
    return defaultLogger
}

/**
 * Logs as trace level.
 */
fun trace(message: String?, vararg args: Any?) {
    defaultLogger.trace(message, *args)
}

/**
 * Logs as debug level.
 */
fun debug(message: String?, vararg args: Any?) {
    defaultLogger.debug(message, *args)
}

/**
 * Logs as info level.
 */
fun info(message: String?, vararg args: Any?) {
    defaultLogger.info(message, *args)
}

/**
 * Logs as warn level.
 */
fun warn(message: String?, vararg args: Any?) {
    defaultLogger.warn(message, *args)
}

/**
 * Logs as error level.
 */
fun error(message: String?, vararg args: Any?) {
    defaultLogger.error(message, *args)
}

/**
 * Logs as specified message level.
 */
fun log(messageLevel: Int, message: String?, vararg args: Any?) {
    defaultLogger.log(messageLevel, message, *args)
}

/**
 * Simple light-weight logger interface.
 */
interface Logger {

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
     * Logs as trace level.
     */
    fun trace(message: String?, vararg args: Any?) {
        log(TRACE_LEVEL, message, *args)
    }

    /**
     * Logs as debug level.
     */
    fun debug(message: String?, vararg args: Any?) {
        log(DEBUG_LEVEL, message, *args)
    }

    /**
     * Logs as info level.
     */
    fun info(message: String?, vararg args: Any?) {
        log(INFO_LEVEL, message, *args)
    }

    /**
     * Logs as warn level.
     */
    fun warn(message: String?, vararg args: Any?) {
        log(WARN_LEVEL, message, *args)
    }

    /**
     * Logs as error level.
     */
    fun error(message: String?, vararg args: Any?) {
        log(ERROR_LEVEL, message, *args)
    }

    /**
     * Logs as specified message level.
     */
    fun log(messageLevel: Int, message: String?, vararg args: Any?)

    companion object {

        const val TRACE_LEVEL = 0
        const val DEBUG_LEVEL = 1000
        const val INFO_LEVEL = 2000
        const val WARN_LEVEL = 3000
        const val ERROR_LEVEL = 4000

        /**
         * Creates a simple implementation of [Logger].
         */
        @JvmOverloads
        @JvmStatic
        fun newLogger(
            level: Int = DEBUG_LEVEL,
            output: OutputStream = System.out,
            callerOffset: Int = 0,
            charset: Charset = defaultCharset(),
        ): Logger {
            return SimpleLogger(level, output, callerOffset, charset)
        }
    }
}

/**
 * [StackLogger] logger is used to log and store `key-value` message as `stack message` with `xxxStack` methods,
 * and can get them with [getStack] to statistics.
 */
interface StackLogger : Logger {

    /**
     * Logs `stack message` as trace level.
     */
    fun traceStack(key: Any, value: Any?) {
        logStack(Logger.TRACE_LEVEL, key, value)
    }

    /**
     * Logs `stack message` as debug level.
     */
    fun debugStack(key: Any, value: Any?) {
        logStack(Logger.DEBUG_LEVEL, key, value)
    }

    /**
     * Logs `stack message` as info level.
     */
    fun infoStack(key: Any, value: Any?) {
        logStack(Logger.INFO_LEVEL, key, value)
    }

    /**
     * Logs `stack message` as warn level.
     */
    fun warnStack(key: Any, value: Any?) {
        logStack(Logger.WARN_LEVEL, key, value)
    }

    /**
     * Logs `stack message` as error level.
     */
    fun errorStack(key: Any, value: Any?) {
        logStack(Logger.ERROR_LEVEL, key, value)
    }

    /**
     * Logs `stack message` as specified message level.
     */
    fun logStack(messageLevel: Int, key: Any, value: Any?)

    /**
     * Gets `stack messages` with [key].
     */
    fun getStack(key: Any): List<Any?>

    companion object {
        /**
         * Creates a simple implementation of [StackLogger].
         */
        @JvmOverloads
        @JvmStatic
        fun newLogger(
            level: Int = Logger.DEBUG_LEVEL,
            output: OutputStream = System.out,
            callerOffset: Int = 0,
            charset: Charset = defaultCharset(),
        ): StackLogger {
            return SimpleStackLogger(level, output, callerOffset, charset)
        }
    }
}

private abstract class AbstractSimpleLogger(
    override val level: Int,
    private val output: OutputStream,
    private val callerOffset: Int,
    private val charset: Charset,
) : Logger {

    override fun log(messageLevel: Int, message: String?, vararg args: Any?) {
        if (messageLevel < this.level) {
            return
        }

        //Configures args.
        val arguments: Array<Any?> = args.asType()
        val length = arguments.size
        if (arguments.isNotEmpty()) {
            val lastArgument = arguments[length - 1]
            if (lastArgument is Throwable) {
                arguments[length - 1] = lastArgument.stackTraceToString()
            }
        }

        //Builds message.
        val formattedMessage = if (message === null) defaultNullString() else format(message, *arguments)
        val timestamp = timeFormatter.format(LocalDateTime.now())

        //Computes caller stack trace.
        val callerTrace = callerStackTrace(callerOffset) { trace, findLogger ->
            if (!findLogger) {
                if (trace.className == this.javaClass.name
                    || trace.className == Logger::class.java.name
                ) {
                    return@callerStackTrace true
                }
            }
            if (findLogger) {
                if (trace.className != this.javaClass.name
                    && trace.className != Logger::class.java.name
                ) {
                    return@callerStackTrace true
                }
            }
            false
        }

        //Level string
        val levelString: ByteArray = run {
            when (messageLevel) {
                Logger.TRACE_LEVEL -> "TRACE".getBytes()
                Logger.DEBUG_LEVEL -> "DEBUG".getBytes()
                Logger.INFO_LEVEL -> "INFO".getBytes()
                Logger.WARN_LEVEL -> "WARN".getBytes()
                Logger.ERROR_LEVEL -> "ERROR".getBytes()
                else -> "LOG".getBytes()
            }
        }

        if (callerTrace === null) {
            writeBytes(
                levelString, timestamp.getBytes(), formattedMessage.getBytes(charset),
                false,
                EMPTY_BYTES, EMPTY_BYTES, EMPTY_BYTES
            )
        } else {
            writeBytes(
                levelString, timestamp.getBytes(), formattedMessage.getBytes(charset),
                true,
                callerTrace.className.getBytes(charset),
                callerTrace.methodName.getBytes(charset),
                "${callerTrace.lineNumber}".getBytes()
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
        private val LINE_SEPARATOR: ByteArray = System.lineSeparator().getBytes()
        private val timeFormatter: DateTimeFormatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss.SSS")
    }
}

private class SimpleLogger(
    override val level: Int,
    private val output: OutputStream,
    private val callerOffset: Int,
    private val charset: Charset,
) : AbstractSimpleLogger(level, output, callerOffset, charset)

private class SimpleStackLogger(
    override val level: Int,
    private val output: OutputStream,
    private val callerOffset: Int,
    private val charset: Charset,
) : AbstractSimpleLogger(level, output, callerOffset, charset), StackLogger {

    private val stackMap: MutableMap<Any, MutableList<Any?>> = ConcurrentHashMap()

    override fun logStack(messageLevel: Int, key: Any, value: Any?) {
        if (messageLevel < this.level) {
            return
        }
        stackMap.compute(key) { _, v ->
            if (v === null) {
                val l = LinkedList<Any?>()
                l
            } else {
                v.add(value)
                v
            }
        }
    }

    override fun getStack(key: Any): List<Any?> {
        return stackMap[key] ?: emptyList()
    }
}