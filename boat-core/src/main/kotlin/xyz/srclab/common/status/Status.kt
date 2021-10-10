package xyz.srclab.common.status

import xyz.srclab.annotations.Immutable
import xyz.srclab.common.exception.StatusException

/**
 * Represents a `status` such as response status.
 *
 * @param C code type
 * @param D description type
 *
 * @author sunqian
 *
 * @see StringStatus
 * @see IntStringStatus
 * @see StatusException
 */
@Immutable
interface Status<C, D> {

    /**
     * Code of status.
     */
    val code: C

    /**
     * Returns all [descriptions] as one description, or null if [descriptions] is empty.
     */
    val description: D?

    /**
     * Returns all descriptions of this `status`.
     */
    val descriptions: List<D>

    /**
     * Returns a new `status` instance which is appended additional description.
     */
    fun withMoreDescription(addition: D): Status<C, D> {
        return withMoreDescriptions(listOf(addition))
    }

    /**
     * Returns a new `status` instance which is appended additional descriptions.
     */
    fun withMoreDescriptions(additions: Iterable<D>): Status<C, D>

    /**
     * Returns a new `status` instance of which descriptions are replaced by [description] but code is not changed.
     */
    fun withNewDescription(description: D?): Status<C, D> {
        return withNewDescriptions(if (description === null) emptyList() else listOf(description))
    }

    /**
     * Returns a new `status` instance of which descriptions are replaced by [descriptions] but code is not changed.
     */
    fun withNewDescriptions(descriptions: Iterable<D>): Status<C, D>
}