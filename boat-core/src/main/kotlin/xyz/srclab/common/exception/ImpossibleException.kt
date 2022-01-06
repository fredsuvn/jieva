package xyz.srclab.common.exception

/**
 * Exception represents an impossible cause.
 */
open class ImpossibleException : StatusException {

    @JvmOverloads
    constructor(message: String? = null, cause: Throwable? = null) : super(
        if (message === null)
            IMPOSSIBLE_STATUS
        else
            IMPOSSIBLE_STATUS.withMoreDescription(message),
        cause
    )

    constructor(cause: Throwable?) : this(null, cause)
}