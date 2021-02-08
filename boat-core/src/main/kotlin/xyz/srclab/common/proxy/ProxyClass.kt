package xyz.srclab.common.proxy

import xyz.srclab.common.base.Current

/**
 * Proxy class.
 *
 * This class first try to use spring-cglib if it exists in current classpath,
 * if not, try cglib,
 * if not, use jdk-proxy.
 * @author sunqian
 *
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