/**
 * Reflection utilities.
 */
@file:JvmName("BtReflect")

package xyz.srclab.common.reflect

import com.google.common.base.CharMatcher
import org.apache.commons.lang3.ArrayUtils
import xyz.srclab.annotations.Accepted
import xyz.srclab.annotations.Written
import xyz.srclab.common.base.asType
import xyz.srclab.common.base.currentThread
import xyz.srclab.common.base.dotMatcher
import java.lang.reflect.*
import java.util.function.Predicate

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

val Member.isPublic: Boolean
    get() {
        return Modifier.isPublic(this.modifiers)
    }

val Member.isPrivate: Boolean
    get() {
        return Modifier.isPrivate(this.modifiers)
    }

val Member.isProtected: Boolean
    get() {
        return Modifier.isProtected(this.modifiers)
    }

val Member.isStatic: Boolean
    get() {
        return Modifier.isStatic(this.modifiers)
    }

val Member.isFinal: Boolean
    get() {
        return Modifier.isFinal(this.modifiers)
    }

val Member.isSynchronized: Boolean
    get() {
        return Modifier.isSynchronized(this.modifiers)
    }

val Member.isVolatile: Boolean
    get() {
        return Modifier.isVolatile(this.modifiers)
    }

val Member.isTransient: Boolean
    get() {
        return Modifier.isTransient(this.modifiers)
    }

val Member.isNative: Boolean
    get() {
        return Modifier.isNative(this.modifiers)
    }

val Member.isInterface: Boolean
    get() {
        return Modifier.isInterface(this.modifiers)
    }

val Member.isAbstract: Boolean
    get() {
        return Modifier.isAbstract(this.modifiers)
    }

val Member.isStrict: Boolean
    get() {
        return Modifier.isStrict(this.modifiers)
    }

/**
 * Returns the segment of name after `.` or `$`. For example: `A$B returns B`; `A.B return B`.
 */
val Class<*>.shortName: String
    get() {
        val name = this.name
        val lastDotIndex = BtReflectHolder.shortNameMatcher.lastIndexIn(name)
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
 * Returns whether this is array type.
 */
val Type.isArray: Boolean
    get() {
        return when (this) {
            is Class<*> -> this.isArray
            is GenericArrayType -> true
            else -> false
        }
    }

/**
 * Returns component type of this array type, or null if it is not array type.
 */
val Type.componentTypeOrNull: Type?
    get() {
        return when (this) {
            is Class<*> -> this.componentType
            is GenericArrayType -> this.genericComponentType
            else -> null
        }
    }

/**
 * Returns raw class of this type.
 */
val ParameterizedType.rawClass: Class<*>
    get() {
        return this.rawType.asType()
    }

/**
 * Returns raw class of this type.
 */
val GenericArrayType.rawClass: Class<*>
    get() {
        var deep = 0
        var componentType = this.genericComponentType
        while (componentType is GenericArrayType) {
            deep++
            componentType = componentType.genericComponentType
        }
        var arrayClass = componentType.rawClass.arrayClass
        for (i in 0 until deep) {
            arrayClass = arrayClass.arrayClass
        }
        return arrayClass
    }

/**
 * Returns raw class of this type,
 * the type must be one of [Class], [ParameterizedType] or [GenericArrayType].
 */
val Type.rawClass: Class<*>
    @Accepted(Class::class, ParameterizedType::class, GenericArrayType::class)
    @Throws(IllegalArgumentException::class)
    get() {
        return this.rawClassOrNull ?: throw IllegalArgumentException(
            "Only Class, ParameterizedType or GenericArrayType has raw class: $this"
        )
    }

/**
 * Returns raw class of this type,
 * the type should be one of [Class], [ParameterizedType] or [GenericArrayType].
 * Or null if the type is not in the type range.
 */
val Type.rawClassOrNull: Class<*>?
    get() {
        return when (this) {
            is Class<*> -> this
            is ParameterizedType -> this.rawClass
            is GenericArrayType -> this.rawClass
            else -> null
        }
    }

/**
 * Returns upper bound of this type.
 */
val TypeVariable<*>.upperBound: Type
    get() {
        val bounds = this.bounds
        return if (bounds.isEmpty()) {
            Any::class.java
        } else {
            bounds[0].upperBound
        }
    }

/**
 * Returns upper bound of this type.
 */
val WildcardType.upperBound: Type
    get() {
        val upperBounds = this.upperBounds
        return if (upperBounds.isEmpty()) {
            Any::class.java
        } else {
            upperBounds[0].upperBound
        }
    }

/**
 * Returns upper bound of this type.
 */
val Type.upperBound: Type
    get() {
        return when (this) {
            is TypeVariable<*> -> this.upperBound
            is WildcardType -> this.upperBound
            else -> this
        }
    }

/**
 * Returns lower bound of this type, or null if we don't know.
 */
val WildcardType.lowerBound: Type?
    get() {
        val lowerBounds = this.lowerBounds
        return if (lowerBounds.isEmpty()) {
            null
        } else {
            lowerBounds[0].lowerBound
        }
    }

/**
 * Returns lower bound of this type, or null if we don't know.
 */
val Type.lowerBound: Type?
    get() {
        return when (this) {
            is WildcardType -> this.lowerBound
            is TypeVariable<*> -> null
            else -> this
        }
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

/**
 * Returns public constructor of [this] class.
 * @throws NoSuchMethodException
 */
fun <T> Class<T>.getConstructor(vararg parameterTypes: Class<*>): Constructor<T> {
    return this.getConstructor(*parameterTypes)
}

/**
 * Returns public constructor of [this] class, or null if failed.
 */
fun <T> Class<T>.getConstructorOrNull(vararg parameterTypes: Class<*>): Constructor<T>? {
    return try {
        this.getConstructor(*parameterTypes)
    } catch (e: NoSuchMethodException) {
        null
    }
}

/**
 * Returns all public constructors of [this] class.
 * @throws NoSuchMethodException
 */
fun <T> Class<T>.getConstructors(): List<Constructor<T>> {
    return this.constructors.asList().asType()
}

/**
 * Returns declared constructor of [this] class.
 * @throws NoSuchMethodException
 */
fun <T> Class<T>.getDeclaredConstructor(vararg parameterTypes: Class<*>): Constructor<T> {
    return this.getDeclaredConstructor(*parameterTypes)
}

/**
 * Returns declared constructor of [this] class, or null if failed.
 */
fun <T> Class<T>.getDeclaredConstructorOrNull(vararg parameterTypes: Class<*>): Constructor<T>? {
    return try {
        this.getDeclaredConstructor(*parameterTypes)
    } catch (e: NoSuchMethodException) {
        null
    }
}

/**
 * Returns all declared constructors of [this] class.
 */
fun <T> Class<T>.getDeclaredConstructors(): List<Constructor<T>> {
    return this.declaredConstructors.asList().asType()
}

/**
 * Invokes [this] constructor.
 */
fun <T> Constructor<T>.invoke(vararg args: Any?): T {
    return this.newInstance(*args)
}

/**
 * Invokes [this] constructor forcibly.
 */
fun <T> Constructor<T>.invokeForcibly(vararg args: Any?): T {
    this.isAccessible = true
    return this.newInstance(*args)
}

/**
 * Returns public field of [this] class.
 * @throws NoSuchFieldException
 */
fun Class<*>.getField(name: String): Field {
    return this.getField(name)
}

/**
 * Returns public field of [this] class, or null if failed.
 */
fun Class<*>.getFieldOrNull(name: String): Field? {
    return try {
        this.getField(name)
    } catch (e: NoSuchFieldException) {
        null
    }
}

/**
 * Returns all public fields of [this] class.
 */
fun Class<*>.getFields(): List<Field> {
    return this.fields.asList()
}

/**
 * Returns declared field of [this] class.
 * @throws NoSuchFieldException
 */
fun Class<*>.getDeclaredField(name: String): Field {
    return this.getDeclaredField(name)
}

/**
 * Returns declared field of [this] class, or null if failed.
 */
fun Class<*>.getDeclaredFieldOrNull(name: String): Field? {
    return try {
        this.getDeclaredField(name)
    } catch (e: NoSuchFieldException) {
        null
    }
}

/**
 * Returns declared public fields of [this] class.
 */
fun Class<*>.getDeclaredFields(): List<Field> {
    return this.declaredFields.asList()
}

/**
 * Return public or declared field of [this] class.
 *
 * @throws NoSuchFieldException
 */
fun Class<*>.getOwnedField(name: String): Field {
    return getFieldOrNull(name) ?: getDeclaredField(name)
}

/**
 * Return public or declared field of [this] class., or null if failed.
 */
fun Class<*>.getOwnedFieldOrNull(name: String): Field? {
    return getFieldOrNull(name) ?: getDeclaredFieldOrNull(name)
}

/**
 * Return all declared and public fields of [this] class.
 */
fun Class<*>.getOwnedFields(): List<Field> {
    val set = LinkedHashSet<Field>()
    set.addAll(this.declaredFields)
    for (field in this.fields) {
        if (!set.contains(field)) {
            set.add(field)
        }
    }
    return set.toList()
}

/**
 * Searches and returns field for [this] class.
 *
 * @param name field name
 * @param deep whether recursively search to super class
 */
@JvmOverloads
fun Class<*>.searchFieldOrNull(name: String, deep: Boolean = true): Field? {
    var field = getOwnedFieldOrNull(name)
    if (field !== null) {
        return field
    }
    if (!deep) {
        return null
    }
    var superClass = this.superclass
    while (superClass !== null) {
        field = superClass.getDeclaredFieldOrNull(name)
        if (field !== null) {
            return field
        }
        superClass = superClass.superclass
    }
    return null
}

/**
 * Searches and returns fields for [this] class.
 *
 * @param deep whether recursively search to super class
 * @param predicate true if the field is matched and will be return
 */
@JvmOverloads
fun Class<*>.searchFields(
    deep: Boolean = true,
    predicate: Predicate<Field> = Predicate { true }
): List<Field> {
    return searchFieldsTo(ArrayList(), deep, predicate)
}

/**
 * Searches and returns fields for [this] class into [destination].
 *
 * @param deep whether recursively search to super class
 * @param predicate true if the field is matched and will be return
 */
@JvmOverloads
fun <C : MutableCollection<in Field>> Class<*>.searchFieldsTo(
    destination: C,
    deep: Boolean = true,
    predicate: Predicate<Field> = Predicate { true }
): C {
    for (field in this.fields) {
        if (predicate.test(field)) {
            destination.add(field)
        }
    }
    for (field in this.declaredFields) {
        if (!destination.contains(field) && predicate.test(field)) {
            destination.add(field)
        }
    }
    if (!deep) {
        return destination
    }
    var superClass = this.superclass
    while (superClass !== null) {
        for (field in superClass.declaredFields) {
            if (!destination.contains(field) && predicate.test(field)) {
                destination.add(field)
            }
        }
        superClass = superClass.superclass
    }
    return destination
}

/**
 * Gets value of [this] field.
 * @throws IllegalAccessException
 */
@JvmName("getFieldValue")
@JvmOverloads
fun <T> Field.getValue(owner: Any?, force: Boolean = false): T {
    return try {
        if (force) {
            this.isAccessible = true
        }
        this.get(owner).asType()
    } catch (e: IllegalAccessException) {
        throw e
    }
}

/**
 * Sets value of [this] field.
 * @throws IllegalAccessException
 */
@JvmName("setFieldValue")
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
 * Gets value of field.
 *
 * @throws NoSuchFieldException
 * @throws IllegalAccessException
 */
@JvmOverloads
fun <T> Class<*>.getFieldValue(name: String, owner: Any?, force: Boolean = false): T {
    val field = if (force) getOwnedField(name) else getField(name)
    return field.getValue(owner, force)
}

/**
 * Sets value of field.
 *
 * @throws NoSuchFieldException
 * @throws IllegalAccessException
 */
@JvmOverloads
fun Class<*>.setFieldValue(name: String, owner: Any?, value: Any?, force: Boolean = false) {
    val field = if (force) getOwnedField(name) else getField(name)
    return field.setValue(value, owner, force)
}

/**
 * Returns values of static field.
 *
 * @throws NoSuchFieldException
 * @throws IllegalAccessException
 */
@JvmOverloads
fun <T> Class<*>.getStaticFieldValue(name: String, force: Boolean = false): T {
    return getFieldValue(name, null, force)
}

/**
 * Returns values of static field.
 *
 * @throws NoSuchFieldException
 * @throws IllegalAccessException
 */
@JvmOverloads
fun Class<*>.setStaticFieldValue(name: String, value: Any?, force: Boolean = false) {
    return setFieldValue(name, null, value, force)
}

/**
 * Uses [searchFieldOrNull] to find deep field and gets its value.
 * @throws NoSuchFieldException
 */
fun <T> Class<*>.getDeepFieldValue(name: String, owner: Any?): T {
    val field = searchFieldOrNull(name, true)
    if (field === null) {
        throw NoSuchFieldException(name)
    }
    return field.getValue(owner, true)
}

/**
 * Uses [searchFieldOrNull] to find deep field and sets its value.
 * @throws NoSuchFieldException
 */
fun Class<*>.setDeepFieldValue(name: String, owner: Any?, value: Any?) {
    val field = searchFieldOrNull(name, true)
    if (field === null) {
        throw NoSuchFieldException(name)
    }
    return field.setValue(owner, value, true)
}

/**
 * Returns public method of [this] class.
 * @throws NoSuchMethodException
 */
@JvmName("getMethod")
fun Class<*>.getMethod(name: String, vararg parameterTypes: Class<*>): Method {
    return this.getMethod(name, *parameterTypes)
}

/**
 * Returns public method of [this] class, or null if failed.
 */
fun Class<*>.getMethodOrNull(
    name: String, vararg parameterTypes: Class
    <*>
): Method? {
    return try {
        this.getMethod(name, *parameterTypes)
    } catch (e: NoSuchMethodException) {
        null
    }
}

/**
 * Returns all public methods of [this] class.
 */
@JvmName("getMethods")
fun Class<*>.getMethods(): List<Method> {
    return this.methods.asList()
}

/**
 * Returns declared method of [this] class.
 * @throws NoSuchMethodException
 */
@JvmName("getDeclaredMethod")
fun Class<*>.getDeclaredMethod(name: String, vararg parameterTypes: Class<*>): Method {
    return this.getDeclaredMethod(name, *parameterTypes)
}

/**
 * Returns declared method of [this] class, or null if failed.
 */
@JvmName("getDeclaredMethodOrNull")
fun Class<*>.getDeclaredMethodOrNull(name: String, vararg parameterTypes: Class<*>): Method? {
    return try {
        this.getDeclaredMethod(name, *parameterTypes)
    } catch (e: NoSuchMethodException) {
        null
    }
}

/**
 * Returns all declared methods of [this] class.
 */
@JvmName("getDeclaredMethods")
fun Class<*>.getDeclaredMethods(): List<Method> {
    return this.declaredMethods.asList()
}

/**
 * Returns public or declared method of [this] class.
 * @throws NoSuchMethodException
 */
@JvmName("getOwnedMethod")
fun Class<*>.getOwnedMethod(name: String, vararg parameterTypes: Class<*>): Method {
    return getMethodOrNull(name, *parameterTypes) ?: getDeclaredMethod(name, *parameterTypes)
}

/**
 * Returns public or declared method of [this] class, or null if failed.
 */
@JvmName("getOwnedMethodOrNull")
fun Class<*>.getOwnedMethodOrNull(name: String, vararg parameterTypes: Class<*>): Method? {
    return getMethodOrNull(name, *parameterTypes) ?: getDeclaredMethodOrNull(name, *parameterTypes)
}

/**
 * Returns all declared and public methods of [this] class.
 */
fun Class<*>.getOwnedMethods(): List<Method> {
    val set = LinkedHashSet<Method>()
    set.addAll(this.declaredMethods)
    for (method in this.methods) {
        if (!set.contains(method)) {
            set.add(method)
        }
    }
    return set.toList()
}

/**
 * Searches and returns method for [this] class.
 *
 * @param name field name
 * @param deep whether recursively search to super class
 */
@JvmOverloads
fun Class<*>.searchMethodOrNull(
    name: String,
    parameterTypes: Array<out Class<*>>,
    deep: Boolean = true,
): Method? {
    var method = getOwnedMethodOrNull(name, *parameterTypes)
    if (method !== null) {
        return method
    }
    if (!deep) {
        return null
    }
    var superClass = this.superclass
    while (superClass !== null) {
        method = superClass.getDeclaredMethodOrNull(name, *parameterTypes)
        if (method !== null) {
            return method
        }
        superClass = superClass.superclass
    }
    return null
}

/**
 * Searches and returns methods for [this] class.
 *
 * @param deep whether recursively search to super class
 * @param predicate true if the field is matched and will be return
 */
@JvmOverloads
fun Class<*>.searchMethods(
    deep: Boolean = true,
    predicate: Predicate<Method> = Predicate { true }
): List<Method> {
    return searchMethods(ArrayList(), deep, predicate)
}

/**
 * Searches and returns methods for [this] class into [destination].
 *
 * @param deep whether recursively search to super class
 * @param predicate true if the field is matched and will be return
 */
@JvmOverloads
fun <C : MutableCollection<in Method>> Class<*>.searchMethods(
    destination: C,
    deep: Boolean = true,
    predicate: Predicate<Method> = Predicate { true }
): C {
    for (method in this.methods) {
        if (predicate.test(method)) {
            destination.add(method)
        }
    }
    for (method in this.declaredMethods) {
        if (!destination.contains(method) && predicate.test(method)) {
            destination.add(method)
        }
    }
    if (!deep) {
        return destination
    }
    var superClass = this.superclass
    while (superClass !== null) {
        for (method in superClass.declaredMethods) {
            if (!destination.contains(method) && predicate.test(method)) {
                destination.add(method)
            }
        }
        superClass = superClass.superclass
    }
    return destination
}

/**
 * Invokes [this] method.
 *
 * @throws  IllegalAccessException
 * @throws  IllegalArgumentException
 * @throws  ReflectiveOperationException
 */
fun <T> Method.invoke(owner: Any?, vararg args: Any?): T {
    return this.invoke(owner, *args).asType()
}

/**
 * Invokes [this] method forcibly.
 *
 * @throws  IllegalArgumentException
 * @throws  ReflectiveOperationException
 */
fun <T> Method.invokeForcibly(owner: Any?, vararg args: Any?): T {
    this.isAccessible = true
    return this.invoke(owner, *args).asType()
}

/**
 * Invokes [this] static method.
 *
 * @throws  IllegalAccessException
 * @throws  IllegalArgumentException
 * @throws  ReflectiveOperationException
 */
fun <T> Method.invokeStatic(vararg args: Any?): T {
    return this.invoke(null, *args).asType()
}

/**
 * Invokes [this] static method forcibly.
 *
 * @throws  IllegalArgumentException
 * @throws  ReflectiveOperationException
 */
fun <T> Method.invokeStaticForcibly(vararg args: Any?): T {
    this.isAccessible = true
    return this.invoke(null, *args).asType()
}

/**
 * Returns whether [this] is accessible for given [request].
 */
fun Member.isAccessibleFor(request: Class<*>): Boolean {
    if (this.isPublic) {
        return true
    }
    if (this.declaringClass == request) {
        return true
    }
    if (this.isPrivate) {
        //member inner class
        return request.declaringClass == this.declaringClass && !request.isStatic
    }
    if (this.declaringClass.`package` == request.`package`) {
        return true
    }
    if (this.isProtected) {
        return this.declaringClass.isAssignableFrom(request)
    }
    return false
}

/**
 * Returns type arguments.
 *
 * Assume `class Foo<T>` and its subclass `class StringFoo extends Foo<String>`:
 *
 * ```
 * Reflects.typeArguments(StringFoo.class, Foo.class);
 * ```
 *
 * will return: `{T=java.lang.String}`
 */
@Throws(IllegalArgumentException::class)
fun @receiver:Accepted(
    Class::class,
    ParameterizedType::class,
    GenericArrayType::class
) Type.getTypeArguments(): Map<TypeVariable<*>, Type> {

    fun Type.resolveTypeParameters(to: MutableMap<TypeVariable<*>, Type>) {

        fun ParameterizedType.resolveTypeArguments(to: MutableMap<TypeVariable<*>, Type>) {
            val actualArguments = this.actualTypeArguments
            val typeParameters = this.rawClass.typeParameters
            for (i in typeParameters.indices) {
                to[typeParameters[i]] = actualArguments[i]
            }
        }

        fun Class<*>.resolveTypeParameters(to: MutableMap<TypeVariable<*>, Type>) {

            fun Class<*>.doInterfaces() {
                val genericInterfaces = this.genericInterfaces
                for (genericInterface in genericInterfaces) {
                    if (genericInterface is ParameterizedType) {
                        genericInterface.resolveTypeArguments(to)
                    }
                }
                for (genericInterface in genericInterfaces) {
                    genericInterface.rawClass.doInterfaces()
                }
            }

            fun Class<*>.doSuperclass() {
                val genericSuperclass = this.genericSuperclass
                if (genericSuperclass is ParameterizedType) {
                    genericSuperclass.resolveTypeArguments(to)
                }
                this.doInterfaces()
                if (genericSuperclass !== null) {
                    genericSuperclass.rawClass.doSuperclass()
                }
            }

            doSuperclass()
        }

        when (this) {
            is Class<*> -> {
                this.resolveTypeParameters(to)
            }
            is ParameterizedType -> {
                val ownerType = this.ownerType
                if (ownerType !== null) {
                    ownerType.resolveTypeParameters(to)
                }
                this.resolveTypeArguments(to)
                this.rawClass.resolveTypeParameters(to)
            }
            is GenericArrayType -> {
                this.genericComponentType.resolveTypeParameters(to)
            }
            else -> throw IllegalArgumentException("Cannot resolve type arguments for $this")
        }
    }

    val typeArguments: MutableMap<TypeVariable<*>, Type> = HashMap()
    this.resolveTypeParameters(typeArguments)
    val stack: MutableSet<TypeVariable<*>> = HashSet()
    for (entry in typeArguments) {
        stack.clear()
        val oldType = entry.value
        val newType = oldType.eraseTypeParameters(typeArguments, stack, true)
        if (newType != oldType) {
            entry.setValue(newType)
        }
    }
    return typeArguments
}

/**
 * Returns a [ParameterizedType] as signature of [this], target for [to] type.
 *
 * Assume `class Foo<T>` and its subclass `class StringFoo` extends `Foo<String>`:
 *
 * ```
 * Reflects.toTypeSignature(StringFoo.class, Foo.class);
 * ```
 *
 * will return: `Foo<String>`
 */
@Throws(IllegalArgumentException::class)
fun Type.getTypeSignature(to: Class<*>): ParameterizedType {
    val typeParameters = to.typeParameters
    val typeArguments = this.getTypeArguments()
    val actualArguments = typeParameters.map {
        typeArguments[it] ?: it
    }
    val owner = to.declaringClass
    return if (owner === null) {
        parameterizedType(to, actualArguments)
    } else {
        parameterizedTypeWithOwner(to, owner, actualArguments)
    }
}

/**
 * Erase type parameter if [this] contains [TypeVariable].
 * Return itself if there is no argument found in [typeArguments].
 */
@JvmOverloads
fun Type.eraseTypeParameters(
    typeArguments: Map<TypeVariable<*>, Type>,
    @Written searchStack: MutableSet<TypeVariable<*>> = HashSet()
): Type {
    return eraseTypeParameters(typeArguments.asType(), searchStack, false)
}

private fun Type.eraseTypeParameters(
    @Written typeArguments: MutableMap<TypeVariable<*>, Type>,
    @Written searchStack: MutableSet<TypeVariable<*>>,
    replaceTypeParameter: Boolean,
): Type {

    fun getAndSwapTypes(@Written array: Array<Type>): Boolean {
        var changed = false
        for (i in array.indices) {
            val oldType = array[i]
            val newType = oldType.eraseTypeParameters(typeArguments, searchStack)
            if (newType != oldType) {
                array[i] = newType
                changed = true
            }
        }
        return changed
    }

    when (this) {
        is ParameterizedType -> {
            val actualTypeArguments = this.actualTypeArguments
            return if (getAndSwapTypes(actualTypeArguments)) {
                this.withArguments(*actualTypeArguments)
            } else {
                this
            }
        }
        is WildcardType -> {
            val upperBounds = this.upperBounds
            val lowerBounds = this.lowerBounds
            return if (getAndSwapTypes(upperBounds) || getAndSwapTypes(lowerBounds)) {
                this.withBounds(upperBounds, lowerBounds)
            } else {
                this
            }
        }
        is GenericArrayType -> {
            val componentType = this.genericComponentType
            val newComponentType = componentType.eraseTypeParameters(typeArguments, searchStack)
            return if (newComponentType != componentType) {
                newComponentType.genericArrayType()
            } else {
                this
            }
        }
        is TypeVariable<*> -> {
            val value = typeArguments[this]
            if (value === null || searchStack.contains(value)) {
                return this
            }
            if (value !is TypeVariable<*>) {
                val newType = value.eraseTypeParameters(typeArguments, searchStack)
                if (replaceTypeParameter && newType != this) {
                    for (typeVariable in searchStack) {
                        typeArguments[typeVariable] = newType
                    }
                }
                return newType
            }
            searchStack.add(this)
            return value.eraseTypeParameters(typeArguments, searchStack)
        }
        else -> return this
    }
}

private object BtReflectHolder {
    val shortNameMatcher: CharMatcher = CharMatcher.`is`('$').or(dotMatcher())
}