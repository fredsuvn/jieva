@file:JvmName("ClassKit")
@file:JvmMultifileClass

package xyz.srclab.common.reflect

import xyz.srclab.common.base.*

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
    val clazz: Class<T>? = this.findClass(classLoader)
    if (clazz === null) {
        return null
    }
    return clazz.toInstance(parameterTypes, args)
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
        Boolean::class.javaPrimitiveType -> JavaBoolean::class.java
        Byte::class.javaPrimitiveType -> JavaByte::class.java
        Short::class.javaPrimitiveType -> JavaShort::class.java
        Char::class.javaPrimitiveType -> JavaChar::class.java
        Int::class.javaPrimitiveType -> JavaInt::class.java
        Long::class.javaPrimitiveType -> JavaLong::class.java
        Float::class.javaPrimitiveType -> JavaFloat::class.java
        Double::class.javaPrimitiveType -> JavaDouble::class.java
        Void::class.javaPrimitiveType -> Void::class.java
        else -> this
    }
}