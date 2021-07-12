package xyz.srclab.common.proxy

import xyz.srclab.common.lang.Current

/**
 * Proxy class to create proxy instance.
 *
 * @see ProxyClassFactory
 * @see SpringProxyClassFactory
 * @see CglibProxyClassFactory
 * @see JdkProxyClassFactory
 */
interface ProxyClass<T : Any> {

    fun newInstance(): T

    fun newInstance(parameterTypes: Array<Class<*>>?, args: Array<Any?>?): T

    //fun getProxyClass(): Class<T>

    companion object {

        @JvmStatic
        @JvmOverloads
        fun <T : Any> newProxyClass(
            proxiedClass: Class<T>,
            proxiedMethods: Iterable<ProxyMethod<T>>,
            classLoader: ClassLoader = Current.classLoader,
            proxyClassFactory: ProxyClassFactory = ProxyClassFactory.DEFAULT,
        ): ProxyClass<T> {
            return proxyClassFactory.create(proxiedClass, proxiedMethods, classLoader)
        }
    }
}