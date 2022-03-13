package xyz.srclab.common.exception

import xyz.srclab.common.base.defaultSerialVersion
import xyz.srclab.common.status.Status
import xyz.srclab.common.status.StringStatus
import xyz.srclab.common.status.statusToString

/**
 * Exception with [Status] and [StringStatus].
 *
 * This exception is suitable to be a base exception with `code` and its `description`.
 *
 * @see Status
 * @see StringStatus
 */
open class StatusException @JvmOverloads
/**
 * Constructs with [code], [description] and [cause].
 */
constructor(
    override val code: String,
    override val description: String? = null,
    cause: Throwable? = null
) : RuntimeException(statusToString(code, description), cause), Status<String, String, StringStatus> {

    /**
     * Constructs as [INTERNAL_STATUS].
     */
    constructor() : this(INTERNAL_STATUS)

    /**
     * Constructs with [status] and [cause].
     */
    @JvmOverloads
    constructor(status: Status<String, String, StringStatus>, cause: Throwable? = null) : this(
        status.code,
        status.description,
        cause
    )

    /**
     * Constructs with [cause], will use [INTERNAL_STATUS] as status.
     */
    constructor(cause: Throwable?) : this(
        INTERNAL_STATUS,
        cause
    )

    override fun withMoreDescription(addition: String): StringStatus {
        return StringStatus(code, "$description[$addition]")
    }

    override fun withNewDescription(description: String?): StringStatus {
        return StringStatus(code, description)
    }

    companion object {

        private val serialVersionUID: Long = defaultSerialVersion()

        /**
         * Internal error.
         */
        @JvmField
        val INTERNAL_STATUS = StringStatus("B0999000", "Internal Error")

        /**
         * Unknown error.
         */
        @JvmField
        val UNKNOWN_STATUS = StringStatus("B0999999", "Unknown Error")

        /**
         * It's IMPOSSIBLE!
         */
        @JvmField
        val IMPOSSIBLE_STATUS = StringStatus("B0999666", "WTF??... That's IMPOSSIBLE!!!")
    }
}