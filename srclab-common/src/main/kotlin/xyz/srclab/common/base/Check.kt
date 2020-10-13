@file:JvmName("Check")
@file:JvmMultifileClass

package xyz.srclab.common.base

import java.util.*

fun Boolean.checkArguments() {
    if (!this) {
        throw IllegalArgumentException()
    }
}

fun Boolean.checkArguments(message: String?) {
    if (!this) {
        throw IllegalArgumentException(message)
    }
}

fun Boolean.checkArguments(messagePattern: String?, vararg messageArgs: Any?) {
    if (!this) {
        throw IllegalArgumentException(format(messagePattern, messageArgs))
    }
}

fun Boolean.checkState() {
    if (!this) {
        throw IllegalStateException()
    }
}

fun Boolean.checkState(message: String?) {
    if (!this) {
        throw IllegalStateException(message)
    }
}

fun Boolean.checkState(messagePattern: String?, vararg messageArgs: Any?) {
    if (!this) {
        throw IllegalStateException(format(messagePattern, messageArgs))
    }
}

fun Boolean.checkNull() {
    if (!this) {
        throw NullPointerException()
    }
}

fun Boolean.checkNull(message: String?) {
    if (!this) {
        throw NullPointerException(message)
    }
}

fun Boolean.checkNull(messagePattern: String?, vararg messageArgs: Any?) {
    if (!this) {
        throw NullPointerException(format(messagePattern, messageArgs))
    }
}

fun Boolean.checkSupported() {
    if (!this) {
        throw UnsupportedOperationException()
    }
}

fun Boolean.checkSupported(message: String?) {
    if (!this) {
        throw UnsupportedOperationException(message)
    }
}

fun Boolean.checkSupported(messagePattern: String?, vararg messageArgs: Any?) {
    if (!this) {
        throw UnsupportedOperationException(format(messagePattern, messageArgs))
    }
}

fun Boolean.checkElement() {
    if (!this) {
        throw NoSuchElementException()
    }
}

fun Boolean.checkElement(message: String?) {
    if (!this) {
        throw NoSuchElementException(message)
    }
}

fun Boolean.checkElement(messagePattern: String?, vararg messageArgs: Any?) {
    if (!this) {
        throw NoSuchElementException(format(messagePattern, messageArgs))
    }
}

fun Boolean.checkBounds() {
    if (!this) {
        throw IndexOutOfBoundsException()
    }
}

fun Boolean.checkBounds(message: String?) {
    if (!this) {
        throw IndexOutOfBoundsException(message)
    }
}

fun Boolean.checkBounds(messagePattern: String?, vararg messageArgs: Any?) {
    if (!this) {
        throw IndexOutOfBoundsException(format(messagePattern, messageArgs))
    }
}

fun Int.isIndexInBounds(startInclusive: Int, endExclusive: Int): Boolean {
    return this in startInclusive until endExclusive
}

fun Int.isIndexInBounds(startInclusive: Long, endExclusive: Long): Boolean {
    return this in startInclusive until endExclusive
}

fun Int.checkIndexInBounds(startInclusive: Int, endExclusive: Int) {
    isIndexInBounds(startInclusive, endExclusive).checkBounds(
        "Index out of bounds[" +
                "index: $this, startInclusive: $startInclusive, endExclusive: $endExclusive" +
                "]."
    )
}

fun Int.checkIndexInBounds(startInclusive: Long, endExclusive: Long) {
    isIndexInBounds(startInclusive, endExclusive).checkBounds(
        "Index out of bounds[" +
                "index: $this, startInclusive: $startInclusive, endExclusive: $endExclusive" +
                "]."
    )
}

fun isRangeInLength(startInclusive: Int, endExclusive: Int, length: Int): Boolean {
    return startInclusive >= 0 && endExclusive <= length && startInclusive <= endExclusive
}

fun isRangeInLength(startInclusive: Long, endExclusive: Long, length: Long): Boolean {
    return startInclusive >= 0 && endExclusive <= length && startInclusive <= endExclusive
}

fun isRangeInBounds(
    startInclusive: Int, endExclusive: Int, startBoundInclusive: Int, endBoundExclusive: Int
): Boolean {
    return startInclusive >= startBoundInclusive
            && endExclusive <= endBoundExclusive
            && startInclusive <= endExclusive
}

fun isRangeInBounds(
    startInclusive: Long, endExclusive: Long, startBoundInclusive: Long, endBoundExclusive: Long
): Boolean {
    return startInclusive >= startBoundInclusive
            && endExclusive <= endBoundExclusive
            && startInclusive <= endExclusive
}

fun checkRangeInLength(startInclusive: Int, endExclusive: Int, length: Int) {
    isRangeInLength(startInclusive, endExclusive, length).checkBounds(
        "Range out of bounds[" +
                "startInclusive: $startInclusive, endExclusive: $endExclusive, length: $length" +
                "]."
    )
}

fun checkRangeInLength(startInclusive: Long, endExclusive: Long, length: Long) {
    isRangeInLength(startInclusive, endExclusive, length).checkBounds(
        "Range out of bounds[" +
                "startInclusive: $startInclusive, endExclusive: $endExclusive, length: $length" +
                "]."
    )
}

fun checkRangeInBounds(startInclusive: Int, endExclusive: Int, startBoundInclusive: Int, endBoundExclusive: Int) {
    isRangeInBounds(startInclusive, endExclusive, startBoundInclusive, endBoundExclusive).checkBounds(
        "Range out of bounds[" +
                "startInclusive: $startInclusive, endExclusive: $endExclusive, " +
                "startBoundInclusive: $startBoundInclusive, endBoundExclusive: $endBoundExclusive" +
                "]."
    )
}

fun checkRangeInBounds(
    startInclusive: Long,
    endExclusive: Long,
    startBoundInclusive: Long,
    endBoundExclusive: Long
) {
    isRangeInBounds(startInclusive, endExclusive, startBoundInclusive, endBoundExclusive).checkBounds(
        "Range out of bounds[" +
                "startInclusive: $startInclusive, endExclusive: $endExclusive, " +
                "startBoundInclusive: $startBoundInclusive, endBoundExclusive: $endBoundExclusive" +
                "]."
    )
}

private fun format(pattern: String?, vararg args: Any?): String? {
    if (pattern === null) {
        return null
    }
    return fastFormat(pattern, args)
}