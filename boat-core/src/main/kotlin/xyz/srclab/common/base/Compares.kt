@file:JvmName("Compares")

package xyz.srclab.common.base

/**
 * Represents `less than` in compare.
 */
const val LESS_THAN = -1

/**
 * Represents `equal to` in compare.
 */
const val EQUAL_TO = 0

/**
 * Represents `greater than` in compare.
 */
const val GREATER_THAN = 1

/**
 * [Comparator] of which arguments will be seen as [Comparable] type.
 */
@JvmField
val COMPARABLE_COMPARATOR: Comparator<Comparable<*>> =
    Comparator { o1, o2 ->
        val c1: Comparable<Any?> = o1.asTyped()
        val c2: Comparable<Any?> = o2.asTyped()
        c1.compareTo(c2)
    }

/**
 * [Comparator] which compares the order in inheritance tree in natural order (low to high):
 *
 * * Parent type is greater than subtype;
 * * If there is no inheritance relationship in two classes, second param is greater than first;
 */
@JvmField
val INHERITANCE_COMPARATOR: Comparator<Class<*>> =
    Comparator { c1, c2 ->
        if (c1 == c2) 0 else if (c1.isAssignableFrom(c2)) 1 else -1
    }

/**
 * Returns a [Comparator] of which arguments will be seen as [Comparable] type.
 *
 * @see COMPARABLE_COMPARATOR
 */
fun <T> comparableComparator(): Comparator<T> {
    return COMPARABLE_COMPARATOR.asTyped()
}

/**
 * Returns a [Comparator] which compares the order in inheritance tree in natural order (low to high):
 *
 * * Parent type is greater than subtype;
 * * If there is no inheritance relationship in two classes, second param is greater than first;
 *
 * @see INHERITANCE_COMPARATOR
 */
fun inheritanceComparator(): Comparator<Class<*>> {
    return INHERITANCE_COMPARATOR
}

/**
 * Ensures that this value is not less than [min].
 */
fun <T : Comparable<T>> T.atLeast(min: T): T {
    return this.coerceAtLeast(min)
}

/**
 * Ensures that this value is not greater than [max].
 */
fun <T : Comparable<T>> T.atMost(max: T): T {
    return this.coerceAtMost(max)
}

/**
 * Returns whether given value between the given range:
 *
 * ```
 * value >= min && value <= max
 * ```
 */
fun <T : Comparable<T>> T.isBetween(min: T, max: T): Boolean {
    return this in min..max
}

/**
 * Ensures that this value lies in the specified range.
 */
fun <T : Comparable<T>> T.between(min: T, max: T): T {
    return this.coerceIn(min, max)
}