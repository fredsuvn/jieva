package xyz.srclab.common.collect

import xyz.srclab.common.reflect.parameterizedType
import xyz.srclab.common.reflect.rawClass
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * To describe [Iterable], [Collection], [Set], [List] and other collect types.
 */
interface IterableType : ParameterizedType {

    val rawClass: Class<*>
    val componentType: Type

    companion object {

        @JvmField
        val RAW_ITERABLE: IterableType = of(Iterable::class.java, Any::class.java)

        @JvmField
        val RAW_COLLECTION: IterableType = of(Collection::class.java, Any::class.java)

        @JvmField
        val RAW_SET: IterableType = of(Set::class.java, Any::class.java)

        @JvmField
        val RAW_LIST: IterableType = of(List::class.java, Any::class.java)

        @JvmStatic
        fun of(rawClass: Class<*>, componentType: Type): IterableType {
            return parameterizedType(rawClass, componentType).toIterableType()
        }

        @JvmName("of")
        @JvmStatic
        fun ParameterizedType.toIterableType(): IterableType {
            return CollectTypeImpl(this)
        }

        @JvmName("of")
        @JvmStatic
        fun Type.toIterableType(): IterableType {
            return when (this) {
                is ParameterizedType -> toIterableType()
                is Class<*> -> of(this, Any::class.java)
                else -> throw IllegalArgumentException("Must be ParameterizedType or Class.")
            }
        }

        private class CollectTypeImpl(
            private val parameterizedType: ParameterizedType
        ) : IterableType {

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
                if (other !is IterableType) return false
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
    }
}

/**
 * To describe [Map] types.
 */
interface MapType : ParameterizedType {

    val rawClass: Class<*>
    val keyType: Type
    val valueType: Type

    companion object {

        @JvmField
        val RAW_MAP: MapType = of(Map::class.java, Any::class.java, Any::class.java)

        @JvmStatic
        fun of(rawClass: Class<*>, keyType: Type, valueType: Type): MapType {
            return parameterizedType(rawClass, keyType, valueType).toMapType()
        }

        @JvmName("of")
        @JvmStatic
        fun ParameterizedType.toMapType(): MapType {
            return MapTypeImpl(this)
        }

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
        ) : MapType {

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
                if (other !is MapType) return false
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
    }
}