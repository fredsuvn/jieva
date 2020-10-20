@file:JvmName("MethodKit")
@file:JvmMultifileClass

package xyz.srclab.common.reflect

import java.lang.reflect.Method
import java.lang.reflect.Modifier

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
    if (method === null && !declared) {
        return null
    }
    method = this.findDeclaredMethod(name, *parameterTypes)
    if (method === null && !deep) {
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
fun Method.invoke(owner: Any? = null, force: Boolean = false, vararg args: Any?): Any {
    return try {
        if (force) {
            this.isAccessible = true
        }
        this.invoke(owner, *args)
    } catch (e: Exception) {
        throw IllegalStateException(e)
    }
}

fun Method.isOpen(): Boolean {
    val modifiers = this.modifiers
    if (Modifier.isStatic(modifiers)
        || Modifier.isPrivate(modifiers)
        || Modifier.isFinal(modifiers)
    ) {
        return false
    }
    return Modifier.isPublic(modifiers)
}

fun Method.isOpenFor(cls: Class<*>): Boolean {
    if (!this.isOpen()) {
        return false
    }
    val declaring = this.declaringClass
    return if (Modifier.isProtected(modifiers)) {
        declaring.isAssignableFrom(cls)
    } else declaring.getPackage() == cls.getPackage()
}