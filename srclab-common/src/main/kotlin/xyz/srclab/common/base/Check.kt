package xyz.srclab.common.base

import java.util.*

object Check {

    @JvmStatic
    fun checkArguments(expression: Boolean) {
        if (!expression) {
            throw IllegalArgumentException()
        }
    }

    @JvmStatic
    fun checkArguments(expression: Boolean, message: String?) {
        if (!expression) {
            throw IllegalArgumentException(message)
        }
    }

    @JvmStatic
    fun checkArguments(expression: Boolean, messagePattern: String?, vararg messageArgs: Any?) {
        if (!expression) {
            throw IllegalArgumentException(format(messagePattern, messageArgs))
        }
    }

    @JvmStatic
    fun checkState(expression: Boolean) {
        if (!expression) {
            throw IllegalStateException()
        }
    }

    @JvmStatic
    fun checkState(expression: Boolean, message: String?) {
        if (!expression) {
            throw IllegalStateException(message)
        }
    }

    @JvmStatic
    fun checkState(expression: Boolean, messagePattern: String?, vararg messageArgs: Any?) {
        if (!expression) {
            throw IllegalStateException(format(messagePattern, messageArgs))
        }
    }

    @JvmStatic
    fun checkNull(expression: Boolean) {
        if (!expression) {
            throw NullPointerException()
        }
    }

    @JvmStatic
    fun checkNull(expression: Boolean, message: String?) {
        if (!expression) {
            throw NullPointerException(message)
        }
    }

    @JvmStatic
    fun checkNull(expression: Boolean, messagePattern: String?, vararg messageArgs: Any?) {
        if (!expression) {
            throw NullPointerException(format(messagePattern, messageArgs))
        }
    }

    @JvmStatic
    fun checkSupported(expression: Boolean) {
        if (!expression) {
            throw UnsupportedOperationException()
        }
    }

    @JvmStatic
    fun checkSupported(expression: Boolean, message: String?) {
        if (!expression) {
            throw UnsupportedOperationException(message)
        }
    }

    @JvmStatic
    fun checkSupported(expression: Boolean, messagePattern: String?, vararg messageArgs: Any?) {
        if (!expression) {
            throw UnsupportedOperationException(format(messagePattern, messageArgs))
        }
    }

    @JvmStatic
    fun checkElement(expression: Boolean) {
        if (!expression) {
            throw NoSuchElementException()
        }
    }

    @JvmStatic
    fun checkElement(expression: Boolean, message: String?) {
        if (!expression) {
            throw NoSuchElementException(message)
        }
    }

    @JvmStatic
    fun checkElement(expression: Boolean, messagePattern: String?, vararg messageArgs: Any?) {
        if (!expression) {
            throw NoSuchElementException(format(messagePattern, messageArgs))
        }
    }

    @JvmStatic
    fun checkBounds(expression: Boolean) {
        if (!expression) {
            throw IndexOutOfBoundsException()
        }
    }

    @JvmStatic
    fun checkBounds(expression: Boolean, message: String?) {
        if (!expression) {
            throw IndexOutOfBoundsException(message)
        }
    }

    @JvmStatic
    fun checkBounds(expression: Boolean, messagePattern: String?, vararg messageArgs: Any?) {
        if (!expression) {
            throw IndexOutOfBoundsException(format(messagePattern, messageArgs))
        }
    }

    @JvmStatic
    fun isIndexInRange(index: Int, startInclusive: Int, endExclusive: Int): Boolean {
        return index >= startInclusive && index < endExclusive
    }

    @JvmStatic
    fun isIndexInRange(index: Long, startInclusive: Long, endExclusive: Long): Boolean {
        return index >= startInclusive && index < endExclusive
    }

    @JvmStatic
    fun checkIndexInRange(index: Int, startInclusive: Int, endExclusive: Int) {
        checkBounds(
            isIndexInRange(index, startInclusive, endExclusive),
            "Index out of bounds[" +
                    "index: $index, startInclusive: $startInclusive, endExclusive: $endExclusive" +
                    "]."
        )
    }

    @JvmStatic
    fun checkIndexInRange(index: Long, startInclusive: Long, endExclusive: Long) {
        checkBounds(
            isIndexInRange(index, startInclusive, endExclusive),
            "Index out of bounds[" +
                    "index: $index, startInclusive: $startInclusive, endExclusive: $endExclusive" +
                    "]."
        )
    }

    @JvmStatic
    fun isRangeInBounds(startInclusive: Int, endExclusive: Int, length: Int): Boolean {
        return startInclusive >= 0 && endExclusive <= length && startInclusive <= endExclusive
    }

    @JvmStatic
    fun isRangeInBounds(startInclusive: Long, endExclusive: Long, length: Long): Boolean {
        return startInclusive >= 0 && endExclusive <= length && startInclusive <= endExclusive
    }

    @JvmStatic
    fun isRangeInBounds(
        startInclusive: Int, endExclusive: Int, startBoundInclusive: Int, endBoundExclusive: Int
    ): Boolean {
        return startInclusive >= startBoundInclusive
                && endExclusive <= endBoundExclusive
                && startInclusive <= endExclusive
    }

    @JvmStatic
    fun isRangeInBounds(
        startInclusive: Long, endExclusive: Long, startBoundInclusive: Long, endBoundExclusive: Long
    ): Boolean {
        return startInclusive >= startBoundInclusive
                && endExclusive <= endBoundExclusive
                && startInclusive <= endExclusive
    }

    @JvmStatic
    fun checkRangeInBounds(startInclusive: Int, endExclusive: Int, length: Int) {
        checkBounds(
            isRangeInBounds(startInclusive, endExclusive, length),
            "Range out of bounds[" +
                    "startInclusive: $startInclusive, endExclusive: $endExclusive, length: $length" +
                    "]."
        )
    }

    @JvmStatic
    fun checkRangeInBounds(startInclusive: Long, endExclusive: Long, length: Long) {
        checkBounds(
            isRangeInBounds(startInclusive, endExclusive, length),
            "Range out of bounds[" +
                    "startInclusive: $startInclusive, endExclusive: $endExclusive, length: $length" +
                    "]."
        )
    }

    @JvmStatic
    fun checkRangeInBounds(startInclusive: Int, endExclusive: Int, startBoundInclusive: Int, endBoundExclusive: Int) {
        checkBounds(
            isRangeInBounds(startInclusive, endExclusive, startBoundInclusive, endBoundExclusive),
            "Range out of bounds[" +
                    "startInclusive: $startInclusive, endExclusive: $endExclusive, " +
                    "startBoundInclusive: $startBoundInclusive, endBoundExclusive: $endBoundExclusive" +
                    "]."
        )
    }

    @JvmStatic
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

    @JvmStatic
    private fun format(pattern: String?, vararg args: Any?): String? {
        if (pattern == null) {
            return null
        }
        return Format.fastFormat(pattern, args)
    }
}