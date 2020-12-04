package xyz.srclab.common.base

import xyz.srclab.common.reflect.findClassToInstance

interface Provider<S, T : Any> {

    fun parse(spec: S): List<T>

    @JvmDefault
    fun parseFirst(spec: S): T {
        return parseFirstOrNull(spec) ?: throw IllegalArgumentException("Illegal provider specification: $spec")
    }

    fun parseFirstOrNull(spec: S): T?

    companion object {

        @JvmStatic
        @JvmOverloads
        fun <T : Any> charsProvider(strict: Boolean = false): CharsProvider<T> {
            return if (strict) StrictCharsProvider.ofType() else CharsProvider.ofType()
        }

        @JvmStatic
        @JvmOverloads
        @JvmName("parseChars")
        fun <T : Any> CharSequence.parseCharsProviders(strictly: Boolean = false): List<T> {
            return charsProvider<T>(strictly).parse(this)
        }

        @JvmStatic
        @JvmOverloads
        @JvmName("parseCharsFirst")
        fun <T : Any> CharSequence.parseCharsFirstProvider(strictly: Boolean = false): T {
            return charsProvider<T>(strictly).parseFirst(this)
        }

        @JvmStatic
        @JvmOverloads
        @JvmName("parseCharsFirstOrNull")
        fun <T : Any> CharSequence.parseCharsFirstProviderOrNull(strictly: Boolean = false): T? {
            return charsProvider<T>(strictly).parseFirstOrNull(this)
        }
    }
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
            throw IllegalStateException("Instantiate class $trimmedClassName failed.", e)
        }
        if (product === null) {
            throw IllegalStateException("Class $trimmedClassName was not found.")
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