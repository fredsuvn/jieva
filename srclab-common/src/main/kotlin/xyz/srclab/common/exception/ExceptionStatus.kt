package xyz.srclab.common.exception

import xyz.srclab.common.state.*

interface ExceptionStatus : StringState<ExceptionStatus> {

    override fun withNewDescription(newDescription: String?): ExceptionStatus {
        return if (description == newDescription)
            this
        else
            of(code, newDescription)
    }

    override fun withMoreDescription(moreDescription: String?): ExceptionStatus {
        return if (moreDescription === null)
            this
        else
            of(code, description.moreStateDescription(moreDescription))
    }

    companion object {

        @JvmOverloads
        @JvmStatic
        fun of(code: String, description: String? = null): ExceptionStatus {
            return ExceptionStatusImpl(code, description)
        }

        @JvmStatic
        fun of(state: StringState<*>): ExceptionStatus {
            return of(state.code, state.description)
        }
    }
}

fun exceptionStatusOf(code: String, description: String? = null): ExceptionStatus {
    return ExceptionStatus.of(code, description)
}

fun exceptionStatusOf(state: StringState<*>): ExceptionStatus {
    return ExceptionStatus.of(state)
}

private class ExceptionStatusImpl(
    override val code: String,
    override val description: String?
) : ExceptionStatus {

    override fun equals(other: Any?): Boolean {
        return this.stateEquals(other)
    }

    override fun hashCode(): Int {
        return this.stateHash()
    }

    override fun toString(): String {
        return this.stateToString()
    }
}