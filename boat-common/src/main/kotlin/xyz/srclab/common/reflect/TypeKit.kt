@file:JvmName("TypeKit")
@file:JvmMultifileClass

package xyz.srclab.common.reflect

import org.apache.commons.lang3.ArrayUtils
import org.apache.commons.lang3.reflect.TypeUtils
import xyz.srclab.annotations.PossibleTypes
import xyz.srclab.common.base.asAny
import java.lang.reflect.*

val Type.rawClass: Class<*>
    @JvmName("rawClass") get() {
        return when (this) {
            is Class<*> -> this
            is ParameterizedType -> this.rawClass
            else -> throw IllegalArgumentException("$this has no raw type.")
        }
    }

val ParameterizedType.rawClass: Class<*>
    @JvmName("rawClass") get() {
        return this.rawType.asAny()
    }

val Type.upperClass: Class<*>
    @JvmName("upperClass") get() {
        return when (this) {
            is Class<*> -> this
            is ParameterizedType -> this.rawClass
            is TypeVariable<*> -> {
                val bounds = this.bounds
                return if (!bounds.isNullOrEmpty()) {
                    bounds[0].upperClass
                } else {
                    Any::class.java
                }
            }
            is WildcardType -> {
                val upperBounds = this.upperBounds
                return if (!upperBounds.isNullOrEmpty()) {
                    upperBounds[0].upperClass
                } else {
                    Any::class.java
                }
            }
            else -> Any::class.java
        }
    }

val Type.lowerClass: Class<*>?
    @JvmName("lowerClass") get() {
        return when (this) {
            is Class<*> -> this
            is ParameterizedType -> this.rawClass
            is WildcardType -> {
                val lowerBounds = this.lowerBounds
                return if (!lowerBounds.isNullOrEmpty()) {
                    lowerBounds[0].lowerClass
                } else {
                    null
                }
            }
            else -> null
        }
    }

val TypeVariable<*>.upperBound: Type
    @PossibleTypes(Class::class, ParameterizedType::class, GenericArrayType::class)
    @JvmName("upperBound")
    get() {
        val bounds = this.bounds
        if (bounds.isObjectUpperBound) {
            return Any::class.java
        }
        val b = bounds[0]
        return b
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
            else -> Any::class.java
        }
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
            is Class<*> -> if (this.isFinal) this else null
            is ParameterizedType -> this.rawClass.lowerBound
            is TypeVariable<*> -> this.upperBound.lowerBound
            is WildcardType -> this.lowerBound
            else -> null
        }
    }

val Type.deepUpperBound: Type
    @PossibleTypes(Class::class, ParameterizedType::class, GenericArrayType::class)
    @JvmName("deepUpperBound")
    get() {
        return when (this) {
            is ParameterizedType -> {
                val actualTypeArguments = this.actualTypeArguments
                var needTransform = false
                for (i in actualTypeArguments.indices) {
                    val oldType = actualTypeArguments[i]
                    val newType = oldType.deepUpperBound
                    if (oldType !== newType) {
                        needTransform = true
                        actualTypeArguments[i] = newType
                    }
                }
                if (!needTransform) {
                    return this
                }
                return parameterizedType(this.rawType, actualTypeArguments, this.ownerType)
            }
            is TypeVariable<*> -> {
                val bounds = this.bounds
                return if (bounds.isEmpty()) Any::class.java else bounds[0].deepUpperBound
            }
            is WildcardType -> {
                val upperBounds = this.upperBounds
                return if (upperBounds.isEmpty())  Any::class.java else upperBounds[0].deepUpperBound
            }
            else -> this
        }
    }

//val Type.deepLowerBound: Type
//    @PossibleTypes(Class::class, ParameterizedType::class, GenericArrayType::class)
//    @JvmName("deepLowerBound")
//    get() {
//        return when (this) {
//            is ParameterizedType -> {
//                val actualTypeArguments = this.actualTypeArguments
//                var needTransform = false
//                for (i in actualTypeArguments.indices) {
//                    val oldType = actualTypeArguments[i]
//                    val newType = oldType.deepLowerBound
//                    if (oldType !== newType) {
//                        needTransform = true
//                        actualTypeArguments[i] = newType
//                    }
//                }
//                if (!needTransform) {
//                    return this
//                }
//                return parameterizedType(this.rawType, actualTypeArguments, this.ownerType)
//            }
//            is TypeVariable<*> -> this.lowerBound
//            is WildcardType -> this.lowerBound
//            else -> this
//        }
//    }

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
    actualTypeArguments: Array<out Type>? = null,
    ownerType: Type? = null,
): ParameterizedType {
    return if (ownerType === null) {
        TypeUtils.parameterize(rawType.rawClass, *(actualTypeArguments ?: ArrayUtils.EMPTY_TYPE_ARRAY))
    } else {
        TypeUtils.parameterizeWithOwner(
            ownerType,
            rawType.rawClass,
            *(actualTypeArguments ?: ArrayUtils.EMPTY_TYPE_ARRAY)
        )
    }
}

fun wildcardTypeWithUpperBounds(upperBounds: Array<out Type>): WildcardType {
    return TypeUtils.wildcardType().withUpperBounds(*upperBounds).build()
}

fun wildcardTypeWithLowerBounds(lowerBounds: Array<out Type>): WildcardType {
    return TypeUtils.wildcardType().withLowerBounds(*lowerBounds).build()
}

fun Type.genericArrayType(): GenericArrayType {
    return TypeUtils.genericArrayType(this)
}


@PossibleTypes(Class::class, ParameterizedType::class, WildcardType::class, GenericArrayType::class)
fun Type.eraseTypeVariable():Type {
    if (this is Class<*>) {
        return this
    }
    TODO()
}


/**
 * StringFoo(Foo.class) -> Foo<String>
 */
@PossibleTypes(Class::class, ParameterizedType::class)
fun Type.genericSignature(target: Class<*>): Type {
    return this.findGenericSignature(target)
        ?: throw IllegalArgumentException("Generic signature of $target for $this was not found.")
}

/**
 * StringFoo(Foo.class) -> Foo<String>
 */
@PossibleTypes(Class::class, ParameterizedType::class)
fun Type.findGenericSignature(vararg targets: Class<*>): Type? {
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
 * StringFoo(Foo.class) -> Foo<String>
 */
@PossibleTypes(Class::class, ParameterizedType::class)
fun Type.findGenericSuperclass(vararg targets: Class<*>): Type? {
    var rawClass = this.upperClass
    if (targets.contains(rawClass)) {
        return this
    }

    var genericType: Type? = rawClass.genericSuperclass
    while (genericType !== null) {
        rawClass = genericType.upperClass
        if (targets.contains(rawClass)) {
            return genericType
        }
        genericType = rawClass.genericSuperclass
    }
    return null
}

/**
 * StringFoo(Foo.class) -> Foo<String>
 */
@PossibleTypes(Class::class, ParameterizedType::class)
fun Type.findGenericInterface(vararg targets: Class<*>): Type? {
    val rawClass = this.upperClass
    if (targets.contains(rawClass)) {
        return this
    }

    val genericInterfaces = rawClass.genericInterfaces

    fun findInterface(genericTypes: Array<out Type>, vararg targets: Class<*>): Type? {
        //Search level first
        for (genericType in genericTypes) {
            if (targets.contains(genericType.upperClass)) {
                return genericType
            }
        }
        for (genericType in genericTypes) {
            val result = findInterface(genericType.upperClass.genericInterfaces, *targets)
            if (result !== null) {
                return result
            }
        }
        return null
    }

    return findInterface(genericInterfaces, *targets)
}

/**
 * <T>(StringFoo.class) -> String
 */
fun TypeVariable<*>.actualType(lowerType: Type): Type {
    return this.findActualType(lowerType)
        ?: throw IllegalArgumentException("Actual type of $this on $lowerType was not found.")
}

/**
 * <T>(StringFoo.class) -> String
 */
fun TypeVariable<*>.findActualType(lowerType: Type): Type? {
    return when (lowerType) {
        is Class<*> -> this.findActualType(lowerType)
        is ParameterizedType -> this.findActualType(lowerType)
        else -> this.findActualType(lowerType.rawClass)
    }
}

/**
 * <T>(StringFoo.class) -> String
 */
fun TypeVariable<*>.findActualType(lowerType: Class<*>): Type? {
    val declaredType = this.genericDeclaration
    if (declaredType !is Class<*>) {
        return null
    }
    return this.findActualType(lowerType, declaredType)
}

/**
 * <T>(StringFoo.class) -> String
 */
fun TypeVariable<*>.findActualType(lowerType: ParameterizedType): Type? {
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
            return tryActualType
        }
        return actualTypeArguments[index]
    }
}

/**
 * <T>(StringFoo.class) -> String
 */
fun TypeVariable<*>.findActualType(lowerType: Type, declaredType: Class<*>): Type? {
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