@file:JvmName("ConstructorKit")
@file:JvmMultifileClass

package xyz.srclab.common.reflect

import xyz.srclab.common.base.asAny
import java.lang.reflect.Constructor
import java.lang.reflect.Modifier

val Constructor<*>.isOpen: Boolean
    @JvmName("isOpen") get() {
        val modifiers = this.modifiers
        if (Modifier.isStatic(modifiers)
            || Modifier.isPrivate(modifiers)
            || Modifier.isFinal(modifiers)
        ) {
            return false
        }
        return Modifier.isPublic(modifiers)
    }

val Constructor<*>.isPublic: Boolean
    @JvmName("isPublic") get() {
        return Modifier.isPublic(this.modifiers)
    }

val Constructor<*>.isPrivate: Boolean
    @JvmName("isPrivate") get() {
        return Modifier.isPrivate(this.modifiers)
    }

val Constructor<*>.isProtected: Boolean
    @JvmName("isProtected") get() {
        return Modifier.isProtected(this.modifiers)
    }

val Constructor<*>.isStatic: Boolean
    @JvmName("isStatic") get() {
        return Modifier.isStatic(this.modifiers)
    }

val Constructor<*>.isFinal: Boolean
    @JvmName("isFinal") get() {
        return Modifier.isFinal(this.modifiers)
    }

val Constructor<*>.isSynchronized: Boolean
    @JvmName("isSynchronized") get() {
        return Modifier.isSynchronized(this.modifiers)
    }

val Constructor<*>.isVolatile: Boolean
    @JvmName("isVolatile") get() {
        return Modifier.isVolatile(this.modifiers)
    }

val Constructor<*>.isTransient: Boolean
    @JvmName("isTransient") get() {
        return Modifier.isTransient(this.modifiers)
    }

val Constructor<*>.isNative: Boolean
    @JvmName("isNative") get() {
        return Modifier.isNative(this.modifiers)
    }

val Constructor<*>.isInterface: Boolean
    @JvmName("isInterface") get() {
        return Modifier.isInterface(this.modifiers)
    }

val Constructor<*>.isAbstract: Boolean
    @JvmName("isAbstract") get() {
        return Modifier.isAbstract(this.modifiers)
    }

val Constructor<*>.isStrict: Boolean
    @JvmName("isStrict") get() {
        return Modifier.isStrict(this.modifiers)
    }

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

fun <T> Constructor<T>.toInstance(vararg args: Any?): T {
    return this.newInstance(*args)
}