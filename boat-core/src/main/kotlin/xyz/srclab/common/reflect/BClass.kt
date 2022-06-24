@file:JvmName("BClass")

package xyz.srclab.common.reflect

import com.google.common.base.CharMatcher
import org.apache.commons.lang3.ArrayUtils
import xyz.srclab.common.base.asType
import xyz.srclab.common.base.currentThread
import xyz.srclab.common.base.dotMatcher
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

/**
 * Returns the segment of name after `.` or `$`. For example: `A$B returns B`; `A.B return B`.
 */
val Class<*>.shortName: String
    get() {
        val name = this.name
        val lastDotIndex = BClassHolder.shortNameMatcher.lastIndexIn(name)
        return if (lastDotIndex < 0) name else name.substring(lastDotIndex + 1, name.length)
    }

/**
 * Returns array class of which component type is this class.
 */
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
        return arrayClassName.classForName(this.classLoader ?: defaultClassLoader())
    }

/**
 * Returns current [Thread.contextClassLoader], or [BytesClassLoader] if `contextClassLoader` is null.
 */
fun defaultClassLoader(): ClassLoader {
    return currentThread().contextClassLoader ?: BytesClassLoader
}

/**
 * Returns [Class] for [this] name, equivalent to [Class.forName].
 * @throws ClassNotFoundException
 */
@JvmName("forName")
@JvmOverloads
fun <T> CharSequence.classForName(classLoader: ClassLoader = defaultClassLoader()): Class<T> {
    return Class.forName(this.toString(), true, classLoader).asType()
}

/**
 * Returns [Class] for [this] name, equivalent to [Class.forName], or null if not found.
 */
@JvmName("forNameOrNull")
@JvmOverloads
fun <T> CharSequence.classForNameOrNull(classLoader: ClassLoader = defaultClassLoader()): Class<T>? {
    return try {
        Class.forName(this.toString(), true, classLoader)
    } catch (e: ClassNotFoundException) {
        null
    }.asType()
}

/**
 * Returns new instance of this class.
 * @throws NoSuchMethodException
 */
fun <T> Class<*>.newInst(): T {
    return newInst(ArrayUtils.EMPTY_CLASS_ARRAY, ArrayUtils.EMPTY_CLASS_ARRAY)
}

/**
 * Returns new instance of this class, or null if failed.
 */
fun <T> Class<*>.newInstOrNull(): T? {
    return newInstOrNull(ArrayUtils.EMPTY_CLASS_ARRAY, ArrayUtils.EMPTY_CLASS_ARRAY)
}

/**
 * Returns new instance of this class.
 * @throws NoSuchMethodException
 */
fun <T> Class<*>.newInst(parameterTypes: Array<out Class<*>>, args: Array<out Any?>): T {
    val constructor = try {
        this.getConstructor(*parameterTypes)
    } catch (e: NoSuchMethodException) {
        throw e
    }
    return constructor.newInstance(*args).asType()
}

/**
 * Returns new instance of this class, or null if failed.
 */
fun <T> Class<*>.newInstOrNull(parameterTypes: Array<out Class<*>>, args: Array<out Any?>): T? {
    val constructor = try {
        this.getConstructor(*parameterTypes)
    } catch (e: NoSuchMethodException) {
        null
    }
    return constructor?.newInstance(*args)?.asType()
}

/**
 * Returns new instance for this class name.
 * @throws ClassNotFoundException
 * @throws NoSuchMethodException
 */
@JvmOverloads
fun <T> CharSequence.instForName(classLoader: ClassLoader = defaultClassLoader()): T {
    return classForName<T>(classLoader).newInst()
}

/**
 * Returns new instance for this class name, or null if failed.
 */
@JvmOverloads
fun <T> CharSequence.instForNameOrNull(classLoader: ClassLoader = defaultClassLoader()): T? {
    return classForNameOrNull<T>(classLoader)?.newInstOrNull()
}

/**
 * Returns new instance for this class name.
 * @throws ClassNotFoundException
 * @throws NoSuchMethodException
 */
@JvmOverloads
fun <T> CharSequence.instForName(
    classLoader: ClassLoader = defaultClassLoader(),
    parameterTypes: Array<out Class<*>>,
    args: Array<out Any?>
): T {
    return classForName<T>(classLoader).newInst(parameterTypes, args)
}

/**
 * Returns new instance for this class name, or null if failed.
 */
@JvmOverloads
fun <T> CharSequence.instForNameOrNull(
    classLoader: ClassLoader = defaultClassLoader(),
    parameterTypes: Array<out Class<*>>,
    args: Array<out Any?>
): T? {
    return classForNameOrNull<T>(classLoader)?.newInstOrNull(parameterTypes, args)
}

/**
 * Returns wrapper class of primitive type, if not primitive, return itself.
 */
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

/**
 * Returns whether [this] class can be assigned by [other].
 */
fun Class<*>.canAssignedBy(other: Class<*>): Boolean {
    if (this.isAssignableFrom(other)) {
        return true
    }
    return this.toWrapperClass() == other.toWrapperClass()
}

private object BClassHolder {
    val shortNameMatcher: CharMatcher = CharMatcher.`is`('$').or(dotMatcher())
}