package xyz.srclab.common.serialize.json

import com.fasterxml.jackson.databind.ObjectMapper
import xyz.srclab.common.reflect.TypeRef
import xyz.srclab.common.serialize.Serializer
import java.io.InputStream
import java.io.OutputStream
import java.io.Reader
import java.io.Writer
import java.lang.reflect.Type
import java.net.URL
import java.nio.ByteBuffer

/**
 * Json serialization of [Serializer].
 *
 * @author sunqian
 *
 * @see Json
 * @see JsonSerials
 */
interface JsonSerializer : Serializer<Json> {

    @JvmDefault
    override fun serialize(any: Any?): Json {
        return toJson(any)
    }

    @JvmDefault
    override fun <T> deserialize(serial: Json, type: Type): T {
        return serial.toObject(type)
    }

    /**
     * java object -> json string
     *
     * @param javaObject java object
     * @return json string
     */
    @JvmDefault
    fun toJsonString(javaObject: Any?): String {
        return toJson(javaObject).toString()
    }

    /**
     * java object -> json bytes
     *
     * @param javaObject java object
     * @return json bytes
     */
    @JvmDefault
    fun toJsonBytes(javaObject: Any?): ByteArray {
        return toJson(javaObject).toBytes()
    }

    /**
     * java object -> json stream
     *
     * @param javaObject java stream
     * @return json bytes
     */
    @JvmDefault
    fun toJsonStream(javaObject: Any?): InputStream {
        return toJson(javaObject).toInputStream()
    }

    /**
     * java object -> json reader
     *
     * @param javaObject java reader
     * @return json bytes
     */
    @JvmDefault
    fun toJsonReader(javaObject: Any?): Reader {
        return toJson(javaObject).toReader()
    }

    /**
     * java object -> json buffer
     *
     * @param javaObject java buffer
     * @return json bytes
     */
    @JvmDefault
    fun toJsonBuffer(javaObject: Any?): ByteBuffer {
        return toJson(javaObject).toByteBuffer()
    }

    /**
     * Writes json from java object into given [OutputStream].
     *
     * @param javaObject   java object
     * @param outputStream given [OutputStream].
     */
    @JvmDefault
    fun <T : OutputStream> writeJson(javaObject: Any?, outputStream: T): T {
        return toJson(javaObject).writeOutputStream(outputStream)
    }

    /**
     * Writes json from java object into given [Writer].
     *
     * @param javaObject java object
     * @param writer     given [Writer].
     */
    @JvmDefault
    fun <T : Writer> writeJson(javaObject: Any?, writer: T): T {
        return toJson(javaObject).writeWriter(writer)
    }

    /**
     * Writes json from java object into given [ByteBuffer].
     *
     * @param javaObject java object
     * @param buffer     given [ByteBuffer].
     */
    @JvmDefault
    fun <T : ByteBuffer> writeJson(javaObject: Any?, buffer: T): T {
        return toJson(javaObject).writeByteBuffer(buffer)
    }

    /**
     * json string -> [Json]
     *
     * @param jsonString json string
     * @return [Json]
     */
    fun toJson(jsonString: CharSequence): Json

    /**
     * json bytes -> [Json]
     *
     * @param jsonBytes json bytes
     * @return [Json]
     */
    @JvmDefault
    fun toJson(jsonBytes: ByteArray): Json {
        return toJson(jsonBytes, 0)
    }

    /**
     * json bytes -> [Json]
     *
     * @param jsonBytes json bytes
     * @param offset    offset of bytes to be parsed
     * @return [Json]
     */
    @JvmDefault
    fun toJson(jsonBytes: ByteArray, offset: Int): Json {
        return toJson(jsonBytes, offset, jsonBytes.size - offset)
    }

    /**
     * json bytes -> [Json]
     *
     * @param jsonBytes json bytes
     * @param offset    offset of bytes to be parsed
     * @param length    length of bytes to be parsed
     * @return [Json]
     */
    fun toJson(jsonBytes: ByteArray, offset: Int, length: Int): Json

    /**
     * json stream -> [Json]
     *
     * @param jsonStream json stream
     * @return [Json]
     */
    fun toJson(jsonStream: InputStream): Json

    /**
     * json reader -> [Json]
     *
     * @param jsonReader json reader
     * @return [Json]
     */
    fun toJson(jsonReader: Reader): Json

    /**
     * json buffer -> [Json]
     *
     * @param jsonBuffer json buffer
     * @return [Json]
     */
    fun toJson(jsonBuffer: ByteBuffer): Json

    /**
     * json source -> [Json]
     *
     * @param jsonSource json source
     * @return [Json]
     */
    fun toJson(jsonSource: URL): Json

    /**
     * json object -> [Json]
     *
     * @param anyObject any object
     * @return [Json]
     */
    fun toJson(anyObject: Any?): Json

    /**
     * json string -> T
     *
     * @param jsonString json string
     * @param type       class of T
     * @param [T]        java type
     * @return [T]
     */
    @JvmDefault
    fun <T> toObject(jsonString: CharSequence, type: Class<T>): T {
        return toJson(jsonString).toObject(type)
    }

    /**
     * json string -> T
     *
     * @param jsonString json string
     * @param type       type of T
     * @param [T]        java type
     * @return [T]
     */
    @JvmDefault
    fun <T> toObject(jsonString: CharSequence, type: Type): T {
        return toJson(jsonString).toObject(type)
    }

    /**
     * json string -> T
     *
     * @param jsonString    json string
     * @param typeRef type reference of T
     * @param [T]           java type
     * @return [T]
     */
    @JvmDefault
    fun <T> toObject(jsonString: CharSequence, typeRef: TypeRef<T>): T {
        return toJson(jsonString).toObject(typeRef)
    }

    /**
     * json bytes -> T
     *
     * @param jsonBytes json bytes
     * @param type      class of T
     * @param [T]       java type
     * @return [T]
     */
    @JvmDefault
    fun <T> toObject(jsonBytes: ByteArray, type: Class<T>): T {
        return toJson(jsonBytes).toObject(type)
    }

    /**
     * json bytes -> T
     *
     * @param jsonBytes json bytes
     * @param type      type of T
     * @param [T]       java type
     * @return [T]
     */
    @JvmDefault
    fun <T> toObject(jsonBytes: ByteArray, type: Type): T {
        return toJson(jsonBytes).toObject(type)
    }

    /**
     * json bytes -> T
     *
     * @param jsonBytes     json bytes
     * @param typeRef type reference of T
     * @param [T]           java type
     * @return [T]
     */
    @JvmDefault
    fun <T> toObject(jsonBytes: ByteArray, typeRef: TypeRef<T>): T {
        return toJson(jsonBytes).toObject(typeRef)
    }

    /**
     * json bytes -> T
     *
     * @param jsonBytes json bytes
     * @param offset    offset of bytes to be parsed
     * @param length    length of bytes to be parsed
     * @param type      class of T
     * @param [T]       java type
     * @return [T]
     */
    @JvmDefault
    fun <T> toObject(jsonBytes: ByteArray, offset: Int, type: Class<T>): T {
        return toJson(jsonBytes, offset).toObject(type)
    }

    /**
     * json bytes -> T
     *
     * @param jsonBytes json bytes
     * @param offset    offset of bytes to be parsed
     * @param length    length of bytes to be parsed
     * @param type      type of T
     * @param [T]       java type
     * @return [T]
     */
    @JvmDefault
    fun <T> toObject(jsonBytes: ByteArray, offset: Int, type: Type): T {
        return toJson(jsonBytes, offset).toObject(type)
    }

    /**
     * json bytes -> T
     *
     * @param jsonBytes     json bytes
     * @param offset        offset of bytes to be parsed
     * @param length        length of bytes to be parsed
     * @param typeRef type reference of T
     * @param [T]           java type
     * @return [T]
     */
    @JvmDefault
    fun <T> toObject(jsonBytes: ByteArray, offset: Int, typeRef: TypeRef<T>): T {
        return toJson(jsonBytes, offset).toObject(typeRef)
    }

    /**
     * json bytes -> T
     *
     * @param jsonBytes json bytes
     * @param offset    offset of bytes to be parsed
     * @param length    length of bytes to be parsed
     * @param type      class of T
     * @param [T]       java type
     * @return [T]
     */
    @JvmDefault
    fun <T> toObject(jsonBytes: ByteArray, offset: Int, length: Int, type: Class<T>): T {
        return toJson(jsonBytes, offset, length).toObject(type)
    }

    /**
     * json bytes -> T
     *
     * @param jsonBytes json bytes
     * @param offset    offset of bytes to be parsed
     * @param length    length of bytes to be parsed
     * @param type      type of T
     * @param [T]       java type
     * @return [T]
     */
    @JvmDefault
    fun <T> toObject(jsonBytes: ByteArray, offset: Int, length: Int, type: Type): T {
        return toJson(jsonBytes, offset, length).toObject(type)
    }

    /**
     * json bytes -> T
     *
     * @param jsonBytes     json bytes
     * @param offset        offset of bytes to be parsed
     * @param length        length of bytes to be parsed
     * @param typeRef type reference of T
     * @param [T]           java type
     * @return [T]
     */
    @JvmDefault
    fun <T> toObject(jsonBytes: ByteArray, offset: Int, length: Int, typeRef: TypeRef<T>): T {
        return toJson(jsonBytes, offset, length).toObject(typeRef)
    }

    /**
     * json stream -> T
     *
     * @param jsonStream json stream
     * @param type       class of T
     * @param [T]        java type
     * @return [T]
     */
    @JvmDefault
    fun <T> toObject(jsonStream: InputStream, type: Class<T>): T {
        return toJson(jsonStream).toObject(type)
    }

    /**
     * json stream -> T
     *
     * @param jsonStream json stream
     * @param type       type of T
     * @param [T]        java type
     * @return [T]
     */
    @JvmDefault
    fun <T> toObject(jsonStream: InputStream, type: Type): T {
        return toJson(jsonStream).toObject(type)
    }

    /**
     * json stream -> T
     *
     * @param jsonStream    json stream
     * @param typeRef type reference of T
     * @param [T]           java type
     * @return [T]
     */
    @JvmDefault
    fun <T> toObject(jsonStream: InputStream, typeRef: TypeRef<T>): T {
        return toJson(jsonStream).toObject(typeRef)
    }

    /**
     * json reader -> T
     *
     * @param jsonReader json reader
     * @param type       class of T
     * @param [T]        java type
     * @return [T]
     */
    @JvmDefault
    fun <T> toObject(jsonReader: Reader, type: Class<T>): T {
        return toJson(jsonReader).toObject(type)
    }

    /**
     * json reader -> T
     *
     * @param jsonReader json reader
     * @param type       type of T
     * @param [T]        java type
     * @return [T]
     */
    @JvmDefault
    fun <T> toObject(jsonReader: Reader, type: Type): T {
        return toJson(jsonReader).toObject(type)
    }

    /**
     * json reader -> T
     *
     * @param jsonReader    json reader
     * @param typeRef type reference of T
     * @param [T]           java type
     * @return [T]
     */
    @JvmDefault
    fun <T> toObject(jsonReader: Reader, typeRef: TypeRef<T>): T {
        return toJson(jsonReader).toObject(typeRef)
    }

    /**
     * json buffer -> T
     *
     * @param jsonBuffer json buffer
     * @param type       class of T
     * @param [T]        java type
     * @return [T]
     */
    @JvmDefault
    fun <T> toObject(jsonBuffer: ByteBuffer, type: Class<T>): T {
        return toJson(jsonBuffer).toObject(type)
    }

    /**
     * json buffer -> T
     *
     * @param jsonBuffer json buffer
     * @param type       type of T
     * @param [T]        java type
     * @return [T]
     */
    @JvmDefault
    fun <T> toObject(jsonBuffer: ByteBuffer, type: Type): T {
        return toJson(jsonBuffer).toObject(type)
    }

    /**
     * json buffer -> T
     *
     * @param jsonBuffer    json buffer
     * @param typeRef type reference of T
     * @param [T]           java type
     * @return [T]
     */
    @JvmDefault
    fun <T> toObject(jsonBuffer: ByteBuffer, typeRef: TypeRef<T>): T {
        return toJson(jsonBuffer).toObject(typeRef)
    }

    /**
     * json source -> T
     *
     * @param jsonSource json source
     * @param type       class of T
     * @param [T]        java type
     * @return [T]
     */
    @JvmDefault
    fun <T> toObject(jsonSource: URL, type: Class<T>): T {
        return toJson(jsonSource).toObject(type)
    }

    /**
     * json source -> T
     *
     * @param jsonSource json source
     * @param type       type of T
     * @param [T]        java type
     * @return [T]
     */
    @JvmDefault
    fun <T> toObject(jsonSource: URL, type: Type): T {
        return toJson(jsonSource).toObject(type)
    }

    /**
     * json source -> T
     *
     * @param jsonSource    json source
     * @param typeRef type reference of T
     * @param [T]           java type
     * @return [T]
     */
    @JvmDefault
    fun <T> toObject(jsonSource: URL, typeRef: TypeRef<T>): T {
        return toJson(jsonSource).toObject(typeRef)
    }

    companion object {

        @JvmField
        val DEFAULT = DEFAULT_OBJECT_MAPPER.toJsonSerializer()

        /**
         * Returns instance of given [ObjectMapper].
         *
         * @receiver given [ObjectMapper]
         * @return instance of given [ObjectMapper]
         */
        @JvmStatic
        @JvmName("fromObjectMapper")
        fun ObjectMapper.toJsonSerializer(): JsonSerializer {
            return JsonSerializerImpl(this)
        }
    }
}