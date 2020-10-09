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
        fun <T, P : Provider<T>> fromSpec(spec: String): List<P> {
            return CommonSpecParser.parse(spec)
        }

        /**
         * Specification for provider: className1&#91, className2]...
         *
         * This method will throw [IllegalArgumentException] when instantiate provider failed.
         */
        @JvmStatic
        fun <T, P : Provider<T>> fromSpecStrictly(spec: String): List<P> {
            return StrictSpecParser.parse(spec)
        }

        private interface SpecParser {

            fun <T, P : Provider<T>> parse(spec: String): List<P>
        }

        private object CommonSpecParser : SpecParser {

            override fun <T, P : Provider<T>> parse(spec: String): List<P> {
                val classNames = spec.split(",")
                val result = ArrayList<P>(classNames.size)
                for (className in classNames) {
                    val trimmedClassName = className.trim()
                    val providerClass = Loader.findClass<Class<P>>(trimmedClassName) ?: continue
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

        private object StrictSpecParser : SpecParser {

            override fun <T, P : Provider<T>> parse(spec: String): List<P> {
                val classNames = spec.split(",")
                val result = ArrayList<P>(classNames.size)
                for (className in classNames) {
                    val trimmedClassName = className.trim()
                    val providerClass = Loader.findClass<Class<P>>(trimmedClassName)
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
    }
}