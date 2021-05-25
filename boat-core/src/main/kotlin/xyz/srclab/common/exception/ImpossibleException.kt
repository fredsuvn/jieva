package xyz.srclab.common.exception

/**
 * The exception represents an impossible thrown case but actually thrown.
 *
 * @author sunqian
 */
open class ImpossibleException @JvmOverloads constructor(message: String? = null, cause: Throwable? = null) :
    StatusException(
        if (message === null)
            ExceptionStatus.IMPOSSIBLE
        else
            ExceptionStatus.IMPOSSIBLE.withMoreDescription(message),
        cause
    ) {

    constructor(cause: Throwable) : this(null, cause)
}