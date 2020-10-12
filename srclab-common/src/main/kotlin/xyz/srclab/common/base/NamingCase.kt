package xyz.srclab.common.base

import org.apache.commons.lang3.StringUtils
import java.util.*

/**
 * @author sunqian
 */
interface NamingCase {

    fun split(name: CharSequence): List<String>

    fun join(words: List<CharSequence>): String

    fun convert(name: CharSequence, namingCase: NamingCase): String {
        val words = split(name)
        return namingCase.join(words)
    }

    companion object {

        @JvmStatic
        val lowerCamel: NamingCase
            @JvmName("lowerCamel") get() = LowerCamel

        @JvmStatic
        val upperCamel: NamingCase
            @JvmName("upperCamel") get() = UpperCamel

        @JvmStatic
        val lowerUnderscore: NamingCase
            @JvmName("lowerUnderscore") get() = LowerUnderscore

        @JvmStatic
        val upperUnderscore: NamingCase
            @JvmName("upperUnderscore") get() = UpperUnderscore

        @JvmStatic
        val capitalizeUnderscore: NamingCase
            @JvmName("capitalizeUnderscore") get() = CapitalizeUnderscore

        @JvmStatic
        val lowerHyphen: NamingCase
            @JvmName("lowerHyphen") get() = LowerHyphen

        @JvmStatic
        val upperHyphen: NamingCase
            @JvmName("upperHyphen") get() = UpperHyphen

        @JvmStatic
        val capitalizeHyphen: NamingCase
            @JvmName("capitalizeHyphen") get() = CapitalizeHyphen
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

    override fun split(name: CharSequence): List<String> {
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

    override fun split(name: CharSequence): List<String> {
        return name.split("_")
    }

    override fun join(words: List<CharSequence>): String {
        return words.joinToString("_") { doWord(it.toString()) }
    }

    protected abstract fun doWord(word: String): String
}

abstract class HyphenCase : NamingCase {

    override fun split(name: CharSequence): List<String> {
        return name.split("-")
    }

    override fun join(words: List<CharSequence>): String {
        return words.joinToString("-") { doWord(it.toString()) }
    }

    protected abstract fun doWord(word: String): String
}