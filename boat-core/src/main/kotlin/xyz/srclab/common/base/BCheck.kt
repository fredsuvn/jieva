@file:JvmName("BCheck")

package xyz.srclab.common.base

@Throws(NullPointerException::class)
fun checkNotNull(expression: Boolean) {
    if (!expression) {
        throw NullPointerException()
    }
}

@Throws(NullPointerException::class)
fun checkNotNull(expression: Boolean, message: CharSequence?) {
    if (!expression) {
        throw NullPointerException(message?.toString())
    }
}

@Throws(NullPointerException::class)
fun checkNotNull(expression: Boolean, message: () -> CharSequence?) {
    if (!expression) {
        throw NullPointerException(message()?.toString())
    }
}

@Throws(IllegalArgumentException::class)
fun checkArgument(expression: Boolean) {
    if (!expression) {
        throw IllegalArgumentException()
    }
}

@Throws(IllegalArgumentException::class)
fun checkArgument(expression: Boolean, message: CharSequence?) {
    if (!expression) {
        throw IllegalArgumentException(message?.toString())
    }
}

@Throws(IllegalArgumentException::class)
fun checkArgument(expression: Boolean, message: () -> CharSequence?) {
    if (!expression) {
        throw IllegalArgumentException(message()?.toString())
    }
}

@Throws(IllegalStateException::class)
fun checkState(expression: Boolean) {
    if (!expression) {
        throw IllegalStateException()
    }
}

@Throws(IllegalStateException::class)
fun checkState(expression: Boolean, message: CharSequence?) {
    if (!expression) {
        throw IllegalStateException(message?.toString())
    }
}

@Throws(IllegalStateException::class)
fun checkState(expression: Boolean, message: () -> CharSequence?) {
    if (!expression) {
        throw IllegalStateException(message()?.toString())
    }
}

@Throws(UnsupportedOperationException::class)
fun checkSupported(expression: Boolean) {
    if (!expression) {
        throw UnsupportedOperationException()
    }
}

@Throws(UnsupportedOperationException::class)
fun checkSupported(expression: Boolean, message: CharSequence?) {
    if (!expression) {
        throw UnsupportedOperationException(message?.toString())
    }
}

@Throws(UnsupportedOperationException::class)
fun checkSupported(expression: Boolean, message: () -> CharSequence?) {
    if (!expression) {
        throw UnsupportedOperationException(message()?.toString())
    }
}

@Throws(NoSuchElementException::class)
fun checkElement(expression: Boolean) {
    if (!expression) {
        throw NoSuchElementException()
    }
}

@Throws(NoSuchElementException::class)
fun checkElement(expression: Boolean, message: CharSequence?) {
    if (!expression) {
        throw NoSuchElementException(message?.toString())
    }
}

@Throws(NoSuchElementException::class)
fun checkElement(expression: Boolean, message: () -> CharSequence?) {
    if (!expression) {
        throw NoSuchElementException(message()?.toString())
    }
}

@Throws(IndexOutOfBoundsException::class)
fun checkInBounds(expression: Boolean) {
    if (!expression) {
        throw IndexOutOfBoundsException()
    }
}

@Throws(IndexOutOfBoundsException::class)
fun checkInBounds(expression: Boolean, message: CharSequence?) {
    if (!expression) {
        throw IndexOutOfBoundsException(message?.toString())
    }
}

@Throws(IndexOutOfBoundsException::class)
fun checkInBounds(expression: Boolean, message: () -> CharSequence?) {
    if (!expression) {
        throw IndexOutOfBoundsException(message()?.toString())
    }
}

fun Int.isIndexInBounds(startInclusive: Int, endExclusive: Int): Boolean {
    return this in startInclusive until endExclusive
}

fun Long.isIndexInBounds(startInclusive: Long, endExclusive: Long): Boolean {
    return this in startInclusive until endExclusive
}

@Throws(IndexOutOfBoundsException::class)
fun Int.checkIndexInBounds(startInclusive: Int, endExclusive: Int) {
    if (!this.isIndexInBounds(startInclusive, endExclusive)) {
        throw IndexOutOfBoundsException(
            "Index out of bounds[" +
                "index: $this, startInclusive: $startInclusive, endExclusive: $endExclusive" +
                "]."
        )
    }
}

@Throws(IndexOutOfBoundsException::class)
fun Long.checkIndexInBounds(startInclusive: Long, endExclusive: Long) {
    if (!this.isIndexInBounds(startInclusive, endExclusive)) {
        throw IndexOutOfBoundsException(
            "Index out of bounds[" +
                "index: $this, startInclusive: $startInclusive, endExclusive: $endExclusive" +
                "]."
        )
    }
}

fun Int.isLengthInRange(startInclusive: Int, endExclusive: Int): Boolean {
    return startInclusive <= endExclusive && endExclusive - startInclusive >= this
}

fun Long.isLengthInRange(startInclusive: Long, endExclusive: Long): Boolean {
    return startInclusive <= endExclusive && endExclusive - startInclusive >= this
}

@Throws(IndexOutOfBoundsException::class)
fun Int.checkLengthInRange(startInclusive: Int, endExclusive: Int) {
    if (!this.isLengthInRange(startInclusive, endExclusive)) {
        throw IndexOutOfBoundsException(
            "Length out of range[" +
                "length: $this, startInclusive: $startInclusive, endExclusive: $endExclusive" +
                "]."
        )
    }
}

@Throws(IndexOutOfBoundsException::class)
fun Long.checkLengthInRange(startInclusive: Long, endExclusive: Long) {
    if (!this.isLengthInRange(startInclusive, endExclusive)) {
        throw IndexOutOfBoundsException(
            "Length out of range[" +
                "length: $this, startInclusive: $startInclusive, endExclusive: $endExclusive" +
                "]."
        )
    }
}

fun isRangeInBounds(
    rangeStartInclusive: Int, rangeEndExclusive: Int, boundStartInclusive: Int, boundEndExclusive: Int
): Boolean {
    return rangeStartInclusive >= boundStartInclusive
        && rangeEndExclusive <= boundEndExclusive
        && rangeStartInclusive <= rangeEndExclusive
}

fun isRangeInBounds(
    rangeStartInclusive: Long, rangeEndExclusive: Long, boundStartInclusive: Long, boundEndExclusive: Long
): Boolean {
    return rangeStartInclusive >= boundStartInclusive
        && rangeEndExclusive <= boundEndExclusive
        && rangeStartInclusive <= rangeEndExclusive
}

@Throws(IndexOutOfBoundsException::class)
fun checkRangeInBounds(
    rangeStartInclusive: Int,
    rangeEndExclusive: Int,
    boundStartInclusive: Int,
    boundEndExclusive: Int
) {
    if (!isRangeInBounds(rangeStartInclusive, rangeEndExclusive, boundStartInclusive, boundEndExclusive)) {
        throw IndexOutOfBoundsException(
            "Range out of bounds[" +
                "rangeStartInclusive: $rangeStartInclusive, rangeEndExclusive: $rangeEndExclusive, " +
                "boundStartInclusive: $boundStartInclusive, boundEndExclusive: $boundEndExclusive" +
                "]."
        )
    }
}

@Throws(IndexOutOfBoundsException::class)
fun checkRangeInBounds(
    rangeStartInclusive: Long,
    rangeEndExclusive: Long,
    boundStartInclusive: Long,
    boundEndExclusive: Long
) {
    if (!isRangeInBounds(rangeStartInclusive, rangeEndExclusive, boundStartInclusive, boundEndExclusive)) {
        throw IndexOutOfBoundsException(
            "Range out of bounds[" +
                "rangeStartInclusive: $rangeStartInclusive, rangeEndExclusive: $rangeEndExclusive, " +
                "boundStartInclusive: $boundStartInclusive, boundEndExclusive: $boundEndExclusive" +
                "]."
        )
    }
}