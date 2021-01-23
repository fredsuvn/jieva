@file:JvmName("ReflectKit")
@file:JvmMultifileClass

package xyz.srclab.common.reflect

import org.apache.commons.lang3.ArrayUtils
import xyz.srclab.common.base.Current
import xyz.srclab.common.base.asAny
import xyz.srclab.common.base.loadClass
import java.lang.reflect.*

val Member.isPublic: Boolean
    @JvmName("isPublic") get() {
        return Modifier.isPublic(this.modifiers)
    }

val Member.isPrivate: Boolean
    @JvmName("isPrivate") get() {
        return Modifier.isPrivate(this.modifiers)
    }

val Member.isProtected: Boolean
    @JvmName("isProtected") get() {
        return Modifier.isProtected(this.modifiers)
    }

val Member.isStatic: Boolean
    @JvmName("isStatic") get() {
        return Modifier.isStatic(this.modifiers)
    }

val Member.isFinal: Boolean
    @JvmName("isFinal") get() {
        return Modifier.isFinal(this.modifiers)
    }

val Member.isSynchronized: Boolean
    @JvmName("isSynchronized") get() {
        return Modifier.isSynchronized(this.modifiers)
    }

val Member.isVolatile: Boolean
    @JvmName("isVolatile") get() {
        return Modifier.isVolatile(this.modifiers)
    }

val Member.isTransient: Boolean
    @JvmName("isTransient") get() {
        return Modifier.isTransient(this.modifiers)
    }

val Member.isNative: Boolean
    @JvmName("isNative") get() {
        return Modifier.isNative(this.modifiers)
    }

val Member.isInterface: Boolean
    @JvmName("isInterface") get() {
        return Modifier.isInterface(this.modifiers)
    }

val Member.isAbstract: Boolean
    @JvmName("isAbstract") get() {
        return Modifier.isAbstract(this.modifiers)
    }

val Member.isStrict: Boolean
    @JvmName("isStrict") get() {
        return Modifier.isStrict(this.modifiers)
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
 * Returns whether [clazz] can use [this]
 */
fun Member.isAccessibleFor(clazz: Class<*>): Boolean {
    if (this.isPublic) {
        return true
    }
    if (this.isPrivate) {
        return this.declaringClass == clazz
    }
    val declaringPackage = this.declaringClass.`package`
    if (declaringPackage == clazz.`package`) {
        return true
    }
    if (this.isProtected) {
        return this.declaringClass.isAssignableFrom(clazz)
    }
    return false
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
    return toInstance(ArrayUtils.EMPTY_CLASS_ARRAY, ArrayUtils.EMPTY_CLASS_ARRAY, classLoader)
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
    return toInstance(ArrayUtils.EMPTY_CLASS_ARRAY, ArrayUtils.EMPTY_CLASS_ARRAY)
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

fun <T> Class<T>.constructorOrNull(vararg parameterTypes: Class<*>): Constructor<T>? {
    return try {
        this.getConstructor(*parameterTypes)
    } catch (e: NoSuchMethodException) {
        null
    }
}

fun <T> Class<T>.constructors(): List<Constructor<T>> {
    return this.constructors.asList().asAny()
}

fun <T> Class<T>.declaredConstructorOrNull(vararg parameterTypes: Class<*>): Constructor<T>? {
    return try {
        this.getDeclaredConstructor(*parameterTypes)
    } catch (e: NoSuchMethodException) {
        null
    }
}

fun <T> Class<T>.declaredConstructors(): List<Constructor<T>> {
    return this.declaredConstructors.asList().asAny()
}

fun <T> Constructor<T>.invoke(vararg args: Any?): T {
    return this.newInstance(*args)
}

fun <T> Constructor<T>.invokeForcibly(vararg args: Any?): T {
    this.isAccessible = true
    return this.newInstance(*args)
}

fun Class<*>.fieldOrNull(name: String): Field? {
    return try {
        this.getField(name)
    } catch (e: NoSuchFieldException) {
        null
    }
}

fun Class<*>.fields(): List<Field> {
    return this.fields.asList()
}

fun Class<*>.declaredFieldOrNull(name: String): Field? {
    return try {
        this.getDeclaredField(name)
    } catch (e: NoSuchFieldException) {
        null
    }
}

fun Class<*>.declaredFields(): List<Field> {
    return this.declaredFields.asList()
}

fun Class<*>.ownedFieldOrNull(name: String): Field? {
    return searchFieldOrNull(name, false)
}

fun Class<*>.ownedFields(): List<Field> {
    val set = LinkedHashSet<Field>()
    set.addAll(this.fields())
    for (declaredField in this.declaredFields()) {
        if (!set.contains(declaredField)) {
            set.add(declaredField)
        }
    }
    return set.toList()
}

@JvmOverloads
fun Class<*>.searchFieldOrNull(name: String, deep: Boolean = true): Field? {
    var field = try {
        this.getField(name)
    } catch (e: NoSuchFieldException) {
        null
    }
    if (field !== null) {
        return field
    }
    field = this.declaredFieldOrNull(name)
    if (field !== null) {
        return field
    }
    if (!deep) {
        return null
    }
    var superClass = this.superclass
    while (superClass !== null) {
        field = superClass.declaredFieldOrNull(name)
        if (field !== null) {
            return field
        }
        superClass = superClass.superclass
    }
    return null
}

@JvmOverloads
fun Class<*>.searchFields(deep: Boolean = true, predicate: (Field) -> Boolean): List<Field> {
    val result = mutableListOf<Field>()
    for (field in this.fields) {
        if (predicate(field)) {
            result.add(field)
        }
    }
    for (field in this.declaredFields) {
        if (predicate(field)) {
            result.add(field)
        }
    }
    if (!deep) {
        return result
    }
    var superClass = this.superclass
    while (superClass !== null) {
        for (field in superClass.declaredFields) {
            if (predicate(field)) {
                result.add(field)
            }
        }
        superClass = superClass.superclass
    }
    return result
}

/**
 * @throws IllegalAccessException
 */
@JvmOverloads
fun <T> Field.getValue(owner: Any?, force: Boolean = false): T {
    return try {
        if (force) {
            this.isAccessible = true
        }
        this.get(owner).asAny()
    } catch (e: IllegalAccessException) {
        throw e
    }
}

/**
 * @throws IllegalAccessException
 */
@JvmOverloads
fun Field.setValue(owner: Any?, value: Any?, force: Boolean = false) {
    try {
        if (force) {
            this.isAccessible = true
        }
        this.set(owner, value)
    } catch (e: IllegalAccessException) {
        throw e
    }
}

/**
 * @throws NoSuchFieldException
 * @throws IllegalAccessException
 */
fun <T> Class<*>.getFieldValue(name: String, owner: Any?): T {
    val field = this.fieldOrNull(name)
    if (field === null) {
        throw NoSuchFieldException(name)
    }
    return field.getValue(owner)
}

/**
 * @throws NoSuchFieldException
 */
fun <T> Class<*>.getFieldValue(
    name: String,
    owner: Any?,
    deep: Boolean,
    force: Boolean
): T {
    val field = this.searchFieldOrNull(name, deep)
    if (field === null) {
        throw NoSuchFieldException(name)
    }
    return field.getValue(owner, force)
}

/**
 * @throws NoSuchFieldException
 * @throws IllegalAccessException
 */
fun Class<*>.setFieldValue(name: String, owner: Any?, value: Any?) {
    val field = this.fieldOrNull(name)
    if (field === null) {
        throw NoSuchFieldException(name)
    }
    return field.setValue(value, owner)
}

/**
 * @throws NoSuchFieldException
 * @throws IllegalAccessException
 */
fun Class<*>.setFieldValue(
    name: String,
    owner: Any?,
    value: Any?,
    deep: Boolean,
    force: Boolean,
) {
    val field = this.searchFieldOrNull(name, deep)
    if (field === null) {
        throw NoSuchFieldException(name)
    }
    return field.setValue(owner, value, force)
}

fun Class<*>.methodOrNull(name: String, vararg parameterTypes: Class<*>): Method? {
    return try {
        this.getMethod(name, *parameterTypes)
    } catch (e: NoSuchMethodException) {
        null
    }
}

fun Class<*>.methods(): List<Method> {
    return this.methods.asList()
}

fun Class<*>.declaredMethodOrNull(name: String, vararg parameterTypes: Class<*>): Method? {
    return try {
        this.getDeclaredMethod(name, *parameterTypes)
    } catch (e: NoSuchMethodException) {
        null
    }
}

fun Class<*>.declaredMethods(): List<Method> {
    return this.declaredMethods.asList()
}

fun Class<*>.ownedMethodOrNull(name: String, vararg parameterTypes: Class<*>): Method? {
    return searchMethodOrNull(name, parameterTypes, false)
}

fun Class<*>.ownedMethods(): List<Method> {
    val set = LinkedHashSet<Method>()
    set.addAll(this.methods())
    for (declaredMethod in this.declaredMethods()) {
        if (!set.contains(declaredMethod)) {
            set.add(declaredMethod)
        }
    }
    return set.toList()
}

@JvmOverloads
fun Class<*>.searchMethodOrNull(
    name: String,
    parameterTypes: Array<out Class<*>>,
    deep: Boolean = true,
): Method? {
    var method = try {
        this.getMethod(name, *parameterTypes)
    } catch (e: NoSuchMethodException) {
        null
    }
    if (method !== null) {
        return method
    }
    method = declaredMethodOrNull(name, *parameterTypes)
    if (method !== null) {
        return method
    }
    if (!deep) {
        return null
    }
    var superClass = this.superclass
    while (superClass !== null) {
        method = superClass.declaredMethodOrNull(name, *parameterTypes)
        if (method !== null) {
            return method
        }
        superClass = superClass.superclass
    }
    return null
}

@JvmOverloads
fun Class<*>.searchMethods(deep: Boolean = true, predicate: (Method) -> Boolean): List<Method> {
    val result = mutableListOf<Method>()
    for (method in this.methods) {
        if (predicate(method)) {
            result.add(method)
        }
    }
    for (method in this.declaredMethods) {
        if (predicate(method)) {
            result.add(method)
        }
    }
    if (!deep) {
        return result
    }
    var superClass = this.superclass
    while (superClass !== null) {
        for (method in superClass.declaredMethods) {
            if (predicate(method)) {
                result.add(method)
            }
        }
        superClass = superClass.superclass
    }
    return result
}

/**
 * @throws  IllegalAccessException
 * @throws  IllegalArgumentException
 * @throws  ReflectiveOperationException
 */
fun <T> Method.invoke(owner: Any?, vararg args: Any?): T {
    return try {
        this.invoke(owner, *args).asAny()
    } catch (e: Exception) {
        throw e
    }
}

/**
 * @throws  IllegalArgumentException
 * @throws  ReflectiveOperationException
 */
fun <T> Method.invokeForcible(owner: Any?, vararg args: Any?): T {
    this.isAccessible = true
    return try {
        this.invoke(owner, *args).asAny()
    } catch (e: Exception) {
        throw IllegalStateException(e)
    }
}

fun Method.canOverrideBy(clazz: Class<*>): Boolean {
    if (this.isPrivate) {
        return false
    }
    if (!this.declaringClass.isAssignableFrom(clazz)) {
        return false
    }
    return true
}