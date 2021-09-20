package xyz.srclab.common.proxy

import xyz.srclab.common.base.currentClassLoader

/**
 * Represents a proxy class, used to instantiate proxy object.
 *
 * @see ProxyClassGenerator
 * @see SpringProxyClassGenerator
 * @see CglibProxyClassGenerator
 * @see JdkProxyClassGenerator
 */
interface ProxyClass<T : Any> {

    fun instantiate(): T

    fun instantiate(parameterTypes: Array<Class<*>>, args: Array<Any?>): T

    companion object {

        /**
         * Generates proxy class.
         */
        @JvmStatic
        @JvmOverloads
        fun <T : Any> generate(
            sourceClass: Class<T>,
            proxyMethods: Iterable<ProxyMethod<T>>,
            classLoader: ClassLoader = currentClassLoader(),
            proxyClassGenerator: ProxyClassGenerator = ProxyClassGenerator.DEFAULT,
        ): ProxyClass<T> {
            return proxyClassGenerator.generate(sourceClass, proxyMethods, classLoader)
        }
    }
}