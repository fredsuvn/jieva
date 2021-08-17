package xyz.srclab.common.exception

/**
 * Exception represents an impossible cause.
 *
 * @author sunqian
 */
open class ImpossibleException : StatusException {

    constructor(message: String? = null, cause: Throwable? = null) : super(
        if (message === null)
            IMPOSSIBLE_STATUS.toString()
        else
            IMPOSSIBLE_STATUS.withMoreDescription(message).toString(),
        cause
    )

    constructor(cause: Throwable?) : this(null, cause)
}