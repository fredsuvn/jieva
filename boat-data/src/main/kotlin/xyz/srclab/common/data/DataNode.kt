package xyz.srclab.common.data

import xyz.srclab.common.data.json.Json
import xyz.srclab.common.io.*
import xyz.srclab.common.reflect.TypeRef
import java.io.InputStream
import java.io.OutputStream
import java.lang.reflect.Type
import java.math.BigDecimal
import java.math.BigInteger
import java.nio.ByteBuffer

/**
 * Represents intermediate type between java object and data-itself.
 *
 * @see DataParser
 * @see Json
 */
interface DataNode {

    override fun toString(): String

    fun toBytes(): ByteArray

    fun toInputStream(): InputStream {
        return toBytes().asInputStream()
    }

    fun toByteBuffer(): ByteBuffer {
        return ByteBuffer.wrap(toBytes())
    }

    fun write(output: OutputStream): Long {
        return toInputStream().copyTo(output)
    }

    fun <T> toObject(type: Class<T>): T {
        return toObjectOrNull(type) ?: throw DataException("Failed parse to: $type")
    }

    fun <T> toObject(type: Type): T {
        return toObjectOrNull(type) ?: throw DataException("Failed parse to: $type")
    }

    fun <T> toObject(typeRef: TypeRef<T>): T {
        return toObjectOrNull(typeRef) ?: throw DataException("Failed parse to: ${typeRef.type}")
    }

    fun <T> toObjectOrNull(type: Class<T>): T? {
        return toObjectOrNull(type as Type)
    }

    fun <T> toObjectOrNull(type: Type): T?

    fun <T> toObjectOrNull(typeRef: TypeRef<T>): T? {
        return toObjectOrNull(typeRef.type)
    }

    fun toBoolean(): Boolean {
        return toObject(Boolean::class.javaPrimitiveType!!)
    }

    fun toByte(): Byte {
        return toObject(Byte::class.javaPrimitiveType!!)
    }

    fun toShort(): Short {
        return toObject(Short::class.javaPrimitiveType!!)
    }

    fun toChar(): Char {
        return toObject(Char::class.javaPrimitiveType!!)
    }

    fun toInt(): Int {
        return toObject(Int::class.javaPrimitiveType!!)
    }

    fun toLong(): Long {
        return toObject(Long::class.javaPrimitiveType!!)
    }

    fun toFloat(): Float {
        return toObject(Float::class.javaPrimitiveType!!)
    }

    fun toDouble(): Double {
        return toObject(Double::class.javaPrimitiveType!!)
    }

    fun toBigInteger(): BigInteger {
        return toObject(BigInteger::class.java)
    }

    fun toBigDecimal(): BigDecimal {
        return toObject(BigDecimal::class.java)
    }

    fun toMap(): Map<String, Any?> {
        return toObject(object : TypeRef<Map<String, Any?>>() {})
    }

    fun toArray(): Array<Any?> {
        return toObject(Array<Any?>::class.java)
    }
}