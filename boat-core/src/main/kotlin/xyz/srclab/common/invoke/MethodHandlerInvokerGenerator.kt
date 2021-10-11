package xyz.srclab.common.invoke

import xyz.srclab.common.base.asAny
import xyz.srclab.common.reflect.isStatic
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.lang.reflect.Constructor
import java.lang.reflect.Method

/**
 * [InvokerGenerator] by [MethodHandle].
 *
 * Note this generator will always set `accessible` to `true`, means it will never throw an [IllegalAccessException].
 */
object MethodHandlerInvokerGenerator : InvokerGenerator {

    override fun ofMethod(method: Method): Invoker {
        return if (method.isStatic) StaticMethodHandlerInvoker(method) else VirtualMethodHandlerInvoker(method)
    }

    override fun ofConstructor(constructor: Constructor<*>): Invoker {
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

        override fun <T> doInvoke(obj: Any?, force: Boolean, vararg args: Any?): T {
            //return doInvoke0(methodHandle, *args)
            return doInvoke0(methodHandle, args.toList())
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

        override fun <T> doInvoke(obj: Any?, force: Boolean, vararg args: Any?): T {
            //val args0: Array<Any?> = arrayOfNulls(args.size + 1)
            //args0[0] = obj
            //System.arraycopy(args, 0, args0, 1, args.size)
            //return doInvoke0(methodHandle, *args0)
            val list: MutableList<Any?> = ArrayList(args.size + 1)
            list.add(obj)
            list.addAll(args)
            return doInvoke0(methodHandle, list)
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

    private fun <T> doInvoke0(methodHandle: MethodHandle, args: List<Any?>): T {
        return methodHandle.invokeWithArguments(args).asAny()
    }

    //private fun <T> doInvoke0(methodHandle: MethodHandle, vararg args: Any?): T {
    //    return when (args.size) {
    //        0 -> methodHandle.invoke()
    //        1 -> methodHandle.invoke(
    //            args[0]
    //        )
    //        2 -> methodHandle.invoke(
    //            args[0], args[1]
    //        )
    //        3 -> methodHandle.invoke(
    //            args[0], args[1], args[2]
    //        )
    //        4 -> methodHandle.invoke(
    //            args[0], args[1], args[2], args[3]
    //        )
    //        5 -> methodHandle.invoke(
    //            args[0], args[1], args[2], args[3], args[4]
    //        )
    //        6 -> methodHandle.invoke(
    //            args[0], args[1], args[2], args[3], args[4], args[5]
    //        )
    //        7 -> methodHandle.invoke(
    //            args[0], args[1], args[2], args[3], args[4], args[5], args[6]
    //        )
    //        8 -> methodHandle.invoke(
    //            args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7]
    //        )
    //        9 -> methodHandle.invoke(
    //            args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8]
    //        )
    //        10 -> methodHandle.invoke(
    //            args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9],
    //        )
    //        11 -> methodHandle.invoke(
    //            args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10],
    //        )
    //        12 -> methodHandle.invoke(
    //            args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10],
    //            args[11],
    //        )
    //        13 -> methodHandle.invoke(
    //            args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10],
    //            args[11], args[12],
    //        )
    //        14 -> methodHandle.invoke(
    //            args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10],
    //            args[11], args[12], args[13],
    //        )
    //        15 -> methodHandle.invoke(
    //            args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10],
    //            args[11], args[12], args[13], args[14],
    //        )
    //        16 -> methodHandle.invoke(
    //            args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10],
    //            args[11], args[12], args[13], args[14], args[15],
    //        )
    //        else -> throw IllegalStateException("Too many arguments: ${args.size}. Max is 16.")
    //    }.asAny()
    //}
}