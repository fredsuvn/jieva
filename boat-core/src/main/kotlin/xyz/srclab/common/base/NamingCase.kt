package xyz.srclab.common.base

import java.util.*

/**
 * Naming case. Used to convert different naming case style:
 *
 * ```
 * // firstSecond -> FIRST_SECOND
 * NamingCase.LOWER_CAMEL.convertTo("firstSecond", NamingCase.UPPER_UNDERSCORE)
 * ```
 *
 * Note default [NamingCase] implementations only support `[A-Z][a-z][0-9]`.
 *
 * @author sunqian
 *
 * @see LowerCamel
 * @see UpperCamel
 * @see LowerUnderscore
 * @see UpperUnderscore
 * @see LowerHyphen
 * @see UpperHyphen
 */
interface NamingCase {

    /**
     * Validates whether given [name] is in current case style.
     */
    fun validate(name: CharSequence): Boolean

    /**
     * Splits [name] to words which are used for [join].
     */
    @Throws(NamingCaseException::class)
    fun segment(name: CharSequence): List<CharSequence>

    /**
     * Joins [words] to a new name with current case style.
     */
    @Throws(NamingCaseException::class)
    fun join(words: List<CharSequence>): String

    /**
     * Converts [name] to [target] style.
     */
    @Throws(NamingCaseException::class)
    fun convert(name: CharSequence, target: NamingCase): String {
        val words = segment(name)
        return target.join(words)
    }

    companion object {

        @JvmField
        val LOWER_CAMEL: LowerCamel = LowerCamel

        @JvmField
        val UPPER_CAMEL: UpperCamel = UpperCamel

        @JvmField
        val LOWER_UNDERSCORE: LowerUnderscore = LowerUnderscore

        @JvmField
        val UPPER_UNDERSCORE: UpperUnderscore = UpperUnderscore

        @JvmField
        val LOWER_HYPHEN: LowerHyphen = LowerHyphen

        @JvmField
        val UPPER_HYPHEN: UpperHyphen = UpperHyphen
    }
}

/**
 * [NamingCase] with `lowerCamel` style.
 */
object LowerCamel : CamelCase() {
    override fun doFirstChar(first: CharSequence): String {
        return first.uncapitalize()
    }
}

/**
 * [NamingCase] with `UpperCamel` style.
 */
object UpperCamel : CamelCase() {
    override fun doFirstChar(first: CharSequence): String {
        return first.capitalize()
    }
}

/**
 * [NamingCase] with `lower_underscore` style.
 */
object LowerUnderscore : UnderscoreCase() {

    private val LOWER_UNDERSCORE_MATCHER = LOWER_CASE_MATCHER.and(UNDERSCORE_MATCHER)

    override fun validate(name: CharSequence): Boolean {
        return LOWER_UNDERSCORE_MATCHER.matchesAllOf(name)
    }

    override fun doWord(word: CharSequence): String {
        return word.toString().lowercase()
    }
}

/**
 * [NamingCase] with `UPPER_UNDERSCORE` style.
 */
object UpperUnderscore : UnderscoreCase() {

    private val UPPER_UNDERSCORE_MATCHER = UPPER_CASE_MATCHER.and(UNDERSCORE_MATCHER)

    override fun validate(name: CharSequence): Boolean {
        return UPPER_UNDERSCORE_MATCHER.matchesAllOf(name)
    }

    override fun doWord(word: CharSequence): String {
        return word.toString().uppercase()
    }
}

/**
 * [NamingCase] with `lower-hyphen` style.
 */
object LowerHyphen : HyphenCase() {

    private val LOWER_HYPHEN_MATCHER = LOWER_CASE_MATCHER.and(HYPHEN_MATCHER)

    override fun validate(name: CharSequence): Boolean {
        return LOWER_HYPHEN_MATCHER.matchesAllOf(name)
    }

    override fun doWord(word: CharSequence): String {
        return word.toString().lowercase()
    }
}

/**
 * [NamingCase] with `UPPER-HYPHEN` style.
 */
object UpperHyphen : HyphenCase() {

    private val UPPER_HYPHEN_MATCHER = LOWER_CASE_MATCHER.and(HYPHEN_MATCHER)

    override fun validate(name: CharSequence): Boolean {
        return UPPER_HYPHEN_MATCHER.matchesAllOf(name)
    }

    override fun doWord(word: CharSequence): String {
        return word.toString().uppercase()
    }
}

abstract class CamelCase : NamingCase {

    override fun validate(name: CharSequence): Boolean {
        return LETTER_MATCHER.matchesAllOf(name)
    }

    override fun segment(name: CharSequence): List<CharSequence> {

        fun Char.isUpper(): Boolean {
            return this.isUpperCase()
        }

        fun Char.isLower(): Boolean {
            return this.isLowerCase() || (this in '0'..'9')
        }

        val length = name.length
        if (length <= 1) {
            return listOf(name)
        }
        val result = LinkedList<CharSequence>()
        val buffer = StringBuilder()
        var lastLower = true
        for (c in name) {
            if (buffer.isEmpty()) {
                lastLower = c.isLower()
                buffer.append(c)
                continue
            }
            if (lastLower && c.isLower()) {
                buffer.append(c)
                continue
            }
            if (!lastLower && c.isUpper()) {
                buffer.append(c)
                continue
            }
            if (lastLower && c.isUpper()) {
                result.add(buffer.toString())
                buffer.clear()
                buffer.append(c)
                lastLower = false
                continue
            }
            if (!lastLower && c.isLower()) {
                val bufferLength = buffer.length
                if (bufferLength == 1) {
                    buffer.append(c)
                } else {
                    result.add(buffer.substring(0, bufferLength - 1))
                    buffer.delete(0, bufferLength - 1)
                    buffer.append(c)
                }
                lastLower = true
                continue
            }
        }
        if (buffer.isNotEmpty()) {
            result.add(buffer.toString())
        }
        return result
    }

    override fun join(words: List<CharSequence>): String {
        if (words.isEmpty()) {
            throw NamingCaseException("Given joined words list should have at least 1 word.")
        }
        var i = 0
        val buffer = StringBuilder()
        for (word in words) {
            if (i == 0) {
                buffer.append(doFirstChar(word))
                i++
            } else {
                buffer.append(word.capitalize())
            }
        }
        return buffer.toString()
    }

    protected abstract fun doFirstChar(first: CharSequence): CharSequence
}

abstract class UnderscoreCase : NamingCase {

    override fun segment(name: CharSequence): List<CharSequence> {
        return name.split("_")
    }

    override fun join(words: List<CharSequence>): String {
        return words.joinToString("_") { doWord(it.toString()) }
    }

    protected abstract fun doWord(word: CharSequence): CharSequence
}

abstract class HyphenCase : NamingCase {

    override fun segment(name: CharSequence): List<CharSequence> {
        return name.split("-")
    }

    override fun join(words: List<CharSequence>): String {
        return words.joinToString("-") { doWord(it.toString()) }
    }

    protected abstract fun doWord(word: CharSequence): CharSequence
}

open class NamingCaseException @JvmOverloads constructor(
    message: String? = null, cause: Throwable? = null
) : RuntimeException(message, cause)