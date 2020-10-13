package xyz.srclab.common.exception

import xyz.srclab.common.state.stateEquals
import xyz.srclab.common.state.stateHash

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
        buildExceptionStatus(DefaultExceptionStatus.INTERNAL.code, message, cause),
        cause
    )

    constructor(code: String, description: String?, cause: Throwable?) : this(
        buildExceptionStatus(code, description, cause),
        cause
    )

    @Suppress("INAPPLICABLE_JVM_NAME")
    override val code: String
        @JvmName("code") get() = status.code

    @Suppress("INAPPLICABLE_JVM_NAME")
    override val description: String?
        @JvmName("description") get() = status.description

    override fun equals(other: Any?): Boolean {
        return stateEquals(other)
    }

    override fun hashCode(): Int {
        return stateHash()
    }

    companion object {

        private fun buildExceptionStatus(exceptionStatus: ExceptionStatus, cause: Throwable?): ExceptionStatus {
            return if (cause == null)
                exceptionStatus
            else
                exceptionStatus.withMoreDescription(cause.message)
        }

        private fun buildExceptionStatus(code: String, description: String?, cause: Throwable?): ExceptionStatus {
            return if (cause == null)
                ExceptionStatus.of(code, description)
            else
                ExceptionStatus.of(code, "$description[cause: ${cause.message}]")
        }
    }
}