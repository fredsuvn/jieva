package xyz.srclab.common.exception

import xyz.srclab.common.base.defaultSerialVersion

/**
 * It's IMPOSSIBLE!.
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

    companion object {
        private val serialVersionUID: Long = defaultSerialVersion()
    }
}