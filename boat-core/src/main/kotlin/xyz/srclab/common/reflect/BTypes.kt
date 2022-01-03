@file:JvmName("BTypes")

package xyz.srclab.common.reflect

import org.apache.commons.lang3.ArrayUtils
import org.apache.commons.lang3.reflect.TypeUtils
import xyz.srclab.common.collect.toTypedArray
import java.lang.reflect.GenericArrayType
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.lang.reflect.WildcardType

fun parameterizedType(
    rawType: Class<*>,
    vararg actualTypeArguments: Type,
): ParameterizedType {
    return TypeUtils.parameterize(rawType, *actualTypeArguments)
}

fun parameterizedType(
    rawType: Class<*>,
    actualTypeArguments: Iterable<Type>,
): ParameterizedType {
    return parameterizedType(rawType, *actualTypeArguments.toTypedArray())
}

fun parameterizedTypeWithOwner(
    rawType: Class<*>,
    ownerType: Type?,
    vararg actualTypeArguments: Type,
): ParameterizedType {
    return TypeUtils.parameterizeWithOwner(ownerType, rawType, *actualTypeArguments)
}

fun parameterizedTypeWithOwner(
    rawType: Class<*>,
    ownerType: Type?,
    actualTypeArguments: Iterable<Type>,
): ParameterizedType {
    return parameterizedTypeWithOwner(rawType, ownerType, *actualTypeArguments.toTypedArray())
}

@JvmName("parameterizedTypeWithArguments")
fun ParameterizedType.withArguments(vararg actualTypeArguments: Type): ParameterizedType {
    if (this.actualTypeArguments.contentEquals(actualTypeArguments)) {
        return this
    }
    return parameterizedTypeWithOwner(this.rawType.rawClass, this.ownerType, *actualTypeArguments)
}

@JvmName("parameterizedTypeWithArguments")
fun ParameterizedType.withArguments(actualTypeArguments: Iterable<Type>): ParameterizedType {
    return withArguments(*actualTypeArguments.toTypedArray())
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

@JvmName("wildcardTypeWithBounds")
fun WildcardType.withBounds(upperBounds: Array<out Type>?, lowerBounds: Array<out Type>?): WildcardType {
    if (this.upperBounds.contentDeepEquals(upperBounds) && this.lowerBounds.contentDeepEquals(lowerBounds)) {
        return this
    }
    return wildcardType(upperBounds, lowerBounds)
}

@JvmName("wildcardTypeWithBounds")
fun WildcardType.withBounds(upperBounds: Iterable<Type>?, lowerBounds: Iterable<Type>?): WildcardType {
    return withBounds(upperBounds?.toTypedArray(), lowerBounds?.toTypedArray())
}

fun Type.genericArrayType(): GenericArrayType {
    return TypeUtils.genericArrayType(this)
}