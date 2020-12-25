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
        ?: throw SignatureNotFoundException("$this for ${targets.toParameterTypesString()}")
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
        ?: throw SignatureNotFoundException("$this for ${targets.toParameterTypesString()}")
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