@file:JvmName("BClass")

package xyz.srclab.common.reflect

import org.apache.commons.lang3.ArrayUtils
import xyz.srclab.common.base.BytesClassLoader
import xyz.srclab.common.base.DOT_MATCHER
import xyz.srclab.common.base.asTyped
import xyz.srclab.common.base.currentThread
import java.lang.reflect.Modifier

val Class<*>.isStatic: Boolean
    get() {
        return Modifier.isStatic(this.modifiers)
    }

val Class<*>.isBooleanType: Boolean
    get() {
        return this == Boolean::class.javaPrimitiveType || this == Boolean::class.javaObjectType
    }

val Class<*>.isByteType: Boolean
    get() {
        return this == Byte::class.javaPrimitiveType || this == Byte::class.javaObjectType
    }

val Class<*>.isShortType: Boolean
    get() {
        return this == Short::class.javaPrimitiveType || this == Short::class.javaObjectType
    }

val Class<*>.isCharType: Boolean
    get() {
        return this == Char::class.javaPrimitiveType || this == Char::class.javaObjectType
    }

val Class<*>.isIntType: Boolean
    get() {
        return this == Int::class.javaPrimitiveType || this == Int::class.javaObjectType
    }

val Class<*>.isLongType: Boolean
    get() {
        return this == Long::class.javaPrimitiveType || this == Long::class.javaObjectType
    }

val Class<*>.isFloatType: Boolean
    get() {
        return this == Float::class.javaPrimitiveType || this == Float::class.javaObjectType
    }

val Class<*>.isDoubleType: Boolean
    get() {
        return this == Double::class.javaPrimitiveType || this == Double::class.javaObjectType
    }

val Class<*>.isVoidType: Boolean
    get() {
        return this == Void::class.javaPrimitiveType || this == Void::class.javaObjectType
    }

val Class<*>.shortName: String
    get() {
        val name = this.name
        val lastDotIndex = DOT_MATCHER.lastIndexIn(name)
        return if (lastDotIndex < 0) name else name.substring(lastDotIndex + 1, name.length)
    }

val <T> Class<T>.arrayClass: Class<Array<T>>
    get() {
        if (this.isArray) {
            return "[${this.name}".classForName()
        }
        val arrayClassName = when (this) {
            Boolean::class.javaPrimitiveType -> "[Z"
            Byte::class.javaPrimitiveType -> "[B"
            Short::class.javaPrimitiveType -> "[S"
            Char::class.javaPrimitiveType -> "[C"
            Int::class.javaPrimitiveType -> "[I"
            Long::class.javaPrimitiveType -> "[J"
            Float::class.javaPrimitiveType -> "[F"
            Double::class.javaPrimitiveType -> "[D"
            Void::class.javaPrimitiveType -> "[V"
            else -> "[L${this.name};"
        }
        return arrayClassName.classForName(this.classLoader ?: currentClassLoader())
    }

/**
 * Returns current [Thread.contextClassLoader], or [BytesClassLoader] if `contextClassLoader` is null.
 */
fun currentClassLoader(): ClassLoader {
    return currentThread().contextClassLoader ?: BytesClassLoader
}

/**
 * @throws ClassNotFoundException
 */
@JvmName("forName")
@JvmOverloads
fun <T> CharSequence.classForName(classLoader: ClassLoader = currentClassLoader()): Class<T> {
    return Class.forName(this.toString(), true, classLoader).asTyped()
}

@JvmName("forNameOrNull")
@JvmOverloads
fun <T> CharSequence.classForNameOrNull(classLoader: ClassLoader = currentClassLoader()): Class<T>? {
    return try {
        Class.forName(this.toString(), true, classLoader)
    } catch (e: ClassNotFoundException) {
        null
    }.asTyped()
}

/**
 * @throws NoSuchMethodException
 */
fun <T> Class<*>.newInst(): T {
    return newInst(ArrayUtils.EMPTY_CLASS_ARRAY, ArrayUtils.EMPTY_CLASS_ARRAY)
}

fun <T> Class<*>.newInstOrNull(): T? {
    return newInstOrNull(ArrayUtils.EMPTY_CLASS_ARRAY, ArrayUtils.EMPTY_CLASS_ARRAY)
}

/**
 * @throws NoSuchMethodException
 */
fun <T> Class<*>.newInst(parameterTypes: Array<out Class<*>>, args: Array<out Any?>): T {
    val constructor = try {
        this.getConstructor(*parameterTypes)
    } catch (e: NoSuchMethodException) {
        throw e
    }
    return constructor.newInstance(*args).asTyped()
}

fun <T> Class<*>.newInstOrNull(parameterTypes: Array<out Class<*>>, args: Array<out Any?>): T? {
    val constructor = try {
        this.getConstructor(*parameterTypes)
    } catch (e: NoSuchMethodException) {
        null
    }
    return constructor?.newInstance(*args)?.asTyped()
}

/**
 * @throws ClassNotFoundException
 * @throws NoSuchMethodException
 */
@JvmOverloads
fun <T> CharSequence.instForName(classLoader: ClassLoader = currentClassLoader()): T {
    return classForName<T>(classLoader).newInst()
}

@JvmOverloads
fun <T> CharSequence.instForNameOrNull(classLoader: ClassLoader = currentClassLoader()): T? {
    return classForNameOrNull<T>(classLoader)?.newInstOrNull()
}

/**
 * @throws ClassNotFoundException
 * @throws NoSuchMethodException
 */
@JvmOverloads
fun <T> CharSequence.instForName(
    classLoader: ClassLoader = currentClassLoader(),
    parameterTypes: Array<out Class<*>>,
    args: Array<out Any?>
): T {
    return classForName<T>(classLoader).newInst(parameterTypes, args)
}

@JvmOverloads
fun <T> CharSequence.instForNameOrNull(
    classLoader: ClassLoader = currentClassLoader(),
    parameterTypes: Array<out Class<*>>,
    args: Array<out Any?>
): T? {
    return classForNameOrNull<T>(classLoader)?.newInstOrNull(parameterTypes, args)
}

fun Class<*>.toWrapperClass(): Class<*> {
    return when (this) {
        Boolean::class.javaPrimitiveType -> Boolean::class.javaObjectType
        Byte::class.javaPrimitiveType -> Byte::class.javaObjectType
        Short::class.javaPrimitiveType -> Short::class.javaObjectType
        Char::class.javaPrimitiveType -> Char::class.javaObjectType
        Int::class.javaPrimitiveType -> Int::class.javaObjectType
        Long::class.javaPrimitiveType -> Long::class.javaObjectType
        Float::class.javaPrimitiveType -> Float::class.javaObjectType
        Double::class.javaPrimitiveType -> Double::class.javaObjectType
        Void::class.javaPrimitiveType -> Void::class.javaObjectType
        else -> this
    }
}

fun Class<*>.canAssignedBy(other: Class<*>): Boolean {
    if (this.isAssignableFrom(other)) {
        return true
    }
    return this.toWrapperClass() == other.toWrapperClass()
}