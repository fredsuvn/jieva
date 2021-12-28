@file:JvmName("BTemplates")

package xyz.srclab.common.base

import xyz.srclab.annotations.Accepted
import xyz.srclab.common.base.StringRef.Companion.stringRef
import java.util.*
import kotlin.jvm.Throws

@JvmName("resolve")
fun CharSequence.resolveTemplate(parameterPrefix: CharSequence, parameterSuffix: CharSequence): StringTemplate {
    return WithoutEscape(this, parameterPrefix, parameterSuffix)
}

@JvmName("resolve")
fun CharSequence.resolveTemplate(
    parameterPrefix: CharSequence,
    parameterSuffix: CharSequence,
    escape: CharSequence
): StringTemplate {
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
interface StringTemplate {

    /**
     * Resolved nodes of this string template.
     * Some elements' type is [Parameter] although all elements' type is [CharSequence],
     */
    @get:Throws(StringTemplateException::class)
    val nodes: List<CharSequence>

    /**
     * Process with [args].
     */
    @Throws(StringTemplateException::class)
    fun process(args: Map<@Accepted(String::class, Integer::class) Any, Any?>): String {
        val nodesArray = nodes.toTypedArray()
        var i = 0
        while (i < nodesArray.size) {
            val node = nodesArray[i]
            if (node is Parameter) {
                nodesArray[i] = args.getValue(node).toCharSeq()
            }
            i++
        }
        return nodesArray.joinToString("")
    }

    /**
     * Processes with [args].
     */
    @Throws(StringTemplateException::class)
    fun processTo(dest: Appendable, args: Map<@Accepted(String::class, Integer::class) Any, Any?>) {
        for (node in nodes) {
            if (node is Parameter) {
                dest.append(args.getValue(node).toCharSeq())
            } else {
                dest.append(node)
            }
        }
    }

    private fun Map<@Accepted(String::class, Integer::class) Any, Any?>.getValue(node: Parameter): Any? {
        if (node.isEmpty()) {
            return this[node.index]
        }
        return this[node.toString()] ?: this[node.index]
    }

    /**
     * Represents current node is a parameter in this string template.
     * Note content of [Parameter] may be empty.
     */
    interface Parameter : CharSequence {

        /**
         * Index of current parameter.
         */
        val index: Int

        companion object {

            @JvmStatic
            fun empty(index: Int): Parameter {
                return object : Parameter {

                    override val index: Int = index

                    override val length: Int = 0

                    override fun get(index: Int): Char {
                        throw IndexOutOfBoundsException("$index")
                    }

                    override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
                        if (startIndex == endIndex) {
                            return this
                        }
                        throw IndexOutOfBoundsException("startIndex: $startIndex, endIndex: $endIndex")
                    }
                }
            }

            @JvmStatic
            fun of(chars: CharSequence, index: Int): Parameter {
                return object : Parameter {

                    override val index: Int = index

                    override val length: Int = chars.length

                    override fun get(index: Int): Char {
                        return chars[index]
                    }

                    override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
                        return chars.subSequence(startIndex, endIndex)
                    }
                }
            }
        }
    }

    //interface Node {
    //
    //    val tokens: List<Token>
    //
    //    val type: Type
    //
    //    /**
    //     * If this node is text, `value` is actual value of the node;
    //     *
    //     * If this node is a parameter, `value` is either index ([Integer]) or key ([CharSequence]) of arguments.
    //     */
    //    @get:Accepted(CharSequence::class, Integer::class)
    //    val value: Any
    //
    //    val isText: Boolean
    //        get() = (type == Type.TEXT)
    //
    //    val isParameter: Boolean
    //        get() = (type == Type.PARAMETER)
    //
    //    enum class Type {
    //        TEXT, PARAMETER
    //    }
    //
    //    companion object {
    //
    //        /**
    //         * [parameterIndex] may be -1 if returned node is not as an index.
    //         */
    //        @JvmStatic
    //        fun newNode(template: CharSequence, tokens: List<Token>, type: Type, parameterIndex: Int): Node {
    //            return object : Node {
    //                override val tokens: List<Token> = tokens
    //                override val type: Type = type
    //                override val value: Any by lazy {
    //                    if (parameterIndex != -1) {
    //                        return@lazy parameterIndex
    //                    }
    //                    val chars = LinkedList<CharSequence>()
    //                    for (token in tokens) {
    //                        if (!token.isText) {
    //                            continue
    //                        }
    //                        chars.add(template.refOfRange(token.startIndex, token.endIndex))
    //                    }
    //                    chars.joinToString("")
    //                }
    //            }
    //        }
    //    }
    //}

    //interface Token {
    //
    //    val startIndex: Int
    //    val endIndex: Int
    //    val type: Type
    //
    //    val isText: Boolean
    //        get() {
    //            return type == Type.TEXT
    //        }
    //
    //    val isPrefix: Boolean
    //        get() {
    //            return type == Type.PREFIX
    //        }
    //
    //    val isSuffix: Boolean
    //        get() {
    //            return type == Type.SUFFIX
    //        }
    //
    //    val isEscape: Boolean
    //        get() {
    //            return type == Type.ESCAPE
    //        }
    //
    //    enum class Type {
    //        TEXT, PREFIX, SUFFIX, ESCAPE
    //    }
    //
    //    companion object {
    //        @JvmStatic
    //        fun newToken(startIndex: Int, endIndex: Int, type: Type): Token {
    //            return object : Token {
    //                override val startIndex: Int = startIndex
    //                override val endIndex: Int = endIndex
    //                override val type: Type = type
    //            }
    //        }
    //    }
    //}
}

open class SimpleTemplate(
    private val template: CharSequence,
    private val escapeChar: Char,
    parameterPrefix: CharSequence,
    parameterSuffix: CharSequence? = null
) : StringTemplate {

    override val nodes: List<CharSequence> by lazy { resolve() }

    init {
        checkState(parameterPrefix.isNotEmpty(), "Parameter prefix cannot empty.")
        checkState(
            parameterSuffix === null || parameterSuffix.isNotEmpty(),
            "Parameter suffix must be null or non-empty."
        )
    }

    private val prefix = parameterPrefix.toString()
    private val suffix = parameterSuffix?.toString()

    private fun resolve(): List<CharSequence> {

        if (template.isEmpty()) {
            return listOf(template)
        }

        var buffer: MutableList<CharSequence>? = null

        fun getBuffer(): MutableList<CharSequence> {
            val bf = buffer
            if (bf === null) {
                val newBuffer = LinkedList<CharSequence>()
                buffer = newBuffer
                return newBuffer
            }
            return bf
        }

        fun isParameterPrefix(index: Int): Boolean {
            return template.startsWith(prefix, index)
        }

        var start = 0
        var i = 0
        var paramIndex = 0
        while (i < template.length) {
            val c = template[i]
            if (c == escapeChar) {
                i++
                if (i > template.length) {
                    break
                }
                val cn = template[i]
                if (cn == escapeChar) {
                    //Escape itself
                    getBuffer().add(template.stringRef(start, i))
                    i++
                    start = i
                    continue
                }
                if (isParameterPrefix(i)) {
                    //Escape parameter prefix
                    getBuffer().add(template.stringRef(start, i - 1))
                    getBuffer().add(prefix)
                    i += prefix.length
                    start = i
                    continue
                }
            }
            if (isParameterPrefix(i)) {
                i += prefix.length
                if (suffix === null) {
                    //Find next whitespace
                    val paramNameStart = i
                    i++
                    while (!template[i].isWhitespace()) {
                        i++
                    }
                    val nameRef = template.stringRef(paramNameStart, i)
                    getBuffer().add(StringTemplate.Parameter.of(nameRef, paramIndex++))
                    start = i
                    i++
                    continue
                }
                //Find suffix
                val suffixIndex = template.indexOf(suffix,i)
                if (suffixIndex < 0) {
                    throw StringTemplateException("Parameter prefix is not enclose at index: $i.")
                }
                if (suffixIndex == i) {
                    getBuffer().add(StringTemplate.Parameter.empty( paramIndex++))
                    i++
                }
            }
            i++
        }

        if (buffer === null) {
            return listOf(template)
        }
        if (start < this.length) {
            getBuffer().add(this.stringRef(start))
        }

        return getBuffer().joinToString("")
    }
}

open class StringTemplateException @JvmOverloads constructor(
    message: String? = null, cause: Throwable? = null
) : RuntimeException(message, cause)

/**
 * Abstract [StringTemplate].
 */
abstract class AbstractCharsTemplate(
    val template: CharSequence
) : StringTemplate {

    protected abstract val tokens: List<StringTemplate.Token>

    override val nodes: List<StringTemplate.Node> by lazy { resolveNodes(tokens) }

    private fun resolveNodes(tokens: List<StringTemplate.Token>): List<StringTemplate.Node> {
        if (tokens.isEmpty()) {
            return emptyList()
        }
        val nodes = LinkedList<StringTemplate.Node>()
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
                        nodes.add(
                            StringTemplate.Node.newNode(
                                template, tokens.subList(start, i), StringTemplate.Node.Type.TEXT, -1
                            )
                        )
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
                        nodes.add(
                            StringTemplate.Node.newNode(
                                template, tokens.subList(start, i), StringTemplate.Node.Type.PARAMETER, parameterIndex
                            )
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
            nodes.add(
                StringTemplate.Node.newNode(
                    template, tokens.subList(start, i), StringTemplate.Node.Type.TEXT, -1
                )
            )
        }
        return nodes
    }
}

private class WithoutEscape(
    template: CharSequence,
    prefix: CharSequence,
    suffix: CharSequence
) : AbstractCharsTemplate(template) {

    override val tokens: List<StringTemplate.Token> by lazy {
        resolveTokens(template, prefix.toString(), suffix.toString())
    }

    private fun resolveTokens(
        template: CharSequence,
        prefix: String,
        suffix: String
    ): List<StringTemplate.Token> {
        var prefixIndex = template.indexOf(prefix)
        if (prefixIndex < 0) {
            return listOf(StringTemplate.Token.newToken(0, template.length, StringTemplate.Token.Type.TEXT))
        }
        var startIndex = 0
        val tokens = LinkedList<StringTemplate.Token>()
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
                tokens.add(StringTemplate.Token.newToken(startIndex, prefixIndex, StringTemplate.Token.Type.TEXT))
            }
            tokens.add(
                StringTemplate.Token.newToken(
                    prefixIndex,
                    prefixIndex + prefix.length,
                    StringTemplate.Token.Type.PREFIX
                )
            )
            tokens.add(
                StringTemplate.Token.newToken(
                    prefixIndex + prefix.length,
                    suffixIndex,
                    StringTemplate.Token.Type.TEXT
                )
            )
            tokens.add(
                StringTemplate.Token.newToken(
                    suffixIndex,
                    suffixIndex + suffix.length,
                    StringTemplate.Token.Type.SUFFIX
                )
            )
            startIndex = suffixIndex + suffix.length
            prefixIndex = template.indexOf(prefix, startIndex)
        }
        if (startIndex < template.length) {
            tokens.add(StringTemplate.Token.newToken(startIndex, template.length, StringTemplate.Token.Type.TEXT))
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

    override val tokens: List<StringTemplate.Token> by lazy {
        resolveTokens(template, prefix.toString(), suffix.toString(), escape.toString())
    }

    private fun resolveTokens(
        template: CharSequence,
        prefix: String,
        suffix: String,
        escape: String
    ): List<StringTemplate.Token> {
        val tokens = LinkedList<StringTemplate.Token>()
        var startIndex = 0
        var i = 0
        var inParameterScope = false
        while (i < template.length) {
            if (template.startsWith(escape, i)) {
                val nextIndex = i + escape.length
                if (i > startIndex) {
                    tokens.add(StringTemplate.Token.newToken(startIndex, i, StringTemplate.Token.Type.TEXT))
                }
                tokens.add(StringTemplate.Token.newToken(i, nextIndex, StringTemplate.Token.Type.ESCAPE))
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
                    tokens.add(StringTemplate.Token.newToken(startIndex, i, StringTemplate.Token.Type.TEXT))
                }
                tokens.add(StringTemplate.Token.newToken(i, i + prefix.length, StringTemplate.Token.Type.PREFIX))
                inParameterScope = true
                i += prefix.length
                startIndex = i
                continue
            }
            if (template.startsWith(suffix, i) && inParameterScope) {
                if (i > startIndex) {
                    tokens.add(StringTemplate.Token.newToken(startIndex, i, StringTemplate.Token.Type.TEXT))
                }
                tokens.add(StringTemplate.Token.newToken(i, i + suffix.length, StringTemplate.Token.Type.SUFFIX))
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
            tokens.add(StringTemplate.Token.newToken(startIndex, template.length, StringTemplate.Token.Type.TEXT))
        }
        return tokens
    }
}