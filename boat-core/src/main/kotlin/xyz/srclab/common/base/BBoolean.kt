@file:JvmName("BBoolean")

package xyz.srclab.common.base

/**
 * Converts [this] to boolean:
 *
 * * If this is null, return false;
 * * If this is boolean ,return itself;
 * * If this is number, return `this != 0`;
 * * Else calls the [toCharSeq] then return whether the char sequence of result equals to the "true", case ignored.
 */
fun Any?.toBoolean(): Boolean {
    return when (this) {
        null -> false
        is Boolean -> this
        is Number -> this.toInt() != 0
        else -> this.toCharSeq().contentEquals("true", true)
    }
}

/**
 * Returns true if any of the result of [toBoolean] for each [objs] is true.
 */
fun anyTrue(vararg objs: Any?): Boolean {
    for (obj in objs) {
        if (obj.toBoolean()) {
            return true
        }
    }
    return false
}

/**
 * Returns true if all the result of [toBoolean] for each [objs] is true.
 */
fun allTrue(vararg objs: Any?): Boolean {
    for (obj in objs) {
        if (!obj.toBoolean()) {
            return false
        }
    }
    return true
}

/**
 * Returns true if any of the result of [toBoolean] for each [objs] is false.
 */
fun anyFalse(vararg objs: Any?): Boolean {
    for (obj in objs) {
        if (!obj.toBoolean()) {
            return true
        }
    }
    return false
}

/**
 * Returns true if all the result of [toBoolean] for each [objs] is false.
 */
fun allFalse(vararg objs: Any?): Boolean {
    for (obj in objs) {
        if (obj.toBoolean()) {
            return false
        }
    }
    return true
}