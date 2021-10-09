package xyz.srclab.common.logging

import xyz.srclab.common.base.CharsFormat.Companion.fastFormat
import xyz.srclab.common.base.SIMPLE_LOCAL_DATE_TIME_FORMATTER
import xyz.srclab.common.base.asAny
import xyz.srclab.common.base.callerStackTrace
import xyz.srclab.common.base.stackTraceToString
import java.io.PrintStream
import java.time.LocalDateTime

/**
 * Logger.
 */
interface Logger {

    val level: Int

    /**
     * Trace level.
     */
    fun trace(message: String?, vararg args: Any?) {
        log(TRACE_LEVEL, message, *args)
    }

    /**
     * Debug level.
     */
    fun debug(message: String?, vararg args: Any?) {
        log(DEBUG_LEVEL, message, *args)
    }

    /**
     * Info level.
     */
    fun info(message: String?, vararg args: Any?) {
        log(INFO_LEVEL, message, *args)
    }

    /**
     * Warn level.
     */
    fun warn(message: String?, vararg args: Any?) {
        log(WARN_LEVEL, message, *args)
    }

    /**
     * Error level.
     */
    fun error(message: String?, vararg args: Any?) {
        log(ERROR_LEVEL, message, *args)
    }

    /**
     * Logs with specified [level].
     */
    fun log(level: Int, message: String?, vararg args: Any?)

    companion object {

        const val TRACE_LEVEL = 0
        const val DEBUG_LEVEL = 1000
        const val INFO_LEVEL = 2000
        const val WARN_LEVEL = 3000
        const val ERROR_LEVEL = 4000

        @JvmOverloads
        @JvmStatic
        fun simpleLogger(level: Int = DEBUG_LEVEL, output: PrintStream = System.out): Logger {
            return SimpleLogger(level, output)
        }

        private class SimpleLogger(
            override val level: Int,
            private val output: PrintStream
        ) : Logger {
            override fun log(level: Int, message: String?, vararg args: Any?) {
                if (level < this.level) {
                    return
                }

                if (message === null) {
                    return output.println(message as String?)
                }

                fun levelToString(level: Int): String {
                    return when {
                        level <= DEBUG_LEVEL -> "TRACE"
                        level <= INFO_LEVEL -> "DEBUG"
                        level <= WARN_LEVEL -> "INFO"
                        level <= ERROR_LEVEL -> "WARN"
                        else -> "ERROR"
                    }
                }

                val arguments: Array<Any?> = args.asAny()
                val length = arguments.size
                if (arguments.isNotEmpty()) {
                    val lastArgument = arguments[length - 1]
                    if (lastArgument is Throwable) {
                        arguments[length - 1] = lastArgument.stackTraceToString()
                    }
                }
                val callerStackTrace = callerStackTrace { e, findCalled ->
                    if (!findCalled) {
                        if (e.className == SimpleLogger::class.java.name || e.className == Logger::class.java.name) {
                            return@callerStackTrace 0
                        }
                    }
                    if (findCalled) {
                        if (e.className != SimpleLogger::class.java.name && e.className != Logger::class.java.name) {
                            return@callerStackTrace 1
                        }
                    }
                    -1
                }
                val levelDescription = levelToString(level)
                val timestamp = SIMPLE_LOCAL_DATE_TIME_FORMATTER.format(LocalDateTime.now())
                val formattedMessage = message.fastFormat(*arguments)
                if (callerStackTrace === null) {
                    output.println("[$levelDescription][$timestamp] - $formattedMessage")
                } else {
                    output.println("[$levelDescription][$timestamp]" +
                        "[${callerStackTrace.className}.${callerStackTrace.methodName}" +
                        "(${callerStackTrace.lineNumber})] - $formattedMessage")
                }
            }
        }
    }
}