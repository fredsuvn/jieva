package xyz.srclab.common.status

import xyz.srclab.common.collect.asToList
import java.io.Serializable
import java.util.*

/**
 * [Status] of which type of code and descriptions are [String].
 */
@JvmDefaultWithoutCompatibility
open class StringStatus(
    override val code: String,
    override val descriptions: List<String>
) : Status<String, String>, Serializable {

    override val description: String? by lazy {
        descriptions.joinToDescription()
    }

    private val _hashcode: Int by lazy {
        Objects.hash(code, description)
    }

    constructor(code: String, description: String?) : this(
        code,
        if (description === null) emptyList() else listOf(description)
    )

    override fun withMoreDescriptions(additions: Iterable<String>): StringStatus {
        return StringStatus(code, descriptions.plus(additions))
    }

    override fun withNewDescriptions(descriptions: Iterable<String>): StringStatus {
        return StringStatus(code, descriptions.asToList())
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is StringStatus) return false
        if (code != other.code) return false
        if (descriptions != other.descriptions) return false
        return true
    }

    override fun hashCode(): Int = _hashcode

    override fun toString(): String {
        return "$code: $description"
    }
}