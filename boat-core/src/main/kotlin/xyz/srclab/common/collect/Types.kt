package xyz.srclab.common.collect

import xyz.srclab.annotations.Acceptable
import xyz.srclab.annotations.Accepted
import xyz.srclab.common.lang.INAPPLICABLE_JVM_NAME
import xyz.srclab.common.reflect.parameterizedType
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
            return from(this, Any::class.java)
        }

        @JvmStatic
        @JvmName("from")
        fun ParameterizedType.toIterableType(): IterableType {
            return IterableTypeImpl(this)
        }

        @JvmStatic
        fun from(type: Class<*>, componentType: Type): IterableType {
            return IterableTypeImpl(parameterizedType(type, componentType))
        }

        private class IterableTypeImpl(private val type: ParameterizedType) : IterableType {

            override val componentType: Type by lazy {
                this.actualTypeArguments[0]
            }

            override fun getActualTypeArguments(): Array<Type> {
                return type.actualTypeArguments
            }

            override fun getRawType(): Type {
                return type.rawType
            }

            override fun getOwnerType(): Type {
                return type.ownerType
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
            return from(this, Any::class.java, Any::class.java)
        }

        @JvmStatic
        @JvmName("from")
        fun ParameterizedType.toMapType(): MapType {
            return MapTypeImpl(this)
        }

        @JvmStatic
        fun from(type: Class<*>, keyType: Type, valueType: Type): MapType {
            return MapTypeImpl(parameterizedType(type, keyType, valueType))
        }

        private class MapTypeImpl(private val type: ParameterizedType) : MapType {

            override val keyType: Type by lazy {
                this.actualTypeArguments[0]
            }

            override val valueType: Type by lazy {
                this.actualTypeArguments[1]
            }

            override fun getActualTypeArguments(): Array<Type> {
                return type.actualTypeArguments
            }

            override fun getRawType(): Type {
                return type.rawType
            }

            override fun getOwnerType(): Type {
                return type.ownerType
            }
        }
    }
}