package xyz.srclab.common.exception

import xyz.srclab.common.state.State

open class CommonException @JvmOverloads constructor(
    private val status: ExceptionStatus,
    cause: Throwable? = null
) : RuntimeException(
    buildExceptionStatus(status, cause).toString(), cause
), ExceptionStatus {

    constructor() : this(DefaultExceptionStatus.UNKNOWN)

    constructor(cause: Throwable?) : this(
        buildExceptionStatus(DefaultExceptionStatus.INTERNAL, cause),
        cause
    )

    constructor(message: String?) : this(DefaultExceptionStatus.INTERNAL.withMoreDescription(message))

    constructor(message: String?, cause: Throwable?) : this(
        buildExceptionStatus(DefaultExceptionStatus.INTERNAL.code(), message, cause),
        cause
    )

    constructor(code: String, description: String?, cause: Throwable?) : this(
        buildExceptionStatus(code, description, cause),
        cause
    )

    override fun code(): String {
        return status.code()
    }

    override fun description(): String? {
        return status.description()
    }

    override fun equals(other: Any?): Boolean {
        return State.equals(this, other)
    }

    override fun hashCode(): Int {
        return State.hashCode(this)
    }

    companion object {

        @JvmStatic
        fun buildExceptionStatus(exceptionStatus: ExceptionStatus, cause: Throwable?): ExceptionStatus {
            return if (cause == null)
                exceptionStatus
            else
                exceptionStatus.withMoreDescription(cause.message)
        }

        @JvmStatic
        fun buildExceptionStatus(code: String, description: String?, cause: Throwable?): ExceptionStatus {
            return if (cause == null)
                ExceptionStatus.newInstance(code, description)
            else
                ExceptionStatus.newInstance(code, "$description[cause: ${cause.message}]")
        }
    }
}