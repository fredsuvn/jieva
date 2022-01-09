package xyz.srclab.common.proxy

import xyz.srclab.common.reflect.currentClassLoader

/**
 * Represents a proxy class, used to instantiate proxy object.
 *
 * @see ClassProxyFactory
 * @see SpringProxyFactory
 * @see CglibProxyFactory
 * @see JdkProxyFactory
 */
interface ClassProxy<T : Any> {

    fun create(): T

    fun create(parameterTypes: Array<Class<*>>, args: Array<Any?>): T

    companion object {

        /**
         * Generates proxy class.
         */
        @JvmStatic
        @JvmOverloads
        fun <T : Any> generate(
            sourceClass: Class<T>,
            proxyMethods: Iterable<ProxyMethod>,
            classLoader: ClassLoader = currentClassLoader(),
            proxyClassGenerator: ClassProxyFactory = ClassProxyFactory.DEFAULT,
        ): ClassProxy<T> {
            return proxyClassGenerator.generate(sourceClass, proxyMethods, classLoader)
        }
    }
}