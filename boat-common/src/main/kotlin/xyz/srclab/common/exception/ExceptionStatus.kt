package xyz.srclab.common.exception

import xyz.srclab.common.state.State
import xyz.srclab.common.state.State.Companion.stateEquals
import xyz.srclab.common.state.State.Companion.stateHashCode
import xyz.srclab.common.state.State.Companion.stateMoreDescription
import xyz.srclab.common.state.State.Companion.stateToString

interface ExceptionStatus : State<String, String, ExceptionStatus> {

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
            of(code, description.stateMoreDescription(moreDescription))
    }

    companion object {

        @JvmField
        val INTERNAL = of("000000", "Internal Error")

        @JvmField
        val UNKNOWN = of("000001", "Unknown Error")

        @JvmStatic
        @JvmOverloads
        fun of(code: CharSequence, description: CharSequence? = null): ExceptionStatus {
            return ExceptionStatusImpl(code, description)
        }
    }
}

private class ExceptionStatusImpl(
    code: CharSequence,
    description: CharSequence?
) : ExceptionStatus {

    override val code = code.toString()
    override val description = description?.toString()

    override fun equals(other: Any?): Boolean {
        return this.stateEquals(other)
    }

    override fun hashCode(): Int {
        return this.stateHashCode()
    }

    override fun toString(): String {
        return this.stateToString()
    }
}