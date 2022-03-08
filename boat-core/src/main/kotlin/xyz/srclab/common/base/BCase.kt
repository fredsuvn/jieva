@file:JvmName("BCase")

package xyz.srclab.common.base

import xyz.srclab.annotations.concurrent.ThreadSafe
import xyz.srclab.common.base.CamelCase.NonLetterPolicy
import java.util.*
import java.util.function.Function

/**
 * Returns [NamingCase] for lower-camel, such as `firstSecond`.
 * @see CamelCase
 */
@JvmName("lowerCamel")
fun lowerCamelCase(): NamingCase = BCaseHolder.LOWER_CAMEL

/**
 * Returns [NamingCase] for lower-camel, such as `FirstSecond`.
 * @see CamelCase
 */
@JvmName("upperCamel")
fun upperCamelCase(): NamingCase = BCaseHolder.UPPER_CAMEL

/**
 * Returns [NamingCase] for lower-camel, such as `first_second`.
 * @see SeparatorCase
 */
@JvmName("lowerUnderscore")
fun lowerUnderscoreCase(): NamingCase = BCaseHolder.LOWER_UNDERSCORE

/**
 * Returns [NamingCase] for lower-camel, such as `FIRST_SECOND`.
 * @see SeparatorCase
 */
@JvmName("upperUnderscore")
fun upperUnderscoreCase(): NamingCase = BCaseHolder.UPPER_UNDERSCORE

/**
 * Returns [NamingCase] for lower-camel, such as `first-second`.
 * @see SeparatorCase
 */
@JvmName("lowerHyphen")
fun lowerHyphenCase(): NamingCase = BCaseHolder.LOWER_HYPHEN

/**
 * Returns [NamingCase] for lower-camel, such as `FIRST-SECOND`.
 * @see SeparatorCase
 */
@JvmName("upperHyphen")
fun upperHyphenCase(): NamingCase = BCaseHolder.UPPER_HYPHEN

/**
 * Converts case of [this] chars from [from] case to [to] case. For example:
 *
 * ```
 * //first-second -> FirstSecond
 * BCase.toCase("first-second", BCase.lowerHyphen(), BCase.upperCamel();
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
 * BCase.toCase("first-second", BCase.lowerHyphen(), BCase.upperCamel();
 * ```
 *
 * @see CamelCase
 * @see SeparatorCase
 */
@ThreadSafe
interface NamingCase {

    /**
     * Splits [name] to words.
     *
     * Note content for each word of return words is unchanged, these word may be changed in `join` process.
     */
    fun <T : CharSequence> split(name: T): Words<T>

    /**
     * Joins [words] with current case style.
     */
    fun <T : CharSequence> join(words: Words<T>): String {
        val sb = StringBuilder(words.charCount)
        joinTo(sb, words)
        return sb.toString()
    }

    /**
     * Joins [words] with current case style into [dest].
     */
    fun <T : CharSequence> joinTo(dest: Appendable, words: Words<T>)

    /**
     * Converts [name] to [target] style.
     */
    fun convert(name: CharSequence, target: NamingCase): String {
        val words = split(name)
        return target.join(words)
    }

    /**
     * Resolved words from specified name-case.
     */
    interface Words<T : CharSequence> {

        /**
         * Source name.
         */
        val source: T

        /**
         * Words list, or `empty` if the [source] consists of only one word which is name itself.
         */
        val wordList: List<CharSequence>

        /**
         * Char count of words (separator chars were exclusive).
         */
        val charCount: Int

        companion object {

            /**
             * Returns the single word: source itself.
             */
            @JvmStatic
            fun <T : CharSequence> of(source: T): Words<T> {
                return object : Words<T> {
                    override val source: T = source
                    override val wordList: List<CharSequence> = emptyList()
                    override val charCount: Int = source.length
                }
            }

            /**
             * Returns the words built from [source], [splitList] and [charCount].
             */
            @JvmStatic
            fun <T : CharSequence> of(source: T, splitList: List<CharSequence>, charCount: Int): Words<T> {
                return object : Words<T> {
                    override val source: T = source
                    override val wordList: List<CharSequence> = splitList
                    override val charCount: Int = charCount
                }
            }
        }
    }
}

/**
 * Camel-Case implementation of [NamingCase], such as `firstSecond`, `FirstSecond`.
 * Default non-letter policy is [NonLetterPolicy.AS_LOWER].
 *
 * @param wordProcessor in `join` operation, each split word will be processed by this at first
 */
open class CamelCase @JvmOverloads constructor(
    private val capitalized: Boolean,
    private val nonLetterPolicy: NonLetterPolicy = NonLetterPolicy.AS_LOWER,
    private val wordProcessor: Function<CharSequence, CharSequence> = Function { it }
) : NamingCase {

    /**
     * Constructs with [capitalized] and [wordProcessor], use [NonLetterPolicy.AS_LOWER].
     */
    constructor(
        capitalized: Boolean, wordProcessor: Function<CharSequence, CharSequence>
    ) : this(capitalized, NonLetterPolicy.AS_LOWER, wordProcessor)

    override fun <T : CharSequence> split(name: T): NamingCase.Words<T> {

        val length = name.length
        if (length <= 1) {
            return CamelWords(name, emptyList(), name.length)
        }

        var wordList: MutableList<CaseRef>? = null

        fun getWordList(): MutableList<CaseRef> {
            val wl = wordList
            if (wl === null) {
                val newList = LinkedList<CaseRef>()
                wordList = newList
                return newList
            }
            return wl
        }

        var startIndex = 0
        var lastCharCase = name[0].case()
        var i = 1
        var isAllUpper = false
        while (i < name.length) {

            val cCase = name[i].case()

            if (lastCharCase == LOWER) {
                if (cCase == LOWER) {
                    //aa -> aa
                    //join the former word
                } else if (cCase == UPPER) {
                    //aA -> a,A
                    getWordList().add(CaseRef(name.subRef(startIndex, i), false))
                    startIndex = i
                    isAllUpper = false
                } else if (nonLetterPolicy === NonLetterPolicy.SEPARATE) {
                    //a0 -> a,0
                    //split to new word for non-letter chars
                    getWordList().add(CaseRef(name.subRef(startIndex, i), false))
                    startIndex = i
                    isAllUpper = false
                } else {
                    throw IllegalArgumentException("Unknown char: ${name[i]}")
                }
            } else if (lastCharCase == UPPER) {
                if (cCase == LOWER) {
                    if (startIndex < i - 1) {
                        //AAa -> A,Aa
                        //AAAa -> AA,Aa
                        getWordList().add(CaseRef(name.subRef(startIndex, i - 1), isAllUpper))
                        startIndex = i - 1
                    } else {
                        //Aa -> Aa
                        //join the former word
                    }
                    isAllUpper = false
                } else if (cCase == UPPER) {
                    //AA -> AA
                    //join the former word
                    if (startIndex < i - 1) {
                        isAllUpper = true
                    }
                } else if (nonLetterPolicy === NonLetterPolicy.SEPARATE) {
                    //A0 -> A,0
                    //split to new word for non-letter chars
                    getWordList().add(CaseRef(name.subRef(startIndex, i), isAllUpper))
                    startIndex = i
                    isAllUpper = false
                } else {
                    throw IllegalArgumentException("Unknown char: ${name[i]}")
                }
            } else {
                if (nonLetterPolicy === NonLetterPolicy.SEPARATE) {
                    if (cCase == LOWER) {
                        //0a -> 0,a
                        //split to new word for non-letter chars
                        getWordList().add(CaseRef(name.subRef(startIndex, i), false))
                        startIndex = i
                        isAllUpper = false
                    } else if (cCase == UPPER) {
                        //0A -> 0,A
                        //split to new word for non-letter chars
                        getWordList().add(CaseRef(name.subRef(startIndex, i), false))
                        startIndex = i
                        isAllUpper = false
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
        if (wordList === null) {
            return CamelWords(name, emptyList(), name.length)
        }
        if (startIndex < name.length) {
            getWordList().add(CaseRef(name.subRef(startIndex), isAllUpper))
        }
        return CamelWords(name, getWordList(), name.length)
    }

    override fun <T : CharSequence> join(words: NamingCase.Words<T>): String {
        if (words.wordList.isEmpty()) {
            return words.source.doFirstWord().toString()
        }
        val sb = StringBuilder(words.charCount)
        join0(sb, words)
        return sb.toString()
    }

    override fun <T : CharSequence> joinTo(dest: Appendable, words: NamingCase.Words<T>) {
        if (words.wordList.isEmpty()) {
            dest.append(words.source.doFirstWord())
            return
        }
        join0(dest, words)
    }

    private fun <T : CharSequence> join0(dest: Appendable, words: NamingCase.Words<T>) {
        val splitList = words.wordList
        dest.append(splitList[0].doFirstWord())
        var i = 1
        while (i < splitList.size) {
            dest.append(splitList[i].doWord())
            i++
        }
    }

    private fun CharSequence.doFirstWord(): CharSequence {
        val chars = wordProcessor.apply(this)
        return if (capitalized) {
            chars.capitalize()
        } else {
            if (chars.isAllUpper()) chars else chars.uncapitalize()
        }
    }

    private fun CharSequence.doWord(): CharSequence {
        val chars = wordProcessor.apply(this)
        return if (chars.isAllUpper()) chars else chars.capitalize()
    }

    private fun CharSequence.isAllUpper(): Boolean {
        if (this is CaseRef) {
            return this.isAllUpper
        }
        if (this.length <= 1) {
            return false
        }
        return this[0].case() == UPPER && this[1].case() == UPPER
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

    private class CaseRef(
        private val chars: CharSequence,
        val isAllUpper: Boolean
    ) : CharSequence by chars {
        override fun toString(): String = chars.toString()
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
         * Non-letter chars will be seen as separate word.
         */
        SEPARATE,
        ;
    }

    private class CamelWords<T : CharSequence>(
        override val source: T,
        override val wordList: List<CaseRef>,
        override val charCount: Int
    ) : NamingCase.Words<T>

    companion object {
        private const val LOWER = 'l'
        private const val UPPER = 'u'
        private const val NON_LETTER = 'n'
    }
}

/**
 * Separator implementation of [NamingCase], such as `first-second`, `FIRST_SECOND`.
 */
open class SeparatorCase(
    private val separator: CharSequence,
    private val wordHandler: Function<CharSequence, CharSequence>
) : NamingCase {

    override fun <T : CharSequence> split(name: T): NamingCase.Words<T> {

        if (name.isEmpty()) {
            return NamingCase.Words.of(name)
        }

        val separatorString = separator.toString()
        var index = name.indexOf(separatorString)
        if (index < 0) {
            return NamingCase.Words.of(name)
        }

        var splitList: MutableList<CharSequence>? = null
        var splitCharCount = 0

        fun getSplitList(): MutableList<CharSequence> {
            val sl = splitList
            if (sl === null) {
                val newList = LinkedList<CharSequence>()
                splitList = newList
                return newList
            }
            return sl
        }

        fun addToDest(startIndex: Int, endIndex: Int) {
            if (endIndex > startIndex) {
                getSplitList().add(name.subRef(startIndex, endIndex))
                splitCharCount += endIndex - startIndex
            }
        }

        addToDest(0, index)

        var startIndex = index + separatorString.length
        while (startIndex < name.length) {
            index = name.indexOf(separatorString, startIndex)
            if (index < 0) {
                addToDest(startIndex, name.length)
                break
            }
            addToDest(startIndex, index)
            startIndex += separatorString.length
        }
        if (splitList === null) {
            return NamingCase.Words.of(name, listOf(""), splitCharCount)
        }
        return NamingCase.Words.of(name, splitList!!, splitCharCount)
    }

    override fun <T : CharSequence> join(words: NamingCase.Words<T>): String {
        if (words.wordList.isEmpty()) {
            return wordHandler.apply(words.source).toString()
        }
        val sb = StringBuilder(words.charCount + separator.length * (words.wordList.size - 1))
        join0(words, sb)
        return sb.toString()
    }

    override fun <T : CharSequence> joinTo(dest: Appendable, words: NamingCase.Words<T>) {
        if (words.wordList.isEmpty()) {
            dest.append(wordHandler.apply(words.source))
            return
        }
        join0(words, dest)
    }

    private fun <T : CharSequence> join0(words: NamingCase.Words<T>, appendable: Appendable) {
        words.wordList.joinTo(appendable, separator) { wordHandler.apply(it) }
    }
}

private object BCaseHolder {
    val LOWER_CAMEL: NamingCase = CamelCase(false)
    val UPPER_CAMEL: NamingCase = CamelCase(true)
    val LOWER_UNDERSCORE: NamingCase = SeparatorCase("_") { it.lowerCase() }
    val UPPER_UNDERSCORE: NamingCase = SeparatorCase("_") { it.upperCase() }
    val LOWER_HYPHEN: NamingCase = SeparatorCase("-") { it.lowerCase() }
    val UPPER_HYPHEN: NamingCase = SeparatorCase("-") { it.upperCase() }
}