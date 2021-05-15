package xyz.srclab.common.lang

import xyz.srclab.common.reflect.TypeRef
import java.io.*
import java.lang.reflect.Type
import java.math.BigDecimal
import java.math.BigInteger
import java.nio.ByteBuffer
import java.nio.charset.Charset

/**
 * Represents a serializable type.
 *
 * @author sunqian
 */
interface Serial {

    @JvmDefault
    fun <T : OutputStream> writeOutputStream(outputStream: T): T {
        toInputStream().copyTo(outputStream)
        return outputStream
    }

    @JvmDefault
    fun <T : Writer> writeWriter(writer: T): T {
        toReader().copyTo(writer)
        return writer
    }

    @JvmDefault
    fun <T : Writer> writeWriter(writer: T, charset: Charset): T {
        toReader(charset).copyTo(writer)
        return writer
    }

    @JvmDefault
    fun <T : ByteBuffer> writeByteBuffer(byteBuffer: T): T {
        byteBuffer.put(toByteBuffer())
        return byteBuffer
    }

    fun toBytes(): ByteArray

    @JvmDefault
    fun toInputStream(): InputStream

    @JvmDefault
    fun toReader(): Reader {
        return toReader(Default.charset)
    }

    @JvmDefault
    fun toReader(charset: Charset): Reader {
        return InputStreamReader(toInputStream(), charset)
    }

    @JvmDefault
    fun toByteBuffer(): ByteBuffer {
        return ByteBuffer.wrap(toBytes())
    }

    @JvmDefault
    fun <T> toObject(type: Class<T>): T {
        return toObject(type as Type)
    }

    fun <T> toObject(type: Type): T

    @JvmDefault
    fun <T> toObject(typeRef: TypeRef<T>): T {
        return toObject(typeRef.type)
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

    override fun toString(): String

    @JvmDefault
    fun toStringOrNull(): String? {
        return toObjectOrNull(String::class.java)
    }

    @JvmDefault
    fun toBoolean(): Boolean {
        return toObject(Boolean::class.java)
    }

    @JvmDefault
    fun toByte(): Byte {
        return toObject(Byte::class.java)
    }

    @JvmDefault
    fun toShort(): Short {
        return toObject(Short::class.java)
    }

    @JvmDefault
    fun toChar(): Char {
        return toObject(Char::class.java)
    }

    @JvmDefault
    fun toInt(): Int {
        return toObject(Int::class.java)
    }

    @JvmDefault
    fun toLong(): Long {
        return toObject(Long::class.java)
    }

    @JvmDefault
    fun toFloat(): Float {
        return toObject(Float::class.java)
    }

    @JvmDefault
    fun toDouble(): Double {
        return toObject(Double::class.java)
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