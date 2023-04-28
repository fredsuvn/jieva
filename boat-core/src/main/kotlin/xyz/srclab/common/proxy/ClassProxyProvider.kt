package xyz.srclab.common.proxy

import xyz.srclab.common.proxy.ClassProxyProvider.Companion.cglib
import xyz.srclab.common.proxy.ClassProxyProvider.Companion.jdk
import xyz.srclab.common.proxy.ClassProxyProvider.Companion.springCglib
import xyz.srclab.common.reflect.classForNameOrNull

/**
 * Provider to create [ClassProxy],
 * default implementations in priority order are:
 *
 * * spring-cglib ([springCglib])
 * * cglib ([cglib])
 * * jdk-proxy ([jdk])
 */
interface ClassProxyProvider {

    /**
     * Returns [ClassProxy] for source class [T].
     *
     * @param sourceClass source class
     * @param proxyInvoker invoker for proxy actions
     * @param classLoader class loader
     */
    fun <T : Any> getProxy(
        sourceClass: Class<T>,
        proxyInvoker: ProxyInvoker,
        classLoader: ClassLoader,
    ): ClassProxy<T>

    companion object {

        private var defaultFactory: ClassProxyProvider = run {
            val springLib = "org.springframework.cglib.proxy.Enhancer".classForNameOrNull<Any>()
            if (springLib !== null) {
                return@run springCglib()
            }
            val cgLib = "net.sf.cglib.proxy.Enhancer".classForNameOrNull<Any>()
            if (cgLib !== null) {
                return@run cglib()
            }
            jdk()
        }

        /**
         * Gets default [ClassProxyProvider].
         */
        @JvmStatic
        fun defaultProvider(): ClassProxyProvider {
            return defaultFactory
        }

        /**
         * Sets default [ClassProxyProvider].
         */
        @JvmStatic
        fun setDefaultProvider(provider: ClassProxyProvider) {
            this.defaultFactory = provider
        }

        /**
         * Returns [ClassProxyProvider] implements with `cglib`.
         */
        @JvmStatic
        fun cglib(): ClassProxyProvider {
            return CglibProxyProvider
        }

        /**
         * Returns [ClassProxyProvider] implements with `spring-cglib`.
         */
        @JvmStatic
        fun springCglib(): ClassProxyProvider {
            return SpringProxyProvider
        }

        /**
         * Returns [ClassProxyProvider] implements with `JDK`.
         */
        @JvmStatic
        fun jdk(): ClassProxyProvider {
            return JdkProxyProvider
        }
    }
}