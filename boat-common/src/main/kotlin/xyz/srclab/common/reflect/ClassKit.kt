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
        throw IllegalArgumentException("Class constructor(${parameterTypes.contentToString()}) not found: $this")
    }
    return constructor.newInstance(*args).asAny()
}

fun Class<*>.toWrapperClass(): Class<*> {
    return when (this) {
        Boolean::class.javaPrimitiveType -> Boolean::class.java
        Byte::class.javaPrimitiveType -> Byte::class.java
        Short::class.javaPrimitiveType -> Short::class.java
        Char::class.javaPrimitiveType -> Char::class.java
        Int::class.javaPrimitiveType -> Int::class.java
        Long::class.javaPrimitiveType -> Long::class.java
        Float::class.javaPrimitiveType -> Float::class.java
        Double::class.javaPrimitiveType -> Double::class.java
        Void::class.javaPrimitiveType -> Void::class.java
        else -> this
    }
}