@file:JvmName("BCase")

package xyz.srclab.common.base

import xyz.srclab.annotations.concurrent.ThreadSafe
import xyz.srclab.common.base.StringRef.Companion.stringRef
import java.util.*
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

fun CharSequence.toCase(from: NamingCase, to: NamingCase): String {
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
         * Name.
         */
        val name: T

        /**
         * Split words list,
         * or `empty` if the [name] consists of only one word which is name itself.
         */
        val splitList: List<CharSequence>

        /**
         * Char number of split words.
         */
        val splitCharCount: Int

        companion object {

            /**
             * Name itself as a [Words].
             */
            @JvmStatic
            fun <T : CharSequence> nameSelf(name: T): Words<T> {
                return object : Words<T> {
                    override val name: T = name
                    override val splitList: List<CharSequence> = emptyList()
                    override val splitCharCount: Int = name.length
                }
            }

            @JvmStatic
            fun <T : CharSequence> of(name: T, splitList: List<CharSequence>, splitCharCount: Int): Words<T> {
                return object : Words<T> {
                    override val name: T = name
                    override val splitList: List<CharSequence> = splitList
                    override val splitCharCount: Int = splitCharCount
                }
            }
        }
    }
}

/**
 * Camel-Case class.
 */
open class CamelCase(private val firstWord: Function<CharSequence, String>) : NamingCase {

    override fun <T : CharSequence> split(name: T): NamingCase.Words<T> {

        fun Char.isUpper(): Boolean {
            return this.isUpperCase()
        }

        fun Char.isLower(): Boolean {
            return this.isLowerCase() || (this in '0'..'9')
        }

        val length = name.length
        if (length <= 1) {
            return NamingCase.Words.nameSelf(name)
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
                getSplitList().add(name.stringRef(startIndex, i))
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
                    getSplitList().add(name.stringRef(startIndex, i - 1))
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
            getSplitList().add(name.stringRef(startIndex))
        }
        return NamingCase.Words.of(name, splitList!!, name.length)
    }

    override fun <T : CharSequence> join(words: NamingCase.Words<T>): String {
        if (words.splitList.isEmpty()) {
            return firstWord.apply(words.name)
        }
        val sb = StringBuilder(words.splitCharCount)
        join0(words, sb)
        return sb.toString()
    }

    override fun <T : CharSequence> joinTo(dest: Appendable, words: NamingCase.Words<T>) {
        if (words.splitList.isEmpty()) {
            dest.append(firstWord.apply(words.name))
            return
        }
        join0(words, dest)
    }

    private fun <T : CharSequence> join0(words: NamingCase.Words<T>, appendable: Appendable) {
        val splitList = words.splitList
        appendable.append(firstWord.apply(splitList[0]))
        var i = 1
        while (i < splitList.size) {
            appendable.append(splitList[i].capitalize())
            i++
        }
    }
}

/**
 * [NamingCase] splits and join by specified separator.
 */
open class SeparatorCase @JvmOverloads constructor(
    private val separator: CharSequence,
    private val wordProcessor: Function<CharSequence, String> = Function { it.toString() }
) : NamingCase {

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
                getSplitList().add(name.stringRef(startIndex, endIndex))
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
        if (words.splitList.isEmpty()) {
            return wordProcessor.apply(words.name)
        }
        val sb = StringBuilder(words.splitCharCount + separator.length * (words.splitList.size - 1))
        join0(words, sb)
        return sb.toString()
    }

    override fun <T : CharSequence> joinTo(dest: Appendable, words: NamingCase.Words<T>) {
        if (words.splitList.isEmpty()) {
            dest.append(wordProcessor.apply(words.name))
            return
        }
        join0(words, dest)
    }

    private fun <T : CharSequence> join0(words: NamingCase.Words<T>, appendable: Appendable) {
        words.splitList.joinTo(appendable, separator) { wordProcessor.apply(it) }
    }
}