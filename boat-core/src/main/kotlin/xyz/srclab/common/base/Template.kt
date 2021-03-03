package xyz.srclab.common.base

import java.util.*


/**
 * To resolve placeholder of a string:
 * ```
 * "This is a {name1}, that is a {name2}"
 * ```
 */
interface Template {

    @Suppress(INAPPLICABLE_JVM_NAME)
    val prefix: String
        @JvmName("prefix") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    val suffix: String
        @JvmName("suffix") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    val escape: String?
        @JvmName("escape") get

    @JvmDefault
    fun resolve(chars: CharSequence): List<Node> {
        if (chars.isEmpty()) {
            return emptyList()
        }

        fun resolveWithEscape(esc: String): List<Node> {

            fun Char.isEscape(index: Int): Boolean {
                if (this != esc[0] || index > chars.length - esc.length) {
                    return false
                }
                var i = 1
                while (i < esc.length) {
                    if (chars[index + 1] != esc[i]) {
                        return false
                    }
                    i++
                }
                return true
            }

            val result = LinkedList<Node>()
            var p = 0
            var i = 0
            while (i < chars.length) {
                val char = chars[i]
                if (char.isEscape(i)) {
                    i += esc.length + 1
                    continue
                }

            }
        }

        fun resolveWithoutEscape(): List<Node> {
            var prefixIndex = chars.indexOf(prefix)
            if (prefixIndex < 0) {
                return listOf(object : Node {
                    override val chars: CharSequence = chars
                    override val startIndex: Int = 0
                    override val endIndex: Int = chars.length
                    override val type: Node.Type = Node.Type.TEXT
                })
            }
            var p = 0
            val result = LinkedList<Node>()
            while (prefixIndex >= 0) {
                val suffixIndex = chars.indexOf(suffix, prefixIndex + prefix.length)
                if (suffixIndex < 0) {
                    throw IllegalArgumentException("Cannot find suffix after prefix at index: $prefixIndex")
                }
                result.add(object : Node {
                    override val chars: CharSequence = chars
                    override val startIndex: Int = p
                    override val endIndex: Int = prefixIndex
                    override val type: Node.Type = Node.Type.TEXT
                })
                result.add(object : Node {
                    override val chars: CharSequence = chars
                    override val startIndex: Int = prefixIndex
                    override val endIndex: Int = suffixIndex + suffix.length
                    override val type: Node.Type = Node.Type.PLACEHOLDER
                })
                p = suffixIndex + suffix.length
                prefixIndex = chars.indexOf(prefix, p)
            }
            if (p < chars.length) {
                result.add(object : Node {
                    override val chars: CharSequence = chars
                    override val startIndex: Int = p
                    override val endIndex: Int = chars.length
                    override val type: Node.Type = Node.Type.TEXT
                })
            }
            return result.toList()
        }

        val esc = escape
        if (esc === null || chars.indexOf(esc) < 0) {
            return resolveWithoutEscape()
        }
        return resolveWithEscape(esc)
    }

    @JvmDefault
    fun resolve(nodes: List<Node>, args: Map<Any, Any?>): String {

        fun Node.getArg(index: Int): Any? {
            if (this.type == Node.Type.TEXT) {
                return this.chars.toString()
            }
            val key = this.chars.substring(this.startIndex + prefix.length, this.endIndex - suffix.length)
            if (args.containsKey(key)) {
                return args[key]
            }
            if (args.containsKey(index)) {
                return args[index]
            }
            return key
        }

        if (nodes.isEmpty()) {
            return ""
        }
        if (nodes.size == 1) {
            return nodes[0].getArg(0).toString()
        }
        val buf = StringBuilder()
        for ((i, node) in nodes.withIndex()) {
            buf.append(node.getArg(i))
        }
        return buf.toString()
    }

    @JvmDefault
    fun resolve(chars: CharSequence, args: Map<Any, Any?>): String {
        return resolve(resolve(chars), args)
    }

    interface Node {

        @Suppress(INAPPLICABLE_JVM_NAME)
        val chars: CharSequence
            @JvmName("chars") get

        @Suppress(INAPPLICABLE_JVM_NAME)
        val startIndex: Int
            @JvmName("startIndex") get

        @Suppress(INAPPLICABLE_JVM_NAME)
        val endIndex: Int
            @JvmName("endIndex") get

        @Suppress(INAPPLICABLE_JVM_NAME)
        val type: Type
            @JvmName("type") get

        enum class Type {
            TEXT, PLACEHOLDER, ESCAPE
        }
    }

    companion object {

    }
}