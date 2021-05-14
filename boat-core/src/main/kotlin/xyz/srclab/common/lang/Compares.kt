@file:JvmName("Compares")
@file:JvmMultifileClass

package xyz.srclab.common.lang

fun <T : Comparable<T>> T.inBounds(min: T, max: T): T {
    if (this < min) {
        return min
    }
    if (this > max) {
        return max
    }
    return this
}

fun <T> castSelfComparableComparator(): Comparator<T> {
    return CAST_SELF_COMPARABLE_COMPARATOR.asAny()
}

private val CAST_SELF_COMPARABLE_COMPARATOR: Comparator<*> by lazy {
    Comparator { o1: Any?, o2: Any? ->
        val c1: Comparable<Any?> = o1.asAny()
        val c2: Comparable<Any?> = o2.asAny()
        c1.compareTo(c2)
    }
}