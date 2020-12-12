@file:JvmName("MethodKit")
@file:JvmMultifileClass

package xyz.srclab.common.reflect

import xyz.srclab.common.base.asAny
import java.lang.reflect.Method

fun Class<*>.findMethod(name: String, vararg parameterTypes: Class<*>): Method? {
    return try {
        this.getMethod(name, *parameterTypes)
    } catch (e: NoSuchMethodException) {
        null
    }
}

fun Class<*>.findMethods(): List<Method> {
    return this.methods.asList()
}

fun Class<*>.findDeclaredMethod(name: String, vararg parameterTypes: Class<*>): Method? {
    return try {
        this.getDeclaredMethod(name, *parameterTypes)
    } catch (e: NoSuchMethodException) {
        null
    }
}

fun Class<*>.findDeclaredMethods(): List<Method> {
    return this.declaredMethods.asList()
}

fun Class<*>.findOwnedMethod(name: String, vararg parameterTypes: Class<*>): Method? {
    return this.searchMethod(name, parameterTypes, false)
}

fun Class<*>.findOwnedMethods(): List<Method> {
    val set = LinkedHashSet<Method>()
    set.addAll(this.findMethods())
    for (declaredMethod in this.findDeclaredMethods()) {
        if (!set.contains(declaredMethod)) {
            set.add(declaredMethod)
        }
    }
    return set.toList()
}

@JvmOverloads
fun Class<*>.searchMethod(
    name: String,
    parameterTypes: Array<out Class<*>>,
    deep: Boolean = true,
): Method? {
    var method = try {
        this.getMethod(name, *parameterTypes)
    } catch (e: NoSuchMethodException) {
        null
    }
    if (method !== null) {
        return method
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
        method = superClass.findDeclaredMethod(name, *parameterTypes)
        if (method !== null) {
            return method
        }
        superClass = superClass.superclass
    }
    return null
}

@JvmOverloads
fun Class<*>.searchMethods(deep: Boolean = true, predicate: (Method) -> Boolean): List<Method> {
    val result = mutableListOf<Method>()
    for (method in this.methods) {
        if (predicate(method)) {
            result.add(method)
        }
    }
    for (method in this.declaredMethods) {
        if (predicate(method)) {
            result.add(method)
        }
    }
    if (!deep) {
        return result
    }
    var superClass = this.superclass
    while (superClass !== null) {
        for (method in superClass.declaredMethods) {
            if (predicate(method)) {
                result.add(method)
            }
        }
        superClass = superClass.superclass
    }
    return result
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

fun Method.canOverrideBy(clazz: Class<*>): Boolean {
    if (this.isPrivate) {
        return false
    }
    if (!this.declaringClass.isAssignableFrom(clazz)) {
        return false
    }
    return true
}