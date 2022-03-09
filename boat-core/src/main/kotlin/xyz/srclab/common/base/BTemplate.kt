@file:JvmName("BTemplate")

package xyz.srclab.common.base

import xyz.srclab.annotations.Accepted
import java.io.Serializable
import java.util.*

/**
 * Parses [this] to [CharsTemplate] implemented by [SimpleTemplate].
 *
 * @see CharsTemplate
 * @see SimpleTemplate
 */
@JvmName("parse")
@JvmOverloads
fun CharSequence.parseTemplate(
    escapeChar: Char,
    parameterPrefix: CharSequence,
    parameterSuffix: CharSequence? = null
): CharsTemplate {
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
interface CharsTemplate {

    /**
     * Parsed nodes of this template. There are two type of node:
     *
     * * Chars node: general node represents a sequence of chars;
     * * [Parameter] node: represents a parameter;
     */
    @get:Throws(TemplateException::class)
    val nodes: List<CharSequence>

    /**
     * Process with [args].
     */
    @Throws(TemplateException::class)
    fun process(vararg args:Any?): String {
        val nodesArray = nodes.toTypedArray()
        var i = 0
        var p = 0
        while (i < nodesArray.size) {
            val node = nodesArray[i]
            if (node is Parameter) {
                nodesArray[i] = args[p].toCharSeq()
            }
            i++
            p++
        }
        return nodesArray.joinToString("")
    }

    /**
     * Process with [args].
     */
    @Throws(TemplateException::class)
    fun processMap(args: Map<String, Any?>): String {
        val nodesArray = nodes.toTypedArray()
        var i = 0
        while (i < nodesArray.size) {
            val node = nodesArray[i]
            if (node is Parameter) {
                nodesArray[i] = args[node.toString()].toCharSeq()
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
     * Represents parameter node.
     */
    interface Parameter : CharSequence {

        /**
         * Index of current parameter.
         */
        val index: Int

        companion object {

            /**
             * Returns a [Parameter] with [chars] and [index].
             */
            @JvmStatic
            fun of(chars: CharSequence, index: Int): Parameter {
                return ParameterImpl(chars, index)
            }

            /**
             * Returns a [Parameter] with empty chars and [index].
             */
            @JvmStatic
            fun of(index: Int): Parameter {
                return of("", index)
            }

            private class ParameterImpl(
                private val chars: CharSequence, override val index: Int
            ) : Parameter, FinalObject() {

                override val length: Int = chars.length

                override fun get(index: Int): Char {
                    return chars[index]
                }

                override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
                    return chars.subSequence(startIndex, endIndex)
                }

                override fun hashCode0(): Int {
                    return toString().hashCode()
                }

                override fun toString0(): String {
                    return chars.toString()
                }
            }
        }
    }
}

/**
 * Simple [CharsTemplate] implementation.
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
) : CharsTemplate {

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
                    getBuffer().add(CharsTemplate.Parameter.of(nameRef, paramIndex++))
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
                    getBuffer().add(CharsTemplate.Parameter.of(paramIndex++))
                    i++
                    start = i
                    continue
                }
                val nameRef = template.subRef(i, suffixIndex)
                getBuffer().add(CharsTemplate.Parameter.of(nameRef, paramIndex++))
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

/**
 * Template exception.
 */
open class TemplateException @JvmOverloads constructor(
    message: String? = null, cause: Throwable? = null
) : RuntimeException(message, cause), Serializable {
    companion object {
        private val serialVersionUID: Long = defaultSerialVersion()
    }
}