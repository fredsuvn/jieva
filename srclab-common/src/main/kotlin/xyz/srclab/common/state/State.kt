package xyz.srclab.common.state

import xyz.srclab.common.base.Hash

/**
 * @author sunqian
 */
interface State<C, DESC, T : State<C, DESC, T>> {

    @Suppress("INAPPLICABLE_JVM_NAME")
    val code: C
        @JvmName("code") get

    @Suppress("INAPPLICABLE_JVM_NAME")
    val description: DESC?
        @JvmName("description") get

    fun withMoreDescription(moreDescription: DESC?): T

    companion object {

        @JvmStatic
        fun toString(state: State<*, *, *>): String {
            val code = state.code
            val description = state.description
            return if (description == null) code.toString() else "$code-$description"
        }

        @JvmStatic
        fun equals(state: State<*, *, *>, other: Any?): Boolean {
            if (state === other) {
                return true
            }
            if (other !is State<*, *, *>) {
                return false
            }
            return (state.code == other.code && state.description == other.description)
        }

        @JvmStatic
        fun hashCode(state: State<*, *, *>): Int {
            return Hash.hash(state.code, state.description)
        }
    }
}