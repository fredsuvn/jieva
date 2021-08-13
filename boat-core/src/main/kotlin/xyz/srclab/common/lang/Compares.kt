@file:JvmName("Compares")

package xyz.srclab.common.lang

/**
 * [Comparator] of which arguments will be seen as [Comparable] type.
 */
@JvmField
val COMPARABLE_COMPARATOR: Comparator<*> =
    Comparator { o1: Any?, o2: Any? ->
        val c1: Comparable<Any?> = o1.asAny()
        val c2: Comparable<Any?> = o2.asAny()
        c1.compareTo(c2)
    }

/**
 * [Comparator] which Compares the order in inheritance tree in natural order,
 * parent type is greater than subtype, so the subclass comes before the parent.
 */
@JvmField
val INHERITANCE_COMPARATOR: Comparator<Class<*>> = Comparator { c1, c2 ->
    if (c1 == c2) 0 else if (c1.isAssignableFrom(c2)) 1 else if (c2.isAssignableFrom(c1)) -1 else 0
}

/**
 * Returns a [Comparator] of which arguments will be seen as [Comparable] type.
 *
 * @see COMPARABLE_COMPARATOR
 */
fun <T> comparableComparator(): Comparator<T> {
    return COMPARABLE_COMPARATOR.asAny()
}

/**
 * Returns a [Comparator] which Compares the order in inheritance tree in natural order,
 * parent type is greater than subtype, so the subclass comes before the parent.
 *
 * @see INHERITANCE_COMPARATOR
 */
fun inheritanceComparator(): Comparator<Class<*>> {
    return INHERITANCE_COMPARATOR
}

/**
 * Returns value between given range.
 */
fun <T : Comparable<T>> T.between(min: T, max: T): T {
    if (this < min) {
        return min
    }
    if (this > max) {
        return max
    }
    return this
}