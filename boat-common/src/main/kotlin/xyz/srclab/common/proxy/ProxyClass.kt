package xyz.srclab.common.proxy

import xyz.srclab.common.base.Current

/**
 * @author sunqian
 */
interface ProxyClass<T : Any> {

    fun newInstance(): T

    fun newInstance(parameterTypes: Array<Class<*>>?, args: Array<Any?>?): T

    //fun getProxyClass(): Class<T>

    companion object {

        @JvmStatic
        fun <T : Any> newProxyClass(proxyClass: Class<T>, proxyMethods: Iterable<ProxyMethod<T>>): ProxyClass<T> {
            return newProxyClass(Current.classLoader, proxyClass, proxyMethods)
        }

        @JvmStatic
        //@JvmOverloads
        fun <T : Any> newProxyClass(
            classLoader: ClassLoader = Current.classLoader,
            proxyClass: Class<T>,
            proxyMethods: Iterable<ProxyMethod<T>>
        ): ProxyClass<T> {
            return ProxyClassGenerator.DEFAULT.generate(classLoader, proxyClass, proxyMethods)
        }
    }
}