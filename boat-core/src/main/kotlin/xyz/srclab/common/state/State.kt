package xyz.srclab.common.state

import xyz.srclab.common.lang.INAPPLICABLE_JVM_NAME
import xyz.srclab.common.lang.hash

import xyz.srclab.common.exception.ExceptionStatus
import xyz.srclab.common.exception.StatusException

/**
 * Represents a state or status.
 *
 * @param C code type
 * @param D description type
 * @param T state type
 *
 * @author sunqian
 *
 * @see CharsState
 * @see ExceptionStatus
 * @see StatusException
 */
interface State<C, D, T : State<C, D, T>> {

    @Suppress(INAPPLICABLE_JVM_NAME)
    val code: C
        @JvmName("code") get

    /**
     * Returns total [descriptions] as one description, or null if [descriptions] is empty.
     */
    @Suppress(INAPPLICABLE_JVM_NAME)
    val description: D?
        @JvmName("description") get

    /**
     * Returns all this state's own description and descriptions inherited by [withNewDescription].
     */
    @Suppress(INAPPLICABLE_JVM_NAME)
    val descriptions: List<D>
        @JvmName("descriptions") get

    fun withNewDescription(newDescription: D?): T

    fun withMoreDescription(moreDescription: D): T

    companion object {

        @JvmName("equals")
        @JvmStatic
        fun State<*, *, *>.stateEquals(other: Any?): Boolean {
            if (this === other) {
                return true
            }
            if (other !is State<*, *, *>) {
                return false
            }
            return (this.code == other.code && this.descriptions == other.descriptions)
        }

        @JvmName("hashCode")
        @JvmStatic
        fun State<*, *, *>.stateHashCode(): Int {
            return hash(this.code, this.descriptions)
        }

        @JvmName("toString")
        @JvmStatic
        fun State<*, *, *>.stateToString(): String {
            val code = this.code
            val description = this.description
            return if (description === null) code.toString() else "$code-$description"
        }
    }
}