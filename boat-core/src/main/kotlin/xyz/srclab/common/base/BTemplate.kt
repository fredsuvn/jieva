@file:JvmName("BTemplate")

package xyz.srclab.common.base

import xyz.srclab.annotations.Accepted
import java.io.Serializable
import java.util.*

/**
 * Resolves receiver string to [StringTemplate] implemented by [SimpleTemplate].
 *
 * @see StringTemplate
 * @see SimpleTemplate
 */
@JvmName("parse")
@JvmOverloads
fun CharSequence.parseTemplate(
    escapeChar: Char,
    parameterPrefix: CharSequence,
    parameterSuffix: CharSequence? = null
): StringTemplate {
    return SimpleTemplate(this, escapeChar, parameterPrefix, parameterSuffix)
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
    @get:Throws(TemplateException::class)
    val nodes: List<CharSequence>

    /**
     * Process with [args].
     */
    @Throws(TemplateException::class)
    fun process(args: Map<@Accepted(String::class, Integer::class) Any, Any?>): String {
        val nodesArray = nodes.toTypedArray()
        var i = 0
        while (i < nodesArray.size) {
            val node = nodesArray[i]
            if (node is Parameter) {
                nodesArray[i] = args[node].toCharSeq()
            }
            i++
        }
        return nodesArray.joinToString("")
    }

    /**
     * Processes with [args].
     */
    @Throws(TemplateException::class)
    fun processTo(dest: Appendable, args: Map<@Accepted(String::class, Integer::class) Any, Any?>) {
        for (node in nodes) {
            if (node is Parameter) {
                dest.append(args[node].toCharSeq())
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
            fun of(chars: CharSequence, index: Int): Parameter {
                return StringParameter(chars, index)
            }

            @JvmStatic
            fun of(index: Int): Parameter {
                return IndexParameter(index)
            }

            private class StringParameter(
                chars: CharSequence, override val index: Int
            ) : Parameter {

                private val value = chars.toString()
                override val length: Int = value.length

                override fun get(index: Int): Char {
                    return value[index]
                }

                override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
                    return value.subSequence(startIndex, endIndex)
                }

                override fun equals(other: Any?): Boolean {
                    if (this === other) return true
                    if (other !is CharSequence) return false
                    return value == other
                }

                override fun hashCode(): Int {
                    return value.hashCode()
                }

                override fun toString(): String {
                    return value
                }
            }

            private class IndexParameter(
                override val index: Int
            ) : Parameter {

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

                override fun equals(other: Any?): Boolean {
                    if (this === other) return true
                    if (other !is Int) return false
                    return index == other
                }

                override fun hashCode(): Int {
                    return index.hashCode()
                }

                override fun toString(): String {
                    return index.toString()
                }
            }
        }
    }
}

/**
 * Simple [StringTemplate] implementation.
 * It supports an [escapeChar], a parameter prefix and a parameter suffix (may null). For example:
 *
 * ```
 * Map<Object, Object> args = BMaps.newMap("name", "Zero")
 * SimpleTemplate st = new SimpleTemplate(
 *     "Ultraman ${name} is strongest!",
 *     '\\',
 *     "${",
 *     "}"
 * );
 * //"Ultraman Zero is strongest!"
 * st.process(args)
 * ```
 *
 * Parameter suffix may be null, in this case [SimpleTemplate] use [Character.isWhitespace] to split a parameter:
 *
 * ```
 * SimpleTemplate st = new SimpleTemplate(
 *     "Ultraman $name is strongest!",
 *     '\\',
 *     "$"
 * );
 * //"Ultraman Zero is strongest!"
 * st.process(args)
 * ```
 *
 * Parameter name may be empty if suffix is not null, in this case use index instead of name:
 *
 * ```
 * Map<Object, Object> args = BMaps.newMap(0, "Zero")
 * SimpleTemplate st = new SimpleTemplate(
 *     "Ultraman ${} is strongest!",
 *     '\\',
 *     "${",
 *     "}"
 * );
 * //"Ultraman Zero is strongest!"
 * st.process(args)
 * ```
 *
 * Escape char is valid in following cases:
 *
 * * Escape char followed by parameter prefix;
 * * Escape char followed by escape char;
 * * If a parameter prefix is found, this template will find next delimiter (suffix or whitespace)
 * and escape char is ignored;
 * * Otherwise no escape;
 *
 * For example:
 *
 * ```
 * Map<Object, Object> args = BMaps.newMap("name", "Taiga", "${name", "Zero")
 * //Note java string `\\` means `\`
 * String template = "Ultraman \\${} \\\\ } ${${name} is strongest rather than ${name}!";
 *
 * ```
 *
 * will output:
 *
 * ```
 * Ultraman ${} \ } Zero is strongest rather than Taiga!
 * ```
 */
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
                if (i >= template.length) {
                    break
                }
                val cn = template[i]
                if (cn == escapeChar) {
                    //Escape itself
                    getBuffer().add(template.subRef(start, i))
                    i++
                    start = i
                    continue
                }
                if (isParameterPrefix(i)) {
                    //Escape parameter prefix
                    getBuffer().add(template.subRef(start, i - 1))
                    getBuffer().add(prefix)
                    i += prefix.length
                    start = i
                    continue
                }
            }
            if (isParameterPrefix(i)) {
                getBuffer().add(template.subRef(start, i))
                i += prefix.length
                if (suffix === null) {
                    //Find next whitespace
                    if (template[i].isWhitespace()) {
                        throw TemplateException("Parameter name cannot be whitespace.")
                    }
                    val paramNameStart = i
                    i++
                    while (!template[i].isWhitespace()) {
                        i++
                    }
                    val nameRef = template.subRef(paramNameStart, i)
                    getBuffer().add(StringTemplate.Parameter.of(nameRef, paramIndex++))
                    start = i
                    i++
                    continue
                }
                //Find suffix
                val suffixIndex = template.indexOf(suffix, i)
                if (suffixIndex < 0) {
                    throw TemplateException("Parameter prefix is not enclose at index: $i.")
                }
                if (suffixIndex == i) {
                    getBuffer().add(StringTemplate.Parameter.of(paramIndex++))
                    i++
                    start = i
                    continue
                }
                val nameRef = template.subRef(i, suffixIndex)
                getBuffer().add(StringTemplate.Parameter.of(nameRef, paramIndex++))
                i = suffixIndex + 1
                start = i
                continue
            }
            i++
        }

        if (buffer === null) {
            return listOf(template)
        }
        if (start < template.length) {
            getBuffer().add(template.subRef(start))
        }
        return getBuffer()
    }
}

open class TemplateException @JvmOverloads constructor(
    message: String? = null, cause: Throwable? = null
) : RuntimeException(message, cause), Serializable {
    companion object {
        private val serialVersionUID: Long = defaultSerialVersion()
    }
}