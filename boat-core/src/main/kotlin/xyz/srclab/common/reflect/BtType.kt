/**
 * Type utilities.
 */
@file:JvmName("BtType")

package xyz.srclab.common.reflect

import org.apache.commons.lang3.ArrayUtils
import org.apache.commons.lang3.reflect.TypeUtils
import xyz.srclab.common.collect.toTypedArray
import java.lang.reflect.GenericArrayType
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.lang.reflect.WildcardType

/**
 * Creates a [ParameterizedType].
 */
fun parameterizedType(
    rawType: Class<*>,
    vararg actualTypeArguments: Type,
): ParameterizedType {
    return TypeUtils.parameterize(rawType, *actualTypeArguments)
}

/**
 * Creates a [ParameterizedType].
 */
fun parameterizedType(
    rawType: Class<*>,
    actualTypeArguments: Iterable<Type>,
): ParameterizedType {
    return parameterizedType(rawType, *actualTypeArguments.toTypedArray())
}

/**
 * Creates a [ParameterizedType].
 */
fun parameterizedTypeWithOwner(
    rawType: Class<*>,
    ownerType: Type?,
    vararg actualTypeArguments: Type,
): ParameterizedType {
    return TypeUtils.parameterizeWithOwner(ownerType, rawType, *actualTypeArguments)
}

/**
 * Creates a [ParameterizedType].
 */
fun parameterizedTypeWithOwner(
    rawType: Class<*>,
    ownerType: Type?,
    actualTypeArguments: Iterable<Type>,
): ParameterizedType {
    return parameterizedTypeWithOwner(rawType, ownerType, *actualTypeArguments.toTypedArray())
}

/**
 * Returns a [ParameterizedType] with actual arguments.
 */
@JvmName("parameterizedTypeWithArguments")
fun ParameterizedType.withArguments(vararg actualTypeArguments: Type): ParameterizedType {
    if (this.actualTypeArguments.contentEquals(actualTypeArguments)) {
        return this
    }
    return parameterizedTypeWithOwner(this.rawType.rawClass, this.ownerType, *actualTypeArguments)
}

/**
 * Returns a [ParameterizedType] with actual arguments.
 */
@JvmName("parameterizedTypeWithArguments")
fun ParameterizedType.withArguments(actualTypeArguments: Iterable<Type>): ParameterizedType {
    return withArguments(*actualTypeArguments.toTypedArray())
}

/**
 * Creates a [WildcardType].
 */
fun wildcardType(upperBounds: Array<out Type>?, lowerBounds: Array<out Type>?): WildcardType {
    val uppers = upperBounds ?: arrayOf(Any::class.java)
    val lowers = lowerBounds ?: ArrayUtils.EMPTY_TYPE_ARRAY
    return TypeUtils.wildcardType().withUpperBounds(*uppers).withLowerBounds(*lowers).build()
}

/**
 * Creates a [WildcardType].
 */
fun wildcardType(upperBounds: Iterable<Type>?, lowerBounds: Iterable<Type>?): WildcardType {
    val uppers = upperBounds ?: emptyList()
    val lowers = lowerBounds ?: emptyList()
    return wildcardType(uppers.toTypedArray(), lowers.toTypedArray())
}

/**
 * Returns a [WildcardType] with bounds.
 */
@JvmName("wildcardTypeWithBounds")
fun WildcardType.withBounds(upperBounds: Array<out Type>?, lowerBounds: Array<out Type>?): WildcardType {
    if (this.upperBounds.contentDeepEquals(upperBounds) && this.lowerBounds.contentDeepEquals(lowerBounds)) {
        return this
    }
    return wildcardType(upperBounds, lowerBounds)
}

/**
 * Returns a [WildcardType] with bounds.
 */
@JvmName("wildcardTypeWithBounds")
fun WildcardType.withBounds(upperBounds: Iterable<Type>?, lowerBounds: Iterable<Type>?): WildcardType {
    return withBounds(upperBounds?.toTypedArray(), lowerBounds?.toTypedArray())
}

/**
 * Returns generic array type of [this] which as component type.
 */
fun Type.genericArrayType(): GenericArrayType {
    return TypeUtils.genericArrayType(this)
}