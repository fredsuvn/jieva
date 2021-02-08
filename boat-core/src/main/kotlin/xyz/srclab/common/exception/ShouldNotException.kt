package xyz.srclab.common.exception

/**
 * @author sunqian
 */
open class ShouldNotException @JvmOverloads constructor(message: String? = null) :
    StatusException(ExceptionStatus.SHOULD_NOT.withMoreDescription(message))