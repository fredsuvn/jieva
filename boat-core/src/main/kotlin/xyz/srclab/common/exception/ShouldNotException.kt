package xyz.srclab.common.exception

/**
 * @author sunqian
 */
open class ShouldNotException @JvmOverloads constructor(message: String? = null) :
    StatusException(
        if (message === null)
            ExceptionStatus.SHOULD_NOT
        else
            ExceptionStatus.SHOULD_NOT.withMoreDescription(message)
    )