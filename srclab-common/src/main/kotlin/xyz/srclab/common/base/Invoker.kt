package xyz.srclab.common.base

import xyz.srclab.common.reflect.invokeStatic
import xyz.srclab.common.reflect.invokeVirtual
import xyz.srclab.common.reflect.toInstance
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType
import java.lang.reflect.Constructor
import java.lang.reflect.Method

/**
 * @see [StaticInvoker]
 * @see [VirtualInvoker]
 * @see [InvokerProvider]
 * @see [ReflectedInvokerProvider]
 * @see [MethodHandlerInvokerProvider]
 */
interface Invoker {

    fun <T> invoke(vararg args: Any?): T

    companion object {

        @JvmStatic
        @JvmName("staticInvoker")
        fun Method.toStaticInvoker(): StaticInvoker {
            return StaticInvoker.forMethod(this)
        }

        @JvmStatic
        @JvmName("staticInvoker")
        fun Constructor<*>.toStaticInvoker(): StaticInvoker {
            return StaticInvoker.forConstructor(this)
        }

        @JvmStatic
        @JvmName("virtualInvoker")
        fun Method.toVirtualInvoker(): VirtualInvoker {
            return VirtualInvoker.forMethod(this)
        }
    }
}

interface StaticInvoker : Invoker {

    fun <T> invokeStatic(vararg args: Any?): T

    companion object {

        @JvmStatic
        fun forMethod(method: Method): StaticInvoker {
            return InvokerProvider.DEFAULT.staticInvoker(method)
        }

        @JvmStatic
        fun forConstructor(constructor: Constructor<*>): StaticInvoker {
            return InvokerProvider.DEFAULT.staticInvoker(constructor)
        }
    }
}

interface VirtualInvoker : Invoker {

    fun <T> invokeVirtual(owner: Any?, vararg args: Any?): T

    companion object {

        @JvmStatic
        fun forMethod(method: Method): VirtualInvoker {
            return InvokerProvider.DEFAULT.virtualInvoker(method)
        }
    }
}

interface InvokerProvider {

    fun staticInvoker(method: Method): StaticInvoker

    fun staticInvoker(constructor: Constructor<*>): StaticInvoker

    fun virtualInvoker(method: Method): VirtualInvoker

    companion object {

        @JvmField
        val DEFAULT: InvokerProvider = ReflectedInvokerProvider
    }
}

object ReflectedInvokerProvider : InvokerProvider {

    override fun staticInvoker(method: Method): StaticInvoker {
        return ReflectedStaticMethodInvoker(method)
    }

    override fun staticInvoker(constructor: Constructor<*>): StaticInvoker {
        return ReflectedConstructorInvoker(constructor)
    }

    override fun virtualInvoker(method: Method): VirtualInvoker {
        return ReflectedVirtualMethodInvoker(method)
    }
}

object MethodHandlerInvokerProvider : InvokerProvider {

    override fun staticInvoker(method: Method): StaticInvoker {
        return StaticMethodHandlerInvoker(method)
    }

    override fun staticInvoker(constructor: Constructor<*>): StaticInvoker {
        return StaticMethodHandlerInvoker(constructor)
    }

    override fun virtualInvoker(method: Method): VirtualInvoker {
        return VirtualMethodHandlerInvoker(method)
    }
}

private const val CHECK_VIRTUAL_ARGUMENTS_EMPTY_MESSAGE =
    "Arguments of virtual invoking cannot be empty, it contains at least a \"this\" object at first."

private class ReflectedStaticMethodInvoker(private val method: Method) : StaticInvoker {

    override fun <T> invokeStatic(vararg args: Any?): T {
        return method.invokeStatic(args = args)
    }

    override fun <T> invoke(vararg args: Any?): T {
        return invokeStatic(*args)
    }
}

private class ReflectedVirtualMethodInvoker(private val method: Method) : VirtualInvoker {

    override fun <T> invoke(vararg args: Any?): T {
        checkArgument(args.isNotEmpty(), CHECK_VIRTUAL_ARGUMENTS_EMPTY_MESSAGE)
        val owner = args[0]
        val arguments = args.copyOfRange(1, args.size)
        return invoke0(owner, *arguments)
    }

    override fun <T> invokeVirtual(owner: Any?, vararg args: Any?): T {
        return invoke0(owner, *args)
    }

    private fun <T> invoke0(owner: Any?, vararg args: Any?): T {
        return method.invokeVirtual(owner, args = args)
    }
}

private class ReflectedConstructorInvoker(private val constructor: Constructor<*>) : StaticInvoker {

    override fun <T> invokeStatic(vararg args: Any?): T {
        return constructor.toInstance(*args).asAny()
    }

    override fun <T> invoke(vararg args: Any?): T {
        return invokeStatic(*args)
    }
}

private class StaticMethodHandlerInvoker private constructor() : StaticInvoker {

    private lateinit var methodHandle: MethodHandle

    constructor(method: Method) : this() {
        methodHandle = findStaticMethodHandle(method)
    }

    constructor(constructor: Constructor<*>) : this() {
        methodHandle = findStaticMethodHandle(constructor)
    }

    override fun <T> invokeStatic(vararg args: Any?): T {
        return when (args.size) {
            0 -> methodHandle.invoke()
            1 -> methodHandle.invoke(args[0])
            2 -> methodHandle.invoke(args[0], args[1])
            3 -> methodHandle.invoke(args[0], args[1], args[2])
            4 -> methodHandle.invoke(args[0], args[1], args[2], args[3])
            5 -> methodHandle.invoke(args[0], args[1], args[2], args[3], args[4])
            6 -> methodHandle.invoke(args[0], args[1], args[2], args[3], args[4], args[5])
            7 -> methodHandle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6])
            8 -> methodHandle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7])
            else -> methodHandle.invokeWithArguments(*args)
        }.asAny()
    }

    override fun <T> invoke(vararg args: Any?): T {
        return invokeStatic(*args)
    }

    private fun findStaticMethodHandle(method: Method): MethodHandle {
        val methodType: MethodType = when (method.parameterCount) {
            0 -> MethodType.methodType(method.returnType)
            1 -> MethodType.methodType(method.returnType, method.parameterTypes[0])
            else -> MethodType.methodType(method.returnType, method.parameterTypes)
        }
        return MethodHandles.lookup().findStatic(method.declaringClass, method.name, methodType)
    }

    private fun findStaticMethodHandle(constructor: Constructor<*>): MethodHandle {
        val methodType: MethodType = when (constructor.parameterCount) {
            0 -> MethodType.methodType(Void.TYPE)
            1 -> MethodType.methodType(Void.TYPE, constructor.parameterTypes[0])
            else -> MethodType.methodType(Void.TYPE, constructor.parameterTypes)
        }
        return MethodHandles.lookup().findConstructor(constructor.declaringClass, methodType)
    }
}

private class VirtualMethodHandlerInvoker(method: Method) : VirtualInvoker {

    private var methodHandle = findMethodHandle(method)

    override fun <T> invoke(vararg args: Any?): T {
        checkArgument(args.isNotEmpty(), CHECK_VIRTUAL_ARGUMENTS_EMPTY_MESSAGE)
        val owner = args[0]
        val arguments = args.copyOfRange(1, args.size)
        return invoke0(owner, *arguments)
    }

    override fun <T> invokeVirtual(owner: Any?, vararg args: Any?): T {
        return invoke0(owner, *args)
    }

    private fun <T> invoke0(owner: Any?, vararg args: Any?): T {
        return when (args.size) {
            0 -> methodHandle.invoke(owner)
            1 -> methodHandle.invoke(owner, args[0])
            2 -> methodHandle.invoke(owner, args[0], args[1])
            3 -> methodHandle.invoke(owner, args[0], args[1], args[2])
            4 -> methodHandle.invoke(owner, args[0], args[1], args[2], args[3])
            5 -> methodHandle.invoke(owner, args[0], args[1], args[2], args[3], args[4])
            6 -> methodHandle.invoke(owner, args[0], args[1], args[2], args[3], args[4], args[5])
            7 -> methodHandle.invoke(owner, args[0], args[1], args[2], args[3], args[4], args[5], args[6])
            8 -> methodHandle.invoke(owner, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7])
            else -> methodHandle.invokeWithArguments(owner, *args)
        }.asAny()
    }

    private fun findMethodHandle(method: Method): MethodHandle {
        val methodType: MethodType = when (method.parameterCount) {
            0 -> MethodType.methodType(method.returnType)
            1 -> MethodType.methodType(method.returnType, method.parameterTypes[0])
            else -> MethodType.methodType(method.returnType, method.parameterTypes)
        }
        return MethodHandles.lookup().findVirtual(method.declaringClass, method.name, methodType)
    }
}