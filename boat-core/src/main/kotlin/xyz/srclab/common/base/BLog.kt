@file:JvmName("BLog")

package xyz.srclab.common.base

import java.io.PrintStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val logger: Logger = Logger.simpleLogger(callerOffset = 1)

/**
 * Trace level.
 */
fun trace(message: String?, vararg args: Any?) {
    logger.trace(message, *args)
}

/**
 * Debug level.
 */
fun debug(message: String?, vararg args: Any?) {
    logger.debug(message, *args)
}

/**
 * Info level.
 */
fun info(message: String?, vararg args: Any?) {
    logger.info(message, *args)
}

/**
 * Warn level.
 */
fun warn(message: String?, vararg args: Any?) {
    logger.warn(message, *args)
}

/**
 * Error level.
 */
fun error(message: String?, vararg args: Any?) {
    logger.error(message, *args)
}

/**
 * Logs with specified [level].
 */
fun log(level: Int, message: String?, vararg args: Any?) {
    logger.log(level, message, *args)
}

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
                        level < DEBUG_LEVEL -> "TRACE"
                        level < INFO_LEVEL -> "DEBUG"
                        level < WARN_LEVEL -> "INFO"
                        level < ERROR_LEVEL -> "WARN"
                        else -> "ERROR"
                    }
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
                val formattedMessage = message?.fastFormat(*args)
                val levelDescription = levelToString(level)
                val timestamp = TIMESTAMP_FORMATTER.format(LocalDateTime.now())

                //Computes caller stack trace.
                val callerTrace = callerStackTraceOrNull(callerOffset) { trace, findCalled ->
                    if (!findCalled) {
                        if (trace.className == SimpleLogger::class.java.name
                            || trace.className == Logger::class.java.name
                        ) {
                            return@callerStackTraceOrNull 0
                        }
                    }
                    if (findCalled) {
                        if (trace.className != SimpleLogger::class.java.name
                            && trace.className != Logger::class.java.name
                        ) {
                            return@callerStackTraceOrNull 1
                        }
                    }
                    -1
                }

                if (callerTrace === null) {
                    output.println("[$levelDescription][$timestamp]: $formattedMessage")
                } else {
                    output.println(
                        "[$levelDescription][$timestamp]" +
                            "[${callerTrace.className}.${callerTrace.methodName}" +
                            "(${callerTrace.lineNumber})]: $formattedMessage"
                    )
                }
            }

            companion object {
                private val TIMESTAMP_FORMATTER: DateTimeFormatter =
                    DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss.SSS")
            }
        }
    }
}