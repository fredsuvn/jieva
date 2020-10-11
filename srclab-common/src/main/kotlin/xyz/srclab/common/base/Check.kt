@file:JvmName("Check")
@file:JvmMultifileClass

package xyz.srclab.common.base

import java.util.*

fun checkArguments(expression: Boolean) {
    if (!expression) {
        throw IllegalArgumentException()
    }
}

fun checkArguments(expression: Boolean, message: String?) {
    if (!expression) {
        throw IllegalArgumentException(message)
    }
}

fun checkArguments(expression: Boolean, messagePattern: String?, vararg messageArgs: Any?) {
    if (!expression) {
        throw IllegalArgumentException(format(messagePattern, messageArgs))
    }
}

fun checkState(expression: Boolean) {
    if (!expression) {
        throw IllegalStateException()
    }
}

fun checkState(expression: Boolean, message: String?) {
    if (!expression) {
        throw IllegalStateException(message)
    }
}

fun checkState(expression: Boolean, messagePattern: String?, vararg messageArgs: Any?) {
    if (!expression) {
        throw IllegalStateException(format(messagePattern, messageArgs))
    }
}

fun checkNull(expression: Boolean) {
    if (!expression) {
        throw NullPointerException()
    }
}

fun checkNull(expression: Boolean, message: String?) {
    if (!expression) {
        throw NullPointerException(message)
    }
}

fun checkNull(expression: Boolean, messagePattern: String?, vararg messageArgs: Any?) {
    if (!expression) {
        throw NullPointerException(format(messagePattern, messageArgs))
    }
}

fun checkSupported(expression: Boolean) {
    if (!expression) {
        throw UnsupportedOperationException()
    }
}

fun checkSupported(expression: Boolean, message: String?) {
    if (!expression) {
        throw UnsupportedOperationException(message)
    }
}

fun checkSupported(expression: Boolean, messagePattern: String?, vararg messageArgs: Any?) {
    if (!expression) {
        throw UnsupportedOperationException(format(messagePattern, messageArgs))
    }
}

fun checkElement(expression: Boolean) {
    if (!expression) {
        throw NoSuchElementException()
    }
}

fun checkElement(expression: Boolean, message: String?) {
    if (!expression) {
        throw NoSuchElementException(message)
    }
}

fun checkElement(expression: Boolean, messagePattern: String?, vararg messageArgs: Any?) {
    if (!expression) {
        throw NoSuchElementException(format(messagePattern, messageArgs))
    }
}

fun checkBounds(expression: Boolean) {
    if (!expression) {
        throw IndexOutOfBoundsException()
    }
}

fun checkBounds(expression: Boolean, message: String?) {
    if (!expression) {
        throw IndexOutOfBoundsException(message)
    }
}

fun checkBounds(expression: Boolean, messagePattern: String?, vararg messageArgs: Any?) {
    if (!expression) {
        throw IndexOutOfBoundsException(format(messagePattern, messageArgs))
    }
}

fun isIndexInRange(index: Int, startInclusive: Int, endExclusive: Int): Boolean {
    return index >= startInclusive && index < endExclusive
}

fun isIndexInRange(index: Long, startInclusive: Long, endExclusive: Long): Boolean {
    return index >= startInclusive && index < endExclusive
}

fun checkIndexInRange(index: Int, startInclusive: Int, endExclusive: Int) {
    checkBounds(
        isIndexInRange(index, startInclusive, endExclusive),
        "Index out of bounds[" +
                "index: $index, startInclusive: $startInclusive, endExclusive: $endExclusive" +
                "]."
    )
}

fun checkIndexInRange(index: Long, startInclusive: Long, endExclusive: Long) {
    checkBounds(
        isIndexInRange(index, startInclusive, endExclusive),
        "Index out of bounds[" +
                "index: $index, startInclusive: $startInclusive, endExclusive: $endExclusive" +
                "]."
    )
}

fun isRangeInBounds(startInclusive: Int, endExclusive: Int, length: Int): Boolean {
    return startInclusive >= 0 && endExclusive <= length && startInclusive <= endExclusive
}

fun isRangeInBounds(startInclusive: Long, endExclusive: Long, length: Long): Boolean {
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

fun checkRangeInBounds(startInclusive: Int, endExclusive: Int, length: Int) {
    checkBounds(
        isRangeInBounds(startInclusive, endExclusive, length),
        "Range out of bounds[" +
                "startInclusive: $startInclusive, endExclusive: $endExclusive, length: $length" +
                "]."
    )
}

fun checkRangeInBounds(startInclusive: Long, endExclusive: Long, length: Long) {
    checkBounds(
        isRangeInBounds(startInclusive, endExclusive, length),
        "Range out of bounds[" +
                "startInclusive: $startInclusive, endExclusive: $endExclusive, length: $length" +
                "]."
    )
}

fun checkRangeInBounds(startInclusive: Int, endExclusive: Int, startBoundInclusive: Int, endBoundExclusive: Int) {
    checkBounds(
        isRangeInBounds(startInclusive, endExclusive, startBoundInclusive, endBoundExclusive),
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
    checkBounds(
        isRangeInBounds(startInclusive, endExclusive, startBoundInclusive, endBoundExclusive),
        "Range out of bounds[" +
                "startInclusive: $startInclusive, endExclusive: $endExclusive, " +
                "startBoundInclusive: $startBoundInclusive, endBoundExclusive: $endBoundExclusive" +
                "]."
    )
}

private fun format(pattern: String?, vararg args: Any?): String? {
    if (pattern == null) {
        return null
    }
    return fastFormat(pattern, args)
}