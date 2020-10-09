package xyz.srclab.common.base

/**
 * @author sunqian
 */
interface Key {

    @Suppress("INAPPLICABLE_JVM_NAME")
    val content: List<*>
        @JvmName("content") get

    override fun equals(other: Any?): Boolean

    override fun hashCode(): Int

    companion object {

        @JvmStatic
        fun of(vararg content: Any?): Key? {
            return KeyImpl(listOf(content))
        }

        @JvmStatic
        fun of(content: Iterable<*>): Key? {
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
                return Hash.hash(content)
            }

            override fun toString(): String {
                return "Key[$content]"
            }
        }
    }
}