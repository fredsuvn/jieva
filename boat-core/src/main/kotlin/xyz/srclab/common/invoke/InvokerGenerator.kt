package xyz.srclab.common.invoke

import xyz.srclab.common.reflect.declaredConstructorOrNull
import xyz.srclab.common.reflect.ownedMethodOrNull
import xyz.srclab.common.reflect.parameterTypesToString
import java.lang.reflect.Constructor
import java.lang.reflect.Method

/**
 * Generator to generate [Invoker].
 *
 * @see ReflectInvokerGenerator
 * @see MethodHandlerInvokerGenerator
 */
interface InvokerGenerator {

    fun ofMethod(method: Method): Invoker

    /**
     * @throws NoSuchMethodException
     */
    fun ofMethod(clazz: Class<*>, methodName: String, vararg parameterTypes: Class<*>): Invoker {
        val method = clazz.ownedMethodOrNull(methodName, *parameterTypes)
        if (method === null) {
            throw NoSuchMethodException("$clazz.$methodName(${parameterTypes.parameterTypesToString()})")
        }
        return ofMethod(method)
    }

    fun ofConstructor(constructor: Constructor<*>): Invoker

    /**
     * @throws NoSuchMethodException
     */
    fun ofConstructor(clazz: Class<*>, vararg parameterTypes: Class<*>): Invoker {
        val constructor = clazz.declaredConstructorOrNull(*parameterTypes)
        if (constructor === null) {
            throw NoSuchMethodException("$clazz(${parameterTypes.parameterTypesToString()})")
        }
        return ofConstructor(constructor)
    }
}