package xyz.srclab.common.serialize

import xyz.srclab.annotations.Immutable
import xyz.srclab.annotations.Written
import xyz.srclab.common.io.toReader
import xyz.srclab.common.lang.Defaults
import xyz.srclab.common.reflect.TypeRef
import xyz.srclab.common.serialize.json.Json
import java.io.InputStream
import java.io.OutputStream
import java.io.Reader
import java.io.Writer
import java.lang.reflect.Type
import java.math.BigDecimal
import java.math.BigInteger
import java.nio.ByteBuffer
import java.nio.charset.Charset

/**
 * Represents a serial type, immutable.
 *
 * Serial type is serializable.
 *
 * @see Serializer
 * @see Json
 */
@Immutable
interface Serial {

    // Serial -> Binary

    @JvmDefault
    fun writeTo(@Written outputStream: OutputStream) {
        toInputStream().copyTo(outputStream)
    }

    @JvmDefault
    fun writeTo(@Written writer: Writer) {
        toReader().copyTo(writer)
    }

    @JvmDefault
    fun writeTo(@Written writer: Writer, charset: Charset) {
        toReader(charset).copyTo(writer)
    }

    @JvmDefault
    fun writeTo(@Written byteBuffer: ByteBuffer) {
        byteBuffer.put(toByteBuffer())
    }

    fun toInputStream(): InputStream

    @JvmDefault
    fun toReader(): Reader {
        return toReader(Defaults.charset)
    }

    @JvmDefault
    fun toReader(charset: Charset): Reader {
        return toInputStream().toReader(charset)
    }

    @JvmDefault
    fun toByteBuffer(): ByteBuffer {
        return ByteBuffer.wrap(toBytes())
    }

    @JvmDefault
    fun toBytes(): ByteArray {
        return toInputStream().readBytes()
    }

    // Serial -> Java Object

    @JvmDefault
    fun <T> toObject(type: Class<T>): T {
        return toObjectOrNull(type)!!
    }

    @JvmDefault
    fun <T> toObject(type: Type): T {
        return toObjectOrNull(type)!!
    }

    @JvmDefault
    fun <T> toObject(typeRef: TypeRef<T>): T {
        return toObjectOrNull(typeRef)!!
    }

    @JvmDefault
    fun <T> toObjectOrNull(type: Class<T>): T? {
        return toObjectOrNull(type as Type)
    }

    fun <T> toObjectOrNull(type: Type): T?

    @JvmDefault
    fun <T> toObjectOrNull(typeRef: TypeRef<T>): T? {
        return toObjectOrNull(typeRef.type)
    }

    @JvmDefault
    fun toStringOrNull(): String? {
        return toObjectOrNull(String::class.java)
    }

    @JvmDefault
    fun toBoolean(): Boolean {
        return toObject(Boolean::class.javaPrimitiveType!!)
    }

    @JvmDefault
    fun toByte(): Byte {
        return toObject(Byte::class.javaPrimitiveType!!)
    }

    @JvmDefault
    fun toShort(): Short {
        return toObject(Short::class.javaPrimitiveType!!)
    }

    @JvmDefault
    fun toChar(): Char {
        return toObject(Char::class.javaPrimitiveType!!)
    }

    @JvmDefault
    fun toInt(): Int {
        return toObject(Int::class.javaPrimitiveType!!)
    }

    @JvmDefault
    fun toLong(): Long {
        return toObject(Long::class.javaPrimitiveType!!)
    }

    @JvmDefault
    fun toFloat(): Float {
        return toObject(Float::class.javaPrimitiveType!!)
    }

    @JvmDefault
    fun toDouble(): Double {
        return toObject(Double::class.javaPrimitiveType!!)
    }

    @JvmDefault
    fun toBigInteger(): BigInteger {
        return toObject(BigInteger::class.java)
    }

    @JvmDefault
    fun toBigDecimal(): BigDecimal {
        return toObject(BigDecimal::class.java)
    }

    @JvmDefault
    fun toMap(): Map<String, Any?> {
        return toObject(object : TypeRef<Map<String, Any?>>() {})
    }
}