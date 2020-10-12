package xyz.srclab.common.base

interface Provider<T : Any> {

    fun provide(): T

    companion object {

        /**
         * Specification for provider: className1&#91, className2]...
         *
         * This method will ignore wrong provider specification.
         */
        @JvmStatic
        fun <T, P : Provider<T>> parseSpec(spec: String): List<P> {
            return CommonProviderSpecParser.parse(spec)
        }

        /**
         * Specification for provider: className1&#91, className2]...
         *
         * This method will throw [IllegalArgumentException] when instantiate provider failed.
         */
        @JvmStatic
        fun <T, P : Provider<T>> parseSpecStrictly(spec: String): List<P> {
            return StrictProviderSpecParser.parse(spec)
        }
    }
}

/**
 * Specification for provider: className1&#91, className2]...
 *
 * This method will ignore wrong provider specification.
 */
fun <T, P : Provider<T>> providerOfSpec(spec: String): List<P> {
    return CommonProviderSpecParser.parse(spec)
}

/**
 * Specification for provider: className1&#91, className2]...
 *
 * This method will throw [IllegalArgumentException] when instantiate provider failed.
 */
fun <T, P : Provider<T>> providerOfSpecStrictly(spec: String): List<P> {
    return StrictProviderSpecParser.parse(spec)
}

interface ProviderSpecParser {

    fun <T, P : Provider<T>> parse(spec: String): List<P>
}

object CommonProviderSpecParser : ProviderSpecParser {

    override fun <T, P : Provider<T>> parse(spec: String): List<P> {
        val classNames = spec.split(",")
        val result = ArrayList<P>(classNames.size)
        for (className in classNames) {
            val trimmedClassName = className.trim()
            val providerClass = trimmedClassName.findClass<Class<P>>() ?: continue
            if (!Provider::class.java.isAssignableFrom(providerClass)) {
                continue
            }
            val instance = try {
                providerClass.newInstance()
            } catch (e: Exception) {
                continue
            }
            result.add(instance.asAny())
        }
        return result
    }
}

object StrictProviderSpecParser : ProviderSpecParser {

    override fun <T, P : Provider<T>> parse(spec: String): List<P> {
        val classNames = spec.split(",")
        val result = ArrayList<P>(classNames.size)
        for (className in classNames) {
            val trimmedClassName = className.trim()
            val providerClass = trimmedClassName.findClass<Class<P>>()
                ?: throw IllegalArgumentException("Class $trimmedClassName was not found.")
            if (!Provider::class.java.isAssignableFrom(providerClass)) {
                throw IllegalArgumentException("Class $trimmedClassName is not type of ${Provider::class.java}.")
            }
            val instance = try {
                providerClass.newInstance()
            } catch (e: Exception) {
                throw IllegalArgumentException(e)
            }
            result.add(instance.asAny())
        }
        return result
    }
}