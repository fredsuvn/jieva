package xyz.srclab.common.base

import xyz.srclab.annotations.Acceptable
import xyz.srclab.annotations.Accepted
import xyz.srclab.annotations.concurrent.ThreadSafe
import xyz.srclab.common.collect.toImmutableSet
import java.io.StringWriter
import java.io.Writer
import java.util.*

/**
 * Chars template, to build a template with custom parameters, then process with given arguments:
 * ```
 * Map<Object, Object> args = new HashMap<>();
 * args.put("name", "Dog");
 * args.put("name}", "DogX");
 * args.put(1, "Cat");
 * args.put(2, "Bird");
 * CharsTemplate template1 = CharsTemplate.resolve(
 * "This is a {name}, that is a {}", "{", "}");
 * Assert.assertEquals(template1.process(args), "This is a Dog, that is a Cat");
 * CharsTemplate template2 = CharsTemplate.resolve(
 * "This is a } {name}, that is a {}}", "{", "}");
 * Assert.assertEquals(template2.process(args), "This is a } Dog, that is a Cat}");
 * CharsTemplate template3 = CharsTemplate.resolve(
 * "This is a } \\{{name\\}} ({name}), that is a {}\\\\\\{\\", "{", "}", "\\");
 * //logger.log(template3.process(args));
 * Assert.assertEquals(template3.process(args), "This is a } {DogX (Dog), that is a Bird\\\\{\\");
 * ```
 */
@ThreadSafe
interface CharsTemplate {

    @Suppress(INAPPLICABLE_JVM_NAME)
    val chars: CharSequence
        @JvmName("chars") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    val nodes: List<Node>
        @JvmName("nodes") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    @JvmDefault
    val parameters: Set<@Acceptable(
        Accepted(String::class),
        Accepted(Integer::class),
    ) Any>
        @JvmName("parameters") get() {
            return nodes.filter { it.isParameter }
                .map { it.toParameterNameOrIndex(chars) }
                .toImmutableSet()
        }

    @JvmDefault
    fun process(
        args: Map<out @Acceptable(
            Accepted(String::class),
            Accepted(Integer::class),
        ) Any, Any?>
    ): String {
        val writer = StringWriter()
        process(writer, args)
        return writer.toString()
    }

    @JvmDefault
    fun process(
        dest: Writer,
        args: Map<out @Acceptable(
            Accepted(String::class),
            Accepted(Integer::class),
        ) Any, Any?>,
    ) {
        for (node in nodes) {
            if (node.isText) {
                dest.write(node.toText(chars))
                continue
            }
            val key = node.toParameterNameOrIndex(chars)
            dest.write(args[key].toString())
        }
    }

    /**
     * Node of [CharsTemplate], each one denotes a text or parameter variable.
     */
    interface Node {

        @Suppress(INAPPLICABLE_JVM_NAME)
        val tokens: List<Token>
            @JvmName("tokens") get

        @Suppress(INAPPLICABLE_JVM_NAME)
        val type: Type
            @JvmName("type") get

        @Suppress(INAPPLICABLE_JVM_NAME)
        val isText: Boolean
            @JvmName("isText") get() = (type == Type.TEXT)

        @Suppress(INAPPLICABLE_JVM_NAME)
        val isParameter: Boolean
            @JvmName("isParameter") get() = (type == Type.PARAMETER)

        @JvmDefault
        fun toText(chars: CharSequence): String {
            if (tokens.isEmpty()) {
                return ""
            }
            if (tokens.size == 1) {
                return tokens[0].toText(chars)
            }
            val buf = StringBuilder()
            for (token in tokens) {
                buf.append(token.toText(chars))
            }
            return buf.toString()
        }

        @Acceptable(
            Accepted(String::class),
            Accepted(Integer::class),
        )
        @JvmDefault
        fun toParameterNameOrIndex(chars: CharSequence): Any {
            if (tokens.size < 2) {
                throw IllegalArgumentException("A parameter node has at least 2 tokens.")
            }
            if (!tokens.first().isPrefix || !tokens.last().isSuffix) {
                throw IllegalArgumentException(
                    "A parameter node must start with a prefix token and end with a suffix token."
                )
            }
            if (tokens.size == 2) {
                return tokens[0].argIndex
            }
            if (tokens.size == 3) {
                val key = tokens[1].toText(chars)
                return if (key.isEmpty()) tokens[1].argIndex else key
            }
            val buf = StringBuilder()
            var i = 1
            while (i < tokens.size - 1) {
                buf.append(tokens[i].toText(chars))
                i++
            }
            return buf.toString()
        }

        enum class Type {
            TEXT, PARAMETER
        }

        companion object {

            @JvmStatic
            fun newNode(tokens: List<Token>, type: Type): Node {
                return object : Node {
                    override val tokens: List<Token> = tokens
                    override val type: Type = type
                }
            }
        }
    }

    /**
     * Token of [CharsTemplate], each one denotes a text or parameter prefix or parameter suffix.
     */
    interface Token {

        @Suppress(INAPPLICABLE_JVM_NAME)
        val startIndex: Int
            @JvmName("startIndex") get

        @Suppress(INAPPLICABLE_JVM_NAME)
        val endIndex: Int
            @JvmName("endIndex") get

        @Suppress(INAPPLICABLE_JVM_NAME)
        val type: Type
            @JvmName("type") get

        @Suppress(INAPPLICABLE_JVM_NAME)
        val argIndex: Int
            @JvmName("argIndex") get() = DEFAULT_ARG_INDEX

        @Suppress(INAPPLICABLE_JVM_NAME)
        val isText: Boolean
            @JvmName("isText") get() = (type == Type.TEXT)

        @Suppress(INAPPLICABLE_JVM_NAME)
        val isPrefix: Boolean
            @JvmName("isPrefix") get() = (type == Type.PREFIX)

        @Suppress(INAPPLICABLE_JVM_NAME)
        val isSuffix: Boolean
            @JvmName("isSuffix") get() = (type == Type.SUFFIX)

        @JvmDefault
        fun toText(chars: CharSequence): String {
            return chars.substring(startIndex, endIndex)
        }

        enum class Type {
            TEXT, PREFIX, SUFFIX
        }

        companion object {

            private const val DEFAULT_ARG_INDEX = -1

            @JvmStatic
            @JvmOverloads
            fun newToken(startIndex: Int, endIndex: Int, type: Type, argIndex: Int = DEFAULT_ARG_INDEX): Token {
                return object : Token {
                    override val startIndex: Int = startIndex
                    override val endIndex: Int = endIndex
                    override val type: Type = type
                    override val argIndex: Int = argIndex
                }
            }
        }
    }

    companion object {

        private const val ELLIPSES_NUMBER = 10

        @JvmStatic
        fun newCharsTemplate(chars: CharSequence, nodes: List<Node>): CharsTemplate {
            return object : CharsTemplate {
                override val chars: CharSequence = chars
                override val nodes: List<Node> = nodes
                override val parameters: Set<Any> = super.parameters
            }
        }

        @JvmStatic
        fun newCharsTemplateByTokens(chars: CharSequence, tokens: List<Token>): CharsTemplate {
            if (tokens.isEmpty()) {
                return newCharsTemplate(chars, emptyList())
            }
            val nodes = LinkedList<Node>()
            val subTokens = LinkedList<Token>()
            var i = 0
            loop@ while (i < tokens.size) {
                val token = tokens[i]
                if (token.isText) {
                    subTokens.add(token)
                    i++
                    while (i < tokens.size) {
                        if (tokens[i].isText) {
                            subTokens.add(tokens[i])
                            i++
                        } else {
                            nodes.add(Node.newNode(subTokens.toList(), Node.Type.TEXT))
                            subTokens.clear()
                            continue@loop
                        }
                    }
                    break
                }
                if (token.isPrefix) {
                    subTokens.add(token)
                    i++
                    while (i < tokens.size) {
                        if (tokens[i].isText) {
                            subTokens.add(tokens[i])
                            i++
                        } else if (tokens[i].isSuffix) {
                            subTokens.add(tokens[i])
                            nodes.add(Node.newNode(subTokens.toList(), Node.Type.PARAMETER))
                            subTokens.clear()
                            i++
                            continue@loop
                        } else {
                            throw IllegalArgumentException(
                                "Only text or suffix token is permitted after a prefix token."
                            )
                        }
                    }
                    break
                }
                throw IllegalArgumentException("Suffix token must after a prefix token.")
            }
            if (subTokens.isNotEmpty()) {
                nodes.add(Node.newNode(subTokens.toList(), Node.Type.TEXT))
            }
            return newCharsTemplate(chars, nodes.toList())
        }

        @JvmStatic
        @JvmName("resolve")
        fun CharSequence.resolveTemplate(parameterPrefix: String, parameterSuffix: String): CharsTemplate {
            var prefixIndex = this.indexOf(parameterPrefix)
            if (prefixIndex < 0) {
                return newCharsTemplateByTokens(this, listOf(Token.newToken(0, this.length, Token.Type.TEXT)))
            }
            var p = 0
            val tokens = LinkedList<Token>()
            var argIndex = 0
            while (prefixIndex >= 0) {
                val suffixIndex = this.indexOf(parameterSuffix, prefixIndex + parameterPrefix.length)
                if (suffixIndex < 0) {
                    throw IllegalArgumentException(
                        "Cannot find suffix after prefix at index: $prefixIndex (${
                            this.subSequence(prefixIndex, this.length).ellipses(
                                ELLIPSES_NUMBER
                            )
                        })"
                    )
                }
                tokens.add(Token.newToken(p, prefixIndex, Token.Type.TEXT))
                tokens.add(
                    Token.newToken(
                        prefixIndex,
                        prefixIndex + parameterPrefix.length,
                        Token.Type.PREFIX,
                        argIndex++
                    )
                )
                if (prefixIndex + parameterPrefix.length < suffixIndex) {
                    tokens.add(
                        Token.newToken(
                            prefixIndex + parameterPrefix.length,
                            suffixIndex,
                            Token.Type.TEXT,
                            argIndex
                        )
                    )
                }
                tokens.add(
                    Token.newToken(
                        suffixIndex,
                        suffixIndex + parameterSuffix.length,
                        Token.Type.SUFFIX,
                        argIndex
                    )
                )
                p = suffixIndex + parameterSuffix.length
                prefixIndex = this.indexOf(parameterPrefix, p)
            }
            if (p < this.length) {
                tokens.add(Token.newToken(p, this.length, Token.Type.TEXT))
            }
            return newCharsTemplateByTokens(this, tokens)
        }

        @JvmStatic
        @JvmName("resolve")
        fun CharSequence.resolveTemplate(
            parameterPrefix: String,
            parameterSuffix: String,
            escape: String
        ): CharsTemplate {

            fun Char.isToken(index: Int, token: String): Boolean {
                if (this != token[0] || index > this@resolveTemplate.length - token.length) {
                    return false
                }
                var i = 1
                while (i < token.length) {
                    if (this@resolveTemplate[index + i] != token[i]) {
                        return false
                    }
                    i++
                }
                return true
            }

            val tokens = LinkedList<Token>()
            var p = 0
            var i = 0
            var inParameterScope = false
            var argIndex = 0
            while (i < this.length) {
                val char = this[i]
                if (char.isToken(i, escape)) {
                    val nextIndex = i + escape.length
                    if (nextIndex >= this.length) {
                        break
                    }
                    val nextChar = this[nextIndex]
                    if (inParameterScope && nextChar.isToken(nextIndex, parameterSuffix)) {
                        if (i > p) {
                            tokens.add(Token.newToken(p, i, Token.Type.TEXT, argIndex))
                        }
                        p = nextIndex
                        i = nextIndex + parameterSuffix.length
                        continue
                    }
                    if (!inParameterScope && nextChar.isToken(nextIndex, parameterPrefix)) {
                        tokens.add(Token.newToken(p, i, Token.Type.TEXT))
                        p = nextIndex
                        i = nextIndex + parameterPrefix.length
                        continue
                    }
                }
                if (char.isToken(i, parameterPrefix)) {
                    if (inParameterScope) {
                        throw IllegalArgumentException(
                            "Wrong token $parameterPrefix at index $i (${
                                this.subSequence(i, this.length).ellipses(ELLIPSES_NUMBER)
                            })."
                        )
                    }
                    tokens.add(Token.newToken(p, i, Token.Type.TEXT))
                    tokens.add(Token.newToken(i, i + parameterPrefix.length, Token.Type.PREFIX, argIndex++))
                    inParameterScope = true
                    i += parameterPrefix.length
                    p = i
                    continue
                }
                if (char.isToken(i, parameterSuffix) && inParameterScope) {
                    if (i > p) {
                        tokens.add(Token.newToken(p, i, Token.Type.TEXT, argIndex))
                    }
                    tokens.add(Token.newToken(i, i + parameterSuffix.length, Token.Type.SUFFIX, argIndex))
                    inParameterScope = false
                    i += parameterSuffix.length
                    p = i
                    continue
                }
                i++
            }
            if (inParameterScope) {
                throw IllegalArgumentException(
                    "Suffix not found since index $p (${
                        this.subSequence(p, this.length).ellipses(ELLIPSES_NUMBER)
                    })."
                )
            }
            if (p < this.length) {
                tokens.add(Token.newToken(p, this.length, Token.Type.TEXT))
            }
            return newCharsTemplateByTokens(this, tokens)
        }
    }
}