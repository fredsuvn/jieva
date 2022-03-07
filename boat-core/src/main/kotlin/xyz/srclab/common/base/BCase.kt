@file:JvmName("BCase")

package xyz.srclab.common.base

import xyz.srclab.annotations.concurrent.ThreadSafe
import xyz.srclab.common.base.CharsRef.Companion.charsRef
import java.util.*
import java.util.function.Function

@JvmField
val LOWER_CAMEL: NamingCase = lowerCamelCase(CamelCase.NonLetterPolicy.FOLLOW_START_LOWER)

@JvmField
val UPPER_CAMEL: NamingCase = upperCamelCase(CamelCase.NonLetterPolicy.FOLLOW_START_LOWER)

@JvmField
val LOWER_UNDERSCORE: NamingCase = separatorCase("_") { it.lowerCase() }

@JvmField
val UPPER_UNDERSCORE: NamingCase = separatorCase("_") { it.upperCase() }

@JvmField
val LOWER_HYPHEN: NamingCase = separatorCase("-") { it.lowerCase() }

@JvmField
val UPPER_HYPHEN: NamingCase = separatorCase("-") { it.upperCase() }

fun CharSequence.toCase(from: NamingCase, to: NamingCase): String {
    return from.convert(this, to)
}

fun lowerCamelCase(nonLetterPolicy: CamelCase.NonLetterPolicy): NamingCase {
    return object : CamelCase() {
        override fun isLowerLetter(c: Char, index: Int, isLastLower: Boolean): Boolean {
            return nonLetterPolicy.isLowerLetter(c, index, isLastLower)
        }

        override fun doFirstWord(firstWord: CharSequence): String {
            return firstWord.uncapitalize()
        }
    }
}

fun upperCamelCase(nonLetterPolicy: CamelCase.NonLetterPolicy): NamingCase {
    return object : CamelCase() {
        override fun isLowerLetter(c: Char, index: Int, isLastLower: Boolean): Boolean {
            return nonLetterPolicy.isLowerLetter(c, index, isLastLower)
        }

        override fun doFirstWord(firstWord: CharSequence): String {
            return firstWord.capitalize()
        }
    }
}

fun separatorCase(separator: CharSequence, wordProcess: Function<CharSequence, CharSequence>): NamingCase {
    return object : SeparatorCase(separator) {
        override fun doProcessWord(word: CharSequence): CharSequence {
            return wordProcess.apply(word)
        }
    }
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
@ThreadSafe
interface NamingCase {

    /**
     * Splits [name] by case boundary.
     */
    fun <T : CharSequence> split(name: T): Words<T>

    /**
     * Joins [words] with current case style.
     */
    fun <T : CharSequence> join(words: Words<T>): String {
        val sb = StringBuilder()
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
         * Char count of words.
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
             * Returns the words built from [source], [splitList] and [splitCharCount].
             */
            @JvmStatic
            fun <T : CharSequence> of(source: T, splitList: List<CharSequence>, splitCharCount: Int): Words<T> {
                return object : Words<T> {
                    override val source: T = source
                    override val wordList: List<CharSequence> = splitList
                    override val charCount: Int = splitCharCount
                }
            }
        }
    }
}

/**
 * Camel-Case implementation of [NamingCase].
 */
open class CamelCase @JvmOverloads constructor(
    private val capitalized: Boolean,
    private val nonLetterCasePolicy: NonLetterCasePolicy = NonLetterCasePolicy.CONTRARY
) : NamingCase {

    override fun <T : CharSequence> split(name: T): NamingCase.Words<T> {

        val length = name.length
        if (length <= 1) {
            return NamingCase.Words.of(name)
        }

        fun isSameCase(c1: Char, c2: Char): Boolean {
            if (c1.isLowerCase()) {
                if (c2.isLowerCase()) {
                    return true
                }
                if (c2.isUpperCase()) {
                    return false
                }
                return nonLetterCasePolicy.isSameCaseLN()
            } else if (c1.isUpperCase()) {
                if (c2.isLowerCase()) {
                    return false
                }
                if (c2.isUpperCase()) {
                    return true
                }
                return nonLetterCasePolicy.isSameCaseUN()
            } else {
                if (c2.isLowerCase()) {
                    return nonLetterCasePolicy.isSameCaseNL()
                }
                if (c2.isUpperCase()) {
                    return nonLetterCasePolicy.isSameCaseNU()
                }
                return true
            }
        }

        var splitList: MutableList<CharSequence>? = null

        fun getSplitList(): MutableList<CharSequence> {
            val sl = splitList
            if (sl === null) {
                val newList = LinkedList<CharSequence>()
                splitList = newList
                return newList
            }
            return sl
        }

        var startIndex = 0
        var lastChar = name[0]
        var isLastLower =
        var i = 1
        while (i <name.length) {
            val c = name[i]
            if (isSameCase(lastChar, c)) {
                lastChar = c
                i++
                continue
            }



            val isCurLower = isLowerLetter(c, i, isLastLower)
            if (i == startIndex) {
                isLastLower = isCurLower
                continue
            }
            if (isLastLower && isCurLower) {
                continue
            }
            if (!isLastLower && !isCurLower) {
                continue
            }
            if (isLastLower && !isCurLower) {
                getSplitList().add(name.charsRef(startIndex, i))
                startIndex = i
                isLastLower = false
                continue
            }
            if (!isLastLower && isCurLower) {
                //"Ab" or "AAb":
                //"Ab" is one word;
                //"AAb" are two words: "A" and "Ab";
                if (i - startIndex == 1) {
                    //Case "Ab"
                } else {
                    //Case "AAb"
                    getSplitList().add(name.charsRef(startIndex, i - 1))
                    startIndex = i - 1
                }
                isLastLower = true
                continue
            }
        }
        if (splitList === null) {
            return NamingCase.Words.nameSelf(name)
        }
        if (startIndex < name.length) {
            getSplitList().add(name.charsRef(startIndex))
        }
        return NamingCase.Words.of(name, splitList!!, name.length)
    }

    override fun <T : CharSequence> join(words: NamingCase.Words<T>): String {
        if (words.wordList.isEmpty()) {
            return doFirstWord(words.source).toString()
        }
        val sb = StringBuilder(words.charCount)
        join0(words, sb)
        return sb.toString()
    }

    override fun <T : CharSequence> joinTo(dest: Appendable, words: NamingCase.Words<T>) {
        if (words.wordList.isEmpty()) {
            dest.append(doFirstWord(words.source))
            return
        }
        join0(words, dest)
    }

    private fun <T : CharSequence> join0(words: NamingCase.Words<T>, appendable: Appendable) {
        val splitList = words.wordList
        appendable.append(doFirstWord(splitList[0]))
        var i = 1
        while (i < splitList.size) {
            appendable.append(splitList[i].capitalize())
            i++
        }
    }

    private fun Char.isLowerLetter(): Boolean {
        return this in 'a'..'z'
    }

    private fun Char.isUpperLetter(): Boolean {
        return this in 'A'..'Z'
    }

    /**
     * Camel-Case policy for non-letter chars.
     */
    enum class NonLetterCasePolicy {

        /**
         * Non-Letter chars will be seen as lower case.
         */
        AS_LOWER,

        /**
         * Non-Letter chars will be seen as upper case.
         */
        AS_UPPER,

        /**
         * Letters and non-letter will be seen as same case if they are adjoined.
         */
        FOLLOWING,

        /**
         * Letters and non-letter will be seen as different case if they are adjoined.
         */
        CONTRARY,
        ;

        /**
         * Returns whether two char are same case if a lower letter followed by a non-letter.
         */
        fun isSameCaseLN(): Boolean {
            if (this === AS_LOWER) {
                return true
            }
            if (this === AS_UPPER) {
                return false
            }
            if (this === FOLLOWING) {
                return true
            }
            // CONTRARY
            return false
        }

        /**
         * Returns whether two char are same case if a upper letter followed by a non-letter.
         */
        fun isSameCaseUN(): Boolean {
            if (this === AS_LOWER) {
                return false
            }
            if (this === AS_UPPER) {
                return true
            }
            if (this === FOLLOWING) {
                return true
            }
            // CONTRARY
            return false
        }

        /**
         * Returns whether two char are same case if a non-letter followed by a lower letter.
         */
        fun isSameCaseNL(): Boolean {
            if (this === AS_LOWER) {
                return true
            }
            if (this === AS_UPPER) {
                return false
            }
            if (this === FOLLOWING) {
                return true
            }
            // CONTRARY
            return false
        }

        /**
         * Returns whether two char are same case if a non-letter followed by a upper letter.
         */
        fun isSameCaseNU(): Boolean {
            if (this === AS_LOWER) {
                return false
            }
            if (this === AS_UPPER) {
                return true
            }
            if (this === FOLLOWING) {
                return true
            }
            // CONTRARY
            return false
        }
    }
}

/**
 * [NamingCase] splits and join by specified separator.
 */
abstract class SeparatorCase(
    private val separator: CharSequence,
) : NamingCase {

    protected abstract fun doProcessWord(word: CharSequence): CharSequence

    override fun <T : CharSequence> split(name: T): NamingCase.Words<T> {

        if (name.isEmpty()) {
            return NamingCase.Words.nameSelf(name)
        }

        val separatorString = separator.toString()
        var index = name.indexOf(separatorString)
        if (index < 0) {
            return NamingCase.Words.nameSelf(name)
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
                getSplitList().add(name.charsRef(startIndex, endIndex))
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
            return doProcessWord(words.source).toString()
        }
        val sb = StringBuilder(words.charCount + separator.length * (words.wordList.size - 1))
        join0(words, sb)
        return sb.toString()
    }

    override fun <T : CharSequence> joinTo(dest: Appendable, words: NamingCase.Words<T>) {
        if (words.wordList.isEmpty()) {
            dest.append(doProcessWord(words.source))
            return
        }
        join0(words, dest)
    }

    private fun <T : CharSequence> join0(words: NamingCase.Words<T>, appendable: Appendable) {
        words.wordList.joinTo(appendable, separator) { doProcessWord(it) }
    }
}