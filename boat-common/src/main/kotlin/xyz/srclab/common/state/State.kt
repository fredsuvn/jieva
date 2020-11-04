package xyz.srclab.common.state

import xyz.srclab.common.base.hash
import xyz.srclab.kotlin.compile.COMPILE_INAPPLICABLE_JVM_NAME

/**
 * @author sunqian
 */
interface State<C, DESC, T : State<C, DESC, T>> {

    @Suppress(COMPILE_INAPPLICABLE_JVM_NAME)
    val code: C
        @JvmName("code") get

    @Suppress(COMPILE_INAPPLICABLE_JVM_NAME)
    val description: DESC?
        @JvmName("description") get

    fun withNewDescription(newDescription: DESC?): T

    fun withMoreDescription(moreDescription: DESC?): T

    companion object {

        @JvmStatic
        @JvmName("equals")
        fun State<*, *, *>.stateEquals(other: Any?): Boolean {
            if (this === other) {
                return true
            }
            if (other !is State<*, *, *>) {
                return false
            }
            return (this.code == other.code && this.description == other.description)
        }

        @JvmStatic
        @JvmName("hashCode")
        fun State<*, *, *>.stateHashCode(): Int {
            return hash(this.code, this.description)
        }

        @JvmStatic
        @JvmName("toString")
        fun State<*, *, *>.stateToString(): String {
            val code = this.code
            val description = this.description
            return if (description === null) code.toString() else "$code-$description"
        }

        @JvmStatic
        @JvmName("moreDescription")
        fun CharSequence?.stateMoreDescription(moreDescription: CharSequence?): String? {
            return when {
                this === null -> moreDescription?.toString()
                moreDescription === null -> this.toString()
                else -> "$this[$moreDescription]"
            }
        }
    }
}