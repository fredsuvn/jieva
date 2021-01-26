package xyz.srclab.common.collect

import xyz.srclab.common.base.INAPPLICABLE_JVM_NAME
import xyz.srclab.common.reflect.parameterizedType
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

interface IterableType : ParameterizedType {

    @Suppress(INAPPLICABLE_JVM_NAME)
    val componentType: Type
        @JvmName("componentType") get

    companion object {

        @JvmField
        val RAW_ITERABLE = from(Iterable::class.java)

        @JvmField
        val RAW_COLLECTION = from(Collection::class.java)

        @JvmField
        val RAW_SET = from(Set::class.java)

        @JvmField
        val RAW_LIST = from(List::class.java)

        @JvmStatic
        fun from(type: ParameterizedType): IterableType {
            return IterableTypeImpl(type)
        }

        @JvmStatic
        fun from(type: Class<out Iterable<*>>): IterableType {
            return from(type, Any::class.java)
        }

        @JvmStatic
        fun from(type: Class<out Iterable<*>>, componentType: Type): IterableType {
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
        val RAW = from(Map::class.java)

        @JvmField
        val BEAN_PATTERN = from(Map::class.java, String::class.java, Any::class.java)

        @JvmStatic
        fun from(type: ParameterizedType): MapType {
            return MapTypeImpl(type)
        }

        @JvmStatic
        fun from(type: Class<out Map<*, *>>): MapType {
            return from(type, Any::class.java, Any::class.java)
        }

        @JvmStatic
        fun from(type: Class<out Map<*, *>>, keyType: Type, valueType: Type): MapType {
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