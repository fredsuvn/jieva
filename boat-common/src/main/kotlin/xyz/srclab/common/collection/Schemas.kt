package xyz.srclab.common.collection

import xyz.srclab.common.reflect.rawClass
import xyz.srclab.kotlin.compile.COMPILE_INAPPLICABLE_JVM_NAME
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

interface IterableSchema {

    @Suppress(COMPILE_INAPPLICABLE_JVM_NAME)
    val rawClass: Class<*>
        @JvmName("rawClass") get

    @Suppress(COMPILE_INAPPLICABLE_JVM_NAME)
    val componentType: Type
        @JvmName("componentType") get

    companion object {

        @JvmField
        val RAW_ITERABLE = of(Iterable::class.java, Any::class.java)

        @JvmField
        val RAW_COLLECTION = of(Collection::class.java, Any::class.java)

        @JvmField
        val RAW_SET = of(Set::class.java, Any::class.java)

        @JvmField
        val RAW_LIST = of(List::class.java, Any::class.java)

        @JvmStatic
        fun of(rawClass: Class<*>, componentType: Type): IterableSchema {
            return IterableSchemaImpl(rawClass, componentType)
        }

        @JvmStatic
        fun resolve(type: Type): IterableSchema {
            val result = resolveOrNull(type)
            if (result === null) {
                throw IllegalArgumentException("$type is not a type of Iterable.")
            }
            return result
        }

        @JvmStatic
        fun resolveOrNull(type: Type): IterableSchema? {
            if (type is Class<*>) {
                if (type == Iterable::class.java) {
                    return RAW_ITERABLE
                }
                if (type == Collection::class.java) {
                    return RAW_COLLECTION
                }
                if (type == Set::class.java) {
                    return RAW_SET
                }
                if (type == List::class.java) {
                    return RAW_LIST
                }
                if (!Iterable::class.java.isAssignableFrom(type)) {
                    return null
                }
                return of(type, Any::class.java)
            }
            if (type is ParameterizedType) {
                val rawClass = type.rawClass
                if (!Iterable::class.java.isAssignableFrom(rawClass)) {
                    return null
                }
                val actualTypeArguments = type.actualTypeArguments
                if (actualTypeArguments.size == 1) {
                    return of(rawClass, actualTypeArguments[0])
                }
            }
            return null
        }
    }
}

fun iterableSchema(rawClass: Class<*>, componentType: Type): IterableSchema {
    return IterableSchema.of(rawClass, componentType)
}

fun Type.resolveIterableSchema(): IterableSchema {
    return IterableSchema.resolve(this)
}

fun Type.resolveIterableSchemaOrNull(): IterableSchema? {
    return IterableSchema.resolveOrNull(this)
}

private class IterableSchemaImpl(
    override val rawClass: Class<*>,
    override val componentType: Type
) : IterableSchema {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as IterableSchemaImpl

        if (rawClass != other.rawClass) return false
        if (componentType != other.componentType) return false

        return true
    }

    override fun hashCode(): Int {
        var result = rawClass.hashCode()
        result = 31 * result + componentType.hashCode()
        return result
    }

    override fun toString(): String {
        return "IterableSchemaImpl(rawClass=$rawClass, componentType=$componentType)"
    }
}

interface MapSchema {

    @Suppress(COMPILE_INAPPLICABLE_JVM_NAME)
    val rawClass: Class<*>
        @JvmName("rawClass") get

    @Suppress(COMPILE_INAPPLICABLE_JVM_NAME)
    val keyType: Type
        @JvmName("keyType") get

    @Suppress(COMPILE_INAPPLICABLE_JVM_NAME)
    val valueType: Type
        @JvmName("valueType") get

    companion object {

        @JvmField
        val RAW = of(Map::class.java, Any::class.java, Any::class.java)

        @JvmField
        val BEAN_PATTERN = of(Map::class.java, String::class.java, Any::class.java)

        @JvmStatic
        fun of(rawClass: Class<*>, keyType: Type, valueType: Type): MapSchema {
            return MapSchemaImpl(rawClass, keyType, valueType)
        }

        @JvmStatic
        fun resolve(type: Type): MapSchema {
            val result = resolveOrNull(type)
            if (result === null) {
                throw IllegalArgumentException("$type is not a type of Map.")
            }
            return result
        }

        @JvmStatic
        fun resolveOrNull(type: Type): MapSchema? {
            if (type is Class<*> && Map::class.java.isAssignableFrom(type)) {
                if (type == Map::class.java) {
                    return RAW
                }
                if (!Map::class.java.isAssignableFrom(type)) {
                    return null
                }
                return of(type, Any::class.java, Any::class.java)
            }
            if (type is ParameterizedType) {
                val rawClass = type.rawClass
                if (!Map::class.java.isAssignableFrom(rawClass)) {
                    return null
                }
                val actualTypeArguments = type.actualTypeArguments
                if (actualTypeArguments.size == 2) {
                    return of(rawClass, actualTypeArguments[0], actualTypeArguments[1])
                }
            }
            return null
        }
    }
}

fun mapSchema(rawClass: Class<*>, keyType: Type, valueType: Type): MapSchema {
    return MapSchema.of(rawClass, keyType, valueType)
}

fun Type.resolveMapSchema(): MapSchema {
    return MapSchema.resolve(this)
}

fun Type.resolveMapSchemaOrNull(): MapSchema? {
    return MapSchema.resolveOrNull(this)
}

private class MapSchemaImpl(
    override val rawClass: Class<*>,
    override val keyType: Type,
    override val valueType: Type
) : MapSchema {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MapSchemaImpl

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

    override fun toString(): String {
        return "MapSchemaImpl(rawClass=$rawClass, keyType=$keyType, valueType=$valueType)"
    }
}