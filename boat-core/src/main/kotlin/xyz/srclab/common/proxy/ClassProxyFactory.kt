package xyz.srclab.common.proxy

import xyz.srclab.common.reflect.classForNameOrNull

/**
 * Generator to generate [ClassProxy].
 *
 * Default implementations in priority order are:
 *
 * * spring-cglib
 * * cglib
 * * jdk-proxy
 */
interface ClassProxyFactory {

    fun <T : Any> generate(
        sourceClass: Class<T>,
        proxyMethods: Iterable<ProxyMethod>,
        classLoader: ClassLoader,
    ): ClassProxy<T>

    companion object {

        private var defaultFactory: ClassProxyFactory = findDefaultFactory()

        @JvmStatic
        fun defaultFactory(): ClassProxyFactory {
            return defaultFactory
        }

        @JvmStatic
        fun setDefaultFactory(factory: ClassProxyFactory) {
            this.defaultFactory = factory
        }

        @JvmStatic
        fun cglib(): ClassProxyFactory {
            return CglibProxyFactory
        }

        @JvmStatic
        fun spring(): ClassProxyFactory {
            return SpringProxyFactory
        }

        @JvmStatic
        fun jdk(): ClassProxyFactory {
            return JdkProxyFactory
        }

        private fun findDefaultFactory(): ClassProxyFactory {
            val springLib = "org.springframework.cglib.proxy.Enhancer".classForNameOrNull<Any>()
            if (springLib !== null) {
                return spring()
            }
            val cgLib = "net.sf.cglib.proxy.Enhancer".classForNameOrNull<Any>()
            if (cgLib !== null) {
                return cglib()
            }
            return jdk()
        }
    }
}