package xyz.srclab.common.collection

import xyz.srclab.common.base.INAPPLICABLE_JVM_NAME
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

interface IterableSchema {

    @Suppress(INAPPLICABLE_JVM_NAME)
    val componentType: Type
        @JvmName("componentType") get

    companion object {

        @JvmField
        val RAW = of(Any::class.java)

        @JvmStatic
        fun of(componentType: Type): IterableSchema {
            return IterableSchemaImpl(componentType)
        }

        @JvmStatic
        fun resolve(type: Type): IterableSchema {
            if (type is Class<*>) {
                return RAW
            }
            if (type is ParameterizedType) {
                val actualTypeArguments = type.actualTypeArguments
                if (actualTypeArguments.size == 1) {
                    return of(actualTypeArguments[0])
                }
            }
            throw IllegalArgumentException("$type is not a type of Iterable.")
        }
    }
}

fun iterableSchema(componentType: Type): IterableSchema {
    return IterableSchema.of(componentType)
}

fun Type.resolveIterableSchema(): IterableSchema {
    return IterableSchema.resolve(this)
}

private class IterableSchemaImpl(override val componentType: Type) : IterableSchema {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as IterableSchemaImpl

        if (componentType != other.componentType) return false

        return true
    }

    override fun hashCode(): Int {
        return componentType.hashCode()
    }

    override fun toString(): String {
        return "IterableSchemaImpl(componentType=$componentType)"
    }
}

interface MapSchema {

    @Suppress(INAPPLICABLE_JVM_NAME)
    val keyType: Type
        @JvmName("keyType") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    val valueType: Type
        @JvmName("valueType") get

    companion object {

        @JvmField
        val RAW = of(Any::class.java, Any::class.java)

        @JvmField
        val BEAN_PATTERN = of(String::class.java, Any::class.java)

        @JvmStatic
        fun of(keyType: Type, valueType: Type): MapSchema {
            return MapSchemaImpl(keyType, valueType)
        }

        @JvmStatic
        fun resolve(type: Type): MapSchema {
            if (type is Class<*>) {
                return RAW
            }
            if (type is ParameterizedType) {
                val actualTypeArguments = type.actualTypeArguments
                if (actualTypeArguments.size == 2) {
                    return of(actualTypeArguments[0], actualTypeArguments[1])
                }
            }
            throw IllegalArgumentException("$type is not a type of Map.")
        }
    }
}

fun mapSchema(keyType: Type, valueType: Type): MapSchema {
    return MapSchema.of(keyType, valueType)
}

fun Type.resolveMapSchema(): MapSchema {
    return MapSchema.resolve(this)
}

private class MapSchemaImpl(override val keyType: Type, override val valueType: Type) : MapSchema {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MapSchemaImpl

        if (keyType != other.keyType) return false
        if (valueType != other.valueType) return false

        return true
    }

    override fun hashCode(): Int {
        var result = keyType.hashCode()
        result = 31 * result + valueType.hashCode()
        return result
    }

    override fun toString(): String {
        return "MapSchemaImpl(keyType=$keyType, valueType=$valueType)"
    }
}