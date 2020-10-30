package xyz.srclab.common.proxy

interface ProxyClassGenerator {

    fun <T : Any> generate(proxyClass: Class<T>, proxyMethods: Collection<ProxyMethod<T>>): ProxyClass<T>

    companion object {

        @JvmField
        val DEFAULT: ProxyClassGenerator = null as ProxyClassGenerator
    }
}