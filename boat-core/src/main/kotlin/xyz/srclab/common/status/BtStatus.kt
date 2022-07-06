/**
 * Status utilities.
 */
@file:JvmName("BtStatus")

package xyz.srclab.common.status

/**
 * Converts status to [String]:
 * * If [Status.message] is null, return `code`, else return `code-description`.
 */
@JvmName("toString")
fun statusToString(status: Status<out Any, *, *>): String {
    return statusToString(status.code, status.message)
}

/**
 * Converts status to [String]:
 * * If [message] is null, return `code`, else return `code-description`.
 */
@JvmName("toString")
fun statusToString(code: Any, message: Any?): String {
    return if (message === null) code.toString() else "$code-$message"
}

/**
 * Builds message consists of original [message] and [additional] message.
 */
@JvmName("buildMessages")
fun buildStatusMessages(message: String?, additional: String): String {
    return if (message === null) additional else "$message[$additional]"
}