@file:JvmName("Reflects")
@file:JvmMultifileClass

package xyz.srclab.common.reflect

import xyz.srclab.common.base.asAny
import java.lang.reflect.Method

/**
 * @throws NoSuchMethodException
 */
@JvmName("getMethod")
fun Class<*>.method(name: String, vararg parameterTypes: Class<*>): Method {
    return try {
        this.getMethod(name, *parameterTypes)
    } catch (e: NoSuchMethodException) {
        throw e
    }
}

@JvmName("getMethodOrNull")
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

@JvmName("getMethods")
fun Class<*>.methods(): List<Method> {
    return this.methods.asList()
}

/**
 * @throws NoSuchMethodException
 */
@JvmName("getDeclaredMethod")
fun Class<*>.declaredMethod(name: String, vararg parameterTypes: Class<*>): Method {
    return try {
        this.getDeclaredMethod(name, *parameterTypes)
    } catch (e: NoSuchMethodException) {
        throw e
    }
}

@JvmName("getDeclaredMethodOrNull")
fun Class<*>.declaredMethodOrNull(name: String, vararg parameterTypes: Class<*>): Method? {
    return try {
        this.getDeclaredMethod(name, *parameterTypes)
    } catch (e: NoSuchMethodException) {
        null
    }
}

@JvmName("getDeclaredMethods")
fun Class<*>.declaredMethods(): List<Method> {
    return this.declaredMethods.asList()
}

/**
 * Return public or declared method.
 *
 * @throws NoSuchMethodException
 */
@JvmName("getOwnedMethod")
fun Class<*>.ownedMethod(name: String, vararg parameterTypes: Class<*>): Method {
    return methodOrNull(name, *parameterTypes) ?: declaredMethod(name, *parameterTypes)
}

/**
 * Return public or declared method.
 */
@JvmName("getOwnedMethodOrNull")
fun Class<*>.ownedMethodOrNull(name: String, vararg parameterTypes: Class<*>): Method? {
    return methodOrNull(name, *parameterTypes) ?: declaredMethodOrNull(name, *parameterTypes)
}

/**
 * Return declared and public methods.
 */
@JvmName("getOwnedMethods")
fun Class<*>.ownedMethods(): List<Method> {
    val set = LinkedHashSet<Method>()
    set.addAll(this.declaredMethods)
    for (method in this.methods) {
        if (!set.contains(method)) {
            set.add(method)
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
    var method = ownedMethodOrNull(name, *parameterTypes)
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
fun <C : MutableCollection<in Method>> Class<*>.searchMethods(
    destination: C,
    deep: Boolean = true,
    predicate: (Method) -> Boolean = { true }
): C {
    for (method in this.methods) {
        if (predicate(method)) {
            destination.add(method)
        }
    }
    for (method in this.declaredMethods) {
        if (!destination.contains(method) && predicate(method)) {
            destination.add(method)
        }
    }
    if (!deep) {
        return destination
    }
    var superClass = this.superclass
    while (superClass !== null) {
        for (method in superClass.declaredMethods) {
            if (!destination.contains(method) && predicate(method)) {
                destination.add(method)
            }
        }
        superClass = superClass.superclass
    }
    return destination
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

fun Array<out Class<*>>.parameterTypesToString(): String {
    return this.joinToString { it.name }
}