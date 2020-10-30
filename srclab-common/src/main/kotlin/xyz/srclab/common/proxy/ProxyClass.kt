package xyz.srclab.common.proxy

import xyz.srclab.common.base.CachingProductBuilder
import xyz.srclab.common.base.asNotNull
import java.util.*

/**
 * @author sunqian
 */
interface ProxyClass<T : Any> {

    fun newInstance(): T

    fun newInstance(parameterTypes: Array<Class<*>>?, args: Array<Any?>?): T

    //fun getProxyClass(): Class<T>

    class Builder<T : Any>(private val proxyClass: Class<T>) : CachingProductBuilder<ProxyClass<T>>() {

        private var proxyMethods: MutableList<ProxyMethod<T>>? = null

        fun addProxyMethod(proxyMethod: ProxyMethod<T>) = apply {
            notNullProxyMethods().add(proxyMethod)
            commitChange()
        }

        fun addProxyMethods(vararg proxyMethods: ProxyMethod<T>) = apply {
            notNullProxyMethods().addAll(proxyMethods)
            commitChange()
        }

        fun addProxyMethods(proxyMethods: Iterable<ProxyMethod<T>>) = apply {
            notNullProxyMethods().addAll(proxyMethods)
            commitChange()
        }

        override fun buildNew(): ProxyClass<T> {
            return ProxyClassGenerator.DEFAULT.generate(proxyClass, proxyMethods())
        }

        private fun notNullProxyMethods(): MutableList<ProxyMethod<T>> {
            if (proxyMethods === null) {
                proxyMethods = LinkedList()
            }
            return proxyMethods.asNotNull()
        }

        private fun proxyMethods(): List<ProxyMethod<T>> {
            val hs = proxyMethods
            if (hs === null) {
                return emptyList()
            }
            return hs.toList()
        }
    }
}