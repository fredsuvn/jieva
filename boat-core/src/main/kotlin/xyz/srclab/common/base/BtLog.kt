/**
 * Log utilities.
 */
@file:JvmName("BtLog")

package xyz.srclab.common.base

import xyz.srclab.common.base.Logger.Companion.LEVEL_INFO
import xyz.srclab.common.base.Logger.Companion.LEVEL_TRACE
import xyz.srclab.common.linkedHashMap
import xyz.srclab.common.linkedList
import java.time.LocalDateTime
import java.util.*
import java.util.function.Supplier

private var defaultLogger: Logger = Logger.newBuilder().build()

/**
 * Gets default logger.
 */
fun defaultLogger(): Logger {
    return defaultLogger
}

/**
 * Sets default logger.
 */
fun setDefaultLogger(logger: Logger) {
    defaultLogger = logger
}

/**
 * Logs in [Logger.LEVEL_TRACE] level with default logger.
 */
@JvmName("trace")
fun traceLog(message: CharSequence?, vararg args: Any?) {
    defaultLogger.trace(message, *args)
}

/**
 * Logs in [Logger.LEVEL_DEBUG] level with default logger.
 */
@JvmName("debug")
fun debugLog(message: CharSequence?, vararg args: Any?) {
    defaultLogger.debug(message, *args)
}

/**
 * Logs in [Logger.LEVEL_INFO] level with default logger.
 */
@JvmName("info")
fun infoLog(message: CharSequence?, vararg args: Any?) {
    defaultLogger.info(message, *args)
}

/**
 * Logs in [Logger.LEVEL_WARN] level with default logger.
 */
@JvmName("warn")
fun warnLog(message: CharSequence?, vararg args: Any?) {
    defaultLogger.warn(message, *args)
}

/**
 * Logs in [Logger.LEVEL_ERROR] level with default logger.
 */
@JvmName("error")
fun errorLog(message: CharSequence?, vararg args: Any?) {
    defaultLogger.error(message, *args)
}

/**
 * Using default logger to log in given [level].
 *
 * @see Logger
 */
fun log(level: Int, message: CharSequence?, vararg args: Any?) {
    defaultLogger.log(level, message, *args)
}

/**
 * Returns string of [level] of [Logger]:
 *
 * * If the [level] is equal to one of level constant such as [LEVEL_INFO], return name of the value "INFO";
 * * If the [level] is less than [LEVEL_TRACE], return "TRACE-";
 * * If the [level] is greater than one of level constant such as [LEVEL_INFO] but less than next level,
 * return the low level name followed by a plus sign: "INFO+"
 */
@JvmName("levelToString")
fun logLevelToString(level: Int): String {
    return if (level < LEVEL_TRACE) {
        "TRACE-"
    } else if (level == LEVEL_TRACE) {
        "TRACE"
    } else if (level in (LEVEL_TRACE + 1) until Logger.LEVEL_DEBUG) {
        "TRACE+"
    } else if (level == Logger.LEVEL_DEBUG) {
        "DEBUG"
    } else if (level in (Logger.LEVEL_DEBUG + 1) until LEVEL_INFO) {
        "DEBUG+"
    } else if (level == LEVEL_INFO) {
        "INFO"
    } else if (level in (LEVEL_INFO + 1) until Logger.LEVEL_WARN) {
        "INFO+"
    } else if (level == Logger.LEVEL_WARN) {
        "WARN"
    } else if (level in (Logger.LEVEL_WARN + 1) until Logger.LEVEL_ERROR) {
        "WARN+"
    } else if (level == Logger.LEVEL_ERROR) {
        "ERROR"
    } else {
        "ERROR+"
    }
}

/**
 * Logger interface.
 *
 * @see Logger.Builder
 */
interface Logger {

    val level: Int

    /**
     * Returns whether level of this logger can output [LEVEL_TRACE] level message.
     */
    fun isTraceEnabled(): Boolean {
        return level <= LEVEL_TRACE
    }

    /**
     * Returns whether level of this logger can output [LEVEL_DEBUG] level message.
     */
    fun isDebugEnabled(): Boolean {
        return level <= LEVEL_DEBUG
    }

    /**
     * Returns whether level of this logger can output [LEVEL_INFO] level message.
     */
    fun isInfoEnabled(): Boolean {
        return level <= LEVEL_INFO
    }

    /**
     * Returns whether level of this logger can output [LEVEL_WARN] level message.
     */
    fun isWarnEnabled(): Boolean {
        return level <= LEVEL_WARN
    }

    /**
     * Returns whether level of this logger can output [LEVEL_ERROR] level message.
     */
    fun isErrorEnabled(): Boolean {
        return level <= LEVEL_ERROR
    }

    /**
     * Logs in [Logger.LEVEL_TRACE] level.
     */
    fun trace(message: CharSequence?, vararg args: Any?) {
        log(LEVEL_TRACE, message, *args)
    }

    /**
     * Logs in [Logger.LEVEL_DEBUG] level.
     */
    fun debug(message: CharSequence?, vararg args: Any?) {
        log(LEVEL_DEBUG, message, *args)
    }

    /**
     * Logs in [Logger.LEVEL_INFO] level.
     */
    fun info(message: CharSequence?, vararg args: Any?) {
        log(LEVEL_INFO, message, *args)
    }

    /**
     * Logs in [Logger.LEVEL_WARN] level.
     */
    fun warn(message: CharSequence?, vararg args: Any?) {
        log(LEVEL_WARN, message, *args)
    }

    /**
     * Logs in [Logger.LEVEL_ERROR] level.
     */
    fun error(message: CharSequence?, vararg args: Any?) {
        log(LEVEL_ERROR, message, *args)
    }

    /**
     * Logs in given [level].
     */
    fun log(level: Int, message: CharSequence?, vararg args: Any?)

    /**
     * Formatter to format log message and args.
     */
    fun interface Formatter {

        /**
         * Formats [message] with [args] and [stackTrace] into [dest].
         */
        fun format(
            dest: Appendable,
            level: Int,
            stackTrace: StackTraceElement?,
            message: CharSequence?,
            vararg args: Any?
        )

        companion object {

            /**
             * Default log formatter:
             *
             * ```
             * [yyyy-MM-dd HH:mm:ss.SSS] [level] [thread] class.name:line - message
             * ```
             */
            @JvmField
            val DEFAULT: Formatter = Default

            private object Default : Formatter {
                override fun format(
                    dest: Appendable,
                    level: Int,
                    stackTrace: StackTraceElement?,
                    message: CharSequence?,
                    vararg args: Any?
                ) {
                    //Date
                    dest.append('[')
                    dest.append(BtProps.datePattern().format(LocalDateTime.now()))
                    dest.append(']')
                    //Level
                    dest.append(' ')
                    dest.append('[')
                    dest.append(logLevelToString(level))
                    dest.append(']')
                    if (stackTrace !== null) {
                        //Thread
                        dest.append(' ')
                        dest.append('[')
                        dest.append(currentThread().name)
                        dest.append(']')
                        //Class
                        dest.append(' ')
                        dest.append(stackTrace.className)
                        dest.append(':')
                        dest.append(stackTrace.lineNumber.toString())
                    }
                    //Message
                    dest.append(' ')
                    dest.append('-')
                    dest.append(' ')
                    if (message !== null) {
                        dest.append(fastFormat(message, args))
                    } else {
                        dest.append(BtProps.nullString())
                    }
                    dest.append('\n')
                }
            }
        }
    }

    /**
     * Builder for [Logger] and [MapLogger].
     */
    open class Builder {

        /**
         * Logger level, default is [LEVEL_INFO].
         */
        open var level: Int = LEVEL_INFO

        /**
         * Output, default is [System.out].
         */
        open var output: Appendable = System.out

        /**
         * Log formatter, default is [Formatter.DEFAULT].
         */
        open var formatter: Formatter = Formatter.DEFAULT

        /**
         * Built-in map for [MapLogger], default is [LinkedHashMap].
         */
        open var builtinMap: MutableMap<Any, MutableList<Any?>>? = null

        /**
         * List generator for [builtinMap], default is to get synchronized [LinkedList].
         */
        open var listGen: Supplier<MutableList<Any?>> = LIST_GEN

        /**
         * Sets logger level.
         */
        fun level(level: Int) = apply {
            this.level = level
        }

        /**
         * Sets output.
         */
        fun output(output: Appendable) = apply {
            this.output = output
        }

        /**
         * Sets formatter.
         */
        fun formatter(formatter: Formatter) = apply {
            this.formatter = formatter
        }

        /**
         * Builds and returns a new [Logger].
         */
        fun build(): Logger {
            return LoggerImpl(level, output, formatter)
        }

        /**
         * Builds and returns a new [MapLogger].
         */
        fun buildMap(): MapLogger {
            val builtinMap = this.builtinMap
            val map = if (builtinMap === null) linkedHashMap() else builtinMap
            return MapLoggerImpl(level, output, formatter, map, listGen)
        }

        private open class LoggerImpl(
            override val level: Int,
            private val output: Appendable,
            private val formatter: Formatter,
        ) : Logger {
            override fun log(level: Int, message: CharSequence?, vararg args: Any?) {
                if (level < this.level) {
                    return
                }
                val callerTrace = callerStackTrace(1) { trace, findLogger ->
                    if (!findLogger) {
                        if (trace.className == this.javaClass.name
                            || trace.className == Logger::class.java.name
                            || trace.className == MapLogger::class.java.name
                        ) {
                            return@callerStackTrace true
                        }
                    }
                    if (findLogger) {
                        if (trace.className != this.javaClass.name
                            && trace.className != Logger::class.java.name
                            && trace.className != MapLogger::class.java.name
                        ) {
                            return@callerStackTrace true
                        }
                    }
                    false
                }
                formatter.format(output, level, callerTrace, message, *args)
            }
        }

        private class MapLoggerImpl(
            override val level: Int,
            output: Appendable,
            formatter: Formatter,
            private val map: MutableMap<Any, MutableList<Any?>>,
            private val listGen: Supplier<MutableList<Any?>>,
        ) : LoggerImpl(level, output, formatter), MapLogger {

            override fun logMap(level: Int, key: Any, value: Any?) {
                if (level < this.level) {
                    return
                }
                val list = map.computeIfAbsent(key) { listGen.get() }
                list.add(value)
            }

            override fun getMap(): Map<Any, List<Any?>> {
                return Collections.unmodifiableMap(map)
            }

            override fun getValues(key: Any): List<Any?> {
                return Collections.unmodifiableList(map.computeIfAbsent(key) { listGen.get() })
            }

            override fun clearMap(key: Any) {
                map.remove(key)
            }

            override fun clearMap() {
                map.clear()
            }
        }

        companion object {
            private val LIST_GEN: Supplier<MutableList<Any?>> = Supplier { Collections.synchronizedList(linkedList()) }
        }
    }

    companion object {

        const val LEVEL_TRACE = 0
        const val LEVEL_DEBUG = 1000
        const val LEVEL_INFO = 2000
        const val LEVEL_WARN = 3000
        const val LEVEL_ERROR = 4000

        /**
         * Returns new builder for [Logger] and [MapLogger].
         */
        @JvmStatic
        fun newBuilder(): Builder {
            return Builder()
        }
    }
}

/**
 * A subtype of [Logger],
 * which can log message in key-value style to store into the built-in Map,
 * and can get the message from the Map.
 *
 * This logger is very convenient in testing to check stack trace info.
 */
interface MapLogger : Logger {

    /**
     * Map-logs in [Logger.LEVEL_TRACE] level.
     */
    fun traceMap(key: Any, value: Any?) {
        logMap(Logger.LEVEL_TRACE, key, value)
    }

    /**
     * Map-logs in [Logger.LEVEL_DEBUG] level.
     */
    fun debugMap(key: Any, value: Any?) {
        logMap(Logger.LEVEL_DEBUG, key, value)
    }

    /**
     * Map-logs in [Logger.LEVEL_INFO] level.
     */
    fun infoMap(key: Any, value: Any?) {
        logMap(Logger.LEVEL_INFO, key, value)
    }

    /**
     * Map-logs in [Logger.LEVEL_WARN] level.
     */
    fun warnMap(key: Any, value: Any?) {
        logMap(Logger.LEVEL_WARN, key, value)
    }

    /**
     * Map-logs in [Logger.LEVEL_ERROR] level.
     */
    fun errorMap(key: Any, value: Any?) {
        logMap(Logger.LEVEL_ERROR, key, value)
    }

    /**
     * Map-logs in given [level].
     */
    fun logMap(level: Int, key: Any, value: Any?)

    /**
     * Returns built-in log-map.
     * Note any operation of map-log will influence the returned map.
     */
    fun getMap(): Map<Any, List<Any?>>

    /**
     * Returns values of [key] in built-in log-map.
     * Note any operation of map-log will influence the returned list.
     */
    fun getValues(key: Any): List<Any?>

    /**
     * Clears values of [key] in built-in log-map.
     */
    fun clearMap(key: Any)

    /**
     * Clears built-in log-map.
     */
    fun clearMap()
}