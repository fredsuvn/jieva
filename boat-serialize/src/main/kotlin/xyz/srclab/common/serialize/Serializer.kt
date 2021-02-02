package xyz.srclab.common.serialize

import xyz.srclab.common.reflect.TypeRef
import java.lang.reflect.Type

interface Serializer<S> {

    fun serialize(any: Any?): S

    @JvmDefault
    fun <T> deserialize(serial: S, type: Class<T>): T? {
        return deserialize(serial, type as Type)
    }

    fun <T> deserialize(serial: S, type: Type): T?

    @JvmDefault
    fun <T> deserialize(serial: S, typeRef: TypeRef<T>): T? {
        return deserialize(serial, typeRef.type)
    }
}