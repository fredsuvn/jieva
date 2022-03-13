@file:JvmName("BStatus")

package xyz.srclab.common.status

/**
 * Converts status (from [code] and [description]) to [String].
 * If [code] is null, return `code`, else return `code-description`.
 */
fun statusToString(code: Any, description: Any?): String {
    return if (description === null) code.toString() else "$code-$description"
}