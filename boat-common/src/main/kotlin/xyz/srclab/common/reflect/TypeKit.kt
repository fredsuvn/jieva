@file:JvmName("TypeKit")
@file:JvmMultifileClass

package xyz.srclab.common.reflect

import org.apache.commons.lang3.ArrayUtils
import org.apache.commons.lang3.reflect.TypeUtils
import xyz.srclab.annotations.OutParam
import xyz.srclab.annotations.PossibleTypes
import xyz.srclab.common.base.asAny
import xyz.srclab.common.collection.BaseIterableOps.Companion.toTypedArray
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

@JvmOverloads
fun parameterizedType(
    rawType: Type,
    ownerType: Type? = null,
    actualTypeArguments: Iterable<Type>,
): ParameterizedType {
    return parameterizedType(rawType, ownerType, actualTypeArguments.toTypedArray())
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

fun Type.genericArrayType(): GenericArrayType {
    return TypeUtils.genericArrayType(this)
}

/**
 * @throws IllegalArgumentException
 */
fun @PossibleTypes(
    Class::class,
    ParameterizedType::class
) Type.findTypeArguments(vararg to: Class<*>): Map<TypeVariable<*>, Type> {
    return this.findTypeArguments(to.toList())
}

/**
 * @throws IllegalArgumentException
 */
@JvmOverloads
fun @PossibleTypes(
    Class::class,
    ParameterizedType::class
) Type.findTypeArguments(to: List<Class<*>>? = null): Map<TypeVariable<*>, Type> {

    fun findTypeArguments(
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

    fun findTypeArguments(
        type: Type,
        typeArguments: MutableMap<TypeVariable<*>, Type>
    ): MutableMap<TypeVariable<*>, Type> {
        if (type is ParameterizedType) {
            val ownerType = type.ownerType;
            if (ownerType !== null) {
                findTypeArguments(ownerType, typeArguments)
            }
            findTypeArguments(type, typeArguments)
        }
        val rawClass = type.rawClass
        val genericSuperclass = rawClass.genericSuperclass
        if (genericSuperclass !== null) {
            findTypeArguments(genericSuperclass, typeArguments)
        }
        val genericInterfaces = rawClass.genericInterfaces
        for (genericInterface in genericInterfaces) {
            findTypeArguments(genericInterface, typeArguments)
        }
        return typeArguments
    }

    fun findTypeArguments(
        type: Type,
        to: MutableCollection<Class<*>>,
        typeArguments: MutableMap<TypeVariable<*>, Type>
    ): MutableMap<TypeVariable<*>, Type> {
        if (to.isEmpty()) {
            return typeArguments
        }
        if (type is ParameterizedType) {
            val ownerType = type.ownerType;
            if (ownerType !== null) {
                findTypeArguments(ownerType, typeArguments)
            }
            findTypeArguments(type, typeArguments)
        }
        val rawClass = type.rawClass
        to.remove(rawClass)
        if (to.isEmpty()) {
            return typeArguments
        }
        val genericSuperclass = rawClass.genericSuperclass
        if (genericSuperclass !== null) {
            findTypeArguments(genericSuperclass, to, typeArguments)
        }
        val genericInterfaces = rawClass.genericInterfaces
        for (genericInterface in genericInterfaces) {
            findTypeArguments(genericInterface, to, typeArguments)
        }
        return typeArguments
    }

    val typeArguments = if (to === null || to.isEmpty()) {
        findTypeArguments(this, mutableMapOf())
    } else {
        findTypeArguments(this, to.toMutableList(), mutableMapOf())
    }

    fun eraseTypeVariables(@OutParam typeArguments: MutableMap<TypeVariable<*>, Type>) {
        for (entry in typeArguments.entries) {
            val param = entry.key
            val type = entry.value
            typeArguments[param] = type.eraseTypeVariables(typeArguments)
        }
    }

    eraseTypeVariables(typeArguments)
    return typeArguments
}

@JvmOverloads
@PossibleTypes(Class::class, ParameterizedType::class, WildcardType::class, GenericArrayType::class)
fun Type.eraseTypeVariables(typeArguments: Map<TypeVariable<*>, Type> = this.findTypeArguments()): Type {

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
    return this.genericSignature(targets.asList())
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
fun @PossibleTypes(Class::class, ParameterizedType::class) Type.genericSignature(targets: Iterable<Class<*>>): Type {
    return this.findGenericSignature(targets)
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
    return this.findGenericSignature(targets.asList())
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
fun @PossibleTypes(
    Class::class,
    ParameterizedType::class
) Type.findGenericSignature(targets: Iterable<Class<*>>): Type? {
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
    return this.findGenericSuperclass(targets.asList())
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
fun @PossibleTypes(
    Class::class,
    ParameterizedType::class
) Type.findGenericSuperclass(targets: Iterable<Class<*>>): Type? {
        var rawClass = this.rawClass
        if (targets.contains(rawClass)) {
            return this.eraseTypeVariables(this.findTypeArguments(rawClass))
        }

        var genericType: Type? = rawClass.genericSuperclass
        while (genericType !== null) {
            rawClass = genericType.rawClass
            if (targets.contains(rawClass)) {
                return genericType.eraseTypeVariables(this.findTypeArguments(rawClass))
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
    return this.findGenericInterface(targets.asList())
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
@JvmOverloads
@PossibleTypes(Class::class, ParameterizedType::class)
fun @PossibleTypes(
    Class::class,
    ParameterizedType::class
) Type.findGenericInterface(typeArguments: Map<TypeVariable<*>, Type>? = null, targets: Iterable<Class<*>>): Type? {

    //Level searching
    fun findInterface(genericTypes: List<Type>, targets: Iterable<Class<*>>): Type? {
        for (genericType in genericTypes) {
            val genericClass = genericType.rawClass
            if (targets.contains(genericClass)) {
                return genericType.eraseTypeVariables(typeArguments?:this.findTypeArguments(genericClass))
            }
        }
        val nextLevel = mutableListOf<Type>()
        for (genericType in genericTypes) {
            val genericClass = genericType.rawClass
            nextLevel.addAll(genericClass.genericInterfaces)
        }
        if (nextLevel.isEmpty()) {
            return null
        }
        return findInterface(nextLevel, targets)
    }

    val rawClass = this.rawClass
    if (targets.contains(rawClass)) {
        return this.eraseTypeVariables(typeArguments?:this.findTypeArguments(rawClass))
    }
    val tryInterfaces = findInterface(rawClass.genericInterfaces.asList(), targets)
    if (tryInterfaces !== null) {
        return tryInterfaces
    }

    // Try interfaces of superclasses
    var superclass = rawClass.superclass
    var trySuperclass: Type? = null
    while (superclass !== null) {
        trySuperclass = findInterface(superclass.genericInterfaces.asList(), targets)
        if (trySuperclass !== null) {
            return trySuperclass
        }
        superclass = superclass.superclass
    }
    return trySuperclass
}

open class SignatureNotFoundException @JvmOverloads constructor(message: String? = null) : RuntimeException(message)