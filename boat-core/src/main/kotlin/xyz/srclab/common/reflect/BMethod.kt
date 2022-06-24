@file:JvmName("BMethod")

package xyz.srclab.common.reflect

import xyz.srclab.common.base.asType
import java.lang.reflect.Method
import java.util.function.Predicate

/**
 * Returns public method of [this] class.
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

/**
 * Returns public method of [this] class, or null if failed.
 */
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

/**
 * Returns all public methods of [this] class.
 */
@JvmName("getMethods")
fun Class<*>.methods(): List<Method> {
    return this.methods.asList()
}

/**
 * Returns declared method of [this] class.
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

/**
 * Returns declared method of [this] class, or null if failed.
 */
@JvmName("getDeclaredMethodOrNull")
fun Class<*>.declaredMethodOrNull(name: String, vararg parameterTypes: Class<*>): Method? {
    return try {
        this.getDeclaredMethod(name, *parameterTypes)
    } catch (e: NoSuchMethodException) {
        null
    }
}

/**
 * Returns all declared methods of [this] class.
 */
@JvmName("getDeclaredMethods")
fun Class<*>.declaredMethods(): List<Method> {
    return this.declaredMethods.asList()
}

/**
 * Returns public or declared method of [this] class.
 * @throws NoSuchMethodException
 */
@JvmName("getOwnedMethod")
fun Class<*>.ownedMethod(name: String, vararg parameterTypes: Class<*>): Method {
    return methodOrNull(name, *parameterTypes) ?: declaredMethod(name, *parameterTypes)
}

/**
 * Returns public or declared method of [this] class, or null if failed.
 */
@JvmName("getOwnedMethodOrNull")
fun Class<*>.ownedMethodOrNull(name: String, vararg parameterTypes: Class<*>): Method? {
    return methodOrNull(name, *parameterTypes) ?: declaredMethodOrNull(name, *parameterTypes)
}

/**
 * Returns all declared and public methods of [this] class.
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

/**
 * Searches and returns method for [this] class.
 *
 * @param name field name
 * @param deep whether recursively search to super class
 */
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

/**
 * Searches and returns methods for [this] class.
 *
 * @param deep whether recursively search to super class
 * @param predicate true if the field is matched and will be return
 */
@JvmOverloads
fun Class<*>.searchMethods(
    deep: Boolean = true,
    predicate: Predicate<Method> = Predicate { true }
): List<Method> {
    return searchMethods(ArrayList(), deep, predicate)
}

/**
 * Searches and returns methods for [this] class into [destination].
 *
 * @param deep whether recursively search to super class
 * @param predicate true if the field is matched and will be return
 */
@JvmOverloads
fun <C : MutableCollection<in Method>> Class<*>.searchMethods(
    destination: C,
    deep: Boolean = true,
    predicate: Predicate<Method> = Predicate { true }
): C {
    for (method in this.methods) {
        if (predicate.test(method)) {
            destination.add(method)
        }
    }
    for (method in this.declaredMethods) {
        if (!destination.contains(method) && predicate.test(method)) {
            destination.add(method)
        }
    }
    if (!deep) {
        return destination
    }
    var superClass = this.superclass
    while (superClass !== null) {
        for (method in superClass.declaredMethods) {
            if (!destination.contains(method) && predicate.test(method)) {
                destination.add(method)
            }
        }
        superClass = superClass.superclass
    }
    return destination
}

/**
 * Invokes [this] method.
 *
 * @throws  IllegalAccessException
 * @throws  IllegalArgumentException
 * @throws  ReflectiveOperationException
 */
fun <T> Method.invoke(owner: Any?, vararg args: Any?): T {
    return try {
        this.invoke(owner, *args).asType()
    } catch (e: Exception) {
        throw e
    }
}

/**
 * Invokes [this] method forcibly.
 *
 * @throws  IllegalArgumentException
 * @throws  ReflectiveOperationException
 */
fun <T> Method.invokeForcibly(owner: Any?, vararg args: Any?): T {
    return try {
        this.isAccessible = true
        this.invoke(owner, *args).asType()
    } catch (e: Exception) {
        throw e
    }
}

/**
 * Invokes [this] static method.
 *
 * @throws  IllegalAccessException
 * @throws  IllegalArgumentException
 * @throws  ReflectiveOperationException
 */
fun <T> Method.invokeStatic(vararg args: Any?): T {
    return try {
        this.invoke(null, *args).asType()
    } catch (e: Exception) {
        throw e
    }
}

/**
 * Invokes [this] static method forcibly.
 *
 * @throws  IllegalArgumentException
 * @throws  ReflectiveOperationException
 */
fun <T> Method.invokeStaticForcibly(vararg args: Any?): T {
    return try {
        this.isAccessible = true
        this.invoke(null, *args).asType()
    } catch (e: Exception) {
        throw e
    }
}