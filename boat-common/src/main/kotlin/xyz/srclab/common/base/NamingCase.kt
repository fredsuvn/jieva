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
    fun convert(name: CharSequence, namingCase: NamingCase): String {
        val words = segment(name)
        return namingCase.join(words)
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
    override fun doFirst(first: String): String {
        return first.decapitalize(Defaults.locale)
    }
}

object UpperCamel : CamelCase() {
    override fun doFirst(first: String): String {
        return first.capitalize(Defaults.locale)
    }
}

object LowerUnderscore : UnderscoreCase() {
    override fun doWord(word: String): String {
        return word.toLowerCase(Defaults.locale)
    }
}

object UpperUnderscore : UnderscoreCase() {
    override fun doWord(word: String): String {
        return word.toUpperCase(Defaults.locale)
    }
}

object CapitalizeUnderscore : UnderscoreCase() {
    override fun doWord(word: String): String {
        return word.toLowerCase(Defaults.locale).capitalize(Defaults.locale)
    }
}

object LowerHyphen : HyphenCase() {
    override fun doWord(word: String): String {
        return word.toLowerCase(Defaults.locale)
    }
}

object UpperHyphen : HyphenCase() {
    override fun doWord(word: String): String {
        return word.toUpperCase(Defaults.locale)
    }
}

object CapitalizeHyphen : HyphenCase() {
    override fun doWord(word: String): String {
        return word.toLowerCase(Defaults.locale).capitalize(Defaults.locale)
    }
}

abstract class CamelCase : NamingCase {

    override fun segment(name: CharSequence): List<String> {
        val length = name.length
        if (length <= 1) {
            return listOf(name.toString())
        }
        val result = LinkedList<String>()
        val buffer = StringBuilder()
        var lastLower = true
        for (c in name) {
            if (buffer.isEmpty()) {
                lastLower = c.isLowerCase()
                buffer.append(c)
                continue
            }
            if (lastLower && c.isLowerCase()) {
                buffer.append(c)
                continue
            }
            if (!lastLower && c.isUpperCase()) {
                buffer.append(c)
                continue
            }
            if (lastLower && c.isUpperCase()) {
                result.add(buffer.toString())
                buffer.clear()
                buffer.append(c)
                lastLower = false
                continue
            }
            if (!lastLower && c.isLowerCase()) {
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
            return words.joinToString("") { it.toString().capitalize(Defaults.locale) }
        }
        return doFirst(first) +
                words.subList(1, words.size).joinToString("") {
                    it.toString().capitalize(Defaults.locale)
                }
    }

    protected abstract fun doFirst(first: String): String
}

abstract class UnderscoreCase : NamingCase {

    override fun segment(name: CharSequence): List<String> {
        return name.split("_")
    }

    override fun join(words: List<CharSequence>): String {
        return words.joinToString("_") { doWord(it.toString()) }
    }

    protected abstract fun doWord(word: String): String
}

abstract class HyphenCase : NamingCase {

    override fun segment(name: CharSequence): List<String> {
        return name.split("-")
    }

    override fun join(words: List<CharSequence>): String {
        return words.joinToString("-") { doWord(it.toString()) }
    }

    protected abstract fun doWord(word: String): String
}