@file:JvmName("ClassKit")
@file:JvmMultifileClass

package xyz.srclab.common.reflect

import org.apache.commons.lang3.ArrayUtils
import xyz.srclab.common.base.Current
import xyz.srclab.common.base.asAny
import xyz.srclab.common.base.findClass

@JvmOverloads
fun <T> CharSequence.classNameToInstance(classLoader: ClassLoader = Current.classLoader): T {
    val cls: Class<T>? = this.findClass(classLoader)
    if (cls === null) {
        throw IllegalArgumentException("Class not found: $this")
    }
    val constructor = cls.findConstructor()
    if (constructor === null) {
        throw IllegalArgumentException("Class constructor() not found: $this")
    }
    return constructor.newInstance()
}

fun <T> Class<*>.toInstance(): T {
    return this.toInstance(ArrayUtils.EMPTY_CLASS_ARRAY, ArrayUtils.EMPTY_OBJECT_ARRAY)
}

fun <T> Class<*>.toInstance(parameterTypes: Array<Class<*>>, arguments: Array<Any?>): T {
    val constructor = this.findConstructor(*parameterTypes)
    if (constructor === null) {
        throw IllegalArgumentException("Class constructor(${parameterTypes.contentToString()}) not found: $this")
    }
    return constructor.newInstance(*arguments).asAny()
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