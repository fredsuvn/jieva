@file:JvmName("BBoolean")

package xyz.srclab.common.base

/**
 * If [this] is boolean type ,return itself;
 * if null, return false;
 * else calls the [toCharSeq] then return whether the charSeq equals to the string "true", case ignored.
 */
fun Any?.toBoolean(): Boolean {
    return when (this) {
        null -> false
        is Boolean -> this
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

/**
 * Returns 1 if [this] is true, else 0.
 */
fun Boolean.toInt(): Int {
    return if (this) 1 else 0
}

/**
 * Returns 1 if [this] is not-null and true, else 0.
 */
fun Boolean?.toInt(): Int {
    return if (this !== null && this) 1 else 0
}