@file:JvmName("BStatus")

package xyz.srclab.common.status

fun statusToString(code: Any, description: Any?): String {
    return if (description === null) code.toString() else "$code-$description"
}