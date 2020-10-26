package xyz.srclab.common.base

import xyz.srclab.common.reflect.findClassToInstance

interface Provider<S, T : Any> {

    fun parse(spec: S): List<T>

    fun parseFirst(spec: S): T {
        return parseFirstOrNull(spec) ?: throw IllegalArgumentException("Illegal provider specification: $spec")
    }

    fun parseFirstOrNull(spec: S): T?

    companion object {

        @JvmStatic
        @JvmOverloads
        fun <T : Any> charsProvider(strictly: Boolean = false): CharsProvider<T> {
            return if (strictly) StrictCharsProvider.ofType() else CharsProvider.ofType()
        }

        @JvmStatic
        @JvmOverloads
        fun <T : Any> parse(spec: CharSequence, strictly: Boolean = false): List<T> {
            return charsProvider<T>(strictly).parse(spec)
        }

        @JvmStatic
        @JvmOverloads
        fun <T : Any> parseFirst(spec: CharSequence, strictly: Boolean = false): T {
            return charsProvider<T>(strictly).parseFirst(spec)
        }

        @JvmStatic
        @JvmOverloads
        fun <T : Any> parseFirstOrNull(spec: CharSequence, strictly: Boolean = false): T? {
            return charsProvider<T>(strictly).parseFirstOrNull(spec)
        }
    }
}

fun <T : Any> CharSequence.charsProviderParse(strictly: Boolean = false): List<T> {
    return Provider.parse(this)
}

fun <T : Any> CharSequence.charsProviderParseFirst(strictly: Boolean = false): T {
    return Provider.parseFirst(this)
}

fun <T : Any> CharSequence.charsProviderParseFirstOrNull(strictly: Boolean = false): T? {
    return Provider.parseFirstOrNull(this)
}

open class CharsProvider<T : Any> : Provider<CharSequence, T> {

    override fun parse(spec: CharSequence): List<T> {
        val classNames = spec.split(",")
        val result = ArrayList<T>(classNames.size)
        for (className in classNames) {
            val trimmedClassName = className.trim()
            val product: T? = try {
                trimmedClassName.findClassToInstance()
            } catch (e: Exception) {
                continue
            }
            if (product !== null) {
                result.add(product)
            }
        }
        return result
    }

    override fun parseFirstOrNull(spec: CharSequence): T? {
        val classNames = spec.split(",")
        for (className in classNames) {
            val trimmedClassName = className.trim()
            val product: T? = try {
                trimmedClassName.findClassToInstance()
            } catch (e: Exception) {
                continue
            }
            if (product !== null) {
                return product
            }
        }
        return null
    }

    companion object {

        private val INSTANCE = CharsProvider<Any>()

        @JvmStatic
        fun <T : Any> ofType(): CharsProvider<T> {
            return INSTANCE.asAny()
        }
    }
}

class StrictCharsProvider<T : Any> : CharsProvider<T>() {

    override fun parse(spec: CharSequence): List<T> {
        val classNames = spec.split(",")
        val result = ArrayList<T>(classNames.size)
        for (className in classNames) {
            result.add(createInstance(className))
        }
        return result
    }

    override fun parseFirstOrNull(spec: CharSequence): T? {
        val classNames = spec.split(",")
        for (className in classNames) {
            return createInstance(className)
        }
        return null
    }

    private fun createInstance(className: CharSequence): T {
        val trimmedClassName = className.trim()
        val product: T? = try {
            trimmedClassName.findClassToInstance()
        } catch (e: Exception) {
            throw IllegalArgumentException("Instantiate class $trimmedClassName failed.", e)
        }
        if (product === null) {
            throw IllegalArgumentException("Class $trimmedClassName was not found.")
        }
        return product
    }

    companion object {

        private val INSTANCE = StrictCharsProvider<Any>()

        @JvmStatic
        fun <T : Any> ofType(): StrictCharsProvider<T> {
            return INSTANCE.asAny()
        }
    }
}