package xyz.srclab.common.exception

import xyz.srclab.common.base.INAPPLICABLE_JVM_NAME
import xyz.srclab.common.state.State
import xyz.srclab.common.state.State.Companion.stateEquals
import xyz.srclab.common.state.State.Companion.stateHashCode

/**
 * Exception implements [ExceptionStatus].
 *
 * @see ExceptionStatus
 * @see State
 */
open class StatusException @JvmOverloads constructor(
    private val exceptionStatus: ExceptionStatus,
    cause: Throwable? = null
) : RuntimeException(
    exceptionStatus.toExceptionMessage(), cause
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
        ExceptionStatus.of(code, description),
        cause
    )

    @Suppress(INAPPLICABLE_JVM_NAME)
    override val code: String
        @JvmName("code") get() = exceptionStatus.code

    @Suppress(INAPPLICABLE_JVM_NAME)
    override val description: String?
        @JvmName("description") get() = exceptionStatus.description

    @Suppress(INAPPLICABLE_JVM_NAME)
    override val descriptions: List<String>
        @JvmName("descriptions") get() = exceptionStatus.descriptions

    override fun equals(other: Any?): Boolean {
        return this.stateEquals(other)
    }

    override fun hashCode(): Int {
        return this.stateHashCode()
    }
}