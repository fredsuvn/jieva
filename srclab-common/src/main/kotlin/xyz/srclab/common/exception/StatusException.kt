package xyz.srclab.common.exception

import xyz.srclab.common.state.stateEquals
import xyz.srclab.common.state.stateHash

open class StatusException @JvmOverloads constructor(
    private val exceptionStatus: ExceptionStatus,
    cause: Throwable? = null
) : RuntimeException(
    exceptionStatus.toString(), cause
), ExceptionStatus {

    constructor() : this(ExceptionStatus.UNKNOWN)

    constructor(cause: Throwable?) : this(
        ExceptionStatus.INTERNAL,
        cause
    )

    constructor(message: String?) : this(ExceptionStatus.INTERNAL.withNewDescription(message))

    constructor(message: String?, cause: Throwable?) : this(
        ExceptionStatus.INTERNAL.withNewDescription(message),
        cause
    )

    constructor(code: String, description: String?, cause: Throwable?) : this(
        exceptionStatusOf(code, description),
        cause
    )

    @Suppress("INAPPLICABLE_JVM_NAME")
    override val code: String
        @JvmName("code") get() = exceptionStatus.code

    @Suppress("INAPPLICABLE_JVM_NAME")
    override val description: String?
        @JvmName("description") get() = exceptionStatus.description

    override fun equals(other: Any?): Boolean {
        return stateEquals(other)
    }

    override fun hashCode(): Int {
        return stateHash()
    }
}