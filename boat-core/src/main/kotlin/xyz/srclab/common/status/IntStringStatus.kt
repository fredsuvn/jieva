package xyz.srclab.common.status

import xyz.srclab.common.base.FinalClass
import xyz.srclab.common.base.defaultSerialVersion
import java.io.Serializable

/**
 * [Status] of which type of code is [Int] and of descriptions is [String].
 */
open class IntStringStatus @JvmOverloads constructor(
    override val code: Int,
    override val description: String? = null,
) : Status<Int, String, IntStringStatus>, FinalClass(), Serializable {

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

    override fun hashCode0(): Int {
        var result = code.hashCode()
        result = 31 * result + (description?.hashCode() ?: 0)
        return result
    }

    override fun toString0(): String {
        return statusToString(code, description)
    }

    companion object {
        private val serialVersionUID: Long = defaultSerialVersion()
    }
}