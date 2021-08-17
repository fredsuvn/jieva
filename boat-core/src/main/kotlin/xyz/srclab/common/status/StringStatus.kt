package xyz.srclab.common.status

import java.io.Serializable
import java.util.*

/**
 * [Status] of which type of code and descriptions are [String].
 */
open class StringStatus : Status<String, String>, Serializable {

    private val _code: String
    private val _description: String?
    private val _descriptions: List<String>
    private val _hashcode: Int by lazy {
        Objects.hash(code, description)
    }

    constructor(code: String, description: String?) {
        _code = code
        _description = description
        _descriptions = if (description === null) emptyList() else listOf(description)
    }

    constructor(code: String, descriptions: List<String>) {
        _code = code
        _description = run {
            if (descriptions.size == 1) {
                return@run descriptions[0]
            }
            val builder = StringBuilder(descriptions[0])
            for (i in 1 until descriptions.size) {
                builder.append("[").append(descriptions[i]).append("]")
            }
            builder.toString()
        }
        _descriptions = descriptions
    }

    override val code: String get() = _code

    override val description: String? get() = _description

    override val descriptions: List<String> get() = _descriptions

    override fun withNewDescription(newDescription: String?): StringStatus {
        return StringStatus(code, description)
    }

    override fun withMoreDescription(moreDescription: String): StringStatus {
        return StringStatus(code, descriptions.plus(moreDescription))
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is StringStatus) return false
        if (code != other.code) return false
        if (description != other.description) return false
        return true
    }

    override fun hashCode(): Int = _hashcode

    override fun toString(): String {
        return "$code-$description"
    }
}