package xyz.srclab.common.collect

import xyz.srclab.annotations.Acceptable
import xyz.srclab.annotations.Accepted
import xyz.srclab.common.lang.INAPPLICABLE_JVM_NAME
import xyz.srclab.common.reflect.rawClass
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

interface IterableType : ParameterizedType {

    @Suppress(INAPPLICABLE_JVM_NAME)
    val componentType: Type
        @JvmName("componentType") get

    companion object {

        @JvmField
        val RAW_ITERABLE = Iterable::class.java.toIterableType()

        @JvmField
        val RAW_COLLECTION = Collection::class.java.toIterableType()

        @JvmField
        val RAW_SET = Set::class.java.toIterableType()

        @JvmField
        val RAW_LIST = List::class.java.toIterableType()

        /**
         * @throws IllegalArgumentException
         */
        @JvmStatic
        @JvmName("from")
        fun @Acceptable(
            Accepted(Class::class),
            Accepted(ParameterizedType::class),
        ) Type.toIterableType(): IterableType {
            return when (this) {
                is Class<*> -> this.toIterableType()
                is ParameterizedType -> this.toIterableType()
                else -> throw IllegalArgumentException("Should be Class or ParameterizedType")
            }
        }

        @JvmStatic
        @JvmName("from")
        fun Class<*>.toIterableType(): IterableType {
            return from(this, Any::class.java, this.declaringClass)
        }

        @JvmStatic
        @JvmName("from")
        fun ParameterizedType.toIterableType(): IterableType {
            return from(this.rawClass, this.actualTypeArguments[0], this.ownerType)
        }

        @JvmOverloads
        @JvmStatic
        fun from(type: Class<*>, componentType: Type, ownerType: Type? = null): IterableType {
            return IterableTypeImpl(type, componentType, ownerType)
        }

        private class IterableTypeImpl(
            private val rawType: Class<*>,
            override val componentType: Type,
            private val ownerType: Type?,
        ) : IterableType {

            override fun getActualTypeArguments(): Array<Type> {
                return arrayOf(componentType)
            }

            override fun getRawType(): Type {
                return rawType
            }

            override fun getOwnerType(): Type? {
                return ownerType
            }

            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other !is IterableType) return false
                if (rawType != other.rawType) return false
                if (componentType != other.componentType) return false
                if (ownerType != other.ownerType) return false
                return true
            }

            override fun hashCode(): Int {
                var result = rawType.hashCode()
                result = 31 * result + componentType.hashCode()
                result = 31 * result + ownerType.hashCode()
                return result
            }

            override fun toString(): String {
                return "${rawType.typeName}<${componentType.typeName}>"
            }
        }
    }
}

interface MapType : ParameterizedType {

    @Suppress(INAPPLICABLE_JVM_NAME)
    val keyType: Type
        @JvmName("keyType") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    val valueType: Type
        @JvmName("valueType") get

    companion object {

        @JvmField
        val RAW = Map::class.java.toMapType()

        @JvmField
        val BEAN_PATTERN = from(Map::class.java, String::class.java, Any::class.java)

        /**
         * @throws IllegalArgumentException
         */
        @JvmStatic
        @JvmName("from")
        fun @Acceptable(
            Accepted(Class::class),
            Accepted(ParameterizedType::class),
        ) Type.toMapType(): MapType {
            return when (this) {
                is Class<*> -> this.toMapType()
                is ParameterizedType -> this.toMapType()
                else -> throw IllegalArgumentException("Should be Class or ParameterizedType")
            }
        }

        @JvmStatic
        @JvmName("from")
        fun Class<*>.toMapType(): MapType {
            return from(this, Any::class.java, Any::class.java, this.declaringClass)
        }

        @JvmStatic
        @JvmName("from")
        fun ParameterizedType.toMapType(): MapType {
            val actualTypeArguments = this.actualTypeArguments
            return from(this.rawClass, actualTypeArguments[0], actualTypeArguments[1], this.ownerType)
        }

        @JvmOverloads
        @JvmStatic
        fun from(rawType: Class<*>, keyType: Type, valueType: Type, ownerType: Type? = null): MapType {
            return MapTypeImpl(rawType, keyType, valueType, ownerType)
        }

        private class MapTypeImpl(
            private val rawType: Class<*>,
            override val keyType: Type,
            override val valueType: Type,
            private val ownerType: Type?,
        ) : MapType {

            override fun getActualTypeArguments(): Array<Type> {
                return arrayOf(keyType, valueType)
            }

            override fun getRawType(): Type {
                return rawType
            }

            override fun getOwnerType(): Type? {
                return ownerType
            }

            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other !is MapType) return false
                if (rawType != other.rawType) return false
                if (keyType != other.keyType) return false
                if (valueType != other.valueType) return false
                if (ownerType != other.ownerType) return false
                return true
            }

            override fun hashCode(): Int {
                var result = rawType.hashCode()
                result = 31 * result + keyType.hashCode()
                result = 31 * result + valueType.hashCode()
                result = 31 * result + (ownerType?.hashCode() ?: 0)
                return result
            }

            override fun toString(): String {
                return "${rawType.typeName}<${keyType.typeName},${valueType.typeName}>"
            }
        }
    }
}