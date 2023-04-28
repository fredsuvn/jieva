package xyz.srclab.common.collect

import xyz.srclab.common.base.FinalClass
import xyz.srclab.common.base.defaultSerialVersion
import xyz.srclab.common.reflect.parameterizedType
import xyz.srclab.common.reflect.rawClass
import java.io.Serializable
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * To describe [Iterable], [Collection], [Set], [List] and other collect types.
 */
interface IterableType : Serializable, ParameterizedType {

    val containerType: Class<*>
    val componentType: Type

    companion object {

        /**
         * Raw [Iterable] type.
         */
        @JvmField
        val RAW_ITERABLE: IterableType = of(Iterable::class.java, Any::class.java)

        /**
         * Raw [Collection] type.
         */
        @JvmField
        val RAW_COLLECTION: IterableType = of(Collection::class.java, Any::class.java)

        /**
         * Raw [Set] type.
         */
        @JvmField
        val RAW_SET: IterableType = of(Set::class.java, Any::class.java)

        /**
         * Raw [List] type.
         */
        @JvmField
        val RAW_LIST: IterableType = of(List::class.java, Any::class.java)

        /**
         * Returns [IterableType] consists of [containerType] and [componentType].
         */
        @JvmStatic
        fun of(containerType: Class<*>, componentType: Type): IterableType {
            return parameterizedType(containerType, componentType).toIterableType()
        }

        /**
         * Converts [ParameterizedType] to [IterableType].
         */
        @JvmName("of")
        @JvmStatic
        fun ParameterizedType.toIterableType(): IterableType {
            return IterableTypeImpl(this)
        }

        /**
         * Converts [Type] to [IterableType], the [Type] must be [ParameterizedType] or [Class].
         */
        @JvmName("of")
        @JvmStatic
        fun Type.toIterableType(): IterableType {
            return when (this) {
                is ParameterizedType -> toIterableType()
                is Class<*> -> of(this, Any::class.java)
                else -> throw IllegalArgumentException("Must be ParameterizedType or Class.")
            }
        }

        private class IterableTypeImpl(
            private val parameterizedType: ParameterizedType
        ) : IterableType, FinalClass() {

            override val containerType: Class<*>
            override val componentType: Type

            init {
                val args = actualTypeArguments
                if (args.size > 1) {
                    throw IllegalArgumentException("Number of actual arguments must <= 1.")
                }
                containerType = parameterizedType.rawClass
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
                if (other !is IterableType) return false
                if (containerType != other.containerType) return false
                if (componentType != other.componentType) return false
                return true
            }

            override fun hashCode0(): Int {
                var result = containerType.hashCode()
                result = 31 * result + componentType.hashCode()
                return result
            }

            override fun toString0(): String {
                return "$containerType<$containerType>"
            }

            companion object {
                private val serialVersionUID: Long = defaultSerialVersion()
            }
        }
    }
}

/**
 * To describe [Map] types.
 */
interface MapType : Serializable, ParameterizedType {

    val containerType: Class<*>
    val keyType: Type
    val valueType: Type

    companion object {

        /**
         * Raw [Map] type.
         */
        @JvmField
        val RAW_MAP: MapType = of(Map::class.java, Any::class.java, Any::class.java)

        /**
         * Returns [MapType] consists of [containerType], [keyType] and [valueType].
         */
        @JvmStatic
        fun of(containerType: Class<*>, keyType: Type, valueType: Type): MapType {
            return parameterizedType(containerType, keyType, valueType).toMapType()
        }

        /**
         * Converts [ParameterizedType] to [MapType].
         */
        @JvmName("of")
        @JvmStatic
        fun ParameterizedType.toMapType(): MapType {
            return MapTypeImpl(this)
        }

        /**
         * Converts [Type] to [MapType], the [Type] must be [ParameterizedType] or [Class].
         */
        @JvmName("of")
        @JvmStatic
        fun Type.toMapType(): MapType {
            return when (this) {
                is ParameterizedType -> toMapType()
                is Class<*> -> of(this, Any::class.java, Any::class.java)
                else -> throw IllegalArgumentException("Must be ParameterizedType or Class.")
            }
        }

        private class MapTypeImpl(
            private val parameterizedType: ParameterizedType
        ) : MapType, FinalClass() {

            override val containerType: Class<*>
            override val keyType: Type
            override val valueType: Type

            init {
                val args = actualTypeArguments
                if (args.isNotEmpty() && args.size != 2) {
                    throw IllegalArgumentException("Number of actual arguments must be 0 or 2.")
                }
                containerType = parameterizedType.rawClass
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
                if (other !is MapType) return false
                if (containerType != other.containerType) return false
                if (keyType != other.keyType) return false
                if (valueType != other.valueType) return false
                return true
            }

            override fun hashCode0(): Int {
                var result = containerType.hashCode()
                result = 31 * result + keyType.hashCode()
                result = 31 * result + valueType.hashCode()
                return result
            }

            override fun toString0(): String {
                return "$containerType<$keyType, $valueType>"
            }

            companion object {
                private val serialVersionUID: Long = defaultSerialVersion()
            }
        }
    }
}