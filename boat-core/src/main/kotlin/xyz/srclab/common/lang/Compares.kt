@file:JvmName("Compares")

package xyz.srclab.common.lang

/**
 * A [Comparator] which arguments as [Comparable] type.
 */
@JvmField
val AS_COMPARABLE_COMPARATOR: Comparator<*> =
    Comparator { o1: Any?, o2: Any? ->
        val c1: Comparable<Any?> = o1.asAny()
        val c2: Comparable<Any?> = o2.asAny()
        c1.compareTo(c2)
    }

/**
 * Compares the inheritance tree in natural order,
 * parent type is greater then subtype, so the subclass comes before the parent.
 */
@JvmField
val INHERITANCE_COMPARATOR: Comparator<Class<*>> = Comparator { c1, c2 ->
    if (c1 == c2) 0 else if (c1.isAssignableFrom(c2)) 1 else if (c2.isAssignableFrom(c1)) -1 else 0
}

fun <T : Comparable<T>> T.inBounds(min: T, max: T): T {
    if (this < min) {
        return min
    }
    if (this > max) {
        return max
    }
    return this
}

/**
 * Returns a [Comparator] which arguments as [Comparable] type.
 *
 * @see AS_COMPARABLE_COMPARATOR
 */
fun <T> asComparableComparator(): Comparator<T> {
    return AS_COMPARABLE_COMPARATOR.asAny()
}