package xyz.srclab.common.base

import xyz.srclab.common.reflect.toInstance

interface SpecParser<S> {

    fun <T : Any> parse(spec: S): List<T>

    @JvmDefault
    fun <T : Any> parseFirst(spec: S): T {
        return parseFirstOrNull(spec) ?: throw java.lang.IllegalStateException("Spec parsed failed: $spec.")
    }

    fun <T : Any> parseFirstOrNull(spec: S): T?

    companion object {

        @JvmStatic
        @JvmOverloads
        @JvmName("specParse")
        fun <T : Any> CharSequence.parseCharsProviders(strict: Boolean = false): List<T> {
            return getCharsSpecParser(strict).parse(this)
        }

        @JvmStatic
        @JvmOverloads
        @JvmName("specParseFirst")
        fun <T : Any> CharSequence.parseCharsFirstProvider(strict: Boolean = false): T {
            return getCharsSpecParser(strict).parseFirst(this)
        }

        @JvmStatic
        @JvmOverloads
        @JvmName("specParseFirstOrNull")
        fun <T : Any> CharSequence.parseCharsFirstProviderOrNull(strict: Boolean = false): T? {
            return getCharsSpecParser(strict).parseFirstOrNull(this)
        }

        private fun getCharsSpecParser(strict: Boolean): SpecParser<CharSequence> {
            return if (strict) StrictCharsSpecParser else CharsSpecParser
        }
    }
}

object CharsSpecParser : SpecParser<CharSequence> {

    override fun <T : Any> parse(spec: CharSequence): List<T> {
        val classNames = spec.split(",")
        val result = ArrayList<T>(classNames.size)
        for (className in classNames) {
            val trimmedClassName = className.trim()
            val product: T? = try {
                trimmedClassName.toInstance()
            } catch (e: Exception) {
                continue
            }
            if (product !== null) {
                result.add(product)
            }
        }
        return result
    }

    override fun <T : Any> parseFirstOrNull(spec: CharSequence): T? {
        val classNames = spec.split(",")
        for (className in classNames) {
            val trimmedClassName = className.trim()
            val product: T? = try {
                trimmedClassName.toInstance()
            } catch (e: Exception) {
                continue
            }
            if (product !== null) {
                return product
            }
        }
        return null
    }
}

object StrictCharsSpecParser : SpecParser<CharSequence> {

    override fun <T : Any> parse(spec: CharSequence): List<T> {
        val classNames = spec.split(",")
        val result = ArrayList<T>(classNames.size)
        for (className in classNames) {
            result.add(createInstance(className))
        }
        return result
    }

    override fun <T : Any> parseFirstOrNull(spec: CharSequence): T? {
        val classNames = spec.split(",")
        for (className in classNames) {
            return createInstance(className)
        }
        return null
    }

    private fun <T : Any> createInstance(className: CharSequence): T {
        val trimmedClassName = className.trim()
        val product: T? = try {
            trimmedClassName.toInstance()
        } catch (e: Exception) {
            throw IllegalStateException("Instantiate class $trimmedClassName failed.", e)
        }
        if (product === null) {
            throw IllegalStateException("Class $trimmedClassName was not found.")
        }
        return product
    }
}