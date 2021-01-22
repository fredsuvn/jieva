package xyz.srclab.common.proxy

import xyz.srclab.common.base.loadClassOrNull

interface ProxyClassGenerator {

    fun <T : Any> generate(
        classLoader: ClassLoader,
        proxyClass: Class<T>,
        proxyMethods: Iterable<ProxyMethod<T>>
    ): ProxyClass<T>

    companion object {

        @JvmField
        val DEFAULT: ProxyClassGenerator = DefaultProxyClassGenerator

        private object DefaultProxyClassGenerator : ProxyClassGenerator {

            private val bytecodeProxyClassGenerator: ProxyClassGenerator? = findBytecodeProxyClassGenerator()

            private fun findBytecodeProxyClassGenerator(): ProxyClassGenerator? {
                val springLib = "org.springframework.cglib.proxy.Enhancer".loadClassOrNull<Any>()
                if (springLib !== null) {
                    return SpringProxyClassGenerator
                }
                val cgLib = "net.sf.cglib.proxy.Enhancer".loadClassOrNull<Any>()
                if (cgLib !== null) {
                    return CglibProxyClassGenerator
                }
                return null
            }

            override fun <T : Any> generate(
                classLoader: ClassLoader,
                proxyClass: Class<T>,
                proxyMethods: Iterable<ProxyMethod<T>>
            ): ProxyClass<T> {
                if (proxyClass.isInterface) {
                    return JdkProxyClassGenerator.generate(classLoader, proxyClass, proxyMethods)
                }
                if (bytecodeProxyClassGenerator === null) {
                    throw UnsupportedOperationException(
                        "Cannot proxy non-interface class because neither Spring nor cglib context found."
                    )
                }
                return bytecodeProxyClassGenerator.generate(classLoader, proxyClass, proxyMethods)
            }
        }
    }
}