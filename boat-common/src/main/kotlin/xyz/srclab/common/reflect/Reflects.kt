@file:JvmName("ReflectKit")
@file:JvmMultifileClass

package xyz.srclab.common.reflect

import org.apache.commons.lang3.ArrayUtils
import org.apache.commons.lang3.reflect.TypeUtils
import xyz.srclab.annotations.OutParam
import xyz.srclab.annotations.PossibleTypes
import xyz.srclab.common.base.Current
import xyz.srclab.common.base.asAny
import xyz.srclab.common.base.loadClass
import xyz.srclab.common.collect.toTypedArray
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

val <T> Class<T>.arrayClass: Class<Array<T>>
    @JvmName("arrayClass") get() {
        val arrayClassName = "[L" + this.canonicalName + ";"
        return arrayClassName.loadClass()
    }

val Type.rawClass: Class<*>?
    @JvmName("rawClass") get() {
        return when (this) {
            is Class<*> -> this
            is ParameterizedType -> this.rawClass
            else -> null
        }
    }

/**
 * @throws IllegalArgumentException
 */
val @PossibleTypes(Class::class, ParameterizedType::class) Type.rawClassOrThrow: Class<*>
    @JvmName("rawClassOrThrow") get() {
        return this.rawClass ?: throw IllegalArgumentException("Only Class or ParameterizedType has raw class.")
    }

val ParameterizedType.rawClass: Class<*>
    @JvmName("rawClass") get() {
        return this.rawType.asAny()
    }

val TypeVariable<*>.upperClass: Class<*>
    @JvmName("upperClass") get() {
        val bounds = this.bounds
        return if (bounds.isEmpty()) {
            Any::class.java
        } else {
            bounds[0].upperClass
        }
    }

val WildcardType.upperClass: Class<*>
    @JvmName("upperClass") get() {
        val upperBounds = this.upperBounds
        return if (upperBounds.isEmpty()) {
            Any::class.java
        } else {
            upperBounds[0].upperClass
        }
    }

val Type.upperClass: Class<*>
    @JvmName("upperClass") get() {
        return when (this) {
            is Class<*> -> this
            is ParameterizedType -> this.rawClass
            is TypeVariable<*> -> this.upperClass
            is WildcardType -> this.upperClass
            else -> Any::class.java
        }
    }

val TypeVariable<*>.lowerClass: Class<*>?
    @JvmName("lowerClass") get() {
        val upperClass = this.upperClass
        return if (upperClass.isFinal) upperClass else null
    }

val WildcardType.lowerClass: Class<*>?
    @JvmName("lowerClass") get() {
        val lowerBounds = this.lowerBounds
        return if (lowerBounds.isEmpty()) {
            null
        } else {
            lowerBounds[0].lowerClass
        }
    }

/**
 * @throws IllegalArgumentException
 */
val Type.lowerClass: Class<*>?
    @JvmName("lowerClass") get() {
        return when (this) {
            is Class<*> -> this
            is ParameterizedType -> this.rawClass
            is TypeVariable<*> -> this.lowerClass
            is WildcardType -> this.lowerClass
            else -> throw IllegalArgumentException()
        }
    }

val TypeVariable<*>.upperBound: Type
    @PossibleTypes(Class::class, ParameterizedType::class, GenericArrayType::class)
    @JvmName("upperBound")
    get() {
        val bounds = this.bounds
        return if (bounds.isEmpty()) {
            Any::class.java
        } else {
            bounds[0].upperBound
        }
    }

val WildcardType.upperBound: Type
    @PossibleTypes(Class::class, ParameterizedType::class, GenericArrayType::class)
    @JvmName("upperBound")
    get() {
        val upperBounds = this.upperBounds
        return if (upperBounds.isEmpty()) {
            Any::class.java
        } else {
            upperBounds[0].upperBound
        }
    }

val Type.upperBound: Type
    @PossibleTypes(Class::class, ParameterizedType::class, GenericArrayType::class)
    @JvmName("upperBound")
    get() {
        return when (this) {
            is TypeVariable<*> -> this.upperBound
            is WildcardType -> this.upperBound
            else -> this
        }
    }

val TypeVariable<*>.lowerBound: Type?
    @PossibleTypes(Class::class, ParameterizedType::class, GenericArrayType::class)
    @JvmName("lowerBound")
    get() {
        val upperBound = this.upperBound
        return if (upperBound is Class<*> && upperBound.isFinal) upperBound else null
    }

val WildcardType.lowerBound: Type?
    @PossibleTypes(Class::class, ParameterizedType::class, GenericArrayType::class)
    @JvmName("lowerBound")
    get() {
        val lowerBounds = this.lowerBounds
        return if (lowerBounds.isEmpty()) {
            null
        } else {
            lowerBounds[0].lowerBound
        }
    }

val Type.lowerBound: Type?
    @PossibleTypes(Class::class, ParameterizedType::class, GenericArrayType::class)
    @JvmName("lowerBound")
    get() {
        return when (this) {
            is TypeVariable<*> -> this.lowerBound
            is WildcardType -> this.lowerBound
            else -> this
        }
    }

val WildcardType.isUnbounded: Boolean
    @JvmName("isUnbounded") get() {
        return this.upperBounds.isEmpty() && this.lowerBounds.isEmpty()
    }

val Array<out Type>.isObjectUpperBound: Boolean
    @JvmName("isObjectUpperBound") get() {
        return this.isEmpty() || (this.size == 1 && this[0] == Any::class.java)
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

/**
 * @throws NoSuchMethodException
 */
fun <T> Class<T>.constructor(vararg parameterTypes: Class<*>): Constructor<T> {
    return try {
        this.getConstructor(*parameterTypes)
    } catch (e: NoSuchMethodException) {
        throw e
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

/**
 * @throws NoSuchMethodException
 */
fun <T> Class<T>.declaredConstructor(vararg parameterTypes: Class<*>): Constructor<T> {
    return try {
        this.getDeclaredConstructor(*parameterTypes)
    } catch (e: NoSuchMethodException) {
        throw e
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

/**
 * @throws NoSuchFieldException
 */
fun Class<*>.field(name: String): Field {
    return try {
        this.getField(name)
    } catch (e: NoSuchFieldException) {
        throw e
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

/**
 * @throws NoSuchFieldException
 */
fun Class<*>.declaredField(name: String): Field {
    return try {
        this.getDeclaredField(name)
    } catch (e: NoSuchFieldException) {
        throw e
    }
}

fun Class<*>.declaredFields(): List<Field> {
    return this.declaredFields.asList()
}

fun Class<*>.ownedFieldOrNull(name: String): Field? {
    return searchFieldOrNull(name, false)
}

/**
 * @throws NoSuchFieldException
 */
fun Class<*>.ownedField(name: String): Field {
    return ownedFieldOrNull(name) ?: throw NoSuchFieldException(name)
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

/**
 * @throws NoSuchMethodException
 */
fun Class<*>.method(name: String, vararg parameterTypes: Class<*>): Method {
    return try {
        this.getMethod(name, *parameterTypes)
    } catch (e: NoSuchMethodException) {
        throw e
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

/**
 * @throws NoSuchMethodException
 */
fun Class<*>.declaredMethod(name: String, vararg parameterTypes: Class<*>): Method {
    return try {
        this.getDeclaredMethod(name, *parameterTypes)
    } catch (e: NoSuchMethodException) {
        throw e
    }
}

fun Class<*>.declaredMethods(): List<Method> {
    return this.declaredMethods.asList()
}

fun Class<*>.ownedMethodOrNull(name: String, vararg parameterTypes: Class<*>): Method? {
    return searchMethodOrNull(name, parameterTypes, false)
}

/**
 * @throws NoSuchMethodException
 */
fun Class<*>.ownedMethod(name: String, vararg parameterTypes: Class<*>): Method {
    return ownedMethodOrNull(name, *parameterTypes) ?: throw NoSuchMethodException(
        "name: $name, parameters: ${parameterTypes.contentToString()}"
    )
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

fun parameterizedType(
    rawType: Type,
    vararg actualTypeArguments: Type,
): ParameterizedType {
    return TypeUtils.parameterize(rawType.rawClass, *actualTypeArguments)
}

fun parameterizedType(
    rawType: Type,
    actualTypeArguments: Iterable<Type>,
): ParameterizedType {
    return parameterizedType(rawType, *actualTypeArguments.toTypedArray())
}

fun parameterizedTypeWithOwner(
    rawType: Type,
    ownerType: Type?,
    vararg actualTypeArguments: Type,
): ParameterizedType {
    return TypeUtils.parameterizeWithOwner(ownerType, rawType.rawClass, *actualTypeArguments)
}

fun parameterizedTypeWithOwner(
    rawType: Type,
    ownerType: Type?,
    actualTypeArguments: Iterable<Type>,
): ParameterizedType {
    return parameterizedTypeWithOwner(rawType, ownerType, *actualTypeArguments.toTypedArray())
}

fun wildcardType(upperBounds: Array<out Type>?, lowerBounds: Array<out Type>?): WildcardType {
    val uppers = upperBounds ?: arrayOf(Any::class.java)
    val lowers = lowerBounds ?: ArrayUtils.EMPTY_TYPE_ARRAY
    return TypeUtils.wildcardType().withUpperBounds(*uppers).withLowerBounds(*lowers).build()
}

fun wildcardType(upperBounds: Iterable<Type>?, lowerBounds: Iterable<Type>?): WildcardType {
    val uppers = upperBounds ?: emptyList()
    val lowers = lowerBounds ?: emptyList()
    return wildcardType(uppers.toTypedArray(), lowers.toTypedArray())
}

fun wildcardTypeWithUpperBounds(vararg upperBounds: Type): WildcardType {
    return TypeUtils.wildcardType().withUpperBounds(*upperBounds).build()
}

fun wildcardTypeWithUpperBounds(upperBounds: Iterable<Type>): WildcardType {
    return wildcardTypeWithUpperBounds(*upperBounds.toTypedArray())
}

fun wildcardTypeWithLowerBounds(vararg lowerBounds: Type): WildcardType {
    return TypeUtils.wildcardType().withLowerBounds(*lowerBounds).build()
}

fun wildcardTypeWithLowerBounds(lowerBounds: Iterable<Type>): WildcardType {
    return wildcardTypeWithLowerBounds(*lowerBounds.toTypedArray())
}

fun Type.genericArrayType(): GenericArrayType {
    return TypeUtils.genericArrayType(this)
}

/**
 * Returns type arguments of [this].
 *
 * @throws IllegalArgumentException
 */
fun @PossibleTypes(
    Class::class,
    ParameterizedType::class
) Type.findTypeArguments(): Map<TypeVariable<*>, Type> {

    fun findTypeArguments0(type: Type, @OutParam typeArguments: MutableMap<TypeVariable<*>, Type>) {
        if (type is ParameterizedType) {
            val ownerType = type.ownerType;
            if (ownerType !== null) {
                findTypeArguments0(ownerType, typeArguments)
            }
            findTypeArguments(type, typeArguments)
        }
        val rawClass = type.rawClassOrThrow
        val genericSuperclass = rawClass.genericSuperclass
        if (genericSuperclass !== null) {
            findTypeArguments0(genericSuperclass, typeArguments)
        }
        val genericInterfaces = rawClass.genericInterfaces
        for (genericInterface in genericInterfaces) {
            findTypeArguments0(genericInterface, typeArguments)
        }
    }

    val typeArguments = mutableMapOf<TypeVariable<*>, Type>()
    findTypeArguments0(this, typeArguments)
    eraseTypeVariablesSelves(typeArguments)
    return typeArguments
}

/**
 * Returns type arguments of [this].
 *
 * This method will iterate on the inheritance tree util anyone (included) contained by [to].
 *
 * @throws IllegalArgumentException
 */
fun @PossibleTypes(
    Class::class,
    ParameterizedType::class
) Type.findTypeArguments(vararg to: Class<*>): Map<TypeVariable<*>, Type> {
    return this.findTypeArguments(to.toList())
}

/**
 * Returns type arguments of [this].
 *
 * This method will iterate on the inheritance tree util anyone (included) contained by [to].
 *
 * @throws IllegalArgumentException
 */
fun @PossibleTypes(
    Class::class,
    ParameterizedType::class
) Type.findTypeArguments(to: Iterable<Class<*>>): Map<TypeVariable<*>, Type> {

    fun findTypeArguments0(
        type: Type,
        to: MutableCollection<Class<*>>,
        typeArguments: MutableMap<TypeVariable<*>, Type>
    ) {
        if (to.isEmpty()) {
            return
        }
        if (type is ParameterizedType) {
            val ownerType = type.ownerType;
            if (ownerType !== null) {
                findTypeArguments0(ownerType, to, typeArguments)
            }
            findTypeArguments(type, typeArguments)
        }
        val rawClass = type.rawClassOrThrow
        to.remove(rawClass)
        if (to.isEmpty()) {
            return
        }
        val genericSuperclass = rawClass.genericSuperclass
        if (genericSuperclass !== null) {
            findTypeArguments0(genericSuperclass, to, typeArguments)
        }
        val genericInterfaces = rawClass.genericInterfaces
        for (genericInterface in genericInterfaces) {
            findTypeArguments0(genericInterface, to, typeArguments)
        }
    }

    val typeArguments = mutableMapOf<TypeVariable<*>, Type>()
    findTypeArguments0(this, to.toMutableList(), typeArguments)
    eraseTypeVariablesSelves(typeArguments)
    return typeArguments
}

private fun findTypeArguments(
    type: ParameterizedType,
    @OutParam typeArguments: MutableMap<TypeVariable<*>, Type>
): MutableMap<TypeVariable<*>, Type> {
    val arguments = type.actualTypeArguments
    val parameters = type.rawClass.typeParameters
    for (i in parameters.indices) {
        val parameter = parameters[i]
        val argument = arguments[i]
        typeArguments[parameter] = typeArguments.getOrDefault(argument, argument)
    }
    return typeArguments
}

private fun eraseTypeVariablesSelves(@OutParam typeArguments: MutableMap<TypeVariable<*>, Type>) {
    for (entry in typeArguments.entries) {
        val param = entry.key
        val type = entry.value
        typeArguments[param] = type.eraseTypeVariables(typeArguments)
    }
}

fun Type.eraseTypeVariables(typeArgumentsGenerator: (Type) -> Map<TypeVariable<*>, Type>): Type {

    var typeArgumentsTemp: Map<TypeVariable<*>, Type>? = null

    fun getTypeArguments(): Map<TypeVariable<*>, Type> {
        val result = typeArgumentsTemp
        return if (result === null) {
            val args = typeArgumentsGenerator(this)
            typeArgumentsTemp = args
            args
        } else {
            result
        }
    }

    return when (this) {
        is Class<*> -> this
        is ParameterizedType -> {
            val actualTypeArguments = this.actualTypeArguments
            if (replaceTypeVariable(actualTypeArguments, getTypeArguments())) {
                return parameterizedTypeWithOwner(this.rawType, this.ownerType, *actualTypeArguments)
            }
            return this
        }
        is TypeVariable<*> -> {
            val actual = getTypeArguments()[this]
            if (actual === null) {
                return this
            }
            if (actual is TypeVariable<*>) {
                return actual
            }
            return actual.eraseTypeVariables(getTypeArguments())
        }
        is WildcardType -> {
            val upperBounds = this.upperBounds
            val replaceUppers = replaceTypeVariable(upperBounds, getTypeArguments())
            val lowerBounds = this.lowerBounds
            val replaceLowers = replaceTypeVariable(lowerBounds, getTypeArguments())
            if (replaceUppers || replaceLowers) {
                return wildcardType(upperBounds, lowerBounds)
            }
            return this
        }
        is GenericArrayType -> {
            val componentType = this.genericComponentType.eraseTypeVariables(getTypeArguments())
            if (componentType === this.genericComponentType) {
                return this
            }
            return componentType.genericArrayType()
        }
        else -> this
    }
}

@JvmOverloads
fun Type.eraseTypeVariables(typeArguments: Map<TypeVariable<*>, Type> = this.findTypeArguments()): Type {
    return when (this) {
        is Class<*> -> this
        is ParameterizedType -> {
            val actualTypeArguments = this.actualTypeArguments
            if (replaceTypeVariable(actualTypeArguments, typeArguments)) {
                return parameterizedTypeWithOwner(this.rawType, this.ownerType, *actualTypeArguments)
            }
            return this
        }
        is TypeVariable<*> -> {
            val actual = typeArguments[this]
            if (actual === null) {
                return this
            }
            if (actual is TypeVariable<*>) {
                return actual
            }
            return actual.eraseTypeVariables(typeArguments)
        }
        is WildcardType -> {
            val upperBounds = this.upperBounds
            val replaceUppers = replaceTypeVariable(upperBounds, typeArguments)
            val lowerBounds = this.lowerBounds
            val replaceLowers = replaceTypeVariable(lowerBounds, typeArguments)
            if (replaceUppers || replaceLowers) {
                return wildcardType(upperBounds, lowerBounds)
            }
            return this
        }
        is GenericArrayType -> {
            val componentType = this.genericComponentType.eraseTypeVariables(typeArguments)
            if (componentType === this.genericComponentType) {
                return this
            }
            return componentType.genericArrayType()
        }
        else -> this
    }
}

private fun replaceTypeVariable(@OutParam array: Array<Type>, typeArguments: Map<TypeVariable<*>, Type>): Boolean {
    var result = false
    for (i in array.indices) {
        val oldType = array[i]
        if (oldType is TypeVariable<*>) {
            val newType = typeArguments[oldType]
            if (newType !== null) {
                array[i] = newType
                result = true
            }
        } else {
            val newType = oldType.eraseTypeVariables(typeArguments)
            if (oldType !== newType) {
                array[i] = newType
                result = true
            }
        }
    }
    return result
}

/**
 * Returns generic signature of [this].
 *
 * This method will iterate on the inheritance tree util anyone (included) contained by [targets].
 *
 * For example:
 * ```
 * class StringFoo : Foo<String>
 * ```
 * Let [this] be `StringFoo`, [targets] contains only `Foo.class`, this method will return `Foo<String>`.
 *
 * @throws IllegalArgumentException
 * @throws SignatureNotFoundException
 */
@PossibleTypes(Class::class, ParameterizedType::class)
fun @PossibleTypes(
    Class::class,
    ParameterizedType::class
) Type.genericSignature(vararg targets: Class<*>): Type {
    return this.genericSignature(targets.toList())
}

/**
 * Returns generic signature of [this].
 *
 * This method will iterate on the inheritance tree util anyone (included) contained by [targets].
 *
 * For example:
 * ```
 * class StringFoo : Foo<String>
 * ```
 * Let [this] be `StringFoo`, [targets] contains only `Foo.class`, this method will return `Foo<String>`.
 *
 * @throws IllegalArgumentException
 * @throws SignatureNotFoundException
 */
@PossibleTypes(Class::class, ParameterizedType::class)
fun @PossibleTypes(
    Class::class,
    ParameterizedType::class
) Type.genericSignature(targets: Iterable<Class<*>>): Type {
    return this.findGenericSignature(null, targets)
        ?: throw SignatureNotFoundException("$this for $targets")
}

/**
 * Returns generic signature of [this].
 *
 * This method will iterate on the inheritance tree util anyone (included) contained by [targets].
 *
 * For example:
 * ```
 * class StringFoo : Foo<String>
 * ```
 * Let [this] be `StringFoo`, [targets] contains only `Foo.class`, this method will return `Foo<String>`.
 *
 * @throws IllegalArgumentException
 * @throws SignatureNotFoundException
 */
@PossibleTypes(Class::class, ParameterizedType::class)
fun @PossibleTypes(
    Class::class,
    ParameterizedType::class
) Type.genericSignature(typeArguments: Map<TypeVariable<*>, Type>, vararg targets: Class<*>): Type {
    return this.genericSignature(typeArguments, targets.asList())
}

/**
 * Returns generic signature of [this].
 *
 * This method will iterate on the inheritance tree util anyone (included) contained by [targets].
 *
 * For example:
 * ```
 * class StringFoo : Foo<String>
 * ```
 * Let [this] be `StringFoo`, [targets] contains only `Foo.class`, this method will return `Foo<String>`.
 *
 * @throws IllegalArgumentException
 * @throws SignatureNotFoundException
 */
@PossibleTypes(Class::class, ParameterizedType::class)
fun @PossibleTypes(
    Class::class,
    ParameterizedType::class
) Type.genericSignature(typeArguments: Map<TypeVariable<*>, Type>, targets: Iterable<Class<*>>): Type {
    return this.findGenericSignature(typeArguments, targets)
        ?: throw SignatureNotFoundException("$this for $targets")
}

/**
 * Returns generic signature of [this].
 *
 * This method will iterate on the inheritance tree util anyone (included) contained by [targets].
 *
 * For example:
 * ```
 * class StringFoo : Foo<String>
 * ```
 * Let [this] be `StringFoo`, [targets] contains only `Foo.class`, this method will return `Foo<String>`.
 *
 * @throws IllegalArgumentException
 */
@JvmOverloads
@PossibleTypes(Class::class, ParameterizedType::class)
fun @PossibleTypes(
    Class::class,
    ParameterizedType::class
) Type.findGenericSignature(typeArguments: Map<TypeVariable<*>, Type>? = null, vararg targets: Class<*>): Type? {
    return this.findGenericSignature(typeArguments, targets.asList())
}

/**
 * Returns generic signature of [this].
 *
 * This method will iterate on the inheritance tree util anyone (included) contained by [targets].
 *
 * For example:
 * ```
 * class StringFoo : Foo<String>
 * ```
 * Let [this] be `StringFoo`, [targets] contains only `Foo.class`, this method will return `Foo<String>`.
 *
 * @throws IllegalArgumentException
 */
@JvmOverloads
@PossibleTypes(Class::class, ParameterizedType::class)
fun @PossibleTypes(
    Class::class,
    ParameterizedType::class
) Type.findGenericSignature(typeArguments: Map<TypeVariable<*>, Type>? = null, targets: Iterable<Class<*>>): Type? {
    for (target in targets) {
        val result = if (target.isInterface)
            this.findGenericInterface(typeArguments, target)
        else
            this.findGenericSuperclass(typeArguments, target)
        if (result !== null) {
            return result
        }
    }
    return null
}

/**
 * Returns generic signature of [this].
 *
 * This method will iterate on the inheritance tree util anyone (included) contained by [targets].
 *
 * For example:
 * ```
 * class StringFoo : Foo<String>
 * ```
 * Let [this] be `StringFoo`, [targets] contains only `Foo.class`, this method will return `Foo<String>`.
 *
 * @throws IllegalArgumentException
 */
@JvmOverloads
@PossibleTypes(Class::class, ParameterizedType::class)
fun @PossibleTypes(
    Class::class,
    ParameterizedType::class
) Type.findGenericSuperclass(typeArguments: Map<TypeVariable<*>, Type>? = null, vararg targets: Class<*>): Type? {
    return this.findGenericSuperclass(typeArguments, targets.asList())
}

/**
 * Returns generic signature of [this].
 *
 * This method will iterate on the inheritance tree util anyone (included) contained by [targets].
 *
 * For example:
 * ```
 * class StringFoo : Foo<String>
 * ```
 * Let [this] be `StringFoo`, [targets] contains only `Foo.class`, this method will return `Foo<String>`.
 *
 * @throws IllegalArgumentException
 */
@JvmOverloads
@PossibleTypes(Class::class, ParameterizedType::class)
fun @PossibleTypes(
    Class::class,
    ParameterizedType::class
) Type.findGenericSuperclass(typeArguments: Map<TypeVariable<*>, Type>? = null, targets: Iterable<Class<*>>): Type? {
    var rawClass = this.rawClassOrThrow
    if (targets.contains(rawClass)) {
        return if (typeArguments === null) {
            this.eraseTypeVariables { it.findTypeArguments(rawClass) }
        } else {
            this.eraseTypeVariables(typeArguments)
        }
    }

    var genericType: Type? = rawClass.genericSuperclass
    while (genericType !== null) {
        rawClass = genericType.rawClassOrThrow
        if (targets.contains(rawClass)) {
            return if (typeArguments === null) {
                genericType.eraseTypeVariables { it.findTypeArguments(rawClass) }
            } else {
                genericType.eraseTypeVariables(typeArguments)
            }
        }
        genericType = rawClass.genericSuperclass
    }
    return null
}

/**
 * Returns generic signature of [this].
 *
 * This method will iterate on the inheritance tree util anyone (included) contained by [targets].
 *
 * For example:
 * ```
 * class StringFoo : Foo<String>
 * ```
 * Let [this] be `StringFoo`, [targets] contains only `Foo.class`, this method will return `Foo<String>`.
 *
 * @throws IllegalArgumentException
 */
@JvmOverloads
@PossibleTypes(Class::class, ParameterizedType::class)
fun @PossibleTypes(
    Class::class,
    ParameterizedType::class
) Type.findGenericInterface(typeArguments: Map<TypeVariable<*>, Type>? = null, vararg targets: Class<*>): Type? {
    return this.findGenericInterface(typeArguments, targets.asList())
}

/**
 * Returns generic signature of [this].
 *
 * This method will iterate on the inheritance tree util anyone (included) contained by [targets].
 *
 * For example:
 * ```
 * class StringFoo : Foo<String>
 * ```
 * Let [this] be `StringFoo`, [targets] contains only `Foo.class`, this method will return `Foo<String>`.
 *
 * @throws IllegalArgumentException
 */
@JvmOverloads
@PossibleTypes(Class::class, ParameterizedType::class)
fun @PossibleTypes(
    Class::class,
    ParameterizedType::class
) Type.findGenericInterface(typeArguments: Map<TypeVariable<*>, Type>? = null, targets: Iterable<Class<*>>): Type? {

    //Level searching
    fun findInterface(genericTypes: List<Type>, targets: Iterable<Class<*>>): Type? {
        for (genericType in genericTypes) {
            val genericClass = genericType.rawClassOrThrow
            if (targets.contains(genericClass)) {
                return if (typeArguments === null) {
                    genericType.eraseTypeVariables { it.findTypeArguments(genericClass) }
                } else {
                    genericType.eraseTypeVariables(typeArguments)
                }
            }
        }
        val nextLevel = mutableListOf<Type>()
        for (genericType in genericTypes) {
            val genericClass = genericType.rawClassOrThrow
            nextLevel.addAll(genericClass.genericInterfaces)
        }
        if (nextLevel.isEmpty()) {
            return null
        }
        return findInterface(nextLevel, targets)
    }

    val rawClass = this.rawClassOrThrow
    if (targets.contains(rawClass)) {
        return if (typeArguments === null) {
            this.eraseTypeVariables { it.findTypeArguments(rawClass) }
        } else {
            this.eraseTypeVariables(typeArguments)
        }
    }
    val tryInterfaces = findInterface(rawClass.genericInterfaces.asList(), targets)
    if (tryInterfaces !== null) {
        return if (typeArguments === null) {
            tryInterfaces.eraseTypeVariables { it.findTypeArguments(tryInterfaces.rawClassOrThrow) }
        } else {
            tryInterfaces.eraseTypeVariables(typeArguments)
        }
    }

    // Try interfaces of superclasses
    var superclass = rawClass.superclass
    var trySuperInterface: Type?
    while (superclass !== null) {
        trySuperInterface = findInterface(superclass.genericInterfaces.asList(), targets)
        if (trySuperInterface !== null) {
            return if (typeArguments === null) {
                trySuperInterface.eraseTypeVariables { it.findTypeArguments(trySuperInterface.rawClassOrThrow) }
            } else {
                trySuperInterface.eraseTypeVariables(typeArguments)
            }
        }
        superclass = superclass.superclass
    }
    return null
}

open class SignatureNotFoundException @JvmOverloads constructor(message: String? = null) : RuntimeException(message)