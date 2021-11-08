@file:JvmName("Reflects")
@file:JvmMultifileClass

package xyz.srclab.common.reflect

import xyz.srclab.common.base.asTyped
import java.lang.reflect.Constructor

/**
 * @throws NoSuchMethodException
 */
@JvmName("getConstructor")
fun <T> Class<T>.constructor(vararg parameterTypes: Class<*>): Constructor<T> {
    return try {
        this.getConstructor(*parameterTypes)
    } catch (e: NoSuchMethodException) {
        throw e
    }
}

@JvmName("getConstructorOrNull")
fun <T> Class<T>.constructorOrNull(vararg parameterTypes: Class<*>): Constructor<T>? {
    return try {
        this.getConstructor(*parameterTypes)
    } catch (e: NoSuchMethodException) {
        null
    }
}

@JvmName("getConstructors")
fun <T> Class<T>.constructors(): List<Constructor<T>> {
    return this.constructors.asList().asTyped()
}

/**
 * @throws NoSuchMethodException
 */
@JvmName("getDeclaredConstructor")
fun <T> Class<T>.declaredConstructor(vararg parameterTypes: Class<*>): Constructor<T> {
    return try {
        this.getDeclaredConstructor(*parameterTypes)
    } catch (e: NoSuchMethodException) {
        throw e
    }
}

@JvmName("getDeclaredConstructorOrNull")
fun <T> Class<T>.declaredConstructorOrNull(vararg parameterTypes: Class<*>): Constructor<T>? {
    return try {
        this.getDeclaredConstructor(*parameterTypes)
    } catch (e: NoSuchMethodException) {
        null
    }
}

@JvmName("getDeclaredConstructors")
fun <T> Class<T>.declaredConstructors(): List<Constructor<T>> {
    return this.declaredConstructors.asList().asTyped()
}

fun <T> Constructor<T>.invoke(vararg args: Any?): T {
    return this.newInstance(*args)
}

fun <T> Constructor<T>.enforce(vararg args: Any?): T {
    this.isAccessible = true
    return this.newInstance(*args)
}