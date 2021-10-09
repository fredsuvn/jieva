package xyz.srclab.common.logging

import xyz.srclab.common.base.*
import xyz.srclab.common.base.CharsFormat.Companion.fastFormat
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
        fun simpleLogger(level: Int = DEBUG_LEVEL, output: PrintStream = System.out, callerOffset: Int = 0): Logger {
            return SimpleLogger(level, output, callerOffset)
        }

        private class SimpleLogger(
            override val level: Int,
            private val output: PrintStream,
            private val callerOffset: Int
        ) : Logger {
            override fun log(level: Int, message: String?, vararg args: Any?) {
                if (level < this.level) {
                    return
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

                //Configures args.
                val arguments: Array<Any?> = args.asAny()
                val length = arguments.size
                if (arguments.isNotEmpty()) {
                    val lastArgument = arguments[length - 1]
                    if (lastArgument is Throwable) {
                        arguments[length - 1] = lastArgument.stackTraceToString()
                    }
                }

                //Builds message.
                val formattedMessage = if (message === null) null else message.fastFormat(*arguments)
                val levelDescription = levelToString(level)
                val timestamp = SIMPLE_LOCAL_DATE_TIME_FORMATTER.format(LocalDateTime.now())

                //Computes stack trace.
                val stackTrace = currentThread().stackTrace
                if (stackTrace.isNullOrEmpty()) {
                    output.println("[$levelDescription][$timestamp] - $formattedMessage")
                    return
                }
                var callerTraceIndex = -1
                for (i in stackTrace.indices) {
                    val currentTrace = stackTrace[i]
                    if (currentTrace.className != SimpleLogger::class.java.name
                        && currentTrace.className != Logger::class.java.name) {
                        callerTraceIndex = i + callerOffset
                        break
                    }
                }

                if (isIndexInBounds(callerTraceIndex, 0, stackTrace.size)) {
                    val callerTrace = stackTrace[callerTraceIndex]
                    output.println("[$levelDescription][$timestamp]" +
                        "[${callerTrace.className}.${callerTrace.methodName}" +
                        "(${callerTrace.lineNumber})] - $formattedMessage")
                } else {
                    output.println("[$levelDescription][$timestamp] - $formattedMessage")
                }
            }
        }
    }
}