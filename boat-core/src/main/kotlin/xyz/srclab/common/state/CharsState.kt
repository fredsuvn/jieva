package xyz.srclab.common.state

import xyz.srclab.common.state.State.Companion.stateEquals
import xyz.srclab.common.state.State.Companion.stateHashCode
import xyz.srclab.common.state.State.Companion.stateToString

/**
 * Abstract [State] for [String] type code and description.
 */
abstract class CharsState<T : State<String, String, T>> @JvmOverloads constructor(
    code: CharSequence,
    descriptions: List<CharSequence> = emptyList()
) : State<String, String, T> {

    constructor(code: CharSequence, description: CharSequence) : this(code, listOf(description))

    protected abstract fun newStateImpl(code: String, descriptions: List<String>): T

    override val code: String = code.toString()

    override val description: String? by lazy {
        descriptions.joinStateDescriptions()
    }

    override val descriptions: List<String> = descriptions.map { it.toString() }

    override fun withNewDescription(newDescription: String?): T {
        return newStateImpl(code, newDescriptions(newDescription))
    }

    override fun withMoreDescription(moreDescription: String): T {
        return newStateImpl(code, this.descriptions.moreDescriptions(moreDescription))
    }

    override fun equals(other: Any?): Boolean {
        return this.stateEquals(other)
    }

    override fun hashCode(): Int {
        return this.stateHashCode()
    }

    override fun toString(): String {
        return this.stateToString()
    }

    companion object {

        @JvmName("newDescriptions")
        @JvmStatic
        fun newDescriptions(newDescription: String?): List<String> {
            return if (newDescription === null) emptyList() else listOf(newDescription)
        }

        @JvmName("moreDescriptions")
        @JvmStatic
        fun List<CharSequence>.moreDescriptions(moreDescription: String): List<String> {
            val newDescriptions = this.plus(moreDescription)
            return newDescriptions.toList().map { it.toString() }
        }

        @JvmName("joinDescriptions")
        @JvmStatic
        fun List<CharSequence>.joinStateDescriptions(): String? {
            if (this.isEmpty()) {
                return null
            }
            if (this.size == 1) {
                return this.first().toString()
            }
            return "${this.first()}${this.subList(1, this.size).joinToString("") { "[$it]" }}"
        }
    }
}