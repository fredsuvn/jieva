@file:JvmName("MethodKit")
@file:JvmMultifileClass

package xyz.srclab.common.reflect

import xyz.srclab.common.base.asAny
import java.lang.reflect.Method
import java.lang.reflect.Modifier

val Method.isOpen: Boolean
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

val Method.isPublic: Boolean
    @JvmName("isPublic") get() {
        return Modifier.isPublic(this.modifiers)
    }

val Method.isPrivate: Boolean
    @JvmName("isPrivate") get() {
        return Modifier.isPrivate(this.modifiers)
    }

val Method.isProtected: Boolean
    @JvmName("isProtected") get() {
        return Modifier.isProtected(this.modifiers)
    }

val Method.isStatic: Boolean
    @JvmName("isStatic") get() {
        return Modifier.isStatic(this.modifiers)
    }

val Method.isFinal: Boolean
    @JvmName("isFinal") get() {
        return Modifier.isFinal(this.modifiers)
    }

val Method.isSynchronized: Boolean
    @JvmName("isSynchronized") get() {
        return Modifier.isSynchronized(this.modifiers)
    }

val Method.isVolatile: Boolean
    @JvmName("isVolatile") get() {
        return Modifier.isVolatile(this.modifiers)
    }

val Method.isTransient: Boolean
    @JvmName("isTransient") get() {
        return Modifier.isTransient(this.modifiers)
    }

val Method.isNative: Boolean
    @JvmName("isNative") get() {
        return Modifier.isNative(this.modifiers)
    }

val Method.isInterface: Boolean
    @JvmName("isInterface") get() {
        return Modifier.isInterface(this.modifiers)
    }

val Method.isAbstract: Boolean
    @JvmName("isAbstract") get() {
        return Modifier.isAbstract(this.modifiers)
    }

val Method.isStrict: Boolean
    @JvmName("isStrict") get() {
        return Modifier.isStrict(this.modifiers)
    }

@JvmOverloads
fun Class<*>.findMethod(
    name: String,
    declared: Boolean = false,
    deep: Boolean = false,
    vararg parameterTypes: Class<*>
): Method? {
    var method = try {
        this.getMethod(name, *parameterTypes)
    } catch (e: NoSuchFieldException) {
        null
    }
    if (method !== null) {
        return method
    }
    if (!declared) {
        return null
    }
    method = this.findDeclaredMethod(name, *parameterTypes)
    if (method !== null) {
        return method
    }
    if (!deep) {
        return null
    }
    var superClass = this.superclass
    while (superClass !== null) {
        method = this.findDeclaredMethod(name, *parameterTypes)
        if (method !== null) {
            return method
        }
        superClass = superclass.superclass
    }
    return null
}

fun Class<*>.findDeclaredMethod(methodName: String, vararg parameterTypes: Class<*>): Method? {
    return try {
        this.getDeclaredMethod(methodName, *parameterTypes)
    } catch (e: NoSuchMethodException) {
        null
    }
}

fun Class<*>.findMethods(): List<Method> {
    return this.methods.asList()
}

fun Class<*>.findDeclaredMethods(): List<Method> {
    return this.declaredMethods.asList()
}

@JvmOverloads
fun <T> Method.invokeStatic(force: Boolean = false, vararg args: Any?): T {
    return try {
        if (force) {
            this.isAccessible = true
        }
        this.invoke(null, *args).asAny()
    } catch (e: Exception) {
        throw IllegalStateException(e)
    }
}

@JvmOverloads
fun <T> Method.invokeVirtual(owner: Any? = null, force: Boolean = false, vararg args: Any?): T {
    return try {
        if (force) {
            this.isAccessible = true
        }
        this.invoke(owner, *args).asAny()
    } catch (e: Exception) {
        throw IllegalStateException(e)
    }
}

fun Method.isOpenFor(clazz: Class<*>): Boolean {
    if (!this.isOpen) {
        return false
    }
    val declaring = this.declaringClass
    return if (Modifier.isProtected(modifiers)) {
        declaring.isAssignableFrom(clazz)
    } else declaring.getPackage() == clazz.getPackage()
}