package xyz.srclab.common.serialize.json

import com.fasterxml.jackson.databind.ObjectMapper
import xyz.srclab.common.reflect.TypeRef
import java.io.InputStream
import java.io.OutputStream
import java.io.Reader
import java.io.Writer
import java.lang.reflect.Type
import java.net.URL
import java.nio.ByteBuffer

/**
 * Encapsulation of [ObjectMapper], core class of fastjackson.
 *
 * @author sunqian
 */
interface JsonSerializer {

    /**
     * java object -> json string
     *
     * @param javaObject java object
     * @return json string
     */
    @JvmDefault
    fun toJsonString(javaObject: Any): String {
        return toJson(javaObject).toJsonString()
    }

    /**
     * java object -> json bytes
     *
     * @param javaObject java object
     * @return json bytes
     */
    @JvmDefault
    fun toJsonBytes(javaObject: Any): ByteArray {
        return toJson(javaObject).toJsonBytes()
    }

    /**
     * java object -> json stream
     *
     * @param javaObject java stream
     * @return json bytes
     */
    @JvmDefault
    fun toJsonStream(javaObject: Any): InputStream {
        return toJson(javaObject).toJsonStream()
    }

    /**
     * java object -> json reader
     *
     * @param javaObject java reader
     * @return json bytes
     */
    @JvmDefault
    fun toJsonReader(javaObject: Any): Reader {
        return toJson(javaObject).toJsonReader()
    }

    /**
     * java object -> json buffer
     *
     * @param javaObject java buffer
     * @return json bytes
     */
    @JvmDefault
    fun toJsonBuffer(javaObject: Any): ByteBuffer {
        return toJson(javaObject).toJsonBuffer()
    }

    /**
     * Writes json from java object into given [OutputStream].
     *
     * @param javaObject   java object
     * @param outputStream given [OutputStream].
     */
    @JvmDefault
    fun writeJson(javaObject: Any, outputStream: OutputStream) {
        toJson(javaObject).writeJson(outputStream)
    }

    /**
     * Writes json from java object into given [Writer].
     *
     * @param javaObject java object
     * @param writer     given [Writer].
     */
    @JvmDefault
    fun writeJson(javaObject: Any, writer: Writer) {
        toJson(javaObject).writeJson(writer)
    }

    /**
     * Writes json from java object into given [ByteBuffer].
     *
     * @param javaObject java object
     * @param buffer     given [ByteBuffer].
     */
    @JvmDefault
    fun writeJson(javaObject: Any, buffer: ByteBuffer) {
        toJson(javaObject).writeJson(buffer)
    }

    /**
     * json string -> T
     *
     * @param jsonString json string
     * @param type       class of T
     * @param [T]        java type
     * @return [T]
     */
    @JvmDefault
    fun <T> toJavaObject(jsonString: CharSequence, type: Class<T>): T {
        return toJson(jsonString).toJavaObject(type)
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
    fun <T> toJavaObject(jsonString: CharSequence, type: Type): T {
        return toJson(jsonString).toJavaObject(type)
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
    fun <T> toJavaObject(jsonString: CharSequence, typeRef: TypeRef<T>): T {
        return toJson(jsonString).toJavaObject(typeRef)
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
    fun <T> toJavaObject(jsonBytes: ByteArray, type: Class<T>): T {
        return toJson(jsonBytes).toJavaObject(type)
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
    fun <T> toJavaObject(jsonBytes: ByteArray, type: Type): T {
        return toJson(jsonBytes).toJavaObject(type)
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
    fun <T> toJavaObject(jsonBytes: ByteArray, typeRef: TypeRef<T>): T {
        return toJson(jsonBytes).toJavaObject(typeRef)
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
    fun <T> toJavaObject(jsonBytes: ByteArray, offset: Int, length: Int, type: Class<T>): T {
        return toJson(jsonBytes, offset, length).toJavaObject(type)
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
    fun <T> toJavaObject(jsonBytes: ByteArray, offset: Int, length: Int, type: Type): T {
        return toJson(jsonBytes, offset, length).toJavaObject(type)
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
    fun <T> toJavaObject(jsonBytes: ByteArray, offset: Int, length: Int, typeRef: TypeRef<T>): T {
        return toJson(jsonBytes, offset, length).toJavaObject(typeRef)
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
    fun <T> toJavaObject(jsonStream: InputStream, type: Class<T>): T {
        return toJson(jsonStream).toJavaObject(type)
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
    fun <T> toJavaObject(jsonStream: InputStream, type: Type): T {
        return toJson(jsonStream).toJavaObject(type)
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
    fun <T> toJavaObject(jsonStream: InputStream, typeRef: TypeRef<T>): T {
        return toJson(jsonStream).toJavaObject(typeRef)
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
    fun <T> toJavaObject(jsonReader: Reader, type: Class<T>): T {
        return toJson(jsonReader).toJavaObject(type)
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
    fun <T> toJavaObject(jsonReader: Reader, type: Type): T {
        return toJson(jsonReader).toJavaObject(type)
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
    fun <T> toJavaObject(jsonReader: Reader, typeRef: TypeRef<T>): T {
        return toJson(jsonReader).toJavaObject(typeRef)
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
    fun <T> toJavaObject(jsonBuffer: ByteBuffer, type: Class<T>): T {
        return toJson(jsonBuffer).toJavaObject(type)
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
    fun <T> toJavaObject(jsonBuffer: ByteBuffer, type: Type): T {
        return toJson(jsonBuffer).toJavaObject(type)
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
    fun <T> toJavaObject(jsonBuffer: ByteBuffer, typeRef: TypeRef<T>): T {
        return toJson(jsonBuffer).toJavaObject(typeRef)
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
    fun <T> toJavaObject(jsonSource: URL, type: Class<T>): T {
        return toJson(jsonSource).toJavaObject(type)
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
    fun <T> toJavaObject(jsonSource: URL, type: Type): T {
        return toJson(jsonSource).toJavaObject(type)
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
    fun <T> toJavaObject(jsonSource: URL, typeRef: TypeRef<T>): T {
        return toJson(jsonSource).toJavaObject(typeRef)
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
        return toJson(jsonBytes, 0, jsonBytes.size)
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
     * @param javaObject json object
     * @return [Json]
     */
    fun toJson(javaObject: Any): Json

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