package xyz.srclab.common.collect

import xyz.srclab.annotations.Acceptable
import xyz.srclab.annotations.Accepted
import xyz.srclab.common.reflect.rawClass
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * Represents actual types of [Iterable]s and [Collection]s.
 */
interface CollectType {

    val rawClass: Class<*>

    val componentType: Type

    companion object {

        @JvmName("of")
        @JvmStatic
        fun @Acceptable(
            Accepted(Class::class),
            Accepted(ParameterizedType::class)
        ) Type.toCollectType(): CollectType {
            return when (this) {
                is Class<*> -> CollectTypeImpl(this)
                is ParameterizedType -> CollectTypeImpl(this.rawClass, this.actualTypeArguments[0])
                else -> throw IllegalArgumentException("Collect type must be a Class or ParameterizedType.")
            }
        }

        private class CollectTypeImpl(
            override val rawClass: Class<*>,
            private val _componentType: Type? = null
        ) : CollectType {

            override val componentType: Type = _componentType ?: Any::class.java

            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other !is CollectType) return false
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
                return if (_componentType === null)
                    rawClass.typeName
                else
                    "${rawClass.typeName}<${componentType.typeName}>"
            }
        }
    }
}