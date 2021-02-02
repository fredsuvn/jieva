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
 * @receiver json string
 * @param type       class of T
 * @param [T]        java type
 * @return [T]
 */
@JvmName("toJavaObject")
fun <T> CharSequence.jsonToJavaObject(type: Class<T>): T {
    return jackson.toJavaObject(this, type)
}

/**
 * json string -> T
 *
 * @receiver json string
 * @param type       type of T
 * @param [T]        java type
 * @return [T]
 */
@JvmName("toJavaObject")
fun <T> CharSequence.jsonToJavaObject(type: Type): T {
    return jackson.toJavaObject(this, type)
}

/**
 * json string -> T
 *
 * @receiver    json string
 * @param typeRef type reference of T
 * @param [T]           java type
 * @return [T]
 */
@JvmName("toJavaObject")
fun <T> CharSequence.jsonToJavaObject(typeRef: TypeRef<T>): T {
    return jackson.toJavaObject(this, typeRef)
}

/**
 * json bytes -> T
 *
 * @receiver json bytes
 * @param type      class of T
 * @param [T]       java type
 * @return [T]
 */
@JvmName("toJavaObject")
fun <T> ByteArray.jsonToJavaObject(type: Class<T>): T {
    return jackson.toJavaObject(this, type)
}

/**
 * json bytes -> T
 *
 * @receiver json bytes
 * @param type      type of T
 * @param [T]       java type
 * @return [T]
 */
@JvmName("toJavaObject")
fun <T> ByteArray.jsonToJavaObject(type: Type): T {
    return jackson.toJavaObject(this, type)
}

/**
 * json bytes -> T
 *
 * @receiver     json bytes
 * @param typeRef type reference of T
 * @param [T]           java type
 * @return [T]
 */
@JvmName("toJavaObject")
fun <T> ByteArray.jsonToJavaObject(typeRef: TypeRef<T>): T {
    return jackson.toJavaObject(this, typeRef)
}

/**
 * json bytes -> T
 *
 * @receiver json bytes
 * @param offset    offset of bytes to be parsed
 * @param length    length of bytes to be parsed
 * @param type      class of T
 * @param [T]       java type
 * @return [T]
 */
@JvmName("toJavaObject")
fun <T> ByteArray.jsonToJavaObject(offset: Int, length: Int, type: Class<T>): T {
    return jackson.toJavaObject(this, offset, length, type)
}

/**
 * json bytes -> T
 *
 * @receiver json bytes
 * @param offset    offset of bytes to be parsed
 * @param length    length of bytes to be parsed
 * @param type      type of T
 * @param [T]       java type
 * @return [T]
 */
@JvmName("toJavaObject")
fun <T> ByteArray.jsonToJavaObject(offset: Int, length: Int, type: Type): T {
    return jackson.toJavaObject(this, offset, length, type)
}

/**
 * json bytes -> T
 *
 * @receiver     json bytes
 * @param offset        offset of bytes to be parsed
 * @param length        length of bytes to be parsed
 * @param typeRef type reference of T
 * @param [T]           java type
 * @return [T]
 */
@JvmName("toJavaObject")
fun <T> ByteArray.jsonToJavaObject(offset: Int, length: Int, typeRef: TypeRef<T>): T {
    return jackson.toJavaObject(this, offset, length, typeRef)
}

/**
 * json stream -> T
 *
 * @receiver json stream
 * @param type       class of T
 * @param [T]        java type
 * @return [T]
 */
@JvmName("toJavaObject")
fun <T> InputStream.jsonToJavaObject(type: Class<T>): T {
    return jackson.toJavaObject(this, type)
}

/**
 * json stream -> T
 *
 * @receiver json stream
 * @param type       type of T
 * @param [T]        java type
 * @return [T]
 */
@JvmName("toJavaObject")
fun <T> InputStream.jsonToJavaObject(type: Type): T {
    return jackson.toJavaObject(this, type)
}

/**
 * json stream -> T
 *
 * @receiver    json stream
 * @param typeRef type reference of T
 * @param [T]           java type
 * @return [T]
 */
@JvmName("toJavaObject")
fun <T> InputStream.jsonToJavaObject(typeRef: TypeRef<T>): T {
    return jackson.toJavaObject(this, typeRef)
}

/**
 * json reader -> T
 *
 * @receiver json reader
 * @param type       class of T
 * @param [T]        java type
 * @return [T]
 */
@JvmName("toJavaObject")
fun <T> Reader.jsonToJavaObject(type: Class<T>): T {
    return jackson.toJavaObject(this, type)
}

/**
 * json reader -> T
 *
 * @receiver json reader
 * @param type       type of T
 * @param [T]        java type
 * @return [T]
 */
@JvmName("toJavaObject")
fun <T> Reader.jsonToJavaObject(type: Type): T {
    return jackson.toJavaObject(this, type)
}

/**
 * json reader -> T
 *
 * @receiver    json reader
 * @param typeRef type reference of T
 * @param [T]           java type
 * @return [T]
 */
@JvmName("toJavaObject")
fun <T> Reader.jsonToJavaObject(typeRef: TypeRef<T>): T {
    return jackson.toJavaObject(this, typeRef)
}

/**
 * json buffer -> T
 *
 * @receiver json buffer
 * @param type       class of T
 * @param [T]        java type
 * @return [T]
 */
@JvmName("toJavaObject")
fun <T> ByteBuffer.jsonToJavaObject(type: Class<T>): T {
    return jackson.toJavaObject(this, type)
}

/**
 * json buffer -> T
 *
 * @receiver json buffer
 * @param type       type of T
 * @param [T]        java type
 * @return [T]
 */
@JvmName("toJavaObject")
fun <T> ByteBuffer.jsonToJavaObject(type: Type): T {
    return jackson.toJavaObject(this, type)
}

/**
 * json buffer -> T
 *
 * @receiver    json buffer
 * @param typeRef type reference of T
 * @param [T]           java type
 * @return [T]
 */
@JvmName("toJavaObject")
fun <T> ByteBuffer.jsonToJavaObject(typeRef: TypeRef<T>): T {
    return jackson.toJavaObject(this, typeRef)
}

/**
 * json source -> T
 *
 * @receiver json source
 * @param type       class of T
 * @param [T]        java type
 * @return [T]
 */
@JvmName("toJavaObject")
fun <T> URL.jsonToJavaObject(type: Class<T>): T {
    return jackson.toJavaObject(this, type)
}

/**
 * json source -> T
 *
 * @receiver json source
 * @param type       type of T
 * @param [T]        java type
 * @return [T]
 */
@JvmName("toJavaObject")
fun <T> URL.jsonToJavaObject(type: Type): T {
    return jackson.toJavaObject(this, type)
}

/**
 * json source -> T
 *
 * @receiver    json source
 * @param typeRef type reference of T
 * @param [T]           java type
 * @return [T]
 */
@JvmName("toJavaObject")
fun <T> URL.jsonToJavaObject(typeRef: TypeRef<T>): T {
    return jackson.toJavaObject(this, typeRef)
}

/**
 * json string -> [Json]
 *
 * @receiver json string
 * @return [Json]
 */
fun CharSequence.toJson(): Json {
    return jackson.toJson(this)
}

/**
 * json bytes -> [Json]
 *
 * @receiver json bytes
 * @return [Json]
 */
fun ByteArray.toJson(): Json {
    return jackson.toJson(this)
}

/**
 * json bytes -> [Json]
 *
 * @receiver json bytes
 * @param offset    offset of bytes to be parsed
 * @param length    length of bytes to be parsed
 * @return [Json]
 */
fun ByteArray.toJson(offset: Int, length: Int): Json {
    return jackson.toJson(this, offset, length)
}

/**
 * json stream -> [Json]
 *
 * @receiver json stream
 * @return [Json]
 */
fun InputStream.toJson(): Json {
    return jackson.toJson(this)
}

/**
 * json reader -> [Json]
 *
 * @receiver json reader
 * @return [Json]
 */
fun Reader.toJson(): Json {
    return jackson.toJson(this)
}

/**
 * json buffer -> [Json]
 *
 * @receiver json buffer
 * @return [Json]
 */
fun ByteBuffer.toJson(): Json {
    return jackson.toJson(this)
}

/**
 * json source -> [Json]
 *
 * @receiver json source
 * @return [Json]
 */
fun URL.toJson(): Json {
    return jackson.toJson(this)
}

/**
 * json object -> [Json]
 *
 * @receiver json object
 * @return [Json]
 */
fun Any.toJson(): Json {
    return jackson.toJson(this)
}