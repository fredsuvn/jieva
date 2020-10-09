package xyz.srclab.common.exception

import xyz.srclab.common.state.State
import xyz.srclab.common.state.StringState

interface ExceptionStatus : StringState<ExceptionStatus> {

    override fun withMoreDescription(moreDescription: String?): ExceptionStatus {
        return if (moreDescription == null)
            this
        else
            newInstance(code, StringState.buildDescription(description, moreDescription))
    }

    companion object {

        @JvmOverloads
        @JvmStatic
        fun newInstance(code: String, description: String? = null): ExceptionStatus {
            return ExceptionStatusImpl(code, description)
        }

        @JvmStatic
        fun from(state: StringState<*>): ExceptionStatus {
            return newInstance(state.code, state.description)
        }

        private class ExceptionStatusImpl(
            override val code: String,
            override val description: String?
        ) : ExceptionStatus {

            override fun equals(other: Any?): Boolean {
                return State.equals(this, other)
            }

            override fun hashCode(): Int {
                return State.hashCode(this)
            }

            override fun toString(): String {
                return State.toString(this)
            }
        }
    }
}