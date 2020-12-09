@file:JvmName("ClassKit")
@file:JvmMultifileClass

package xyz.srclab.common.reflect

import xyz.srclab.common.base.*
import java.lang.reflect.Modifier

val Class<*>.isOpen: Boolean
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
        return Modifier.isInterface(this.modifiers)
    }

val Class<*>.isAbstract: Boolean
    @JvmName("isAbstract") get() {
        return Modifier.isAbstract(this.modifiers)
    }

val Class<*>.isStrict: Boolean
    @JvmName("isStrict") get() {
        return Modifier.isStrict(this.modifiers)
    }

@JvmOverloads
fun <T> CharSequence.findClass(classLoader: ClassLoader = Current.classLoader): Class<T>? {
    return try {
        Class.forName(this.toString(), true, classLoader)
    } catch (e: ClassNotFoundException) {
        null
    }.asAny()
}

@JvmOverloads
fun <T> CharSequence.toInstance(classLoader: ClassLoader = Current.classLoader): T? {
    return this.toInstance(classLoader, emptyArray(), emptyArray())
}

@JvmOverloads
fun <T> CharSequence.toInstance(
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
        throw IllegalArgumentException("Constructor not found: ${toSignatureString(this.name, parameterTypes)}")
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