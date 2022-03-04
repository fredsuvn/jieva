package xyz.srclab.common.exception

import xyz.srclab.common.base.defaultSerialVersion
import xyz.srclab.common.status.Status
import xyz.srclab.common.status.StringStatus
import xyz.srclab.common.status.statusToString

/**
 * Exception implementation with [Status] and [StringStatus].
 *
 * @see Status
 * @see StringStatus
 */
open class StatusException @JvmOverloads constructor(
    override val code: String,
    override val description: String? = null,
    cause: Throwable? = null
) : RuntimeException(statusToString(code, description), cause), Status<String, String, StringStatus> {

    constructor() : this(INTERNAL_STATUS)

    @JvmOverloads
    constructor(status: Status<String, String, StringStatus>, cause: Throwable? = null) : this(
        status.code,
        status.description,
        cause
    )

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

        @JvmField
        val INTERNAL_STATUS = StringStatus("B0999000", "Internal Error")

        @JvmField
        val UNKNOWN_STATUS = StringStatus("B0999999", "Unknown Error")

        @JvmField
        val IMPOSSIBLE_STATUS = StringStatus("B0999666", "WTF??... That's IMPOSSIBLE!!")
    }
}