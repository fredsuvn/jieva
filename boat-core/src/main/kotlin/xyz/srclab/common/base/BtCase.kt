/**
 * Naming case utilities.
 */
@file:JvmName("BtCase")

package xyz.srclab.common.base

import xyz.srclab.annotations.concurrent.ThreadSafe
import xyz.srclab.common.base.CamelCase.NonLetterPolicy
import java.util.*

/**
 * Returns [NamingCase] for lower-camel, such as `firstSecond`.
 * @see CamelCase
 */
@JvmName("lowerCamel")
fun lowerCamelCase(): NamingCase = BtCaseHolder.LOWER_CAMEL

/**
 * Returns [NamingCase] for lower-camel, such as `FirstSecond`.
 * @see CamelCase
 */
@JvmName("upperCamel")
fun upperCamelCase(): NamingCase = BtCaseHolder.UPPER_CAMEL

/**
 * Returns [NamingCase] for lower-camel, such as `first_second`.
 * @see SeparatorCase
 */
@JvmName("lowerUnderscore")
fun lowerUnderscoreCase(): NamingCase = BtCaseHolder.LOWER_UNDERSCORE

/**
 * Returns [NamingCase] for lower-camel, such as `FIRST_SECOND`.
 * @see SeparatorCase
 */
@JvmName("upperUnderscore")
fun upperUnderscoreCase(): NamingCase = BtCaseHolder.UPPER_UNDERSCORE

/**
 * Returns [NamingCase] for lower-camel, such as `first-second`.
 * @see SeparatorCase
 */
@JvmName("lowerHyphen")
fun lowerHyphenCase(): NamingCase = BtCaseHolder.LOWER_HYPHEN

/**
 * Returns [NamingCase] for lower-camel, such as `FIRST-SECOND`.
 * @see SeparatorCase
 */
@JvmName("upperHyphen")
fun upperHyphenCase(): NamingCase = BtCaseHolder.UPPER_HYPHEN

/**
 * Converts case of [this] chars from [from] case to [to] case. For example:
 *
 * ```
 * //first-second -> FirstSecond
 * BtCase.toCase("first-second", BtCase.lowerHyphen(), BtCase.upperCamel();
 * ```
 */
fun CharSequence.toCase(from: NamingCase, to: NamingCase): String {
    return from.convert(this, to)
}

/**
 * Naming case. Used to convert different naming case style.
 * For example, to make `first-second` to `FirstSecond`:
 *
 * ```
 * //first-second -> FirstSecond
 * BtCase.toCase("first-second", BtCase.lowerHyphen(), BtCase.upperCamel();
 * ```
 *
 * @see CamelCase
 * @see SeparatorCase
 */
@ThreadSafe
interface NamingCase {

    /**
     * Splits [name] to word list.
     */
    fun split(name: CharSequence): List<CharSequence>

    /**
     * Joins word list to a [String] in current case.
     */
    fun join(words: List<CharSequence>): String {
        val builder = StringBuilder()
        joinTo(builder, words)
        return builder.toString()
    }

    /**
     * Joins and appends word list into [dest] in current case.
     */
    fun joinTo(dest: Appendable, words: List<CharSequence>)

    /**
     * Converts [name] to [target] style.
     */
    fun convert(name: CharSequence, target: NamingCase): String {
        val words = split(name)
        return target.join(words)
    }
}

/**
 * Camel-Case implementation of [NamingCase], such as `firstSecond`, `FirstSecond`.
 *
 * When join the words in [join] or [joinTo],
 * this case will capitalize or uncapitalize first word by [capitalized] option.
 * Then, the word will be processed by [wordHandler].
 *
 * Non-letter char will be seen as lower, upper or independent case by [nonLetterPolicy],
 * default non-letter policy is [NonLetterPolicy.AS_LOWER].
 */
open class CamelCase @JvmOverloads constructor(
    private val capitalized: Boolean,
    private val nonLetterPolicy: NonLetterPolicy = NonLetterPolicy.AS_LOWER,
    private val wordHandler: IndexedFunction<in CharSequence, out CharSequence> = IndexedFunction { _, it -> it }
) : NamingCase {

    /**
     * Constructs with [capitalized] and [wordHandler], use [NonLetterPolicy.AS_LOWER].
     */
    constructor(
        capitalized: Boolean, wordHandler: IndexedFunction<in CharSequence, out CharSequence>
    ) : this(capitalized, NonLetterPolicy.AS_LOWER, wordHandler)

    override fun split(name: CharSequence): List<CharSequence> {

        val length = name.length
        if (length <= 1) {
            return Collections.singletonList(name)
        }

        val appender = ListAppender<CharSequence>()

        var startIndex = 0
        var lastCharCase = name[0].case()
        var i = 1
        while (i < name.length) {

            val cCase = name[i].case()

            if (lastCharCase == LOWER) {
                if (cCase == LOWER) {
                    //aa -> aa
                    //join the former word
                } else if (cCase == UPPER) {
                    //aA -> a,A
                    appender.append(name.subRef(startIndex, i))
                    startIndex = i
                } else if (nonLetterPolicy === NonLetterPolicy.INDEPENDENT) {
                    //a0 -> a,0
                    //split to new word for non-letter chars
                    appender.append(name.subRef(startIndex, i))
                    startIndex = i
                } else {
                    throw IllegalArgumentException("Unknown char: ${name[i]}")
                }
            } else if (lastCharCase == UPPER) {
                if (cCase == LOWER) {
                    if (startIndex < i - 1) {
                        //AAa -> A,Aa
                        //AAAa -> AA,Aa
                        appender.append(name.subRef(startIndex, i - 1))
                        startIndex = i - 1
                    } else {
                        //Aa -> Aa
                        //join the former word
                    }
                } else if (cCase == UPPER) {
                    //AA -> AA
                    //join the former word
                    if (startIndex < i - 1) {
                    }
                } else if (nonLetterPolicy === NonLetterPolicy.INDEPENDENT) {
                    //A0 -> A,0
                    //split to new word for non-letter chars
                    appender.append(name.subRef(startIndex, i))
                    startIndex = i
                } else {
                    throw IllegalArgumentException("Unknown char: ${name[i]}")
                }
            } else {
                if (nonLetterPolicy === NonLetterPolicy.INDEPENDENT) {
                    if (cCase == LOWER) {
                        //0a -> 0,a
                        //split to new word for non-letter chars
                        appender.append(name.subRef(startIndex, i))
                        startIndex = i
                    } else if (cCase == UPPER) {
                        //0A -> 0,A
                        //split to new word for non-letter chars
                        appender.append(name.subRef(startIndex, i))
                        startIndex = i
                    } else {
                        //00 -> 00
                        //join the former word
                    }
                } else {
                    throw IllegalArgumentException("Unknown char: ${name[i]}")
                }
            }
            lastCharCase = cCase
            i++
        }
        if (appender.isEmpty()) {
            return Collections.singletonList(name)
        }
        if (startIndex < name.length) {
            appender.append(name.subRef(startIndex))
        }
        return appender.toListAsNoNull()
    }

    override fun join(words: List<CharSequence>): String {
        var count = 0
        for (word in words) {
            count += word.length
        }
        val builder = StringBuilder(count)
        joinTo(builder, words)
        return builder.toString()
    }

    override fun joinTo(dest: Appendable, words: List<CharSequence>) {
        for ((i, word) in words.withIndex()) {
            if (i == 0) {
                dest.append(word.doFirstWord())
            } else {
                dest.append(word.doWord(i))
            }
        }
    }

    private fun CharSequence.doFirstWord(): CharSequence {
        val chars = if (capitalized) {
            this.capitalize()
        } else {
            if (this.length > 1 && this[1].case() == UPPER) {
                this
            } else {
                this.uncapitalize()
            }
        }
        return wordHandler.apply(0, chars)
    }

    private fun CharSequence.doWord(index: Int): CharSequence {
        return wordHandler.apply(index, this)
    }

    private fun Char.case(): Char {

        fun Char.isLower(): Boolean {
            return this in 'a'..'z'
        }

        fun Char.isUpper(): Boolean {
            return this in 'A'..'Z'
        }

        if (this.isLower()) {
            return LOWER
        }
        if (this.isUpper()) {
            return UPPER
        }
        if (nonLetterPolicy === NonLetterPolicy.AS_LOWER) {
            return LOWER
        }
        if (nonLetterPolicy === NonLetterPolicy.AS_UPPER) {
            return UPPER
        }
        return NON_LETTER
    }

    /**
     * Camel-Case policy for non-letter chars.
     */
    enum class NonLetterPolicy {

        /**
         * Non-Letter chars will be seen as lower case.
         */
        AS_LOWER,

        /**
         * Non-Letter chars will be seen as upper case.
         */
        AS_UPPER,

        /**
         * Non-letter chars will be seen as independent case besides `lower` and `upper`.
         */
        INDEPENDENT,
        ;
    }

    companion object {
        private const val LOWER = 'l'
        private const val UPPER = 'u'
        private const val NON_LETTER = 'n'
    }
}

/**
 * Separator-Case implementation of [NamingCase], such as `first-second`, `FIRST_SECOND`.
 * The separator is specified by [separator],
 * and each word will be processed by [wordHandler] in join operation.
 */
open class SeparatorCase(
    private val separator: CharSequence,
    private val wordHandler: IndexedFunction<in CharSequence, out CharSequence> = IndexedFunction { _, it -> it }
) : NamingCase {

    override fun split(name: CharSequence): List<CharSequence> {

        val length = name.length
        if (length <= 1) {
            return Collections.singletonList(name)
        }

        val separatorString = separator.toString()
        var splitIndex = name.indexOf(separatorString)
        if (splitIndex < 0) {
            return Collections.singletonList(name)
        }

        val appender = ListAppender<CharSequence>()
        appender.append(name.subRef(0, splitIndex))
        var startIndex = splitIndex + separatorString.length

        while (startIndex < name.length) {
            splitIndex = name.indexOf(separatorString, startIndex)
            if (splitIndex < 0) {
                appender.append(name.subRef(startIndex))
                break
            }
            appender.append(name.subRef(startIndex, splitIndex))
            startIndex = splitIndex + separatorString.length
        }
        //Last word is empty
        if (startIndex == name.length) {
            appender.append("")
        }
        return appender.toListAsNoNull()
    }

    override fun join(words: List<CharSequence>): String {
        var count = 0
        for (word in words) {
            count += word.length
        }
        val builder = StringBuilder(count + (words.size - 1) * separator.length)
        joinTo(builder, words)
        return builder.toString()
    }

    override fun joinTo(dest: Appendable, words: List<CharSequence>) {
        val separator = this.separator.toString()
        for ((i, word) in words.withIndex()) {
            dest.append(wordHandler.apply(i, word))
            if (i < words.size - 1) {
                dest.append(separator)
            }
        }
    }
}

private object BtCaseHolder {
    val LOWER_CAMEL: NamingCase = CamelCase(false)
    val UPPER_CAMEL: NamingCase = CamelCase(true)
    val LOWER_UNDERSCORE: NamingCase = SeparatorCase("_") { _, it -> it.lowerCase() }
    val UPPER_UNDERSCORE: NamingCase = SeparatorCase("_") { _, it -> it.upperCase() }
    val LOWER_HYPHEN: NamingCase = SeparatorCase("-") { _, it -> it.lowerCase() }
    val UPPER_HYPHEN: NamingCase = SeparatorCase("-") { _, it -> it.upperCase() }
}