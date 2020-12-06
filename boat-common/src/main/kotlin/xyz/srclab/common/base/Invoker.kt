package xyz.srclab.common.base

import xyz.srclab.common.reflect.findOwnerConstructor
import xyz.srclab.common.reflect.findOwnerMethod
import xyz.srclab.common.reflect.isStatic
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.lang.reflect.Constructor
import java.lang.reflect.Method

/**
 * @see [InvokerProvider]
 * @see [ReflectedInvokerProvider]
 * @see [MethodHandlerInvokerProvider]
 */
interface Invoker {

    fun <T> invoke(`object`: Any?, vararg args: Any?): T

    fun <T> invokeForcibly(`object`: Any?, vararg args: Any?): T

    companion object {

        @JvmStatic
        fun forMethod(clazz: Class<*>, methodName: String, vararg parameterTypes: Class<*>): Invoker {
            return InvokerProvider.DEFAULT.forMethod(clazz, methodName, *parameterTypes)
        }

        @JvmStatic
        fun forConstructor(clazz: Class<*>, vararg parameterTypes: Class<*>): Invoker {
            return InvokerProvider.DEFAULT.forConstructor(clazz, *parameterTypes)
        }

        @JvmStatic
        @JvmName("forMethod")
        fun Method.toInvoker(): Invoker {
            return InvokerProvider.DEFAULT.forMethod(this)
        }

        @JvmStatic
        @JvmName("forConstructor")
        fun Constructor<*>.toInvoker(): Invoker {
            return InvokerProvider.DEFAULT.forConstructor(this)
        }
    }
}

interface InvokerProvider {

    @JvmDefault
    fun forMethod(clazz: Class<*>, methodName: String, vararg parameterTypes: Class<*>): Invoker {
        val method = clazz.findOwnerMethod(methodName, *parameterTypes)
        return forMethod(
            method ?: throw NoSuchMethodException("$clazz.$methodName(${parameterTypes.contentToString()})")
        )
    }

    @JvmDefault
    fun forConstructor(clazz: Class<*>, vararg parameterTypes: Class<*>): Invoker {
        val constructor = clazz.findOwnerConstructor(*parameterTypes)
        return forConstructor(
            constructor ?: throw NoSuchMethodException("$clazz(${parameterTypes.contentToString()})")
        )
    }

    fun forMethod(method: Method): Invoker

    fun forConstructor(constructor: Constructor<*>): Invoker

    companion object {

        @JvmField
        val DEFAULT: InvokerProvider = ReflectedInvokerProvider
    }
}

object ReflectedInvokerProvider : InvokerProvider {

    override fun forMethod(method: Method): Invoker {
        return ReflectedMethodInvoker(method)
    }

    override fun forConstructor(constructor: Constructor<*>): Invoker {
        return ReflectedConstructorInvoker(constructor)
    }

    private class ReflectedMethodInvoker(private val method: Method) : Invoker {

        override fun <T> invoke(`object`: Any?, vararg args: Any?): T {
            return method.invoke(`object`, *args).asAny()
        }

        override fun <T> invokeForcibly(`object`: Any?, vararg args: Any?): T {
            method.isAccessible = true
            return method.invoke(`object`, *args).asAny()
        }
    }

    private class ReflectedConstructorInvoker(private val constructor: Constructor<*>) : Invoker {

        override fun <T> invoke(`object`: Any?, vararg args: Any?): T {
            return constructor.newInstance(*args).asAny()
        }

        override fun <T> invokeForcibly(`object`: Any?, vararg args: Any?): T {
            constructor.isAccessible = true
            return constructor.newInstance(*args).asAny()
        }
    }
}

object MethodHandlerInvokerProvider : InvokerProvider {

    override fun forMethod(method: Method): Invoker {
        return if (method.isStatic) StaticMethodHandlerInvoker(method) else VirtualMethodHandlerInvoker(method)
    }

    override fun forConstructor(constructor: Constructor<*>): Invoker {
        return StaticMethodHandlerInvoker(constructor)
    }

    private class StaticMethodHandlerInvoker() : Invoker {

        private lateinit var methodHandle: MethodHandle

        constructor(method: Method) : this() {
            methodHandle = findStaticMethodHandle(method)
        }

        constructor(constructor: Constructor<*>) : this() {
            methodHandle = findStaticMethodHandle(constructor)
        }

        override fun <T> invoke(`object`: Any?, vararg args: Any?): T {
            return when (args.size) {
                0 -> methodHandle.invoke()
                1 -> methodHandle.invoke(
                    args[0]
                )
                2 -> methodHandle.invoke(
                    args[0], args[1]
                )
                3 -> methodHandle.invoke(
                    args[0], args[1], args[2]
                )
                4 -> methodHandle.invoke(
                    args[0], args[1], args[2], args[3]
                )
                5 -> methodHandle.invoke(
                    args[0], args[1], args[2], args[3], args[4]
                )
                6 -> methodHandle.invoke(
                    args[0], args[1], args[2], args[3], args[4], args[5]
                )
                7 -> methodHandle.invoke(
                    args[0], args[1], args[2], args[3], args[4], args[5], args[6]
                )
                8 -> methodHandle.invoke(
                    args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7]
                )
                9 -> methodHandle.invoke(
                    args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8]
                )
                10 -> methodHandle.invoke(
                    args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9],
                )
                11 -> methodHandle.invoke(
                    args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10],
                )
                12 -> methodHandle.invoke(
                    args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10],
                    args[11],
                )
                13 -> methodHandle.invoke(
                    args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10],
                    args[11], args[12],
                )
                14 -> methodHandle.invoke(
                    args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10],
                    args[11], args[12], args[13],
                )
                15 -> methodHandle.invoke(
                    args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10],
                    args[11], args[12], args[13], args[14],
                )
                16 -> methodHandle.invoke(
                    args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10],
                    args[11], args[12], args[13], args[14], args[15],
                )
                else -> methodHandle.invokeWithArguments(*args)
            }.asAny()
        }

        override fun <T> invokeForcibly(`object`: Any?, vararg args: Any?): T {
            return invoke(`object`, *args)
        }

        private fun findStaticMethodHandle(method: Method): MethodHandle {
            //val methodType: MethodType = when (method.parameterCount) {
            //    0 -> MethodType.methodType(method.returnType)
            //    else -> MethodType.methodType(method.returnType, method.parameterTypes)
            //}
            //return MethodHandles.lookup().findStatic(method.declaringClass, method.name, methodType)
            method.isAccessible = true
            return MethodHandles.lookup().unreflect(method)
        }

        private fun findStaticMethodHandle(constructor: Constructor<*>): MethodHandle {
            //val methodType: MethodType = when (constructor.parameterCount) {
            //    0 -> MethodType.methodType(Void::class.javaPrimitiveType)
            //    else -> MethodType.methodType(Void::class.javaPrimitiveType, constructor.parameterTypes)
            //}
            //if (constructor.isPublic) {
            //    return MethodHandles.lookup().findConstructor(constructor.declaringClass, methodType)
            //} else {
            //    constructor.isAccessible = true
            //    return MethodHandles.lookup().findSpecial(
            //        constructor.declaringClass, "<init>", methodType, constructor.declaringClass
            //    )
            //}
            constructor.isAccessible = true
            return MethodHandles.lookup().unreflectConstructor(constructor)
        }
    }

    private class VirtualMethodHandlerInvoker(method: Method) : Invoker {

        private var methodHandle = findMethodHandle(method)

        override fun <T> invoke(`object`: Any?, vararg args: Any?): T {
            return when (args.size) {
                0 -> methodHandle.invoke(
                    `object`
                )
                1 -> methodHandle.invoke(
                    `object`, args[0]
                )
                2 -> methodHandle.invoke(
                    `object`, args[0], args[1]
                )
                3 -> methodHandle.invoke(
                    `object`, args[0], args[1], args[2]
                )
                4 -> methodHandle.invoke(
                    `object`, args[0], args[1], args[2], args[3]
                )
                5 -> methodHandle.invoke(
                    `object`, args[0], args[1], args[2], args[3], args[4]
                )
                6 -> methodHandle.invoke(
                    `object`, args[0], args[1], args[2], args[3], args[4], args[5]
                )
                7 -> methodHandle.invoke(
                    `object`, args[0], args[1], args[2], args[3], args[4], args[5], args[6]
                )
                8 -> methodHandle.invoke(
                    `object`, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7]
                )
                9 -> methodHandle.invoke(
                    `object`, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8]
                )
                10 -> methodHandle.invoke(
                    `object`, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9]
                )
                11 -> methodHandle.invoke(
                    `object`, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9],
                    args[10]
                )
                12 -> methodHandle.invoke(
                    `object`, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9],
                    args[10], args[11]
                )
                13 -> methodHandle.invoke(
                    `object`, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9],
                    args[10], args[11], args[12]
                )
                14 -> methodHandle.invoke(
                    `object`, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9],
                    args[10], args[11], args[12], args[13]
                )
                15 -> methodHandle.invoke(
                    `object`, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9],
                    args[10], args[11], args[12], args[13], args[14]
                )
                16 -> methodHandle.invoke(
                    `object`, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9],
                    args[10], args[11], args[12], args[13], args[14], args[15]
                )
                else -> methodHandle.invokeWithArguments(`object`, *args)
            }.asAny()
        }

        override fun <T> invokeForcibly(`object`: Any?, vararg args: Any?): T {
            return invoke(`object`, *args)
        }

        private fun findMethodHandle(method: Method): MethodHandle {
            //val methodType: MethodType = when (method.parameterCount) {
            //    0 -> MethodType.methodType(method.returnType)
            //    else -> MethodType.methodType(method.returnType, method.parameterTypes)
            //}
            //return MethodHandles.lookup().findVirtual(method.declaringClass, method.name, methodType)
            method.isAccessible = true
            return MethodHandles.lookup().unreflect(method)
        }
    }
}