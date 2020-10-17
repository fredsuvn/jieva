package xyz.srclab.common.reflect

import xyz.srclab.annotation.Nullable
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class TypeRef<T> {

    val type: Type

    protected constructor() {
        type = genericSuperclass
    }

    private constructor(type: Type) {
        this.type = type
    }

    protected val genericSuperclass: Type
        protected get() {
            @Nullable val generic = TypeKit.getGenericSuperclass(javaClass, TypeRef::class.java)
            check(generic is ParameterizedType) { "Generic super class must be a parameterized type" }
            return generic.actualTypeArguments[0]
        }

    companion object {
        fun <T> of(type: Type): TypeRef<T> {
            return TypeRef(type)
        }
    }
}