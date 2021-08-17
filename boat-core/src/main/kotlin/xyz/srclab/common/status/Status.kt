package xyz.srclab.common.status

import xyz.srclab.common.exception.StatusException
import xyz.srclab.common.lang.INAPPLICABLE_JVM_NAME

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
interface Status<C, D> {

    @get:JvmName("code")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val code: C

    /**
     * Returns total [descriptions] as one description, or null if [descriptions] is empty.
     */
    @get:JvmName("description")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val description: D?

    /**
     * Returns all this state's own description and descriptions inherited by [withNewDescription].
     */
    @get:JvmName("descriptions")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val descriptions: List<D>

    fun withNewDescription(newDescription: D?): Status<C, D>

    fun withMoreDescription(moreDescription: D): Status<C, D>
}