package xyz.srclab.common.serialize.json

import com.fasterxml.jackson.databind.node.NullNode
import xyz.srclab.common.base.Default
import xyz.srclab.common.base.INAPPLICABLE_JVM_NAME
import xyz.srclab.common.reflect.TypeRef
import java.io.*
import java.lang.reflect.Type
import java.math.BigDecimal
import java.math.BigInteger
import java.nio.ByteBuffer
import java.nio.charset.Charset

/**
 * Represents a Json, fast convert between literal json and java object.
 *
 * @author sunqian
 */
interface Json {

    /**
     * Returns type of json.
     *
     * @return type of json
     */
    @Suppress(INAPPLICABLE_JVM_NAME)
    val type: JsonType
        @JvmName("type") get

    /**
     * to json string
     *
     * @return json string
     */
    fun toJsonString(): String

    /**
     * to json bytes
     *
     * @return json bytes
     */
    fun toJsonBytes(): ByteArray

    /**
     * to json stream
     *
     * @return json stream
     */
    @JvmDefault
    fun toJsonStream(): InputStream {
        return ByteArrayInputStream(toJsonBytes())
    }

    /**
     * to json reader
     *
     * @return json reader
     */
    @JvmDefault
    fun toJsonReader(): Reader {
        return toJsonReader(Default.charset)
    }

    /**
     * to json reader
     *
     * @return json reader
     */
    @JvmDefault
    fun toJsonReader(charset: Charset): Reader {
        return InputStreamReader(toJsonStream(), charset)
    }

    /**
     * to json buffer
     *
     * @return json buffer
     */
    @JvmDefault
    fun toJsonBuffer(): ByteBuffer {
        return ByteBuffer.wrap(toJsonBytes())
    }

    /**
     * Writes json into given [OutputStream].
     *
     * @param outputStream given [OutputStream].
     */
    fun writeJson(outputStream: OutputStream)

    /**
     * Writes json into given [Writer].
     *
     * @param writer given [Writer].
     */
    fun writeJson(writer: Writer)

    /**
     * Writes json into given [ByteBuffer].
     *
     * @param buffer given [ByteBuffer].
     */
    fun writeJson(buffer: ByteBuffer)

    /**
     * to T
     *
     * @param type class of T
     * @param [T]  java type
     * @return [T]
     */
    @JvmDefault
    fun <T> toJavaObject(type: Class<T>): T {
        return toJavaObjectOrNull(type).orThrow()
    }

    /**
     * to T
     *
     * @param type type of T
     * @param [T]  java type
     * @return [T]
     */
    @JvmDefault
    fun <T> toJavaObject(type: Type): T {
        return toJavaObjectOrNull<T>(type).orThrow()
    }

    /**
     * to T
     *
     * @param typeRef type reference of T
     * @param [T]           java type
     * @return [T]
     */
    @JvmDefault
    fun <T> toJavaObject(typeRef: TypeRef<T>): T {
        return toJavaObjectOrNull(typeRef).orThrow()
    }

    /**
     * to T
     *
     * @param type class of T
     * @param [T]  java type
     * @return [T]
     */
    @JvmDefault
    fun <T> toJavaObjectOrNull(type: Class<T>): T? {
        return toJavaObjectOrNull(type as Type)
    }

    /**
     * to T
     *
     * @param type type of T
     * @param [T]  java type
     * @return [T]
     */
    fun <T> toJavaObjectOrNull(type: Type): T?

    /**
     * to T
     *
     * @param typeRef type reference of T
     * @param [T]           java type
     * @return [T]
     */
    @JvmDefault
    fun <T> toJavaObjectOrNull(typeRef: TypeRef<T>): T? {
        return toJavaObjectOrNull(typeRef.type)
    }

    /**
     * to [String]
     *
     * @return [String]
     */
    @JvmDefault
    fun toJavaString(): String {
        return toJavaObject(String::class.java)
    }

    /**
     * to [String]
     *
     * @return [String]
     */
    @JvmDefault
    fun toJavaStringOrNull(): String? {
        return toJavaObjectOrNull(String::class.java)
    }

    /**
     * to boolean
     *
     * @return boolean
     */
    @JvmDefault
    fun toJavaBoolean(): Boolean {
        return toJavaObject(Boolean::class.java)
    }

    /**
     * to byte
     *
     * @return byte
     */
    @JvmDefault
    fun toJavaByte(): Byte {
        return toJavaObject(Byte::class.java)
    }

    /**
     * to short
     *
     * @return short
     */
    @JvmDefault
    fun toJavaShort(): Short {
        return toJavaObject(Short::class.java)
    }

    /**
     * to char
     *
     * @return char
     */
    @JvmDefault
    fun toJavaChar(): Char {
        return toJavaObject(Char::class.java)
    }

    /**
     * to int
     *
     * @return int
     */
    @JvmDefault
    fun toJavaInt(): Int {
        return toJavaObject(Int::class.java)
    }

    /**
     * to long
     *
     * @return long
     */
    @JvmDefault
    fun toJavaLong(): Long {
        return toJavaObject(Long::class.java)
    }

    /**
     * to float
     *
     * @return float
     */
    @JvmDefault
    fun toJavaFloat(): Float {
        return toJavaObject(Float::class.java)
    }

    /**
     * to double
     *
     * @return double
     */
    @JvmDefault
    fun toJavaDouble(): Double {
        return toJavaObject(Double::class.java)
    }

    /**
     * to [BigInteger]
     *
     * @return [BigInteger]
     */
    @JvmDefault
    fun toJavaBigInteger(): BigInteger {
        return toJavaObject(BigInteger::class.java)
    }

    /**
     * to [BigDecimal]
     *
     * @return [BigDecimal]
     */
    @JvmDefault
    fun toJavaBigDecimal(): BigDecimal {
        return toJavaObject(BigDecimal::class.java)
    }

    /**
     * to [Map]
     *
     * @return [Map]
     */
    @JvmDefault
    fun toJavaMap(): Map<String, Any?> {
        return toJavaObject(object : TypeRef<Map<String, Any?>>() {})
    }

    companion object {

        @JvmField
        val NULL: Json = JsonImpl(DEFAULT_OBJECT_MAPPER, NullNode.getInstance())

        private fun <T> T?.orThrow(): T {
            return this ?: throw IllegalStateException("Null json node.")
        }
    }
}