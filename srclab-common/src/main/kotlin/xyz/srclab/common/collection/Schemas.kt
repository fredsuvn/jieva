package xyz.srclab.common.collection

import org.checkerframework.checker.units.qual.C
import xyz.srclab.common.base.INAPPLICABLE_JVM_NAME
import xyz.srclab.common.reflect.rawClass
import java.lang.reflect.GenericArrayType
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.ArrayList
import kotlin.collections.HashSet
import kotlin.collections.LinkedHashSet

interface IterableSchema {

    @Suppress(INAPPLICABLE_JVM_NAME)
    val rawClass:Class<*>
        @JvmName("rawClass") get

    @Suppress(INAPPLICABLE_JVM_NAME)
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
        val RAW_HASH_SET = of(HashSet::class.java, Any::class.java)

        @JvmField
        val RAW_LINKED_HASH_SET = of(LinkedHashSet::class.java, Any::class.java)

        @JvmField
        val RAW_SORTED_SET = of(SortedSet::class.java, Any::class.java)

        @JvmField
        val RAW_TREE_SET = of(TreeSet::class.java, Any::class.java)

        @JvmField
        val RAW_LIST = of(List::class.java, Any::class.java)

        @JvmField
        val RAW_ARRAY_LIST = of(ArrayList::class.java, Any::class.java)

        @JvmField
        val RAW_LINKED_LIST = of(LinkedList::class.java, Any::class.java)

        @JvmField
        val BOOLEAN_ARRAY = of(BooleanArray::class.java, Boolean::class.javaPrimitiveType!!)

        @JvmField
        val BYTE_ARRAY = of(ByteArray::class.java, Byte::class.javaPrimitiveType!!)

        @JvmField
        val SHORT_ARRAY = of(ShortArray::class.java, Short::class.javaPrimitiveType!!)

        @JvmField
        val CHAR_ARRAY = of(CharArray::class.java, Char::class.javaPrimitiveType!!)

        @JvmField
        val INT_ARRAY = of(IntArray::class.java, Int::class.javaPrimitiveType!!)

        @JvmField
        val LONG_ARRAY = of(LongArray::class.java, Long::class.javaPrimitiveType!!)

        @JvmField
        val FLOAT_ARRAY = of(FloatArray::class.java, Float::class.javaPrimitiveType!!)

        @JvmField
        val DOUBLE_ARRAY = of(DoubleArray::class.java, Double::class.javaPrimitiveType!!)

        @JvmStatic
        fun of(rawClass: Class<*>,componentType: Type): IterableSchema {
            return IterableSchemaImpl(rawClass,componentType)
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
                return when(type) {
                    Iterable::class.java-> RAW_ITERABLE
                    Collection::class.java-> RAW_COLLECTION
                    Set::class.java-> RAW_SET
                    HashSet::class.java-> RAW_HASH_SET
                    LinkedHashSet::class.java-> RAW_LINKED_HASH_SET
                    SortedSet::class.java-> RAW_SORTED_SET
                    TreeSet::class.java-> RAW_TREE_SET
                    List::class.java-> RAW_LIST
                    ArrayList::class.java-> RAW_ARRAY_LIST
                    LinkedList::class.java-> RAW_LINKED_LIST
                    BooleanArray::class.java-> BOOLEAN_ARRAY
                    ByteArray::class.java-> BYTE_ARRAY
                    ShortArray::class.java-> SHORT_ARRAY
                    CharArray::class.java-> CHAR_ARRAY
                    IntArray::class.java-> INT_ARRAY
                    LongArray::class.java-> LONG_ARRAY
                    FloatArray::class.java-> FLOAT_ARRAY
                    DoubleArray::class.java-> DOUBLE_ARRAY
                    else->of(type, Any::class.java)
                }
            }
            if (type is ParameterizedType) {
                val rawClass = type.rawClass
                if (!Iterable::class.java.isAssignableFrom(rawClass)) {
                    return null
                }
                val actualTypeArguments = type.actualTypeArguments
                if (actualTypeArguments.size == 1) {
                    return of(rawClass,actualTypeArguments[0])
                }
            }
            if (type is GenericArrayType) {
                return of(rawClass,type.genericComponentType)
            }
            return null
        }
    }
}

fun iterableSchema(componentType: Type): IterableSchema {
    return IterableSchema.of(componentType)
}

fun Type.resolveIterableSchema(): IterableSchema {
    return IterableSchema.resolve(this)
}

fun Type.resolveIterableSchemaOrNull(): IterableSchema? {
    return IterableSchema.resolveOrNull(this)
}

private class IterableSchemaImpl(override val rawClass: Class<*>,override val componentType: Type) : IterableSchema {

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
    val rawClass:Class<*>
        @JvmName("rawClass") get

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
        fun of(rawClass: Class<*>,keyType: Type, valueType: Type): MapSchema {
            return MapSchemaImpl(rawClass,keyType, valueType)
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
                return RAW
            }
            if (type is ParameterizedType) {
                val rawClass = type.rawClass
                if (!Map::class.java.isAssignableFrom(rawClass)) {
                    return null
                }
                val actualTypeArguments = type.actualTypeArguments
                if (actualTypeArguments.size == 2) {
                    return of(rawClass,actualTypeArguments[0], actualTypeArguments[1])
                }
            }
            return null
        }
    }
}

fun mapSchema(rawClass: Class<*>,keyType: Type, valueType: Type): MapSchema {
    return MapSchema.of(rawClass,keyType, valueType)
}

fun Type.resolveMapSchema(): MapSchema {
    return MapSchema.resolve(this)
}

fun Type.resolveMapSchemaOrNull(): MapSchema? {
    return MapSchema.resolveOrNull(this)
}

private class MapSchemaImpl(override val rawClass: Class<*>,override val keyType: Type, override val valueType: Type) : MapSchema {

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