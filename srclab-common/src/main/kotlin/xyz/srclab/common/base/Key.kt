@file:JvmName("Keys")
@file:JvmMultifileClass

package xyz.srclab.common.base

/**
 * @author sunqian
 */
interface Key {

    @Suppress("INAPPLICABLE_JVM_NAME")
    @get:JvmName("content")
    val content: List<*>

    override fun equals(other: Any?): Boolean

    override fun hashCode(): Int
}

fun keyOf(vararg content: Any?): Key {
    return KeyImpl(listOf(content))
}

fun keyOf(content: Iterable<*>): Key {
    return KeyImpl(content.toList())
}

private class KeyImpl(override val content: List<*>) : Key {

    override fun equals(other: Any?): Boolean {
        if (other !is Key) {
            return false
        }
        return content == other.content
    }

    override fun hashCode(): Int {
        return hash(content)
    }

    override fun toString(): String {
        return "Key[$content]"
    }
}