/**
 * Constructor utilities.
 */
@file:JvmName("ConstructorBt")

package xyz.srclab.common.reflect

import xyz.srclab.common.base.asType
import java.lang.reflect.Constructor

/**
 * Returns public constructor of [this] class.
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

/**
 * Returns public constructor of [this] class, or null if failed.
 */
@JvmName("getConstructorOrNull")
fun <T> Class<T>.constructorOrNull(vararg parameterTypes: Class<*>): Constructor<T>? {
    return try {
        this.getConstructor(*parameterTypes)
    } catch (e: NoSuchMethodException) {
        null
    }
}

/**
 * Returns all public constructors of [this] class.
 * @throws NoSuchMethodException
 */
@JvmName("getConstructors")
fun <T> Class<T>.constructors(): List<Constructor<T>> {
    return this.constructors.asList().asType()
}

/**
 * Returns declared constructor of [this] class.
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

/**
 * Returns declared constructor of [this] class, or null if failed.
 */
@JvmName("getDeclaredConstructorOrNull")
fun <T> Class<T>.declaredConstructorOrNull(vararg parameterTypes: Class<*>): Constructor<T>? {
    return try {
        this.getDeclaredConstructor(*parameterTypes)
    } catch (e: NoSuchMethodException) {
        null
    }
}

/**
 * Returns all declared constructors of [this] class.
 */
@JvmName("getDeclaredConstructors")
fun <T> Class<T>.declaredConstructors(): List<Constructor<T>> {
    return this.declaredConstructors.asList().asType()
}

/**
 * Invokes [this] constructor.
 */
fun <T> Constructor<T>.invoke(vararg args: Any?): T {
    return this.newInstance(*args)
}

/**
 * Invokes [this] constructor forcibly.
 */
fun <T> Constructor<T>.invokeForcibly(vararg args: Any?): T {
    this.isAccessible = true
    return this.newInstance(*args)
}