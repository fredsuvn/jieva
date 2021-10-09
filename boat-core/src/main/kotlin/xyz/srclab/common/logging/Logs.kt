@file:JvmName("Logs")

package xyz.srclab.common.logging

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