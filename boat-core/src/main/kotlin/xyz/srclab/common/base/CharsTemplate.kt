package xyz.srclab.common.base

import xyz.srclab.annotations.Acceptable
import xyz.srclab.annotations.Accepted
import java.util.*

/**
 * Chars template, to process a char sequence template:
 *
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
 * ```
 *
 * Chars template supports escape:
 *
 * ```
 * CharsTemplate template3 = CharsTemplate.resolve(
 * "This is a } \\{{name\\}} ({name}), that is a {}\\\\\\{\\", "{", "}", "\\");
 * logger.log(template3.process(args));
 * Assert.assertEquals(template3.process(args), "This is a } {DogX (Dog), that is a Bird\\{");
 * ```
 *
 * Note:
 *
 * * Escape works in front of any token;
 * * Parameter suffix token can be used before parameter prefix token --
 *   in this case, it will be seen as a common text;
 */
interface CharsTemplate {

    /**
     * Source chars template.
     */
    val template: CharSequence

    /**
     * Resolved nodes.
     */
    val nodes: List<Node>

    /**
     * Process with [args].
     */
    fun process(args: Map<
        @Acceptable(Accepted(String::class), Accepted(Integer::class)) Any,
        Any?
        >
    ): String {
        val builder = StringBuilder()
        process(builder, args)
        return builder.toString()
    }

    /**
     * Processes with [args].
     */
    fun process(
        dest: Appendable,
        args: Map<
            @Acceptable(Accepted(String::class), Accepted(Integer::class)) Any,
            Any?
            >,
    ) {
        for (node in nodes) {
            val value = node.text
            if (node.isText) {
                dest.append(value)
                continue
            }
            if (node.isParameter) {
                if (value.isEmpty()) {
                    dest.append(args[node.parameterIndex].toCharSeq())
                } else {
                    dest.append(args[value].toCharSeq())
                }
            }
        }
    }

    interface Node {

        val tokens: List<Token>

        val type: Type

        /**
         * Parameter index, or -1 if this is not a parameter.
         */
        val parameterIndex: Int

        val text: String

        val isText: Boolean
            get() = (type == Type.TEXT)

        val isParameter: Boolean
            get() = (type == Type.PARAMETER)

        enum class Type {
            TEXT, PARAMETER
        }

        companion object {
            @JvmStatic
            fun newNode(template: CharSequence, tokens: List<Token>, type: Type, parameterIndex: Int): Node {
                return object : Node {
                    override val tokens: List<Token> = tokens
                    override val type: Type = type
                    override val parameterIndex: Int = parameterIndex
                    override val text: String by lazy {
                        val stringBuilder = StringBuilder()
                        for (token in tokens) {
                            if (!token.isText) {
                                continue
                            }
                            stringBuilder.append(template.subSequence(token.startIndex, token.endIndex))
                        }
                        stringBuilder.toString()
                    }
                }
            }
        }
    }

    interface Token {

        val startIndex: Int
        val endIndex: Int
        val type: Type

        val isText: Boolean
            get() {
                return type == Type.TEXT
            }

        val isPrefix: Boolean
            get() {
                return type == Type.PREFIX
            }

        val isSuffix: Boolean
            get() {
                return type == Type.SUFFIX
            }

        val isEscape: Boolean
            get() {
                return type == Type.ESCAPE
            }

        enum class Type {
            TEXT, PREFIX, SUFFIX, ESCAPE
        }

        companion object {
            @JvmStatic
            fun newToken(startIndex: Int, endIndex: Int, type: Type): Token {
                return object : Token {
                    override val startIndex: Int = startIndex
                    override val endIndex: Int = endIndex
                    override val type: Type = type
                }
            }
        }
    }

    companion object {

        private const val ELLIPSES_NUMBER = 10

        @JvmName("resolve")
        @JvmStatic
        fun CharSequence.resolveTemplate(parameterPrefix: String, parameterSuffix: String): CharsTemplate {
            return WithoutEscape(this, parameterPrefix, parameterSuffix)
        }

        @JvmName("resolve")
        @JvmStatic
        fun CharSequence.resolveTemplate(
            parameterPrefix: String,
            parameterSuffix: String,
            escape: String
        ): CharsTemplate {
            return WithEscape(this, parameterPrefix, parameterSuffix, escape)
        }

        private class WithoutEscape(
            template: CharSequence, prefix: String, suffix: String
        ) : AbstractCharsTemplate(template) {

            override val tokens: List<Token> by lazy {
                var prefixIndex = template.indexOf(prefix)
                if (prefixIndex < 0) {
                    return@lazy listOf(Token.newToken(0, template.length, Token.Type.TEXT))
                }
                var startIndex = 0
                val tokens = LinkedList<Token>()
                while (prefixIndex >= 0) {
                    val suffixIndex = template.indexOf(suffix, prefixIndex + prefix.length)
                    if (suffixIndex < 0) {
                        throw IllegalArgumentException("Cannot find suffix after prefix at index: $prefixIndex (${
                            template.subSequence(prefixIndex, template.length).ellipses(ELLIPSES_NUMBER)
                        })")
                    }
                    if (prefixIndex > startIndex) {
                        tokens.add(Token.newToken(startIndex, prefixIndex, Token.Type.TEXT))
                    }
                    tokens.add(Token.newToken(prefixIndex, prefixIndex + prefix.length, Token.Type.PREFIX))
                    tokens.add(Token.newToken(prefixIndex + prefix.length, suffixIndex, Token.Type.TEXT))
                    tokens.add(Token.newToken(suffixIndex, suffixIndex + suffix.length, Token.Type.SUFFIX))
                    startIndex = suffixIndex + suffix.length
                    prefixIndex = template.indexOf(prefix, startIndex)
                }
                if (startIndex < template.length) {
                    tokens.add(Token.newToken(startIndex, template.length, Token.Type.TEXT))
                }
                tokens
            }
        }

        private class WithEscape(
            template: CharSequence, prefix: String, suffix: String, escape: String
        ) : AbstractCharsTemplate(template) {

            override val tokens: List<Token> by lazy {
                val tokens = LinkedList<Token>()
                var startIndex = 0
                var i = 0
                var inParameterScope = false
                while (i < template.length) {
                    if (template.startsWith(escape, i)) {
                        val nextIndex = i + escape.length
                        if (i > startIndex) {
                            tokens.add(Token.newToken(startIndex, i, Token.Type.TEXT))
                        }
                        tokens.add(Token.newToken(i, nextIndex, Token.Type.ESCAPE))
                        startIndex = nextIndex
                        i = nextIndex + 1
                        continue
                    }
                    if (template.startsWith(prefix, i)) {
                        if (inParameterScope) {
                            throw IllegalArgumentException("Wrong token $prefix at index $i (${
                                template.subSequence(i, template.length).ellipses(ELLIPSES_NUMBER)
                            }).")
                        }
                        if (i > startIndex) {
                            tokens.add(Token.newToken(startIndex, i, Token.Type.TEXT))
                        }
                        tokens.add(Token.newToken(i, i + prefix.length, Token.Type.PREFIX))
                        inParameterScope = true
                        i += prefix.length
                        startIndex = i
                        continue
                    }
                    if (template.startsWith(suffix, i) && inParameterScope) {
                        if (i > startIndex) {
                            tokens.add(Token.newToken(startIndex, i, Token.Type.TEXT))
                        }
                        tokens.add(Token.newToken(i, i + suffix.length, Token.Type.SUFFIX))
                        inParameterScope = false
                        i += suffix.length
                        startIndex = i
                        continue
                    }
                    i++
                }
                if (inParameterScope) {
                    throw IllegalArgumentException("Suffix not found since index $startIndex (${
                        template.subSequence(startIndex, template.length).ellipses(ELLIPSES_NUMBER)
                    }).")
                }
                if (startIndex < template.length) {
                    tokens.add(Token.newToken(startIndex, template.length, Token.Type.TEXT))
                }
                tokens
            }
        }
    }
}

/**
 * Abstract [CharsTemplate].
 */
abstract class AbstractCharsTemplate(
    override val template: CharSequence
) : CharsTemplate {

    protected abstract val tokens: List<CharsTemplate.Token>

    override val nodes: List<CharsTemplate.Node> by lazy { tokensToNodes(tokens) }

    private fun tokensToNodes(tokens: List<CharsTemplate.Token>): List<CharsTemplate.Node> {
        if (tokens.isEmpty()) {
            return emptyList()
        }
        val nodes = LinkedList<CharsTemplate.Node>()
        var i = 0
        var start = i
        var parameterIndex = 0
        loop@ while (i < tokens.size) {
            val token = tokens[i]
            if (token.isText || token.isEscape) {
                i++
                while (i < tokens.size) {
                    if (tokens[i].isText || tokens[i].isEscape) {
                        i++
                    } else {
                        nodes.add(CharsTemplate.Node.newNode(
                            template, tokens.subList(start, i), CharsTemplate.Node.Type.TEXT, -1))
                        start = i
                        continue@loop
                    }
                }
                break
            }
            if (token.isPrefix) {
                i++
                while (i < tokens.size) {
                    if (tokens[i].isText || tokens[i].isEscape) {
                        i++
                    } else if (tokens[i].isSuffix) {
                        nodes.add(CharsTemplate.Node.newNode(
                            template, tokens.subList(start, i), CharsTemplate.Node.Type.PARAMETER, parameterIndex)
                        )
                        i++
                        start = i
                        parameterIndex++
                        continue@loop
                    } else {
                        throw IllegalArgumentException("Only text or suffix token is permitted after a prefix token.")
                    }
                }
                break
            }
            throw IllegalArgumentException("Suffix token must after a prefix token.")
        }
        if (start != i) {
            nodes.add(CharsTemplate.Node.newNode(
                template, tokens.subList(start, i), CharsTemplate.Node.Type.TEXT, -1))
        }
        return nodes
    }
}