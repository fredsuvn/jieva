package xyz.srclab.common.collection

import xyz.srclab.common.base.INAPPLICABLE_JVM_NAME
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

interface MapSchema {

    @Suppress(INAPPLICABLE_JVM_NAME)
    val keyType: Type
        @JvmName("keyType") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    val valueType: Type
        @JvmName("valueType") get

    companion object {

        @JvmStatic
        fun of(keyType: Type, valueType: Type): MapSchema {
            return MapSchemaImpl(keyType, valueType)
        }

        @JvmStatic
        fun resolve(type: Type): MapSchema {
            if (type is Class<*>) {
                return MapSchemaImpl.RAW_SCHEMA
            }
            if (type is ParameterizedType && type.actualTypeArguments.size == 2) {
                return of(type.actualTypeArguments[0], type.actualTypeArguments[1])
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
        return "MapSchema(keyType=$keyType, valueType=$valueType)"
    }

    companion object {

        val RAW_SCHEMA = MapSchemaImpl(Any::class.java, Any::class.java)
    }
}