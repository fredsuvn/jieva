package xyz.srclab.common.reflect

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * Represents a reference of a type, usually use for get a generic type:
 *
 * ```
 * TypeRef<String> stringTypeRef = new TypeRef<String>(){};
 * TypeRef<Map<String, String>> mapTypeRef = new TypeRef<Map<String, String>>(){};
 * ```
 *
 * Then you can simply get target type:
 *
 * ```
 * //Class type: String
 * Type stringType = stringTypeRef.getType();
 * //Parameterized type: Map<String, String>
 * Type mapType = mapTypeRef.getType();
 * ```
 */
abstract class TypeRef<T> {

    /**
     * Actual runtime type.
     */
    val type: Type

    /**
     * Empty constructor, used to get a generic type.
     */
    protected constructor() {
        type = reflectToActualType()
    }

    /**
     * Specifies the [type].
     */
    protected constructor(type: Type) {
        this.type = type
    }

    private fun reflectToActualType(): Type {
        val generic = this.javaClass.genericSuperclass
        if (generic is ParameterizedType && generic.rawClass == TypeRef::class.java) {
            return generic.actualTypeArguments[0]
        }
        val typeRefSignature = generic.getTypeSignature(TypeRef::class.java)
        return typeRefSignature.actualTypeArguments[0]
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TypeRef<*>) return false
        if (type != other.type) return false
        return true
    }

    override fun hashCode(): Int {
        return type.hashCode()
    }

    override fun toString(): String {
        return "typeRef ${type.typeName}"
    }

    companion object {
        /**
         * Returns a [TypeRef] with [type].
         */
        @JvmStatic
        fun <T> of(type: Type): TypeRef<T> {
            return object : TypeRef<T>(type) {}
        }
    }
}