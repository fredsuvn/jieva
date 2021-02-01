package xyz.srclab.common.base

import org.apache.commons.lang3.StringUtils
import java.util.*

/**
 * @author sunqian
 */
interface NamingCase {

    fun segment(name: CharSequence): List<String>

    fun join(words: List<CharSequence>): String

    @JvmDefault
    fun convertTo(name: CharSequence, toCase: NamingCase): String {
        val words = segment(name)
        return toCase.join(words)
    }

    @JvmDefault
    fun convertFrom(name: CharSequence, fromCase: NamingCase): String {
        return fromCase.convertTo(name, this)
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
        val CAPITALIZE_UNDERSCORE: CapitalizeUnderscore = CapitalizeUnderscore

        @JvmField
        val LOWER_HYPHEN: LowerHyphen = LowerHyphen

        @JvmField
        val UPPER_HYPHEN: UpperHyphen = UpperHyphen

        @JvmField
        val CAPITALIZE_HYPHEN: CapitalizeHyphen = CapitalizeHyphen
    }
}

object LowerCamel : CamelCase() {
    override fun doFirst(first: CharSequence): String {
        return first.toString().decapitalize(Default.locale)
    }
}

object UpperCamel : CamelCase() {
    override fun doFirst(first: CharSequence): String {
        return first.toString().capitalize(Default.locale)
    }
}

object LowerUnderscore : UnderscoreCase() {
    override fun doWord(word: CharSequence): String {
        return word.toString().toLowerCase(Default.locale)
    }
}

object UpperUnderscore : UnderscoreCase() {
    override fun doWord(word: CharSequence): String {
        return word.toString().toUpperCase(Default.locale)
    }
}

object CapitalizeUnderscore : UnderscoreCase() {
    override fun doWord(word: CharSequence): String {
        return word.toString().toLowerCase(Default.locale).capitalize(Default.locale)
    }
}

object LowerHyphen : HyphenCase() {
    override fun doWord(word: CharSequence): String {
        return word.toString().toLowerCase(Default.locale)
    }
}

object UpperHyphen : HyphenCase() {
    override fun doWord(word: CharSequence): String {
        return word.toString().toUpperCase(Default.locale)
    }
}

object CapitalizeHyphen : HyphenCase() {
    override fun doWord(word: CharSequence): String {
        return word.toString().toLowerCase(Default.locale).capitalize(Default.locale)
    }
}

abstract class CamelCase : NamingCase {

    override fun segment(name: CharSequence): List<String> {

        fun Char.asUpperCase(): Boolean {
            return this.isUpperCase()
        }

        fun Char.asLowerCase(): Boolean {
            return this.isLowerCase() || (this in '0'..'9')
        }

        val length = name.length
        if (length <= 1) {
            return listOf(name.toString())
        }
        val result = LinkedList<String>()
        val buffer = StringBuilder()
        var lastLower = true
        for (c in name) {
            if (buffer.isEmpty()) {
                lastLower = c.asLowerCase()
                buffer.append(c)
                continue
            }
            if (lastLower && c.asLowerCase()) {
                buffer.append(c)
                continue
            }
            if (!lastLower && c.asUpperCase()) {
                buffer.append(c)
                continue
            }
            if (lastLower && c.asUpperCase()) {
                result.add(buffer.toString())
                buffer.clear()
                buffer.append(c)
                lastLower = false
                continue
            }
            if (!lastLower && c.asLowerCase()) {
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
        return result.toList()
    }

    override fun join(words: List<CharSequence>): String {
        if (words.isEmpty()) {
            throw IllegalArgumentException("Given joined words list should have at least 1 word.")
        }
        val first = words.first().toString()
        if (first.isEmpty()) {
            throw IllegalArgumentException("Word of given first joined word should have at least 1 char.")
        }
        if (first.length > 1 && StringUtils.isAllUpperCase(first)) {
            return words.joinToString("") { it.toString().capitalize(Default.locale) }
        }
        return doFirst(first) +
                words.subList(1, words.size).joinToString("") {
                    it.toString().capitalize(Default.locale)
                }
    }

    protected abstract fun doFirst(first: CharSequence): String
}

abstract class UnderscoreCase : NamingCase {

    override fun segment(name: CharSequence): List<String> {
        return name.split("_")
    }

    override fun join(words: List<CharSequence>): String {
        return words.joinToString("_") { doWord(it.toString()) }
    }

    protected abstract fun doWord(word: CharSequence): String
}

abstract class HyphenCase : NamingCase {

    override fun segment(name: CharSequence): List<String> {
        return name.split("-")
    }

    override fun join(words: List<CharSequence>): String {
        return words.joinToString("-") { doWord(it.toString()) }
    }

    protected abstract fun doWord(word: CharSequence): String
}