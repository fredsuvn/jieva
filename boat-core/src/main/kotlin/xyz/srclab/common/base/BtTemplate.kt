/**
 * Template Utilities.
 */
@file:JvmName("BtTemplate")

package xyz.srclab.common.base

import xyz.srclab.annotations.concurrent.ThreadSafe
import xyz.srclab.common.toChars
import java.io.Serializable

/**
 * Parses [this] to [StringTemplate] implemented by [SimpleTemplate].
 *
 * @receiver this source text to be template
 * @param escapeChar escape char, may be null if no escape
 * @param parameterPrefix parameter prefix
 * @param parameterSuffix parameter suffix, or null if no suffix
 *
 * @see StringTemplate
 * @see SimpleTemplate
 */
@JvmName("parse")
@Throws(TemplateException::class)
@JvmOverloads
fun CharSequence.parseTemplate(
    escapeChar: Char?,
    parameterPrefix: CharSequence,
    parameterSuffix: CharSequence? = null
): StringTemplate {
    return SimpleTemplate(this, escapeChar, parameterPrefix, parameterSuffix)
}

/**
 * Chars template, to process on a parameterized char sequence.
 * Default implementation is [SimpleTemplate], for example:
 *
 * ```
 * Map<String, Object> args = new HashMap<>();
 * args.put("n1", "Dog");
 * args.put("n2}", "Cat");
 * StringTemplate template1 = BtTemplate.parse("This is a {n1}, that is a {n2}", null, "{", "}");
 * Assert.assertEquals(template1.process(args), "This is a Dog, that is a Cat");
 * ```
 *
 * @see SimpleTemplate
 */
@ThreadSafe
interface StringTemplate {

    /**
     * Parsed nodes of this template. There are two type of node:
     *
     * * [CharSequence]: general node represents a sequence of chars;
     * * [Parameter] node: represents a parameter;
     */
    @get:Throws(TemplateException::class)
    val nodes: List<CharSequence>

    /**
     * Processes on this template with named [args].
     */
    @Throws(TemplateException::class)
    fun process(args: Map<String, Any?>): String {
        val nodesArray = nodes.toTypedArray()
        var i = 0
        while (i < nodesArray.size) {
            val node = nodesArray[i]
            if (node is Parameter) {
                nodesArray[i] = args[node.toString()].toChars()
            }
            i++
        }
        return nodesArray.joinToString("")
    }

    /**
     * Processes on this template with unnamed [args].
     */
    @Throws(TemplateException::class)
    fun processArgs(vararg args: Any?): String {
        val nodesArray = nodes.toTypedArray()
        var i = 0
        var p = 0
        while (i < nodesArray.size) {
            val node = nodesArray[i]
            if (node is Parameter) {
                nodesArray[i] = args[p++].toChars()
            }
            i++
        }
        return nodesArray.joinToString("")
    }

    /**
     * Processes on this template with named [args] to [dest].
     */
    @Throws(TemplateException::class)
    fun <T : Appendable> processTo(dest: T, args: Map<String, Any?>): T {
        for (node in nodes) {
            if (node is Parameter) {
                dest.append(args[node.toString()].toChars())
            } else {
                dest.append(node)
            }
        }
        return dest
    }

    /**
     * Processes on this template with unnamed [args] to [dest].
     */
    @Throws(TemplateException::class)
    fun <T : Appendable> processArgsTo(dest: T, vararg args: Any?): T {
        var p = 0
        for (node in nodes) {
            if (node is Parameter) {
                dest.append(args[p++].toChars())
            } else {
                dest.append(node)
            }
        }
        return dest
    }

    /**
     * Represents parameter node.
     */
    interface Parameter : CharSequence {

        /**
         * Index of current parameter.
         */
        val index: Int

        companion object {

            /**
             * Returns a [Parameter] with [index] and [content].
             */
            @JvmStatic
            fun of(index: Int, content: CharSequence = ""): Parameter {
                return ParameterImpl(index, content)
            }

            private class ParameterImpl(
                override val index: Int,
                private val content: CharSequence,
            ) : Parameter, FinalClass() {

                override val length: Int = content.length

                override fun get(index: Int): Char {
                    return content[index]
                }

                override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
                    return content.subSequence(startIndex, endIndex)
                }

                override fun hashCode0(): Int {
                    return toString().hashCode()
                }

                override fun toString0(): String {
                    return content.toString()
                }
            }
        }
    }
}

/**
 * Default and simple implementation of [StringTemplate],
 * supporting escape, parameter prefix and parameter suffix (may null if needn't).
 *
 * ```
 * //Using named ars:
 * Map<String, Object> args = new HashMap<>();
 * args.put("n1", "Dog");
 * args.put("n2}", "Cat");
 * StringTemplate template1 = BtTemplate.parse("This is a {n1}, that is a {n2}", null, "{", "}");
 * Assert.assertEquals(template1.process(args), "This is a Dog, that is a Cat");
 *
 * //Using unnamed args:
 * StringTemplate template2 = BtTemplate.parse("This is a {}, that is a {}", null, "{", "}");
 * Assert.assertEquals(template2.processArgs("Dog", "Cat"), "This is a Dog, that is a Cat");
 *
 * //Using only prefix:
 * StringTemplate template3 = BtTemplate.parse("This is a $n1, that is a $n2", null, "$");
 * Assert.assertEquals(template3.process(args), "This is a Dog, that is a Cat");
 * StringTemplate template4 = BtTemplate.parse("This is a $, that is a $", null, "$");
 * Assert.assertEquals(template4.processArgs("Dog", "Cat"), "This is a Dog, that is a Cat");
 * ```
 *
 * It supports escape:
 *
 * ```
 * StringTemplate template5 = BtTemplate.parse("This \\is a \\{{n1}, that is a {n2}\\", '\\', "{", "}");
 * Assert.assertEquals(template5.process(args), "This \\is a {Dog, that is a Cat\\");
 * ```
 *
 * Note:
 *
 * * Escape is valid only before the parameter prefix or escape itself;
 * * Parameter suffix before the prefix is permitted, the suffix will be seen as a normal char in this case;
 * * If there is no parameter suffix, the parameter name will end at next white char with [Char.isWhitespace];
 *
 * @param source source text to be template
 * @param escapeChar escape char, may be null if no escape
 * @param parameterPrefix parameter prefix
 * @param parameterSuffix parameter suffix, or null if no suffix
 */
open class SimpleTemplate(
    private val source: CharSequence,
    private val escapeChar: Char?,
    parameterPrefix: CharSequence,
    parameterSuffix: CharSequence? = null
) : StringTemplate {

    override val nodes: List<CharSequence> by lazy { parse0() }

    init {
        checkState(parameterPrefix.isNotEmpty(), "Parameter prefix cannot empty.")
        checkState(
            parameterSuffix === null || parameterSuffix.isNotEmpty(),
            "Parameter suffix must be null or non-empty."
        )
    }

    private val prefix = parameterPrefix.toString()
    private val suffix = parameterSuffix?.toString()

    private fun parse0(): List<CharSequence> {

        if (source.isEmpty()) {
            return listOf(source)
        }

        var buffer: ListBuilder<CharSequence>? = null
        fun getBuffer(): ListBuilder<CharSequence> {
            val bf = buffer
            if (bf === null) {
                val newBuffer = ListBuilder<CharSequence>()
                buffer = newBuffer
                return newBuffer
            }
            return bf
        }

        fun isParameterPrefix(index: Int): Boolean {
            return source.startsWith(prefix, index)
        }

        var start = 0
        var i = 0
        var paramIndex = 0
        while (i < source.length) {
            val c = source[i]
            if (escapeChar !== null && c == escapeChar) {
                i++
                if (i >= source.length) {
                    break
                }
                val cn = source[i]
                if (cn == escapeChar) {
                    //Escape itself
                    getBuffer().append(source.subRef(start, i))
                    i++
                    start = i
                    continue
                }
                if (isParameterPrefix(i)) {
                    //Escape parameter prefix
                    getBuffer().append(source.subRef(start, i - 1))
                    getBuffer().append(prefix)
                    i += prefix.length
                    start = i
                    continue
                }
            }
            if (isParameterPrefix(i)) {
                getBuffer().append(source.subRef(start, i))
                i += prefix.length
                if (suffix === null) {
                    //Case no suffix: find next whitespace
                    val paramNameStart = i
                    while (i < source.length && !source[i].isWhitespace()) {
                        i++
                    }
                    if (paramNameStart == i) {
                        //empty name
                        getBuffer().append(StringTemplate.Parameter.of(paramIndex++))
                    } else {
                        val nameRef = source.subRef(paramNameStart, i)
                        getBuffer().append(StringTemplate.Parameter.of(paramIndex++, nameRef))
                    }
                    start = i
                    i++
                    continue
                }
                //Find suffix
                val suffixIndex = source.indexOf(suffix, i)
                if (suffixIndex < 0) {
                    throw TemplateException("Parameter prefix is not enclose at index: $i.")
                }
                if (suffixIndex == i) {
                    //empty name
                    getBuffer().append(StringTemplate.Parameter.of(paramIndex++))
                    i++
                    start = i
                    continue
                }
                val nameRef = source.subRef(i, suffixIndex)
                getBuffer().append(StringTemplate.Parameter.of(paramIndex++, nameRef))
                i = suffixIndex + 1
                start = i
                continue
            }
            i++
        }

        if (buffer === null) {
            return listOf(source)
        }
        if (start < source.length) {
            getBuffer().append(source.subRef(start))
        }
        return getBuffer().toList()
    }
}

/**
 * Template exception.
 */
open class TemplateException @JvmOverloads constructor(
    message: String? = null, cause: Throwable? = null
) : RuntimeException(message, cause), Serializable