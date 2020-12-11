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

fun <T> Class<T>.findDeclaredConstructors(): List<Constructor<T>> {
    return this.declaredConstructors.asList().asAny()
}

fun <T> Class<T>.findOwnedConstructor(vararg parameterTypes: Class<*>): Constructor<T>? {
    val tryPublic = this.findConstructor(*parameterTypes)
    return tryPublic ?: this.findDeclaredConstructor(*parameterTypes)
}

fun <T> Class<T>.findOwnedConstructors(): List<Constructor<T>> {
    val set = LinkedHashSet<Constructor<T>>()
    set.addAll(this.findConstructors())
    set.addAll(this.findDeclaredConstructors())
    return set.toList()
}

fun <T> Constructor<T>.invoke(vararg args: Any?): T {
    return this.newInstance(*args)
}

fun <T> Constructor<T>.invokeForcibly(vararg args: Any?): T {
    this.isAccessible = true
    return this.newInstance(*args)
}