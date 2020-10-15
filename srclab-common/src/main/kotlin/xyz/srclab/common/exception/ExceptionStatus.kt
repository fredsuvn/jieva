package xyz.srclab.common.exception

import xyz.srclab.common.state.*

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
            of(code, description.joinCharsStateDescription(moreDescription))
    }

    companion object {

        @JvmOverloads
        @JvmStatic
        fun of(code: CharSequence, description: CharSequence? = null): ExceptionStatus {
            return ExceptionStatusImpl(code, description)
        }
    }
}

fun exceptionStatusOf(code: String, description: String? = null): ExceptionStatus {
    return ExceptionStatus.of(code, description)
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
        return this.stateHash()
    }

    override fun toString(): String {
        return this.stateToString()
    }
}