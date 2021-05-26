@file:JvmName("Reflects")
@file:JvmMultifileClass

package xyz.srclab.common.reflect

import org.apache.commons.lang3.ArrayUtils
import xyz.srclab.common.lang.Current
import xyz.srclab.common.lang.Defaults
import xyz.srclab.common.lang.asAny
import xyz.srclab.common.lang.loadClass

val Class<*>.isBooleanType: Boolean
    @JvmName("isBooleanType") get() {
        return this == Boolean::class.javaPrimitiveType || this == Boolean::class.javaObjectType
    }

val Class<*>.isByteType: Boolean
    @JvmName("isByteType") get() {
        return this == Byte::class.javaPrimitiveType || this == Byte::class.javaObjectType
    }

val Class<*>.isShortType: Boolean
    @JvmName("isShortType") get() {
        return this == Short::class.javaPrimitiveType || this == Short::class.javaObjectType
    }

val Class<*>.isCharType: Boolean
    @JvmName("isCharType") get() {
        return this == Char::class.javaPrimitiveType || this == Char::class.javaObjectType
    }

val Class<*>.isIntType: Boolean
    @JvmName("isIntType") get() {
        return this == Int::class.javaPrimitiveType || this == Int::class.javaObjectType
    }

val Class<*>.isLongType: Boolean
    @JvmName("isLongType") get() {
        return this == Long::class.javaPrimitiveType || this == Long::class.javaObjectType
    }

val Class<*>.isFloatType: Boolean
    @JvmName("isFloatType") get() {
        return this == Float::class.javaPrimitiveType || this == Float::class.javaObjectType
    }

val Class<*>.isDoubleType: Boolean
    @JvmName("isDoubleType") get() {
        return this == Double::class.javaPrimitiveType || this == Double::class.javaObjectType
    }

val Class<*>.isVoidType: Boolean
    @JvmName("isVoidType") get() {
        return this == Void::class.javaPrimitiveType || this == Void::class.javaObjectType
    }

val Class<*>.shortName: String
    @JvmName("shortName") get() {
        val name = this.name
        val lastDotIndex = Defaults.DOT_MATCHER.lastIndexIn(name)
        return if (lastDotIndex < 0) name else name.substring(lastDotIndex + 1, name.length)
    }

val <T> Class<T>.arrayClass: Class<Array<T>>
    @JvmName("arrayClass") get() {
        if (this.isArray) {
            return "[${this.name}".loadClass()
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
            else -> {
                "[L" + this.canonicalName + ";"
            }
        }
        return arrayClassName.loadClass()
    }

/**
 * @throws ClassNotFoundException
 */
@JvmOverloads
fun <T> CharSequence.toClass(classLoader: ClassLoader = Current.classLoader): Class<T> {
    return this.loadClass(classLoader)
}

/**
 * @throws ClassNotFoundException
 * @throws NoSuchMethodException
 */
@JvmOverloads
fun <T> CharSequence.newInstance(classLoader: ClassLoader = Current.classLoader): T {
    return newInstance(classLoader, ArrayUtils.EMPTY_CLASS_ARRAY, ArrayUtils.EMPTY_CLASS_ARRAY)
}

/**
 * @throws ClassNotFoundException
 * @throws NoSuchMethodException
 */
@JvmOverloads
fun <T> CharSequence.newInstance(
    classLoader: ClassLoader = Current.classLoader,
    parameterTypes: Array<out Class<*>>,
    args: Array<out Any?>
): T {
    val clazz: Class<T> = this.loadClass(classLoader)
    return clazz.newInstance(parameterTypes, args)
}

/**
 * @throws ClassNotFoundException
 * @throws NoSuchMethodException
 */
@JvmOverloads
fun <T> CharSequence.newInstanceWithArguments(
    classLoader: ClassLoader = Current.classLoader,
    vararg args: Any
): T {
    return newInstance(
        classLoader,
        args.map { it.javaClass }.toTypedArray(),
        args
    )
}

/**
 * @throws NoSuchMethodException
 */
fun <T> Class<*>.newInstance(): T {
    return newInstance(ArrayUtils.EMPTY_CLASS_ARRAY, ArrayUtils.EMPTY_CLASS_ARRAY)
}

/**
 * @throws NoSuchMethodException
 */
fun <T> Class<*>.newInstance(parameterTypes: Array<out Class<*>>, args: Array<out Any?>): T {
    val constructor = try {
        this.getConstructor(*parameterTypes)
    } catch (e: NoSuchMethodException) {
        throw e
    }
    return constructor.newInstance(*args).asAny()
}

/**
 * @throws NoSuchMethodException
 */
fun <T> Class<*>.newInstanceWithArguments(vararg args: Any): T {
    return newInstance(args.map { it.javaClass }.toTypedArray(), args)
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