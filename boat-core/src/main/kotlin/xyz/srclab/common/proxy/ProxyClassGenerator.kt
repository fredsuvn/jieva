package xyz.srclab.common.proxy

import xyz.srclab.common.base.loadClassOrNull

/**
 * Generator to generate [ProxyClass].
 *
 * Default implementations in priority order are:
 *
 * * spring-cglib
 * * cglib
 * * jdk-proxy
 */
interface ProxyClassGenerator {

    fun <T : Any> generate(
        sourceClass: Class<T>,
        proxyMethods: Iterable<ProxyMethod<T>>,
        classLoader: ClassLoader,
    ): ProxyClass<T>

    companion object {

        @JvmField
        val DEFAULT: ProxyClassGenerator = findDefaultProxyClassGenerator()

        private fun findDefaultProxyClassGenerator(): ProxyClassGenerator {
            val springLib = "org.springframework.cglib.proxy.Enhancer".loadClassOrNull<Any>()
            if (springLib !== null) {
                return SpringProxyClassGenerator
            }
            val cgLib = "net.sf.cglib.proxy.Enhancer".loadClassOrNull<Any>()
            if (cgLib !== null) {
                return CglibProxyClassGenerator
            }
            return JdkProxyClassGenerator
        }
    }
}