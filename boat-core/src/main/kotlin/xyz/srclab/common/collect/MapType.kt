package xyz.srclab.common.collect

import xyz.srclab.annotations.Acceptable
import xyz.srclab.annotations.Accepted
import xyz.srclab.common.reflect.rawClass
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * Represents actual types of [Map]s.
 */
interface MapType {

    val rawClass: Class<*>

    val keyType: Type

    val valueType: Type

    companion object {

        @JvmName("of")
        @JvmStatic
        fun @Acceptable(
            Accepted(Class::class),
            Accepted(ParameterizedType::class)
        ) Type.toMapType(): MapType {
            return when (this) {
                is Class<*> -> MapTypeImpl(this)
                is ParameterizedType -> {
                    val actualTypeArguments = this.actualTypeArguments
                    MapTypeImpl(this.rawClass, actualTypeArguments[0], actualTypeArguments[1])
                }
                else -> throw IllegalArgumentException("Map type must be a Class or ParameterizedType.")
            }
        }

        private class MapTypeImpl(
            override val rawClass: Class<*>,
            private val _keyType: Type? = null,
            private val _valueType: Type? = null,
        ) : MapType {

            override val keyType: Type = _keyType ?: Any::class.java
            override val valueType: Type = _valueType ?: Any::class.java

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

            override fun toString(): String {
                return if (_keyType === null && _valueType === null)
                    rawClass.typeName
                else
                    "${rawClass.typeName}<${keyType.typeName}, ${valueType.typeName}>"
            }
        }
    }
}