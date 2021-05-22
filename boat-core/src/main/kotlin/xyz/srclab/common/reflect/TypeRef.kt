package xyz.srclab.common.reflect

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

abstract class TypeRef<T> {

    @get:JvmName("type")
    val type: Type

    protected constructor() {
        type = reflectToActualType()
    }

    protected constructor(type: Type) {
        this.type = type
    }

    private fun reflectToActualType(): Type {
        val generic = this.javaClass.genericSuperclass
        if (generic is ParameterizedType && generic.rawClass == TypeRef::class.java) {
            return generic.actualTypeArguments[0]
        }
        val typeRefSignature = generic.toTypeSignature(TypeRef::class.java)
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
        return "ref ${type.typeName}"
    }

    companion object {

        @JvmStatic
        val <T> T.typeRef: TypeRef<T>
            @JvmName("of") get() {
                return object : TypeRef<T>() {}
            }

        @JvmStatic
        fun <T> of(type: Type): TypeRef<T> {
            return object : TypeRef<T>(type) {}
        }
    }
}

fun <T> typeRef(): TypeRef<T> {
    return object : TypeRef<T>() {}
}