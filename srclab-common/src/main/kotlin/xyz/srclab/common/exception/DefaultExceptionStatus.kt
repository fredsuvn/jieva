package xyz.srclab.common.exception

import xyz.srclab.common.state.stateToString

enum class DefaultExceptionStatus(
    @get:JvmName("code") override val code: String,
    @get:JvmName("description") override val description: String?
) : ExceptionStatus {

    INTERNAL("000000", "Internal Error"),
    UNKNOWN("000001", "Unknown Error"),
    ;

    override fun toString(): String {
        return stateToString()
    }
}