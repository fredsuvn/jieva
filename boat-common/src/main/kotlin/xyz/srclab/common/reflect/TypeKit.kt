@file:JvmName("TypeKit")
@file:JvmMultifileClass

package xyz.srclab.common.reflect

import org.apache.commons.lang3.ArrayUtils
import org.apache.commons.lang3.reflect.TypeUtils
import xyz.srclab.annotations.OutParam
import xyz.srclab.annotations.PossibleTypes
import xyz.srclab.common.base.asAny
import java.lang.reflect.*

/**
 * @throws IllegalArgumentException
 */
val @PossibleTypes(Class::class, ParameterizedType::class) Type.rawClass: Class<*>
    @JvmName("rawClass") get() {
        return when (this) {
            is Class<*> -> this
            is ParameterizedType -> this.rawClass
            else -> throw IllegalArgumentException("Only Class and ParameterizedType has rawClass.")
        }
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

@JvmOverloads
fun parameterizedType(
    rawType: Type,
    ownerType: Type? = null,
    actualTypeArguments: Array<out Type>,
): ParameterizedType {
    return if (ownerType === null) {
        TypeUtils.parameterize(rawType.rawClass, *actualTypeArguments)
    } else {
        TypeUtils.parameterizeWithOwner(ownerType, rawType.rawClass, *actualTypeArguments)
    }
}

fun wildcardType(upperBounds: Array<out Type>?, lowerBounds: Array<out Type>?): WildcardType {
    val uppers = upperBounds ?: arrayOf(Any::class.java)
    val lowers = lowerBounds ?: ArrayUtils.EMPTY_TYPE_ARRAY
    return TypeUtils.wildcardType().withUpperBounds(*uppers).withLowerBounds(*lowers).build()
}

fun Type.genericArrayType(): GenericArrayType {
    return TypeUtils.genericArrayType(this)
}

/**
 * @throws IllegalArgumentException
 */
fun @PossibleTypes(Class::class, ParameterizedType::class) Type.getTypeArguments(): Map<TypeVariable<*>, Type> {
    val rawClass = this.rawClass
    val superclass = rawClass.genericSuperclass
    val interfaces = rawClass.genericInterfaces
    val actual = mutableListOf<Type>()
    val params = mutableListOf<TypeVariable<*>>()
    if (superclass is ParameterizedType) {
        actual.addAll(superclass.actualTypeArguments)
        params.addAll(superclass.rawClass.typeParameters)
    }
    for (inter in interfaces) {
        if (inter is ParameterizedType) {
            actual.addAll(inter.actualTypeArguments)
            params.addAll(inter.rawClass.typeParameters)
        }
    }
    if (this is ParameterizedType) {
        actual.addAll(this.actualTypeArguments)
        params.addAll(rawClass.typeParameters)
    }
    return params.zip(actual).toMap()
}

@PossibleTypes(Class::class, ParameterizedType::class, WildcardType::class, GenericArrayType::class)
fun Type.eraseTypeVariables(typeArguments: Map<TypeVariable<*>, Type>): Type {

    fun replaceArray(@OutParam array: Array<Type>): Boolean {
        var result = false
        for (i in array.indices) {
            val oldType = array[i]
            if (oldType is TypeVariable<*>) {
                val newType = typeArguments[oldType]
                if (newType !== null) {
                    array[i] = newType
                    result = true
                }
            }
        }
        return result
    }

    return when (this) {
        is Class<*> -> this
        is ParameterizedType -> {
            val actualTypeArguments = this.actualTypeArguments
            if (replaceArray(actualTypeArguments)) {
                return parameterizedType(this.rawType, this.ownerType, actualTypeArguments)
            }
            return this
        }
        is TypeVariable<*> -> {
            val actual = typeArguments[this]
            return actual ?: this
        }
        is WildcardType -> {
            val upperBounds = this.upperBounds
            val replaceUppers = replaceArray(upperBounds)
            val lowerBounds = this.lowerBounds
            val replaceLowers = replaceArray(lowerBounds)
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

/**
 * Returns generic signature of [this].
 *
 * For example:
 * ```
 * class StringFoo : Foo<String>
 * ```
 * Let [this] be `StringFoo`, [targets] be `Foo.class`, this method will return `Foo<String>`.
 *
 * @throws IllegalArgumentException
 * @throws SignatureNotFoundException
 */
@PossibleTypes(Class::class, ParameterizedType::class)
fun @PossibleTypes(Class::class, ParameterizedType::class) Type.genericSignature(vararg targets: Class<*>): Type {
    return this.findGenericSignature(*targets)
        ?: throw SignatureNotFoundException("$this for ${targets.toParameterTypesString()}")
}

/**
 * Returns generic signature of [this].
 *
 * For example:
 * ```
 * class StringFoo : Foo<String>
 * ```
 * Let [this] be `StringFoo`, [targets] be `Foo.class`, this method will return `Foo<String>`.
 *
 * @throws IllegalArgumentException
 */
@PossibleTypes(Class::class, ParameterizedType::class)
fun @PossibleTypes(Class::class, ParameterizedType::class) Type.findGenericSignature(vararg targets: Class<*>): Type? {
    for (target in targets) {
        val result = if (target.isInterface)
            this.findGenericInterface(target)
        else
            this.findGenericSuperclass(target)
        if (result !== null) {
            return result
        }
    }
    return null
}

/**
 * Returns generic signature of [this].
 *
 * For example:
 * ```
 * class StringFoo : Foo<String>
 * ```
 * Let [this] be `StringFoo`, [targets] be `Foo.class`, this method will return `Foo<String>`.
 *
 * @throws IllegalArgumentException
 */
@PossibleTypes(Class::class, ParameterizedType::class)
fun @PossibleTypes(Class::class, ParameterizedType::class) Type.findGenericSuperclass(vararg targets: Class<*>): Type? {
    var rawClass = this.rawClass
    if (targets.contains(rawClass)) {
        return this
    }

    var genericType: Type? = rawClass.genericSuperclass
    while (genericType !== null) {
        rawClass = genericType.rawClass
        if (targets.contains(rawClass)) {
            return genericType
        }
        genericType = rawClass.genericSuperclass
    }
    return null
}

/**
 * Returns generic signature of [this].
 *
 * For example:
 * ```
 * class StringFoo : Foo<String>
 * ```
 * Let [this] be `StringFoo`, [targets] be `Foo.class`, this method will return `Foo<String>`.
 *
 * @throws IllegalArgumentException
 */
@PossibleTypes(Class::class, ParameterizedType::class)
fun @PossibleTypes(Class::class, ParameterizedType::class) Type.findGenericInterface(vararg targets: Class<*>): Type? {

    //Level searching
    fun findInterface(genericTypes: List<Type>, vararg targets: Class<*>): Type? {
        for (genericType in genericTypes) {
            val genericClass = genericType.rawClass
            if (targets.contains(genericClass)) {
                return genericType
            }
        }
        val nextLevel = mutableListOf<Type>()
        for (genericType in genericTypes) {
            val genericClass = genericType.rawClass
            nextLevel.addAll(genericClass.genericInterfaces)
        }
        return findInterface(nextLevel, *targets)
    }

    val rawClass = this.rawClass
    if (targets.contains(rawClass)) {
        return this
    }
    val tryInterfaces = findInterface(rawClass.genericInterfaces.asList(), *targets)
    if (tryInterfaces !== null) {
        return tryInterfaces
    }

    // Try interfaces of superclasses
    var superclass = rawClass.superclass
    var trySuperclass: Type? = null
    while (superclass !== null) {
        trySuperclass = findInterface(superclass.genericInterfaces.asList(), *targets)
        if (trySuperclass !== null) {
            return trySuperclass
        }
        superclass = superclass.superclass
    }
    return trySuperclass
}

/**
 * Returns actual type of [this] as possible, may failed to return non-actual type.
 *
 * For example:
 * ```
 * class Foo<T>
 * class StringFoo : Foo<String>
 * ```
 * Let [this] be `T`, [lowerType] be `StringFoo.class`, this method will return `String.class`.
 */
@JvmOverloads
fun TypeVariable<*>.actualType(lowerType: Type, typeArguments: Map<TypeVariable<*>, Type> = emptyMap()): Type {
    return this.findActualType(lowerType, typeArguments)
        ?: throw IllegalArgumentException("Actual type of $this on $lowerType was not found.")
}

/**
 * Returns actual type of [this] as possible.
 *
 * For example:
 * ```
 * class Foo<T>
 * class StringFoo : Foo<String>
 * ```
 * Let [this] be `T`, [lowerType] be `StringFoo.class`, this method will return `String.class`.
 */
@JvmOverloads
fun TypeVariable<*>.findActualType(lowerType: Type, typeArguments: Map<TypeVariable<*>, Type> = emptyMap()): Type? {
    return when (lowerType) {
        is Class<*> -> this.findActualType(lowerType, typeArguments)
        is ParameterizedType -> this.findActualType(lowerType, typeArguments)
        else -> this.findActualType(lowerType.rawClass)
    }
}

/**
 * Returns actual type of [this] as possible.
 *
 * For example:
 * ```
 * class Foo<T>
 * class StringFoo : Foo<String>
 * ```
 * Let [this] be `T`, [lowerType] be `StringFoo.class`, this method will return `String.class`.
 */
@JvmOverloads
fun TypeVariable<*>.findActualType(
    lowerType: Class<*>,
    typeArguments: Map<TypeVariable<*>, Type> = emptyMap()
): Type? {
    val declaredType = this.genericDeclaration
    if (declaredType !is Class<*>) {
        return null
    }
    return this.findActualType(lowerType, declaredType)?.eraseTypeVariables(typeArguments)
}

/**
 * Returns actual type of [this] as possible.
 *
 * For example:
 * ```
 * class Foo<T>
 * class StringFoo : Foo<String>
 * ```
 * Let [this] be `T`, [lowerType] be `Foo<String>`, this method will return `String.class`.
 */
@JvmOverloads
fun TypeVariable<*>.findActualType(
    lowerType: ParameterizedType, typeArguments: Map<TypeVariable<*>, Type> = emptyMap()
): Type? {
    val declaredType = this.genericDeclaration
    if (declaredType !is Class<*>) {
        return null
    }

    if (lowerType.rawType == declaredType) {
        val typeParameters = declaredType.typeParameters
        val actualTypeArguments = lowerType.actualTypeArguments
        val index = typeParameters.indexOf(this)
        if (index < 0) {
            return null
        }
        return actualTypeArguments[index]
    } else {
        val tryActualType = this.findActualType(lowerType, declaredType)
        if (tryActualType === null
            || tryActualType !is TypeVariable<*>
            || lowerType.rawType == declaredType
        ) {
            return tryActualType
        }
        val typeParameters = lowerType.rawClass.typeParameters
        val actualTypeArguments = lowerType.actualTypeArguments
        val index = typeParameters.indexOf(tryActualType)
        if (index < 0) {
            return tryActualType.eraseTypeVariables(typeArguments)
        }
        return actualTypeArguments[index]?.eraseTypeVariables(typeArguments)
    }
}

private fun TypeVariable<*>.findActualType(lowerType: Type, declaredType: Class<*>): Type? {
    val genericSignature = lowerType.findGenericSignature(declaredType)
    if (genericSignature === null || genericSignature !is ParameterizedType) {
        return null
    }

    val typeParameters = declaredType.typeParameters
    val actualTypeArguments = genericSignature.actualTypeArguments
    val index = typeParameters.indexOf(this)
    if (index < 0) {
        return null
    }
    return actualTypeArguments[index]
}

open class SignatureNotFoundException @JvmOverloads constructor(message: String? = null) : RuntimeException(message)