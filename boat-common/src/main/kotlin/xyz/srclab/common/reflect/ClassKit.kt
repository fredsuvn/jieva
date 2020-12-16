@file:JvmName("ClassKit")
@file:JvmMultifileClass

package xyz.srclab.common.reflect

import org.apache.commons.lang3.ArrayUtils
import xyz.srclab.common.base.*
import java.lang.reflect.Modifier

val Class<*>.isPublic: Boolean
    @JvmName("isPublic") get() {
        return Modifier.isPublic(this.modifiers)
    }

val Class<*>.isPrivate: Boolean
    @JvmName("isPrivate") get() {
        return Modifier.isPrivate(this.modifiers)
    }

val Class<*>.isProtected: Boolean
    @JvmName("isProtected") get() {
        return Modifier.isProtected(this.modifiers)
    }

val Class<*>.isStatic: Boolean
    @JvmName("isStatic") get() {
        return Modifier.isStatic(this.modifiers)
    }

val Class<*>.isFinal: Boolean
    @JvmName("isFinal") get() {
        return Modifier.isFinal(this.modifiers)
    }

val Class<*>.isSynchronized: Boolean
    @JvmName("isSynchronized") get() {
        return Modifier.isSynchronized(this.modifiers)
    }

val Class<*>.isVolatile: Boolean
    @JvmName("isVolatile") get() {
        return Modifier.isVolatile(this.modifiers)
    }

val Class<*>.isTransient: Boolean
    @JvmName("isTransient") get() {
        return Modifier.isTransient(this.modifiers)
    }

val Class<*>.isNative: Boolean
    @JvmName("isNative") get() {
        return Modifier.isNative(this.modifiers)
    }

val Class<*>.isInterface: Boolean
    @JvmName("isInterface") get() {
        return this.isInterface
    }

val Class<*>.isAbstract: Boolean
    @JvmName("isAbstract") get() {
        return Modifier.isAbstract(this.modifiers)
    }

val Class<*>.isStrict: Boolean
    @JvmName("isStrict") get() {
        return Modifier.isStrict(this.modifiers)
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
fun <T> CharSequence.toInstance(classLoader: ClassLoader = Current.classLoader): T {
    return this.toInstance(ArrayUtils.EMPTY_CLASS_ARRAY, ArrayUtils.EMPTY_CLASS_ARRAY, classLoader)
}

/**
 * @throws ClassNotFoundException
 * @throws NoSuchMethodException
 */
@JvmOverloads
fun <T> CharSequence.toInstance(
    parameterTypes: Array<out Class<*>>,
    args: Array<out Any?>,
    classLoader: ClassLoader = Current.classLoader,
): T {
    val clazz: Class<T> = this.loadClass(classLoader)
    return clazz.toInstance(parameterTypes, args)
}

/**
 * @throws NoSuchMethodException
 */
fun <T> Class<*>.toInstance(): T {
    return this.toInstance(ArrayUtils.EMPTY_CLASS_ARRAY, ArrayUtils.EMPTY_CLASS_ARRAY)
}

/**
 * @throws NoSuchMethodException
 */
fun <T> Class<*>.toInstance(parameterTypes: Array<out Class<*>>, args: Array<out Any?>): T {
    val constructor = try {
        this.getConstructor(*parameterTypes)
    } catch (e: NoSuchMethodException) {
        throw e
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