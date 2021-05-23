@file:JvmName("Reflects")
@file:JvmMultifileClass

package xyz.srclab.common.reflect

import xyz.srclab.common.lang.asAny
import java.lang.reflect.Method

/**
 * @throws NoSuchMethodException
 */
fun Class<*>.method(name: String, vararg parameterTypes: Class<*>): Method {
    return try {
        this.getMethod(name, *parameterTypes)
    } catch (e: NoSuchMethodException) {
        throw e
    }
}

fun Class<*>.methodOrNull(
    name: String, vararg parameterTypes: Class
    <*>
): Method? {
    return try {
        this.getMethod(name, *parameterTypes)
    } catch (e: NoSuchMethodException) {
        null
    }
}

fun Class<*>.methods(): List<Method> {
    return this.methods.asList()
}

/**
 * @throws NoSuchMethodException
 */
fun Class<*>.declaredMethod(name: String, vararg parameterTypes: Class<*>): Method {
    return try {
        this.getDeclaredMethod(name, *parameterTypes)
    } catch (e: NoSuchMethodException) {
        throw e
    }
}

fun Class<*>.declaredMethodOrNull(name: String, vararg parameterTypes: Class<*>): Method? {
    return try {
        this.getDeclaredMethod(name, *parameterTypes)
    } catch (e: NoSuchMethodException) {
        null
    }
}

fun Class<*>.declaredMethods(): List<Method> {
    return this.declaredMethods.asList()
}

/**
 * @throws NoSuchMethodException
 */
fun Class<*>.ownedMethod(name: String, vararg parameterTypes: Class<*>): Method {
    return ownedMethodOrNull(name, *parameterTypes) ?: throw NoSuchMethodException(
        "name: $name, parameters: ${parameterTypes.contentToString()}"
    )
}

fun Class<*>.ownedMethodOrNull(name: String, vararg parameterTypes: Class<*>): Method? {
    return searchMethodOrNull(name, parameterTypes, false)
}

fun Class<*>.ownedMethods(): List<Method> {
    val set = LinkedHashSet<Method>()
    set.addAll(this.methods())
    for (declaredMethod in this.declaredMethods()) {
        if (!set.contains(declaredMethod)) {
            set.add(declaredMethod)
        }
    }
    return set.toList()
}

@JvmOverloads
fun Class<*>.searchMethodOrNull(
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
    method = declaredMethodOrNull(name, *parameterTypes)
    if (method !== null) {
        return method
    }
    if (!deep) {
        return null
    }
    var superClass = this.superclass
    while (superClass !== null) {
        method = superClass.declaredMethodOrNull(name, *parameterTypes)
        if (method !== null) {
            return method
        }
        superClass = superClass.superclass
    }
    return null
}

@JvmOverloads
fun Class<*>.searchMethods(deep: Boolean = true, predicate: (Method) -> Boolean = { true }): List<Method> {
    val result = mutableListOf<Method>()
    for (method in this.methods) {
        if (predicate(method)) {
            result.add(method)
        }
    }
    for (method in this.declaredMethods) {
        if (!result.contains(method) && predicate(method)) {
            result.add(method)
        }
    }
    if (!deep) {
        return result
    }
    var superClass = this.superclass
    while (superClass !== null) {
        for (method in superClass.declaredMethods) {
            if (!result.contains(method) && predicate(method)) {
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
fun <T> Method.enforce(owner: Any?, vararg args: Any?): T {
    return try {
        this.isAccessible = true
        this.invoke(owner, *args).asAny()
    } catch (e: Exception) {
        throw e
    }
}

/**
 * @throws  IllegalAccessException
 * @throws  IllegalArgumentException
 * @throws  ReflectiveOperationException
 */
fun <T> Method.invokeStatic(vararg args: Any?): T {
    return try {
        this.invoke(null, *args).asAny()
    } catch (e: Exception) {
        throw e
    }
}

/**
 * @throws  IllegalArgumentException
 * @throws  ReflectiveOperationException
 */
fun <T> Method.enforceStatic(vararg args: Any?): T {
    return try {
        this.isAccessible = true
        this.invoke(null, *args).asAny()
    } catch (e: Exception) {
        throw e
    }
}

fun Method.canOverrideBy(clazz: Class<*>): Boolean {
    if (this.isPrivate || this.isFinal) {
        return false
    }
    if (!this.declaringClass.isAssignableFrom(clazz)) {
        return false
    }
    return true
}