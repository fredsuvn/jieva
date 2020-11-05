@file:JvmName("ClassKit")
@file:JvmMultifileClass

package xyz.srclab.common.reflect

import xyz.srclab.common.base.Current
import xyz.srclab.common.base.asAny

@JvmOverloads
fun <T> CharSequence.findClass(classLoader: ClassLoader = Current.classLoader): Class<T>? {
    return try {
        Class.forName(this.toString(), true, classLoader)
    } catch (e: ClassNotFoundException) {
        null
    }.asAny()
}

@JvmOverloads
fun <T> CharSequence.findClassToInstance(classLoader: ClassLoader = Current.classLoader): T? {
    return this.findClassToInstance(classLoader, emptyArray(), emptyArray())
}

@JvmOverloads
fun <T> CharSequence.findClassToInstance(
    classLoader: ClassLoader = Current.classLoader,
    parameterTypes: Array<out Class<*>>,
    args: Array<out Any?>
): T? {
    val cls: Class<T>? = this.findClass(classLoader)
    if (cls === null) {
        return null
    }
    return cls.toInstance(parameterTypes, args)
}

fun <T> Class<*>.toInstance(): T {
    return this.toInstance(emptyArray(), emptyArray())
}

fun <T> Class<*>.toInstance(parameterTypes: Array<out Class<*>>, args: Array<out Any?>): T {
    val constructor = this.findConstructor(*parameterTypes)
    if (constructor === null) {
        throw IllegalArgumentException("Constructor of $this with parameters (${parameterTypes.contentToString()}) not found: $this")
    }
    return constructor.newInstance(*args).asAny()
}

fun Class<*>.toWrapperClass(): Class<*> {
    return when (this) {
        Boolean::class.javaPrimitiveType -> java.lang.Boolean::class.java
        Byte::class.javaPrimitiveType -> java.lang.Byte::class.java
        Short::class.javaPrimitiveType -> java.lang.Short::class.java
        Char::class.javaPrimitiveType -> java.lang.Character::class.java
        Int::class.javaPrimitiveType -> java.lang.Integer::class.java
        Long::class.javaPrimitiveType -> java.lang.Long::class.java
        Float::class.javaPrimitiveType -> java.lang.Float::class.java
        Double::class.javaPrimitiveType -> java.lang.Double::class.java
        Void::class.javaPrimitiveType -> Void::class.java
        else -> this
    }
}