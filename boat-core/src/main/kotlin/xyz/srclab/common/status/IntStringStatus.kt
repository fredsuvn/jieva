package xyz.srclab.common.status

import java.io.Serializable

/**
 * [Status] of which type of code is [Int] and of descriptions is [String].
 */
open class IntStringStatus @JvmOverloads constructor(
    override val code: Int,
    override val description: String? = null
) : Status<Int, String, IntStringStatus>, Serializable {

    override fun withMoreDescription(addition: String): IntStringStatus {
        return IntStringStatus(code, "$description[$addition]")
    }

    override fun withNewDescription(description: String?): IntStringStatus {
        return IntStringStatus(code, description)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is IntStringStatus) return false
        if (code != other.code) return false
        if (description != other.description) return false
        return true
    }

    override fun hashCode(): Int {
        var result = code.hashCode()
        result = 31 * result + (description?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "$code-$description"
    }
}