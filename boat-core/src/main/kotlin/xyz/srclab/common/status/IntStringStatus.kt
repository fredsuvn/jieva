package xyz.srclab.common.status

import xyz.srclab.common.collect.asToList
import java.io.Serializable
import java.util.*

/**
 * [Status] of which type of code is [Int] and of descriptions is [String].
 */
@JvmDefaultWithoutCompatibility
open class IntStringStatus(
    override val code: Int,
    override val descriptions: List<String>
) : Status<Int, String>, Serializable {

    override val description: String? by lazy {
        descriptions.joinToDescription()
    }

    private val _hashcode: Int by lazy {
        Objects.hash(code, description)
    }

    constructor(code: Int, description: String?) : this(
        code,
        if (description === null) emptyList() else listOf(description)
    )

    override fun withMoreDescriptions(additions: Iterable<String>): IntStringStatus {
        return IntStringStatus(code, descriptions.plus(additions))
    }

    override fun withNewDescriptions(descriptions: Iterable<String>): IntStringStatus {
        return IntStringStatus(code, descriptions.asToList())
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is IntStringStatus) return false
        if (code != other.code) return false
        if (descriptions != other.descriptions) return false
        return true
    }

    override fun hashCode(): Int = _hashcode

    override fun toString(): String {
        return "$code: $description"
    }
}