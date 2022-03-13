package xyz.srclab.common.status

import xyz.srclab.common.base.FinalObject
import xyz.srclab.common.base.defaultSerialVersion
import java.io.Serializable

/**
 * [Status] of which type of code and descriptions are [String].
 */
open class StringStatus @JvmOverloads constructor(
    override val code: String,
    override val description: String? = null
) : Status<String, String, StringStatus>, FinalObject(), Serializable {

    override fun withMoreDescription(addition: String): StringStatus {
        return StringStatus(code, "$description[$addition]")
    }

    override fun withNewDescription(description: String?): StringStatus {
        return StringStatus(code, description)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is StringStatus) return false
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