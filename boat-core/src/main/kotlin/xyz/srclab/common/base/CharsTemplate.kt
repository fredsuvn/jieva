//package xyz.srclab.common.base
//
//import xyz.srclab.annotations.Acceptable
//import xyz.srclab.annotations.Accepted
//import xyz.srclab.common.collect.reduce
//import java.util.*
//
///**
// * To resolve placeholder of a string:
// * ```
// * "This is a {name1}, that is a {name2}"
// * ```
// */
//interface CharsTemplate {
//
//    @Suppress(INAPPLICABLE_JVM_NAME)
//    val nodes: List<Node>
//        @JvmName("nodes") get
//
//    @JvmDefault
//    fun resolve(chars: CharSequence): List<Token> {
//        if (chars.isEmpty()) {
//            return emptyList()
//        }
//        val esc = escape
//
//        fun resolveWithEscape(esc: String): List<Token> {
//
//            fun Char.isToken(index: Int, token: String): Boolean {
//                if (this != token[0] || index > chars.length - token.length) {
//                    return false
//                }
//                var i = 1
//                while (i < token.length) {
//                    if (chars[index + i] != token[i]) {
//                        return false
//                    }
//                    i++
//                }
//                return true
//            }
//
//            val result = LinkedList<Token>()
//            var p = 0
//            var i = 0
//            var inPlaceholder = false
//            var argIndex = 0
//            while (i < chars.length) {
//                val char = chars[i]
//                if (char.isToken(i, esc)) {
//                    val nextIndex = i + esc.length
//                    if (nextIndex >= chars.length) {
//                        break
//                    }
//                    val nextChar = chars[nextIndex]
//                    if (inPlaceholder && nextChar.isToken(nextIndex, suffix)) {
//                        result.add(object : Token {
//                            override val startIndex: Int = p
//                            override val endIndex: Int = i
//                            override val type: Token.Type = Token.Type.TEXT
//                            override val argIndex: Int = argIndex
//                        })
//                        p = nextIndex
//                        i = nextIndex + suffix.length
//                        continue
//                    }
//                    if (!inPlaceholder && nextChar.isToken(nextIndex, prefix)) {
//                        result.add(object : Token {
//                            override val startIndex: Int = p
//                            override val endIndex: Int = i
//                            override val type: Token.Type = Token.Type.TEXT
//                        })
//                        p = nextIndex
//                        i = nextIndex + prefix.length
//                        continue
//                    }
//                }
//                if (char.isToken(i, prefix)) {
//                    if (inPlaceholder) {
//                        throw IllegalArgumentException(
//                            "Wrong token $prefix at index $i (${
//                                chars.subSequence(i, chars.lastIndex).ellipses(
//                                    ELLIPSES_NUMBER
//                                )
//                            })."
//                        )
//                    }
//                    result.add(object : Token {
//                        override val startIndex: Int = p
//                        override val endIndex: Int = i
//                        override val type: Token.Type = Token.Type.TEXT
//                    })
//                    result.add(object : Token {
//                        override val startIndex: Int = i
//                        override val endIndex: Int = i + prefix.length
//                        override val type: Token.Type = Token.Type.PREFIX
//                        override val argIndex: Int = argIndex++
//                    })
//                    inPlaceholder = true
//                    i += prefix.length
//                    p = i
//                    continue
//                }
//                if (char.isToken(i, suffix) && inPlaceholder) {
//                    result.add(object : Token {
//                        override val startIndex: Int = p
//                        override val endIndex: Int = i
//                        override val type: Token.Type = Token.Type.TEXT
//                        override val argIndex: Int = argIndex
//                    })
//                    result.add(object : Token {
//                        override val startIndex: Int = i
//                        override val endIndex: Int = i + suffix.length
//                        override val type: Token.Type = Token.Type.SUFFIX
//                        override val argIndex: Int = argIndex
//                    })
//                    inPlaceholder = false
//                    i += suffix.length
//                    p = i
//                    continue
//                }
//            }
//            if (inPlaceholder) {
//                throw IllegalArgumentException(
//                    "Suffix not found since index $p (${
//                        chars.subSequence(p, chars.length).ellipses(
//                            ELLIPSES_NUMBER
//                        )
//                    })."
//                )
//            }
//            if (p < chars.length) {
//                result.add(object : Token {
//                    override val startIndex: Int = p
//                    override val endIndex: Int = chars.length
//                    override val type: Token.Type = Token.Type.TEXT
//                })
//            }
//            return result.toList()
//        }
//
//
//
//        if (esc === null) {
//            return resolveWithoutEscape()
//        }
//        return resolveWithEscape(esc)
//    }
//
//    @JvmDefault
//    fun resolve(chars: CharSequence, nodes: List<Token>, args: Map<Any, Any?>): String {
//
//        fun List<Token>.getArgs(): Any? {
//            val key = this.reduce("") { s, it -> s + chars.subSequence(it.startIndex, it.endIndex) }
//            if (args.containsKey(key)) {
//                return args[key]
//            }
//            val index = this[0].argIndex
//            if (args.containsKey(index)) {
//                return args[index]
//            }
//            return key
//        }
//
//        if (nodes.isEmpty()) {
//            throw IllegalArgumentException("Resolved nodes must not empty.")
//        }
//        if (nodes.size == 1) {
//            return chars.toString()
//        }
//        val buf = StringBuilder()
//        var i = 0
//        while (i < nodes.size) {
//            val node = nodes[i]
//            if (node.type == Token.Type.TEXT) {
//                buf.append(chars.subSequence(node.startIndex, node.endIndex))
//                i++
//                continue
//            }
//            //            if (node.type == Node.Type.PREFIX) {
//            //                while ()
//            //            }
//        }
//        return buf.toString()
//    }
//
//    @JvmDefault
//    fun resolve(chars: CharSequence, args: Map<Any, Any?>): String {
//        return resolve(chars, resolve(chars), args)
//    }
//
//    interface Node {
//
//        @Suppress(INAPPLICABLE_JVM_NAME)
//        val tokens: List<Token>
//            @JvmName("tokens") get
//
//        @Suppress(INAPPLICABLE_JVM_NAME)
//        val type: Type
//            @JvmName("type") get
//
//        @JvmDefault
//        fun toText(chars: CharSequence): String {
//            if (tokens.isEmpty()) {
//                return ""
//            }
//            if (tokens.size == 1) {
//                return chars.toString()
//            }
//            val buf = StringBuilder()
//            for (token in tokens) {
//                buf.append(chars.subSequence(token.startIndex, token.endIndex))
//            }
//            return buf.toString()
//        }
//
//        @Acceptable(
//            Accepted(String::class),
//            Accepted(Integer::class),
//        )
//        @JvmDefault
//        fun toPlaceholderKey(chars: CharSequence): Any {
//            if (tokens.size < 3) {
//                throw IllegalArgumentException("A placeholder node has at least 3 tokens.")
//            }
//            if (tokens.first().type != Token.Type.PREFIX || tokens.last().type != Token.Type.SUFFIX) {
//                throw IllegalArgumentException(
//                    "A placeholder node must start with a prefix token and end with a suffix token."
//                )
//            }
//            if (tokens.size == 3) {
//                return chars.substring(tokens[1].startIndex, tokens[1].endIndex)
//            }
//            val buf = StringBuilder()
//            var i = 1
//            while (i < tokens.size - 1) {
//                buf.append(chars.substring(tokens[i].startIndex, tokens[i].endIndex))
//                i++
//            }
//            return buf.toString()
//        }
//
//        enum class Type {
//            TEXT, PLACEHOLDER
//        }
//
//        companion object {
//
//            @JvmStatic
//            fun newNode(tokens: List<Token>, type: Type): Node {
//                return object : Node {
//                    override val tokens: List<Token> = tokens
//                    override val type: Type = type
//                }
//            }
//        }
//    }
//
//    interface Token {
//
//        @Suppress(INAPPLICABLE_JVM_NAME)
//        val startIndex: Int
//            @JvmName("startIndex") get
//
//        @Suppress(INAPPLICABLE_JVM_NAME)
//        val endIndex: Int
//            @JvmName("endIndex") get
//
//        @Suppress(INAPPLICABLE_JVM_NAME)
//        val type: Type
//            @JvmName("type") get
//
//        @Suppress(INAPPLICABLE_JVM_NAME)
//        val argIndex: Int
//            @JvmName("argIndex") get() = DEFAULT_ARG_INDEX
//
//        enum class Type {
//            TEXT, PREFIX, SUFFIX
//        }
//
//        companion object {
//
//            private const val DEFAULT_ARG_INDEX = -1
//
//            @JvmStatic
//            @JvmOverloads
//            fun newToken(startIndex: Int, endIndex: Int, type: Type, argIndex: Int = DEFAULT_ARG_INDEX): Token {
//                return object : Token {
//                    override val startIndex: Int = startIndex
//                    override val endIndex: Int = endIndex
//                    override val type: Type = type
//                    override val argIndex: Int = argIndex
//                }
//            }
//        }
//    }
//
//    companion object {
//
//        private const val ELLIPSES_NUMBER = 10
//
//        @JvmStatic
//        @JvmName("resolve")
//        fun CharSequence.resolveCharsTemplate(placeholderPrefix: String, placeholderSuffix: String): CharsTemplate {
//            var prefixIndex = this.indexOf(placeholderPrefix)
//            if (prefixIndex < 0) {
//                return newCharsTemplate(
//                    listOf(
//                        Node.newNode(
//                            listOf(Token.newToken(0, this.length, Token.Type.TEXT)),
//                            Node.Type.TEXT
//                        )
//                    )
//                )
//            }
//            var p = 0
//            val result = LinkedList<Token>()
//            var argIndex = 0
//            while (prefixIndex >= 0) {
//                val suffixIndex = chars.indexOf(suffix, prefixIndex + prefix.length)
//                if (suffixIndex < 0) {
//                    throw IllegalArgumentException(
//                        "Cannot find suffix after prefix at index: $prefixIndex (${
//                            chars.subSequence(prefixIndex, chars.length).ellipses(
//                                ELLIPSES_NUMBER
//                            )
//                        })"
//                    )
//                }
//                result.add(object : Token {
//                    override val startIndex: Int = p
//                    override val endIndex: Int = prefixIndex
//                    override val type: Token.Type = Token.Type.TEXT
//                })
//                result.add(object : Token {
//                    override val startIndex: Int = prefixIndex
//                    override val endIndex: Int = prefixIndex + prefix.length
//                    override val type: Token.Type = Token.Type.PREFIX
//                    override val argIndex: Int = argIndex++
//                })
//                result.add(object : Token {
//                    override val startIndex: Int = prefixIndex + prefix.length
//                    override val endIndex: Int = suffixIndex
//                    override val type: Token.Type = Token.Type.TEXT
//                    override val argIndex: Int = argIndex
//                })
//                result.add(object : Token {
//                    override val startIndex: Int = suffixIndex
//                    override val endIndex: Int = suffixIndex + suffix.length
//                    override val type: Token.Type = Token.Type.SUFFIX
//                    override val argIndex: Int = argIndex
//                })
//                p = suffixIndex + suffix.length
//                prefixIndex = chars.indexOf(prefix, p)
//            }
//            if (p < chars.length) {
//                result.add(object : Token {
//                    override val startIndex: Int = p
//                    override val endIndex: Int = chars.length
//                    override val type: Token.Type = Token.Type.TEXT
//                })
//            }
//            return result.toList()
//        }
//
//        @JvmStatic
//        fun newCharsTemplate(nodes: List<Node>): CharsTemplate {
//            return object : CharsTemplate {
//                override val nodes: List<Node> = nodes.toList()
//            }
//        }
//
//        @JvmStatic
//        fun newCharsTemplateByTokens(tokens: List<Token>): CharsTemplate {
//            if (tokens.isEmpty()) {
//                return newCharsTemplate(emptyList())
//            }
//            val nodes = LinkedList<Node>()
//            var i = 0
//            while (i < tokens.size) {
//                val token = tokens[i]
//                if (token.type == Token.Type.TEXT){
//                    nodes.toList()
//                }
//            }
//        }
//    }
//}