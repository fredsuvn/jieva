@file:JvmName("MethodKit")
@file:JvmMultifileClass

package xyz.srclab.common.reflect

import xyz.srclab.common.base.asAny
import java.lang.reflect.Method

@JvmOverloads
fun Class<*>.findMethod(
    name: String,
    declared: Boolean = false,
    deep: Boolean = false,
    vararg parameterTypes: Class<*>
): Method? {
    var method = try {
        this.getMethod(name, *parameterTypes)
    } catch (e: NoSuchMethodException) {
        null
    }
    if (method !== null) {
        return method
    }
    if (!declared) {
        return null
    }
    method = findDeclaredMethod(name, *parameterTypes)
    if (method !== null) {
        return method
    }
    if (!deep) {
        return null
    }
    var superClass = this.superclass
    while (superClass !== null) {
        method = findDeclaredMethod(name, *parameterTypes)
        if (method !== null) {
            return method
        }
        superClass = superclass.superclass
    }
    return null
}

fun Class<*>.findDeclaredMethod(methodName: String, vararg parameterTypes: Class<*>): Method? {
    return try {
        this.getDeclaredMethod(methodName, *parameterTypes)
    } catch (e: NoSuchMethodException) {
        null
    }
}

fun Class<*>.findOwnedMethod(name: String, vararg parameterTypes: Class<*>): Method? {
    return findMethod(name, declared = true, deep = false, *parameterTypes)
}

fun Class<*>.findMethods(): List<Method> {
    return this.methods.asList()
}

fun Class<*>.findDeclaredMethods(): List<Method> {
    return this.declaredMethods.asList()
}

fun Method.canOverrideBy(clazz: Class<*>): Boolean {
    if (this.isPrivate) {
        return false
    }
    if (!this.declaringClass.isAssignableFrom(clazz)) {
        return false
    }
    return true
}

/**
 * @throws  IllegalAccessException
 * @throws  IllegalArgumentException
 * @throws  ReflectiveOperationException
 */
fun <T> Method.invoke(owner: Any?, vararg args: Any?): T {
    return try {
        this.invoke(owner, *args).asAny()
    } catch (e: Exception) {
        throw e
    }
}

/**
 * @throws  IllegalArgumentException
 * @throws  ReflectiveOperationException
 */
fun <T> Method.invokeForcible(owner: Any?, vararg args: Any?): T {
    this.isAccessible = true
    return try {
        this.invoke(owner, *args).asAny()
    } catch (e: Exception) {
        throw IllegalStateException(e)
    }
}