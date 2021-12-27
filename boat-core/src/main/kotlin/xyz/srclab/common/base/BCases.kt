@file:JvmName("BCases")

package xyz.srclab.common.base

import xyz.srclab.common.base.StringRef.Companion.stringRef
import java.util.function.Function

@JvmField
val LOWER_CAMEL: NamingCase = CamelCase { it.uncapitalize() }

@JvmField
val UPPER_CAMEL: NamingCase = CamelCase { it.capitalize() }

@JvmField
val LOWER_UNDERSCORE: NamingCase = SeparatorCase("_") { it.lowerCase() }

@JvmField
val UPPER_UNDERSCORE: NamingCase = SeparatorCase("_") { it.upperCase() }

@JvmField
val LOWER_HYPHEN: NamingCase = SeparatorCase("-") { it.lowerCase() }

@JvmField
val UPPER_HYPHEN: NamingCase = SeparatorCase("-") { it.upperCase() }

fun CharSequence.convertCase(from: NamingCase, to: NamingCase): String {
    return from.convert(this, to)
}

/**
 * Naming case. Used to convert different naming case style.
 *
 * For example, to make `firstSecond` to `FIRST_SECOND`:
 *
 * ```
 * BCases.LOWER_CAMEL.convertTo("firstSecond", BCases.UPPER_UNDERSCORE);
 * ```
 *
 * Or
 *
 * ```
 * BCases.convertCase("firstSecond", BCases.LOWER_CAMEL, BCases.UPPER_UNDERSCORE);
 * ```
 *
 * @see CamelCase
 * @see SeparatorCase
 */
interface NamingCase {

    /**
     * Splits [name] by case boundary.
     */
    @Throws(NamingCaseException::class)
    fun split(name: CharSequence): List<CharSequence> {
        return split(name, mutableListOf())
    }

    /**
     * Splits [name] by case boundary into [dest]
     */
    @Throws(NamingCaseException::class)
    fun <C : MutableCollection<in CharSequence>> split(name: CharSequence, dest: C): C

    /**
     * Joins [words] with current case style.
     */
    @Throws(NamingCaseException::class)
    fun join(words: List<CharSequence>): String {
        val sb = StringBuilder()
        join(words, sb)
        return sb.toString()
    }

    /**
     * Joins [words] with current case style into [appendable].
     */
    @Throws(NamingCaseException::class)
    fun join(words: List<CharSequence>, appendable: Appendable)

    /**
     * Converts [name] to [target] style.
     */
    @Throws(NamingCaseException::class)
    fun convert(name: CharSequence, target: NamingCase): String {
        val words = mutableListOf<CharSequence>()
        split(name, words)
        return target.join(words)
    }
}

/**
 * Camel-Case class.
 *
 * @see LowerCamel
 * @see UpperCamel
 */
open class CamelCase(
    private val firstWordProcessor: Function<CharSequence, CharSequence>
) : NamingCase {

    override fun <C : MutableCollection<in CharSequence>> split(name: CharSequence, dest: C): C {

        fun Char.isUpper(): Boolean {
            return this.isUpperCase()
        }

        fun Char.isLower(): Boolean {
            return this.isLowerCase() || (this in '0'..'9')
        }

        val length = name.length
        if (length <= 1) {
            dest.add(name)
            return dest
        }

        var startIndex = 0
        var isLastLower = true
        for (i in name.indices) {
            val c = name[i]
            if (i - startIndex == 0) {
                isLastLower = c.isLower()
                continue
            }
            if (isLastLower && c.isLower()) {
                continue
            }
            if (!isLastLower && c.isUpper()) {
                continue
            }
            if (isLastLower && c.isUpper()) {
                dest.add(name.stringRef(startIndex, i))
                startIndex = i
                isLastLower = false
                continue
            }
            if (!isLastLower && c.isLower()) {
                //"Ab" or "AAb":
                //"Ab" is one word;
                //"AAb" are two words: "A" and "Ab";
                if (i - startIndex == 1) {
                    //Case "Ab"
                } else {
                    //Case "AAb"
                    dest.add(name.stringRef(startIndex, i - 1))
                    startIndex = i - 1
                }
                isLastLower = true
                continue
            }
        }
        if (startIndex < name.length) {
            dest.add(name.stringRef(startIndex))
        }
        return dest
    }

    override fun join(words: List<CharSequence>, appendable: Appendable) {
        if (words.isEmpty()) {
            throw NamingCaseException("Must have at least 1 word.")
        }
        appendable.append(firstWordProcessor.apply(words[0]))
        if (words.size == 1) {
            return
        }
        var i = 1
        while (i < words.size) {
            appendable.append(words[i].capitalize())
            i++
        }
    }
}

/**
 * [NamingCase] splits and join by specified separator.
 */
open class SeparatorCase @JvmOverloads constructor(
    private val separator: CharSequence,
    private val wordProcessor: Function<CharSequence, CharSequence> = Function { it }
) : NamingCase {

    override fun <C : MutableCollection<in CharSequence>> split(name: CharSequence, dest: C): C {

        fun addToDest(startIndex: Int, endIndex: Int) {
            if (endIndex > startIndex) {
                dest.add(name.stringRef(startIndex, endIndex))
            }
        }

        val separatorString = separator.toString()
        var index = name.indexOf(separatorString)
        if (index < 0) {
            dest.add(name)
            return dest
        }

        if (index > 0) {
            addToDest(0, index)
        }

        var startIndex = index + separatorString.length
        while (startIndex < name.length) {
            index = name.indexOf(separatorString, startIndex)
            if (index > 0) {
                addToDest(startIndex, index)
            } else {
                addToDest(startIndex, name.length)
                break
            }
            startIndex += separatorString.length
        }

        return dest
    }

    override fun join(words: List<CharSequence>, appendable: Appendable) {
        if (words.isEmpty()) {
            throw NamingCaseException("Must have at least 1 word.")
        }
        appendable.append(wordProcessor.apply(words[0]))
        if (words.size == 1) {
            return
        }
        var i = 1
        while (i < words.size) {
            appendable.append(wordProcessor.apply(words[i]))
            i++
        }
    }
}

open class NamingCaseException @JvmOverloads constructor(
    message: String? = null, cause: Throwable? = null
) : RuntimeException(message, cause)