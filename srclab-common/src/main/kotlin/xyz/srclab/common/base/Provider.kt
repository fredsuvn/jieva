package xyz.srclab.common.base

/**
 * Specification for provider: className1&#91, className2]...
 *
 * @author sunqian
 */
interface Provider<T : Any> {

    fun provide(): T

    companion object {

        @JvmStatic
        fun <T, P : Provider<T>> fromSpec(spec: String): List<P> {
            return parseSpec(spec)
        }

        @JvmStatic
        fun <T, P : Provider<T>> fromSpecStrictly(spec: String): List<P> {
            return parseSpecStrictly(spec)
        }

        private fun <T, P : Provider<T>> parseSpec(spec: String): List<P> {
            val classNames = spec.split(",")
            val result = ArrayList<P>(classNames.size)
            for (className in classNames) {
                val trimmedClassName = className.trim()
                val `class` = Loader.findClass<Class<P>>(trimmedClassName) ?: continue
                if (!Provider::class.java.isAssignableFrom(`class`)) {
                    continue
                }
                val instance = try {
                    `class`.newInstance()
                } catch (e: Exception) {
                    continue
                }
                if (instance !is Provider<*>) {
                    continue
                }
                result.add(As.notNull(instance))
            }
            return result
        }

        private fun <T, P : Provider<T>> parseSpecStrictly(spec: String): List<P> {
            val classNames = spec.split(",")
            val result = ArrayList<P>(classNames.size)
            for (className in classNames) {
                val trimmedClassName = className.trim()
                val `class` = Loader.findClass<Class<P>>(trimmedClassName)
                    ?: throw IllegalArgumentException("Class $trimmedClassName was not found.")
                if (!Provider::class.java.isAssignableFrom(`class`)) {
                    throw IllegalArgumentException("Class $trimmedClassName is not type of ${Provider::class.java}.")
                }
                val instance = try {
                    `class`.newInstance()
                } catch (e: Exception) {
                    throw IllegalArgumentException(e)
                }
                if (instance !is Provider<*>) {
                    throw IllegalArgumentException("Class $trimmedClassName is not type of ${Provider::class.java}.")
                }
                result.add(As.notNull(instance))
            }
            return result
        }
    }
}