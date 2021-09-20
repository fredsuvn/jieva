@file:JvmName("Statuses")

package xyz.srclab.common.status

fun List<Any?>.joinToDescription(): String? {
    if (this.isEmpty()) {
        return null
    }
    if (this.size == 1) {
        return this[0].toString()
    }
    val builder = StringBuilder(this[0].toString())
    for (i in 1 until this.size) {
        builder.append("[").append(this[i]).append("]")
    }
    return builder.toString()
}