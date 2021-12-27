@file:JvmName("BCompares")

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
val CAST_COMPARABLE_COMPARATOR: Comparator<Comparable<*>> =
    Comparator { o1, o2 ->
        val c1: Comparable<Any?> = o1.asTyped()
        val c2: Comparable<Any?> = o2.asTyped()
        c1.compareTo(c2)
    }

/**
 * [Comparator] which compares the order in inheritance tree in natural order (low to high):
 *
 * * Parent type is greater than subtype;
 * * If there is no inheritance relationship in two classes, keep encounter order;
 */
@JvmField
val CLASS_INHERITANCE_COMPARATOR: Comparator<Class<*>> =
    Comparator { c1, c2 ->
        if (c1 == c2) 0 else if (c1.isAssignableFrom(c2)) 1 else -1
    }

/**
 * Returns a [Comparator] of which arguments will be seen as [Comparable] type.
 *
 * @see CAST_COMPARABLE_COMPARATOR
 */
fun <T> castComparableComparator(): Comparator<T> {
    return CAST_COMPARABLE_COMPARATOR.asTyped()
}

/**
 * Returns a [Comparator] which compares the order in inheritance tree in natural order (low to high):
 *
 * * Parent type is greater than subtype;
 * * If there is no inheritance relationship in two classes, keep encounter order;
 *
 * @see CLASS_INHERITANCE_COMPARATOR
 */
fun <T> classInheritanceComparator(): Comparator<Class<T>> {
    return CLASS_INHERITANCE_COMPARATOR.asTyped()
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
 * Ensures that this value lies in the specified range.
 */
fun <T : Comparable<T>> T.atBetween(min: T, max: T): T {
    return this.coerceIn(min, max)
}

/**
 * Ensures that this value is not less than [min].
 */
fun <T> T.atLeast(min: T, comparator: Comparator<T>): T {
    return if (comparator.compare(this, min) <= 0) min else this
}

/**
 * Ensures that this value is not greater than [max].
 */
fun <T> T.atMost(max: T, comparator: Comparator<T>): T {
    return if (comparator.compare(this, max) >= 0) max else this
}

/**
 * Ensures that this value lies in the specified range.
 */
fun <T> T.atBetween(min: T, max: T, comparator: Comparator<T>): T {
    if (comparator.compare(this, min) <= 0) {
        return min
    }
    if (comparator.compare(this, max) >= 0) {
        return max
    }
    return this
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
 * Returns whether given value between the given range:
 *
 * ```
 * value >= min && value <= max
 * ```
 */
fun <T> T.isBetween(min: T, max: T, comparator: Comparator<T>): Boolean {
    return comparator.compare(this, min) >= 0 && comparator.compare(this, max) <= 0
}