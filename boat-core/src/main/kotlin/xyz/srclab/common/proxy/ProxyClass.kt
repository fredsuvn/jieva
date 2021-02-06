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