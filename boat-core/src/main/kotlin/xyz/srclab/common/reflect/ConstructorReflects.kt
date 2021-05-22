@file:JvmName("Reflects")
@file:JvmMultifileClass

package xyz.srclab.common.reflect

import xyz.srclab.common.lang.asAny
import java.lang.reflect.Constructor

/**
 * @throws NoSuchMethodException
 */
fun <T> Class<T>.constructor(vararg parameterTypes: Class<*>): Constructor<T> {
    return try {
        this.getConstructor(*parameterTypes)
    } catch (e: NoSuchMethodException) {
        throw e
    }
}

fun <T> Class<T>.constructorOrNull(vararg parameterTypes: Class<*>): Constructor<T>? {
    return try {
        this.getConstructor(*parameterTypes)
    } catch (e: NoSuchMethodException) {
        null
    }
}

fun <T> Class<T>.constructors(): List<Constructor<T>> {
    return this.constructors.asList().asAny()
}

/**
 * @throws NoSuchMethodException
 */
fun <T> Class<T>.declaredConstructor(vararg parameterTypes: Class<*>): Constructor<T> {
    return try {
        this.getDeclaredConstructor(*parameterTypes)
    } catch (e: NoSuchMethodException) {
        throw e
    }
}

fun <T> Class<T>.declaredConstructorOrNull(vararg parameterTypes: Class<*>): Constructor<T>? {
    return try {
        this.getDeclaredConstructor(*parameterTypes)
    } catch (e: NoSuchMethodException) {
        null
    }
}

fun <T> Class<T>.declaredConstructors(): List<Constructor<T>> {
    return this.declaredConstructors.asList().asAny()
}

fun <T> Constructor<T>.invoke(vararg args: Any?): T {
    return this.newInstance(*args)
}

fun <T> Constructor<T>.invokeForcibly(vararg args: Any?): T {
    this.isAccessible = true
    return this.newInstance(*args)
}