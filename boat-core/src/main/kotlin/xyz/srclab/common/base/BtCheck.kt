/**
 * Checking utilities.
 */
@file:JvmName("BtCheck")

package xyz.srclab.common.base

import java.util.function.Predicate
import java.util.function.Supplier

/**
 * Checks and if [expr] returns false, throw [exception].
 */
fun checkThrow(expr: Boolean, exception: Supplier<Throwable>) {
    if (!expr) {
        throw exception.get()
    }
}

/**
 * Checks and if [expr] returns false, throw [NullPointerException].
 */
@Throws(NullPointerException::class)
fun checkNull(expr: Boolean) {
    if (!expr) {
        throw NullPointerException()
    }
}

/**
 * Checks and if [expr] returns false, throw [NullPointerException] with [message].
 */
@Throws(NullPointerException::class)
fun checkNull(expr: Boolean, message: CharSequence?) {
    if (!expr) {
        throw NullPointerException(message?.toString())
    }
}

/**
 * Checks and if [expr] returns false, throw [NullPointerException] with [message].
 */
@Throws(NullPointerException::class)
fun checkNull(expr: Boolean, message: Supplier<CharSequence?>) {
    if (!expr) {
        throw NullPointerException(message.get()?.toString())
    }
}

/**
 * Checks and if [expr] returns false, throw [IllegalArgumentException].
 */
@Throws(IllegalArgumentException::class)
fun checkArgument(expr: Boolean) {
    checkNull(true) { "sdas" }
    if (!expr) {
        throw IllegalArgumentException()
    }
}

/**
 * Checks and if [expr] returns false, throw [IllegalArgumentException] with [message].
 */
@Throws(IllegalArgumentException::class)
fun checkArgument(expr: Boolean, message: CharSequence?) {
    if (!expr) {
        throw IllegalArgumentException(message?.toString())
    }
}

/**
 * Checks and if [expr] returns false, throw [IllegalArgumentException] with [message].
 */
@Throws(IllegalArgumentException::class)
fun checkArgument(expr: Boolean, message: Supplier<CharSequence?>) {
    if (!expr) {
        throw IllegalArgumentException(message.get()?.toString())
    }
}

/**
 * Checks and if [expr] returns false, throw [IllegalStateException].
 */
@Throws(IllegalStateException::class)
fun checkState(expr: Boolean) {
    if (!expr) {
        throw IllegalStateException()
    }
}

/**
 * Checks and if [expr] returns false, throw [IllegalStateException] with [message].
 */
@Throws(IllegalStateException::class)
fun checkState(expr: Boolean, message: CharSequence?) {
    if (!expr) {
        throw IllegalStateException(message?.toString())
    }
}

/**
 * Checks and if [expr] returns false, throw [IllegalStateException] with [message].
 */
@Throws(IllegalStateException::class)
fun checkState(expr: Boolean, message: Supplier<CharSequence?>) {
    if (!expr) {
        throw IllegalStateException(message.get()?.toString())
    }
}

/**
 * Checks and if [expr] returns false, throw [UnsupportedOperationException].
 */
@Throws(UnsupportedOperationException::class)
fun checkSupported(expr: Boolean) {
    if (!expr) {
        throw UnsupportedOperationException()
    }
}

/**
 * Checks and if [expr] returns false, throw [UnsupportedOperationException] with [message].
 */
@Throws(UnsupportedOperationException::class)
fun checkSupported(expr: Boolean, message: CharSequence?) {
    if (!expr) {
        throw UnsupportedOperationException(message?.toString())
    }
}

/**
 * Checks and if [expr] returns false, throw [UnsupportedOperationException] with [message].
 */
@Throws(UnsupportedOperationException::class)
fun checkSupported(expr: Boolean, message: Supplier<CharSequence?>) {
    if (!expr) {
        throw UnsupportedOperationException(message.get()?.toString())
    }
}

/**
 * Checks and if [expr] returns false, throw [NoSuchElementException].
 */
@Throws(NoSuchElementException::class)
fun checkElement(expr: Boolean) {
    if (!expr) {
        throw NoSuchElementException()
    }
}

/**
 * Checks and if [expr] returns false, throw [NoSuchElementException] with [message].
 */
@Throws(NoSuchElementException::class)
fun checkElement(expr: Boolean, message: CharSequence?) {
    if (!expr) {
        throw NoSuchElementException(message?.toString())
    }
}

/**
 * Checks and if [expr] returns false, throw [NoSuchElementException] with [message].
 */
@Throws(NoSuchElementException::class)
fun checkElement(expr: Boolean, message: Supplier<CharSequence?>) {
    if (!expr) {
        throw NoSuchElementException(message.get()?.toString())
    }
}

/**
 * Checks and if [expr] returns false, throw [IndexOutOfBoundsException].
 */
@Throws(IndexOutOfBoundsException::class)
fun checkBounds(expr: Boolean) {
    if (!expr) {
        throw IndexOutOfBoundsException()
    }
}

/**
 * Checks and if [expr] returns false, throw [IndexOutOfBoundsException] with [message].
 */
@Throws(IndexOutOfBoundsException::class)
fun checkBounds(expr: Boolean, message: CharSequence?) {
    if (!expr) {
        throw IndexOutOfBoundsException(message?.toString())
    }
}

/**
 * Checks and if [expr] returns false, throw [IndexOutOfBoundsException] with [message].
 */
@Throws(IndexOutOfBoundsException::class)
fun checkBounds(expr: Boolean, message: Supplier<CharSequence?>) {
    if (!expr) {
        throw IndexOutOfBoundsException(message.get()?.toString())
    }
}

/**
 * Returns whether [this] is in bounds from [startInclusive] to [endExclusive].
 */
fun Int.isInBounds(startInclusive: Int, endExclusive: Int): Boolean {
    return this in startInclusive until endExclusive
}

/**
 * Returns whether [this] is in bounds from [startInclusive] to [endExclusive].
 */
fun Long.isInBounds(startInclusive: Long, endExclusive: Long): Boolean {
    return this in startInclusive until endExclusive
}

/**
 * Checks whether [this] is in bounds from [startInclusive] to [endExclusive].
 */
@Throws(IndexOutOfBoundsException::class)
fun Int.checkInBounds(startInclusive: Int, endExclusive: Int) {
    if (!this.isInBounds(startInclusive, endExclusive)) {
        throw IndexOutOfBoundsException("$this is out of bounds: [$startInclusive, $endExclusive)!")
    }
}

/**
 * Checks whether [this] is in bounds from [startInclusive] to [endExclusive].
 */
@Throws(IndexOutOfBoundsException::class)
fun Long.checkInBounds(startInclusive: Long, endExclusive: Long) {
    if (!this.isInBounds(startInclusive, endExclusive)) {
        throw IndexOutOfBoundsException("$this is out of bounds: [$startInclusive, $endExclusive)!")
    }
}

/**
 * Returns whether the range from [rangeStartInclusive] to [rangeEndExclusive]
 * is in bounds from [boundStartInclusive] to [boundEndExclusive].
 */
fun isRangeInBounds(
    rangeStartInclusive: Int, rangeEndExclusive: Int, boundStartInclusive: Int, boundEndExclusive: Int
): Boolean {
    return rangeStartInclusive >= boundStartInclusive
        && rangeEndExclusive <= boundEndExclusive
        && rangeStartInclusive <= rangeEndExclusive
}

/**
 * Returns whether the range from [rangeStartInclusive] to [rangeEndExclusive]
 * is in bounds from [boundStartInclusive] to [boundEndExclusive].
 */
fun isRangeInBounds(
    rangeStartInclusive: Long, rangeEndExclusive: Long, boundStartInclusive: Long, boundEndExclusive: Long
): Boolean {
    return rangeStartInclusive >= boundStartInclusive
        && rangeEndExclusive <= boundEndExclusive
        && rangeStartInclusive <= rangeEndExclusive
}

/**
 * Checks whether the range from [rangeStartInclusive] to [rangeEndExclusive]
 * is in bounds from [boundStartInclusive] to [boundEndExclusive].
 */
@Throws(IndexOutOfBoundsException::class)
fun checkRangeInBounds(
    rangeStartInclusive: Int,
    rangeEndExclusive: Int,
    boundStartInclusive: Int,
    boundEndExclusive: Int
) {
    if (!isRangeInBounds(rangeStartInclusive, rangeEndExclusive, boundStartInclusive, boundEndExclusive)) {
        throw IndexOutOfBoundsException(
            "[$rangeStartInclusive, $rangeEndExclusive) is of bounds: [$boundStartInclusive, $boundEndExclusive)"
        )
    }
}

/**
 * Checks whether the range from [rangeStartInclusive] to [rangeEndExclusive]
 * is in bounds from [boundStartInclusive] to [boundEndExclusive].
 */
@Throws(IndexOutOfBoundsException::class)
fun checkRangeInBounds(
    rangeStartInclusive: Long,
    rangeEndExclusive: Long,
    boundStartInclusive: Long,
    boundEndExclusive: Long
) {
    if (!isRangeInBounds(rangeStartInclusive, rangeEndExclusive, boundStartInclusive, boundEndExclusive)) {
        throw IndexOutOfBoundsException(
            "[$rangeStartInclusive, $rangeEndExclusive) is of bounds: [$boundStartInclusive, $boundEndExclusive)"
        )
    }
}

/**
 * Returns true if any of [objs] pass the test of [this].
 */
fun <T> Predicate<T>.testAny(vararg objs: T): Boolean {
    return anyPredicate({ this.test(it) }, *objs)
}

/**
 * Returns true if all of [objs] pass the test of [this].
 */
fun <T> Predicate<T>.testAll(vararg objs: T): Boolean {
    return allPredicate({ this.test(it) }, *objs)
}