package xyz.srclab.common.exception

import xyz.srclab.common.state.CharsState
import xyz.srclab.common.state.CharsState.Companion.moreDescriptions
import xyz.srclab.common.state.CharsState.Companion.newDescriptions
import xyz.srclab.common.state.State

/**
 * [State] for exception.
 */
interface ExceptionStatus : State<String, String, ExceptionStatus> {

    @JvmDefault
    override fun withNewDescription(newDescription: String?): ExceptionStatus {
        return ExceptionStatusImpl(code, newDescriptions(newDescription))
    }

    @JvmDefault
    override fun withMoreDescription(moreDescription: String): ExceptionStatus {
        return ExceptionStatusImpl(code, this.descriptions.moreDescriptions(moreDescription))
    }

    @JvmDefault
    fun toExceptionMessage(): String {
        return toString()
    }

    companion object {

        @JvmField
        val INTERNAL = of("000000", "Internal Error")

        @JvmField
        val UNKNOWN = of("000001", "Unknown Error")

        @JvmField
        val SHOULD_NOT = of("000002", "It should not be an exception")

        @JvmStatic
        @JvmOverloads
        fun of(code: CharSequence, description: CharSequence? = null): ExceptionStatus {
            return ExceptionStatusImpl(code.toString(), listOf(description.toString()))
        }
    }
}

private class ExceptionStatusImpl(
    code: CharSequence,
    descriptions: List<CharSequence> = emptyList()
) : CharsState<ExceptionStatus>(code, descriptions), ExceptionStatus {

    override fun newStateImpl(code: String, descriptions: List<String>): ExceptionStatus {
        return ExceptionStatusImpl(code, descriptions)
    }

    override fun withNewDescription(newDescription: String?): ExceptionStatus {
        return super<ExceptionStatus>.withNewDescription(newDescription)
    }

    override fun withMoreDescription(moreDescription: String): ExceptionStatus {
        return super<ExceptionStatus>.withMoreDescription(moreDescription)
    }
}