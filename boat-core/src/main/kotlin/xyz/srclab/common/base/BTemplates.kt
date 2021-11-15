@file:JvmName("BTemplates")

package xyz.srclab.common.base

import xyz.srclab.annotations.Accepted
import java.util.*

@JvmName("resolve")
fun CharSequence.resolveTemplate(parameterPrefix: CharSequence, parameterSuffix: CharSequence): BTemplate {
    return WithoutEscape(this, parameterPrefix, parameterSuffix)
}

@JvmName("resolve")
fun CharSequence.resolveTemplate(
    parameterPrefix: CharSequence,
    parameterSuffix: CharSequence,
    escape: CharSequence
): BTemplate {
    return WithEscape(this, parameterPrefix, parameterSuffix, escape)
}

/**
 * Chars template, to process a char sequence template:
 *
 * ```
 * Map<Object, Object> args = new HashMap<>();
 * args.put("name", "Dog");
 * args.put("name}", "DogX");
 * args.put(1, "Cat");
 * args.put(2, "Bird");
 * BTemplate template1 = BTemplates.resolve(
 * "This is a {name}, that is a {}", "{", "}");
 * Assert.assertEquals(template1.process(args), "This is a Dog, that is a Cat");
 * BTemplate template2 = BTemplates.resolve(
 * "This is a } {name}, that is a {}}", "{", "}");
 * Assert.assertEquals(template2.process(args), "This is a } Dog, that is a Cat}");
 * ```
 *
 * Chars template supports escape:
 *
 * ```
 * BTemplate template3 = BTemplates.resolve(
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
interface BTemplate {

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
    fun process(args: Map<@Accepted(String::class, Integer::class) Any, Any?>): String {
        val builder = StringBuilder()
        process(builder, args)
        return builder.toString()
    }

    /**
     * Processes with [args].
     */
    fun process(dest: Appendable, args: Map<@Accepted(String::class, Integer::class) Any, Any?>) {
        for (node in nodes) {
            val value = node.value
            if (node.isText) {
                dest.append(value.toCharSeq())
                continue
            }
            if (node.isParameter) {
                dest.append(args[value].toCharSeq())
            }
        }
    }

    interface Node {

        val tokens: List<Token>

        val type: Type

        /**
         * If this node is text, `value` is actual value of the node;
         *
         * If this node is a parameter, `value` is either index ([Integer]) or key ([CharSequence]) of arguments.
         */
        @get:Accepted(CharSequence::class, Integer::class)
        val value: Any

        val isText: Boolean
            get() = (type == Type.TEXT)

        val isParameter: Boolean
            get() = (type == Type.PARAMETER)

        enum class Type {
            TEXT, PARAMETER
        }

        companion object {

            /**
             * [parameterIndex] may be -1 if returned node is not as an index.
             */
            @JvmStatic
            fun newNode(template: CharSequence, tokens: List<Token>, type: Type, parameterIndex: Int): Node {
                return object : Node {
                    override val tokens: List<Token> = tokens
                    override val type: Type = type
                    override val value: Any by lazy {
                        if (parameterIndex != -1) {
                            return@lazy parameterIndex
                        }
                        val chars = LinkedList<CharSequence>()
                        for (token in tokens) {
                            if (!token.isText) {
                                continue
                            }
                            chars.add(template.refOfRange(token.startIndex, token.endIndex))
                        }
                        chars.joinToString("")
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
}

/**
 * Abstract [BTemplate].
 */
abstract class AbstractCharsTemplate(
    override val template: CharSequence
) : BTemplate {

    protected abstract val tokens: List<BTemplate.Token>

    override val nodes: List<BTemplate.Node> by lazy { resolveNodes(tokens) }

    private fun resolveNodes(tokens: List<BTemplate.Token>): List<BTemplate.Node> {
        if (tokens.isEmpty()) {
            return emptyList()
        }
        val nodes = LinkedList<BTemplate.Node>()
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
                        nodes.add(BTemplate.Node.newNode(
                            template, tokens.subList(start, i), BTemplate.Node.Type.TEXT, -1
                        ))
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
                        nodes.add(BTemplate.Node.newNode(
                            template, tokens.subList(start, i), BTemplate.Node.Type.PARAMETER, parameterIndex
                        ))
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
            nodes.add(BTemplate.Node.newNode(
                template, tokens.subList(start, i), BTemplate.Node.Type.TEXT, -1
            ))
        }
        return nodes
    }
}

private class WithoutEscape(
    template: CharSequence,
    prefix: CharSequence,
    suffix: CharSequence
) : AbstractCharsTemplate(template) {

    override val tokens: List<BTemplate.Token> by lazy {
        resolveTokens(template, prefix.toString(), suffix.toString())
    }

    private fun resolveTokens(
        template: CharSequence,
        prefix: String,
        suffix: String
    ): List<BTemplate.Token> {
        var prefixIndex = template.indexOf(prefix)
        if (prefixIndex < 0) {
            return listOf(BTemplate.Token.newToken(0, template.length, BTemplate.Token.Type.TEXT))
        }
        var startIndex = 0
        val tokens = LinkedList<BTemplate.Token>()
        while (prefixIndex >= 0) {
            val suffixIndex = template.indexOf(suffix, prefixIndex + prefix.length)
            if (suffixIndex < 0) {
                throw IllegalArgumentException(
                    "Cannot find suffix after prefix at index: $prefixIndex (${
                        template.subSequence(prefixIndex, template.length).ellipses()
                    })"
                )
            }
            if (prefixIndex > startIndex) {
                tokens.add(BTemplate.Token.newToken(startIndex, prefixIndex, BTemplate.Token.Type.TEXT))
            }
            tokens.add(BTemplate.Token.newToken(prefixIndex, prefixIndex + prefix.length, BTemplate.Token.Type.PREFIX))
            tokens.add(BTemplate.Token.newToken(prefixIndex + prefix.length, suffixIndex, BTemplate.Token.Type.TEXT))
            tokens.add(BTemplate.Token.newToken(suffixIndex, suffixIndex + suffix.length, BTemplate.Token.Type.SUFFIX))
            startIndex = suffixIndex + suffix.length
            prefixIndex = template.indexOf(prefix, startIndex)
        }
        if (startIndex < template.length) {
            tokens.add(BTemplate.Token.newToken(startIndex, template.length, BTemplate.Token.Type.TEXT))
        }
        return tokens
    }
}

private class WithEscape(
    template: CharSequence,
    prefix: CharSequence,
    suffix: CharSequence,
    escape: CharSequence
) : AbstractCharsTemplate(template) {

    override val tokens: List<BTemplate.Token> by lazy {
        resolveTokens(template, prefix.toString(), suffix.toString(), escape.toString())
    }

    private fun resolveTokens(
        template: CharSequence,
        prefix: String,
        suffix: String,
        escape: String
    ): List<BTemplate.Token> {
        val tokens = LinkedList<BTemplate.Token>()
        var startIndex = 0
        var i = 0
        var inParameterScope = false
        while (i < template.length) {
            if (template.startsWith(escape, i)) {
                val nextIndex = i + escape.length
                if (i > startIndex) {
                    tokens.add(BTemplate.Token.newToken(startIndex, i, BTemplate.Token.Type.TEXT))
                }
                tokens.add(BTemplate.Token.newToken(i, nextIndex, BTemplate.Token.Type.ESCAPE))
                startIndex = nextIndex
                i = nextIndex + 1
                continue
            }
            if (template.startsWith(prefix, i)) {
                if (inParameterScope) {
                    throw IllegalArgumentException(
                        "Wrong token $prefix at index $i (${
                            template.subSequence(i, template.length).ellipses()
                        })."
                    )
                }
                if (i > startIndex) {
                    tokens.add(BTemplate.Token.newToken(startIndex, i, BTemplate.Token.Type.TEXT))
                }
                tokens.add(BTemplate.Token.newToken(i, i + prefix.length, BTemplate.Token.Type.PREFIX))
                inParameterScope = true
                i += prefix.length
                startIndex = i
                continue
            }
            if (template.startsWith(suffix, i) && inParameterScope) {
                if (i > startIndex) {
                    tokens.add(BTemplate.Token.newToken(startIndex, i, BTemplate.Token.Type.TEXT))
                }
                tokens.add(BTemplate.Token.newToken(i, i + suffix.length, BTemplate.Token.Type.SUFFIX))
                inParameterScope = false
                i += suffix.length
                startIndex = i
                continue
            }
            i++
        }
        if (inParameterScope) {
            throw IllegalArgumentException(
                "Suffix not found since index $startIndex (${
                    template.subSequence(startIndex, template.length).ellipses()
                })."
            )
        }
        if (startIndex < template.length) {
            tokens.add(BTemplate.Token.newToken(startIndex, template.length, BTemplate.Token.Type.TEXT))
        }
        return tokens
    }
}