@file:JvmName("BCollects")
@file:JvmMultifileClass

package xyz.srclab.common.collect

import xyz.srclab.common.reflect.parameterizedType
import xyz.srclab.common.reflect.rawClass
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

fun collectType(rawClass: Class<*>, componentType: Type): BCollectType {
    return parameterizedType(rawClass, componentType).toCollectType()
}

fun ParameterizedType.toCollectType(): BCollectType {
    return CollectTypeImpl(this)
}

fun Type.toCollectType(): BCollectType {
    return when (this) {
        is ParameterizedType -> toCollectType()
        is Class<*> -> collectType(this, Any::class.java)
        else -> throw IllegalArgumentException("Must be ParameterizedType or Class.")
    }
}

fun mapType(rawClass: Class<*>, keyType: Type, valueType: Type): BMapType {
    return parameterizedType(rawClass, keyType, valueType).toMapType()
}

fun ParameterizedType.toMapType(): BMapType {
    return MapTypeImpl(this)
}

fun Type.toMapType(): BMapType {
    return when (this) {
        is ParameterizedType -> toMapType()
        is Class<*> -> mapType(this, Any::class.java, Any::class.java)
        else -> throw IllegalArgumentException("Must be ParameterizedType or Class.")
    }
}

/**
 * To describe [Iterable], [Collection], [Set], [List] and other collect types.
 */
interface BCollectType : ParameterizedType {

    val rawClass: Class<*>
    val componentType: Type

    companion object {

        @JvmField
        val RAW_ITERABLE: BCollectType = collectType(Iterable::class.java, Any::class.java)

        @JvmField
        val RAW_COLLECTION: BCollectType = collectType(Collection::class.java, Any::class.java)

        @JvmField
        val RAW_SET: BCollectType = collectType(Set::class.java, Any::class.java)

        @JvmField
        val RAW_LIST: BCollectType = collectType(List::class.java, Any::class.java)
    }
}

/**
 * To describe [Map] types.
 */
interface BMapType : ParameterizedType {

    val rawClass: Class<*>
    val keyType: Type
    val valueType: Type

    companion object {
        @JvmField
        val RAW_MAP: BMapType = mapType(Map::class.java, Any::class.java, Any::class.java)
    }
}

private class CollectTypeImpl(
    private val parameterizedType: ParameterizedType
) : BCollectType {

    override val rawClass: Class<*>
    override val componentType: Type

    init {
        val args = actualTypeArguments
        if (args.size > 1) {
            throw IllegalArgumentException("Number of actual arguments must <= 1.")
        }
        rawClass = parameterizedType.rawClass
        componentType = args[0]
    }

    override fun getActualTypeArguments(): Array<Type> {
        return parameterizedType.actualTypeArguments
    }

    override fun getRawType(): Type {
        return parameterizedType.rawType
    }

    override fun getOwnerType(): Type {
        return parameterizedType.ownerType
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BCollectType) return false
        if (rawClass != other.rawClass) return false
        if (componentType != other.componentType) return false
        return true
    }

    override fun hashCode(): Int {
        var result = rawClass.hashCode()
        result = 31 * result + componentType.hashCode()
        return result
    }
}

private class MapTypeImpl(
    private val parameterizedType: ParameterizedType
) : BMapType {

    override val rawClass: Class<*>
    override val keyType: Type
    override val valueType: Type

    init {
        val args = actualTypeArguments
        if (args.isNotEmpty() && args.size != 2) {
            throw IllegalArgumentException("Number of actual arguments must be 0 or 2.")
        }
        rawClass = parameterizedType.rawClass
        keyType = args[0]
        valueType = args[1]
    }

    override fun getActualTypeArguments(): Array<Type> {
        return parameterizedType.actualTypeArguments
    }

    override fun getRawType(): Type {
        return parameterizedType.rawType
    }

    override fun getOwnerType(): Type {
        return parameterizedType.ownerType
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BMapType) return false
        if (rawClass != other.rawClass) return false
        if (keyType != other.keyType) return false
        if (valueType != other.valueType) return false
        return true
    }

    override fun hashCode(): Int {
        var result = rawClass.hashCode()
        result = 31 * result + keyType.hashCode()
        result = 31 * result + valueType.hashCode()
        return result
    }
}