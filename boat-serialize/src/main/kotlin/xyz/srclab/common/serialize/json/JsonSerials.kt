@file:JvmName("JsonSerials")
@file:JvmMultifileClass

package xyz.srclab.common.serialize.json

import xyz.srclab.common.reflect.TypeRef
import java.io.InputStream
import java.io.Reader
import java.lang.reflect.Type
import java.net.URL
import java.nio.ByteBuffer

private val jackson: JsonSerializer = JsonSerializer.DEFAULT

/**
 * java object -> json string
 *
 * @receiver java object
 * @return json string
 */
fun Any.toJsonString(): String {
    return jackson.toJsonString(this)
}

/**
 * java object -> json bytes
 *
 * @receiver java object
 * @return json bytes
 */
fun Any.toJsonBytes(): ByteArray {
    return jackson.toJsonBytes(this)
}

/**
 * java object -> json stream
 *
 * @receiver java stream
 * @return json bytes
 */
fun Any.toJsonStream(): InputStream {
    return jackson.toJsonStream(this)
}

/**
 * java object -> json reader
 *
 * @receiver java reader
 * @return json bytes
 */
fun Any.toJsonReader(): Reader {
    return jackson.toJsonReader(this)
}

/**
 * java object -> json buffer
 *
 * @receiver java buffer
 * @return json bytes
 */
fun Any.toJsonBuffer(): ByteBuffer {
    return jackson.toJsonBuffer(this)
}

/**
 * json string -> T
 *
 * @param jsonString json string
 * @param type       class of T
 * @param [T]        java type
 * @return [T]
 */
fun <T> CharSequence.toJavaObject( type: Class<T>): T {
    return jackson.toJavaObject(this, type)
}

/**
 * json string -> T
 *
 * @param jsonString json string
 * @param type       type of T
 * @param [T]        java type
 * @return [T]
 */
fun <T> CharSequence.toJavaObject( type: Type): T {
    return jackson.toJavaObject(this, type)
}

/**
 * json string -> T
 *
 * @param jsonString    json string
 * @param typeRef type reference of T
 * @param [T]           java type
 * @return [T]
 */
fun <T> CharSequence.toJavaObject( typeRef: TypeRef<T>): T {
    return jackson.toJavaObject(this, typeRef)
}

/**
 * json bytes -> T
 *
 * @param jsonBytes json bytes
 * @param type      class of T
 * @param [T]       java type
 * @return [T]
 */
fun <T> toJavaObject(jsonBytes: ByteArray, type: Class<T>): T {
    return jackson.toJavaObject(jsonBytes, type)
}

/**
 * json bytes -> T
 *
 * @param jsonBytes json bytes
 * @param type      type of T
 * @param [T]       java type
 * @return [T]
 */
fun <T> toJavaObject(jsonBytes: ByteArray, type: Type): T {
    return jackson.toJavaObject(jsonBytes, type)
}

/**
 * json bytes -> T
 *
 * @param jsonBytes     json bytes
 * @param typeRef type reference of T
 * @param [T]           java type
 * @return [T]
 */
fun <T> toJavaObject(jsonBytes: ByteArray, typeRef: TypeRef<T>): T {
    return jackson.toJavaObject(jsonBytes, typeRef)
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
fun <T> toJavaObject(jsonBytes: ByteArray, offset: Int, length: Int, type: Class<T>): T {
    return jackson.toJavaObject(jsonBytes, offset, length, type)
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
fun <T> toJavaObject(jsonBytes: ByteArray, offset: Int, length: Int, type: Type): T {
    return jackson.toJavaObject(jsonBytes, offset, length, type)
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
fun <T> toJavaObject(jsonBytes: ByteArray, offset: Int, length: Int, typeRef: TypeRef<T>): T {
    return jackson.toJavaObject(jsonBytes, offset, length, typeRef)
}

/**
 * json stream -> T
 *
 * @param jsonStream json stream
 * @param type       class of T
 * @param [T]        java type
 * @return [T]
 */
fun <T> toJavaObject(jsonStream: InputStream, type: Class<T>): T {
    return jackson.toJavaObject(jsonStream, type)
}

/**
 * json stream -> T
 *
 * @param jsonStream json stream
 * @param type       type of T
 * @param [T]        java type
 * @return [T]
 */
fun <T> toJavaObject(jsonStream: InputStream, type: Type): T {
    return jackson.toJavaObject(jsonStream, type)
}

/**
 * json stream -> T
 *
 * @param jsonStream    json stream
 * @param typeRef type reference of T
 * @param [T]           java type
 * @return [T]
 */
fun <T> toJavaObject(jsonStream: InputStream, typeRef: TypeRef<T>): T {
    return jackson.toJavaObject(jsonStream, typeRef)
}

/**
 * json reader -> T
 *
 * @param jsonReader json reader
 * @param type       class of T
 * @param [T]        java type
 * @return [T]
 */
fun <T> toJavaObject(jsonReader: Reader, type: Class<T>): T {
    return jackson.toJavaObject(jsonReader, type)
}

/**
 * json reader -> T
 *
 * @param jsonReader json reader
 * @param type       type of T
 * @param [T]        java type
 * @return [T]
 */
fun <T> toJavaObject(jsonReader: Reader, type: Type): T {
    return jackson.toJavaObject(jsonReader, type)
}

/**
 * json reader -> T
 *
 * @param jsonReader    json reader
 * @param typeRef type reference of T
 * @param [T]           java type
 * @return [T]
 */
fun <T> toJavaObject(jsonReader: Reader, typeRef: TypeRef<T>): T {
    return jackson.toJavaObject(jsonReader, typeRef)
}

/**
 * json buffer -> T
 *
 * @param jsonBuffer json buffer
 * @param type       class of T
 * @param [T]        java type
 * @return [T]
 */
fun <T> toJavaObject(jsonBuffer: ByteBuffer, type: Class<T>): T {
    return jackson.toJavaObject(jsonBuffer, type)
}

/**
 * json buffer -> T
 *
 * @param jsonBuffer json buffer
 * @param type       type of T
 * @param [T]        java type
 * @return [T]
 */
fun <T> toJavaObject(jsonBuffer: ByteBuffer, type: Type): T {
    return jackson.toJavaObject(jsonBuffer, type)
}

/**
 * json buffer -> T
 *
 * @param jsonBuffer    json buffer
 * @param typeRef type reference of T
 * @param [T]           java type
 * @return [T]
 */
fun <T> toJavaObject(jsonBuffer: ByteBuffer, typeRef: TypeRef<T>): T {
    return jackson.toJavaObject(jsonBuffer, typeRef)
}

/**
 * json source -> T
 *
 * @param jsonSource json source
 * @param type       class of T
 * @param [T]        java type
 * @return [T]
 */
fun <T> toJavaObject(jsonSource: URL, type: Class<T>): T {
    return jackson.toJavaObject(jsonSource, type)
}

/**
 * json source -> T
 *
 * @param jsonSource json source
 * @param type       type of T
 * @param [T]        java type
 * @return [T]
 */
fun <T> toJavaObject(jsonSource: URL, type: Type): T {
    return jackson.toJavaObject(jsonSource, type)
}

/**
 * json source -> T
 *
 * @param jsonSource    json source
 * @param typeRef type reference of T
 * @param [T]           java type
 * @return [T]
 */
fun <T> toJavaObject(jsonSource: URL, typeRef: TypeRef<T>): T {
    return jackson.toJavaObject(jsonSource, typeRef)
}

/**
 * json string -> [Json]
 *
 * @param jsonString json string
 * @return [Json]
 */
fun toJson(jsonString: CharSequence): Json {
    return jackson.toJson(jsonString)
}

/**
 * json bytes -> [Json]
 *
 * @param jsonBytes json bytes
 * @return [Json]
 */
fun toJson(jsonBytes: ByteArray): Json {
    return jackson.toJson(jsonBytes)
}

/**
 * json bytes -> [Json]
 *
 * @param jsonBytes json bytes
 * @param offset    offset of bytes to be parsed
 * @param length    length of bytes to be parsed
 * @return [Json]
 */
fun toJson(jsonBytes: ByteArray, offset: Int, length: Int): Json {
    return jackson.toJson(jsonBytes, offset, length)
}

/**
 * json stream -> [Json]
 *
 * @param jsonStream json stream
 * @return [Json]
 */
fun toJson(jsonStream: InputStream): Json {
    return jackson.toJson(jsonStream)
}

/**
 * json reader -> [Json]
 *
 * @param jsonReader json reader
 * @return [Json]
 */
fun toJson(jsonReader: Reader): Json {
    return jackson.toJson(jsonReader)
}

/**
 * json buffer -> [Json]
 *
 * @param jsonBuffer json buffer
 * @return [Json]
 */
fun toJson(jsonBuffer: ByteBuffer): Json {
    return jackson.toJson(jsonBuffer)
}

/**
 * json source -> [Json]
 *
 * @param jsonSource json source
 * @return [Json]
 */
fun toJson(jsonSource: URL): Json {
    return jackson.toJson(jsonSource)
}

/**
 * json object -> [Json]
 *
 * @receiver json object
 * @return [Json]
 */
fun toJson(): Json {
    return jackson.toJson(javaObject)
}