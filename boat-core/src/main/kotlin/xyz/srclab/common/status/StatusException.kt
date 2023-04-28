package xyz.srclab.common.status

import xyz.srclab.common.base.defaultSerialVersion

/**
 * Exception constructed by [Status].
 *
 * @see Status
 */
open class StatusException : RuntimeException {

    @JvmOverloads
    constructor(code: Any, message: Any? = null) : super(statusToString(code, message))

    @JvmOverloads
    constructor(cause: Throwable?, code: Any, message: Any? = null) : super(statusToString(code, message), cause)

    constructor(status: Status<out Any, *, *>) : this(status.code, status.message)

    constructor(cause: Throwable?, status: Status<out Any, *, *>) : this(cause, status.code, status.message)

    companion object {
        private val serialVersionUID: Long = defaultSerialVersion()
    }
}