package xyz.srclab.common.base

import xyz.srclab.common.reflect.toInstance

/**
 * Help parse object by spec of type [S].
 *
 * @see ClassNameSpecParser
 * @see StrictClassNameSpecParser
 */
interface SpecParser<S> {

    /**
     * Returns all parsed results by [spec].
     */
    @Throws(SpecParsingException::class)
    fun <T : Any> parse(spec: S): List<T>

    /**
     * Returns first parsed result by [spec].
     */
    @Throws(SpecParsingException::class)
    fun <T : Any> parseFirst(spec: S): T {
        return parseFirstOrNull(spec) ?: throw SpecParsingException("Spec parsed failed: $spec")
    }

    /**
     * Returns first parsed result by [spec], or null if failed.
     */
    @Throws(SpecParsingException::class)
    fun <T : Any> parseFirstOrNull(spec: S): T?

    companion object {

        @Throws(SpecParsingException::class)
        @JvmStatic
        @JvmOverloads
        fun <T : Any> CharSequence.parseClassNameToInstance(strict: Boolean = false): List<T> {
            return getClassNameSpecParser(strict).parse(this)
        }

        @Throws(SpecParsingException::class)
        @JvmStatic
        @JvmOverloads
        fun <T : Any> CharSequence.parseFirstClassNameToInstance(strict: Boolean = false): T {
            return getClassNameSpecParser(strict).parseFirst(this)
        }

        @Throws(SpecParsingException::class)
        @JvmStatic
        @JvmOverloads
        fun <T : Any> CharSequence.parseFirstClassNameToInstanceOrNull(strict: Boolean = false): T? {
            return getClassNameSpecParser(strict).parseFirstOrNull(this)
        }

        /**
         * Returns a spec parser which parses chars to instance.
         *
         * @see ClassNameSpecParser
         * @see StrictClassNameSpecParser
         */
        @Throws(SpecParsingException::class)
        @JvmStatic
        @JvmOverloads
        fun getClassNameSpecParser(strict: Boolean = false): SpecParser<CharSequence> {
            return if (strict) StrictClassNameSpecParser else ClassNameSpecParser
        }
    }
}

/**
 * Spec parser which parses chars to instance:
 *
 * ```
 * // Spaces will be erased for each class name.
 * List<A> list = ClassNameSpecParser.parse("a.b.A1, a.b.A2, a.b.A3")
 * ```
 *
 * Note this implementation will ignore failed parsing, use [StrictClassNameSpecParser] if you need strict parsing.
 */
object ClassNameSpecParser : SpecParser<CharSequence> {

    override fun <T : Any> parse(spec: CharSequence): List<T> {
        val classNames = spec.split(",")
        val result = ArrayList<T>(classNames.size)
        for (className in classNames) {
            val trimmedClassName = className.trim()
            val product: T = try {
                trimmedClassName.toInstance()
            } catch (e: Exception) {
                continue
            }
            result.add(product)
        }
        return result
    }

    override fun <T : Any> parseFirstOrNull(spec: CharSequence): T? {
        val classNames = spec.split(",")
        for (className in classNames) {
            val trimmedClassName = className.trim()
            val product: T = try {
                trimmedClassName.toInstance()
            } catch (e: Exception) {
                continue
            }
            return product
        }
        return null
    }
}

/**
 * Spec parser which parses chars to instance:
 *
 * ```
 * // Spaces will be erased for each class name.
 * List<A> list = ClassNameSpecParser.parse("a.b.A1, a.b.A2, a.b.A3")
 * ```
 *
 * Note this implementation will throw exception for failed parsing, use [ClassNameSpecParser] if you need ignore error.
 */
object StrictClassNameSpecParser : SpecParser<CharSequence> {

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
        val product: T = try {
            trimmedClassName.toInstance()
        } catch (e: Exception) {
            throw SpecParsingException("Instantiate class $trimmedClassName failed.", e)
        }
        return product
    }
}

open class SpecParsingException @JvmOverloads constructor(
    message: String? = null, cause: Throwable? = null
) : RuntimeException(message, cause)