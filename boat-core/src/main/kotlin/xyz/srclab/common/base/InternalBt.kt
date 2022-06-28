package xyz.srclab.common.base

internal inline fun <T> anyPredicate(predicate: (obj: T) -> Boolean, vararg objs: T): Boolean {
    for (obj in objs) {
        if (predicate(obj)) {
            return true
        }
    }
    return false
}

internal inline fun <T> allPredicate(predicate: (obj: T) -> Boolean, vararg objs: T): Boolean {
    for (obj in objs) {
        if (!predicate(obj)) {
            return false
        }
    }
    return true
}