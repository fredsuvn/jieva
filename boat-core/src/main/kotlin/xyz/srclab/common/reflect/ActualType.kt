package xyz.srclab.common.reflect

import xyz.srclab.common.lang.INAPPLICABLE_JVM_NAME
import xyz.srclab.common.lang.isIndexInBounds
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.*

/**
 * Represents actual type, may come from [ParameterizedType].
 */
interface ActualType {

    @get:JvmName("rawClass")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val rawClass: Class<*>

    @get:JvmName("argumentTypes")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val argumentTypes: List<Type>

    fun argumentType(index: Int): Type {
        return argumentTypes[index]
    }

    fun argumentTypeOrNull(index: Int): Type? {
        if (!isIndexInBounds(index, 0, argumentTypes.size)) {
            return null
        }
        return argumentTypes[index]
    }

    companion object {

        @JvmOverloads
        @JvmStatic
        fun newActualType(rawClass: Class<*>, arguments: List<Type> = emptyList()): ActualType {
            return ActualTypeImpl(rawClass, arguments)
        }

        private class ActualTypeImpl(
            override val rawClass: Class<*>,
            override val argumentTypes: List<Type>
        ) : ActualType {

            private val _hashcode: Int by lazy { Objects.hash(rawClass, argumentTypes) }
            private val _toString: String by lazy {
                if (argumentTypes.isEmpty()) {
                    return@lazy rawClass.typeName
                }
                "${rawClass.typeName}<${argumentTypes.joinToString { it.typeName }}>"
            }

            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other !is ActualType) return false
                if (rawClass != other.rawClass) return false
                if (argumentTypes != other.argumentTypes) return false
                return true
            }

            override fun hashCode(): Int = _hashcode
            override fun toString(): String = _toString
        }
    }
}