package xyz.srclab.common.status

/**
 * Represents a `status` which consists of a [code] and a [message].
 *
 * @param C code type
 * @param D message type
 * @param S type of [Status]
 */
interface Status<C, D, S : Status<C, D, S>> {

    /**
     * Code of status.
     */
    val code: C

    /**
     * Message of status.
     */
    val message: D?

    /**
     * Returns a new `status` with this [code] and [message] followed by [additional] message.
     */
    fun withMoreMessage(additional: D): S

    /**
     * Returns a new `status` with this [code] and new [message].
     */
    fun withNewMessage(message: D?): S
}