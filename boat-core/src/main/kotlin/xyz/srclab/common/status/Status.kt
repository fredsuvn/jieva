package xyz.srclab.common.status

import xyz.srclab.annotations.Immutable
import xyz.srclab.common.exception.StatusException

/**
 * Represents a `status` such as response status.
 *
 * @param C code type
 * @param D description type
 *
 * @see StringStatus
 * @see IntStringStatus
 * @see StatusException
 */
@Immutable
interface Status<C, D, T : Status<C, D, T>> {

    /**
     * Code of status.
     */
    val code: C

    /**
     * Description of status.
     */
    val description: D?

    /**
     * Returns a new `status` instance which is appended additional description.
     */
    fun withMoreDescription(addition: D): T

    /**
     * Returns a new `status` instance of which descriptions are replaced by [description] but code is not changed.
     */
    fun withNewDescription(description: D?): T
}