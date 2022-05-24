@file:JvmName("BBoolean")

package xyz.srclab.common.base

/**
 * Converts [this] to boolean:
 *
 * * If this is null, return false;
 * * If this is boolean, return itself;
 * * If this is number, return `false` if this is 0, else `true`;
 * * If this is [CharSequence], return `false` if this is "false", else `true`;
 * * Else `true`.
 */
fun Any?.toBoolean(): Boolean {
    return when (this) {
        null -> false
        is Boolean -> this
        is Number -> this.toInt() != 0
        is CharSequence -> !this.contentEquals("false")
        else -> true
    }
}

/**
 * Returns true if any of the result of [toBoolean] for each [objs] is true.
 */
fun anyTrue(vararg objs: Any?): Boolean {
    return anyPredicate({ obj -> obj.toBoolean() }, *objs)
}

/**
 * Returns true if any of the result of [toBoolean] for each [objs] is false.
 */
fun anyFalse(vararg objs: Any?): Boolean {
    return anyPredicate({ obj -> !obj.toBoolean() }, *objs)
}

/**
 * Returns true if all the result of [toBoolean] for each [objs] is true.
 */
fun allTrue(vararg objs: Any?): Boolean {
    return allPredicate({ obj -> obj.toBoolean() }, *objs)
}

/**
 * Returns true if all the result of [toBoolean] for each [objs] is false.
 */
fun allFalse(vararg objs: Any?): Boolean {
    return allPredicate({ obj -> !obj.toBoolean() }, *objs)
}