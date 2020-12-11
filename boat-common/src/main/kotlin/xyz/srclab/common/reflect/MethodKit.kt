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
    return searchMethod(name, parameterTypes, declared = true, deep = false)
}

fun Class<*>.findOwnedMethods(): List<Method> {
    val set = LinkedHashSet<Method>()
    set.addAll(this.findMethods())
    val declared = this.findDeclaredMethods()
    for (method in declared) {
        if (!set.contains(method)) {
            set.add(method)
        }
    }
    var superClass = this.superclass
    while (superClass !== null) {
        val superMethods = superClass.findDeclaredMethods()
        for (superMethod in superMethods) {
            if (superMethod.isProtected && !set.contains(superMethod)) {
                set.add(superMethod)
            }
        }
        superClass = superClass.superclass
    }
    return set.toList()
}

fun Class<*>.searchMethod(
    name: String,
    parameterTypes: Array<out Class<*>>,
    declared: Boolean,
    deep: Boolean,
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
        method = superClass.findDeclaredMethod(name, *parameterTypes)
        if (method !== null) {
            return method
        }
        superClass = superClass.superclass
    }
    return null
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