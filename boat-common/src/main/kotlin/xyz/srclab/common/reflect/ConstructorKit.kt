@file:JvmName("ConstructorKit")
@file:JvmMultifileClass

package xyz.srclab.common.reflect

import xyz.srclab.common.base.asAny
import java.lang.reflect.Constructor

fun <T> Class<T>.findConstructor(vararg parameterTypes: Class<*>): Constructor<T>? {
    return try {
        this.getConstructor(*parameterTypes)
    } catch (e: NoSuchMethodException) {
        null
    }
}

fun <T> Class<T>.findConstructors(): List<Constructor<T>> {
    return this.constructors.asList().asAny()
}

fun <T> Class<T>.findDeclaredConstructor(vararg parameterTypes: Class<*>): Constructor<T>? {
    return try {
        this.getDeclaredConstructor(*parameterTypes)
    } catch (e: NoSuchMethodException) {
        null
    }
}

fun <T> Class<T>.findOwnerConstructor(vararg parameterTypes: Class<*>): Constructor<T>? {
    val tryPublic = findConstructor(*parameterTypes)
    return tryPublic ?: findDeclaredConstructor(*parameterTypes)
}

fun <T> Class<T>.findDeclaredConstructors(): List<Constructor<T>> {
    return this.declaredConstructors.asList().asAny()
}

fun <T> Constructor<T>.invoke(vararg args: Any?): T {
    return this.newInstance(*args)
}