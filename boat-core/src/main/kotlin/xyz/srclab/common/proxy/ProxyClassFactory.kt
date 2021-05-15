package xyz.srclab.common.proxy

import xyz.srclab.common.lang.loadClassOrNull

interface ProxyClassFactory {

    fun <T : Any> create(
        proxyClass: Class<T>,
        proxyMethods: Iterable<ProxyMethod<T>>,
        classLoader: ClassLoader,
    ): ProxyClass<T>

    companion object {

        @JvmField
        val DEFAULT: ProxyClassFactory = findDefaultProxyClassGenerator()

        private fun findDefaultProxyClassGenerator(): ProxyClassFactory {
            val springLib = "org.springframework.cglib.proxy.Enhancer".loadClassOrNull<Any>()
            if (springLib !== null) {
                return SpringProxyClassFactory
            }
            val cgLib = "net.sf.cglib.proxy.Enhancer".loadClassOrNull<Any>()
            if (cgLib !== null) {
                return CglibProxyClassFactory
            }
            return JdkProxyClassFactory
        }
    }
}