package xyz.srclab.common.base

interface Provider<S, T : Any> {

    fun parse(spec: S): List<T>

    fun parseFirst(spec: S): T {
        return parseFirstOrNull(spec) ?: throw IllegalArgumentException("Illegal provider specification: $spec")
    }

    fun parseFirstOrNull(spec: S): T?

    companion object {

        @JvmStatic
        fun <T : Any> ofChars(): CharsProvider<T> {
            return CharsProvider.ofType()
        }

        @JvmStatic
        fun <T : Any> ofStrictChars(): StrictCharsProvider<T> {
            return StrictCharsProvider.ofType()
        }
    }
}

fun <T : Any> providerOfChars(): CharsProvider<T> {
    return Provider.ofChars()
}

fun <T : Any> strictProviderOfChars(): StrictCharsProvider<T> {
    return Provider.ofStrictChars()
}

fun <T : Any> CharSequence.parseByCharsProvider(): List<T> {
    return providerOfChars<T>().parse(this)
}

fun <T : Any> CharSequence.parseFirstByCharsProvider(): T {
    return providerOfChars<T>().parseFirst(this)
}

fun <T : Any> CharSequence.parseFirstOrNullByCharsProvider(): T? {
    return providerOfChars<T>().parseFirstOrNull(this)
}

fun <T : Any> CharSequence.parseByCharsProviderStrictly(): List<T> {
    return strictProviderOfChars<T>().parse(this)
}

fun <T : Any> CharSequence.parseFirstByCharsProviderStrictly(): T {
    return strictProviderOfChars<T>().parseFirst(this)
}

fun <T : Any> CharSequence.parseFirstOrNullByCharsProviderStrictly(): T? {
    return strictProviderOfChars<T>().parseFirstOrNull(this)
}

class CharsProvider<T : Any> : Provider<CharSequence, T> {

    override fun parse(spec: CharSequence): List<T> {
        val classNames = spec.split(",")
        val result = ArrayList<T>(classNames.size)
        for (className in classNames) {
            val trimmedClassName = className.trim()
            val providerClass = trimmedClassName.findClass<Class<T>>() ?: continue
            val instance = try {
                providerClass.newInstance()
            } catch (e: Exception) {
                continue
            }
            result.add(instance.asAny())
        }
        return result
    }

    override fun parseFirstOrNull(spec: CharSequence): T? {
        val classNames = spec.split(",")
        val result = ArrayList<T>(classNames.size)
        for (className in classNames) {
            val trimmedClassName = className.trim()
            val providerClass = trimmedClassName.findClass<Class<T>>() ?: continue
            val instance = try {
                providerClass.newInstance()
            } catch (e: Exception) {
                continue
            }
            return instance.asAny()
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

class StrictCharsProvider<T : Any> : Provider<CharSequence, T> {

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
        val providerClass = trimmedClassName.findClass<Class<T>>()
            ?: throw IllegalArgumentException("Class $trimmedClassName was not found.")
        val instance = try {
            providerClass.newInstance()
        } catch (e: Exception) {
            throw IllegalArgumentException(e)
        }
        return instance.asAny()
    }

    companion object {

        private val INSTANCE = StrictCharsProvider<Any>()

        @JvmStatic
        fun <T : Any> ofType(): StrictCharsProvider<T> {
            return INSTANCE.asAny()
        }
    }
}