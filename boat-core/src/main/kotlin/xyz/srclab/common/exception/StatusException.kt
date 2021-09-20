package xyz.srclab.common.exception

import xyz.srclab.common.status.Status
import xyz.srclab.common.status.StringStatus

/**
 * Exception implementation with [Status].
 *
 * @see Status
 * @see StringStatus
 */
@JvmDefaultWithoutCompatibility
open class StatusException @JvmOverloads constructor(
    private val status: Status<String, String>,
    cause: Throwable? = null
) : RuntimeException(
    status.toString(), cause
), Status<String, String> {

    constructor() : this(INTERNAL_STATUS)

    constructor(cause: Throwable?) : this(
        INTERNAL_STATUS,
        cause
    )

    constructor(message: String?) : this(
        INTERNAL_STATUS.withNewDescription(message)
    )

    constructor(message: String?, cause: Throwable?) : this(
        INTERNAL_STATUS.withNewDescription(message),
        cause
    )

    constructor(code: String, description: String?, cause: Throwable?) : this(
        StringStatus(code, description),
        cause
    )

    override val code: String = status.code
    override val description: String? = status.description
    override val descriptions: List<String> = status.descriptions

    override fun withMoreDescriptions(additions: Iterable<String>): Status<String, String> {
        return status.withMoreDescriptions(additions)
    }

    override fun withNewDescriptions(descriptions: Iterable<String>): Status<String, String> {
        return status.withNewDescriptions(descriptions)
    }

    companion object {

        @JvmField
        val INTERNAL_STATUS = StringStatus("B-99999", "Internal Error")

        @JvmField
        val UNKNOWN_STATUS = StringStatus("B-99998", "Unknown Error")

        @JvmField
        val IMPOSSIBLE_STATUS = StringStatus("B-99997", "WTF??... That's IMPOSSIBLE!!")
    }
}