package xyz.srclab.common.proxy

import xyz.srclab.common.reflect.defaultClassLoader

/**
 * Class proxy is used to proxy the class [T].
 *
 * @param T the class to be proxied
 * @see ClassProxyProvider
 * @see SpringProxyProvider
 * @see CglibProxyProvider
 * @see JdkProxyProvider
 */
interface ClassProxy<T : Any> {

    /**
     * Returns new instance which proxies the original instance of class [T].
     */
    fun newInst(): T

    /**
     * Returns new instance which proxies the original instance of class [T].
     *
     * @param parameterTypes parameter types
     * @param args arguments of [parameterTypes]
     */
    fun newInst(parameterTypes: Array<Class<*>>, args: Array<Any?>): T

    companion object {

        /**
         * Generates proxy class.
         */
        @JvmStatic
        @JvmOverloads
        fun <T : Any> generate(
            sourceClass: Class<T>,
            proxyInvoker: ProxyInvoker,
            classLoader: ClassLoader = defaultClassLoader(),
            proxyClassGenerator: ClassProxyProvider = ClassProxyProvider.defaultProvider(),
        ): ClassProxy<T> {
            return proxyClassGenerator.getProxy(sourceClass, proxyInvoker, classLoader)
        }
    }
}