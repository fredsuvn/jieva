@file:JvmName("JsonSerials")
@file:JvmMultifileClass

package xyz.srclab.common.serialize.json

import xyz.srclab.common.reflect.TypeRef
import java.io.InputStream
import java.io.OutputStream
import java.io.Reader
import java.io.Writer
import java.lang.reflect.Type
import java.net.URL
import java.nio.ByteBuffer

private val defaultJsonSerializer: JsonSerializer = JsonSerializer.DEFAULT

/**
 * java object -> json string
 *
 * @receiver java object
 * @return json string
 */
fun Any?.toJsonString(): String {
    return defaultJsonSerializer.toJsonString(this)
}

/**
 * java object -> json bytes
 *
 * @receiver java object
 * @return json bytes
 */
fun Any?.toJsonBytes(): ByteArray {
    return defaultJsonSerializer.toJsonBytes(this)
}

/**
 * java object -> json stream
 *
 * @receiver java stream
 * @return json bytes
 */
fun Any?.toJsonStream(): InputStream {
    return defaultJsonSerializer.toJsonStream(this)
}

/**
 * java object -> json reader
 *
 * @receiver java reader
 * @return json bytes
 */
fun Any?.toJsonReader(): Reader {
    return defaultJsonSerializer.toJsonReader(this)
}

/**
 * java object -> json buffer
 *
 * @receiver java buffer
 * @return json bytes
 */
fun Any?.toJsonBuffer(): ByteBuffer {
    return defaultJsonSerializer.toJsonBuffer(this)
}

/**
 * Writes json from java object into given [OutputStream].
 *
 * @receiver    java object
 * @param outputStream given [OutputStream].
 */

fun <T : OutputStream> Any?.writeJson(outputStream: T): T {
    return this.toJson().writeOutputStream(outputStream)
}

/**
 * Writes json from java object into given [Writer].
 *
 * @receiver  java object
 * @param writer     given [Writer].
 */

fun <T : Writer> Any?.writeJson(writer: T): T {
    return this.toJson().writeWriter(writer)
}

/**
 * Writes json from java object into given [ByteBuffer].
 *
 * @receiver  java object
 * @param buffer     given [ByteBuffer].
 */

fun <T : ByteBuffer> Any?.writeJson(buffer: T): T {
    return this.toJson().writeByteBuffer(buffer)
}

/**
 * json string -> [Json]
 *
 * @receiver json string
 * @return [Json]
 */
fun CharSequence.toJson(): Json {
    return defaultJsonSerializer.toJson(this)
}

/**
 * json bytes -> [Json]
 *
 * @receiver json bytes
 * @return [Json]
 */
fun ByteArray.toJson(): Json {
    return defaultJsonSerializer.toJson(this)
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
    return defaultJsonSerializer.toJson(this, offset, length)
}

/**
 * json stream -> [Json]
 *
 * @receiver json stream
 * @return [Json]
 */
fun InputStream.toJson(): Json {
    return defaultJsonSerializer.toJson(this)
}

/**
 * json reader -> [Json]
 *
 * @receiver json reader
 * @return [Json]
 */
fun Reader.toJson(): Json {
    return defaultJsonSerializer.toJson(this)
}

/**
 * json buffer -> [Json]
 *
 * @receiver json buffer
 * @return [Json]
 */
fun ByteBuffer.toJson(): Json {
    return defaultJsonSerializer.toJson(this)
}

/**
 * json source -> [Json]
 *
 * @receiver json source
 * @return [Json]
 */
fun URL.toJson(): Json {
    return defaultJsonSerializer.toJson(this)
}

/**
 * json object -> [Json]
 *
 * @receiver json object
 * @return [Json]
 */
fun Any?.toJson(): Json {
    return defaultJsonSerializer.toJson(this)
}

/**
 * json string -> T
 *
 * @receiver json string
 * @param type       class of T
 * @param [T]        java type
 * @return [T]
 */
@JvmName("toObject")
fun <T> CharSequence.jsonToObject(type: Class<T>): T {
    return defaultJsonSerializer.toObject(this, type)
}

/**
 * json string -> T
 *
 * @receiver json string
 * @param type       type of T
 * @param [T]        java type
 * @return [T]
 */
@JvmName("toObject")
fun <T> CharSequence.jsonToObject(type: Type): T {
    return defaultJsonSerializer.toObject(this, type)
}

/**
 * json string -> T
 *
 * @receiver    json string
 * @param typeRef type reference of T
 * @param [T]           java type
 * @return [T]
 */
@JvmName("toObject")
fun <T> CharSequence.jsonToObject(typeRef: TypeRef<T>): T {
    return defaultJsonSerializer.toObject(this, typeRef)
}

/**
 * json bytes -> T
 *
 * @receiver json bytes
 * @param type      class of T
 * @param [T]       java type
 * @return [T]
 */
@JvmName("toObject")
fun <T> ByteArray.jsonToObject(type: Class<T>): T {
    return defaultJsonSerializer.toObject(this, type)
}

/**
 * json bytes -> T
 *
 * @receiver json bytes
 * @param type      type of T
 * @param [T]       java type
 * @return [T]
 */
@JvmName("toObject")
fun <T> ByteArray.jsonToObject(type: Type): T {
    return defaultJsonSerializer.toObject(this, type)
}

/**
 * json bytes -> T
 *
 * @receiver     json bytes
 * @param typeRef type reference of T
 * @param [T]           java type
 * @return [T]
 */
@JvmName("toObject")
fun <T> ByteArray.jsonToObject(typeRef: TypeRef<T>): T {
    return defaultJsonSerializer.toObject(this, typeRef)
}

/**
 * json bytes -> T
 *
 * @receiver json bytes
 * @param offset    offset of bytes to be parsed
 * @param type      class of T
 * @param [T]       java type
 * @return [T]
 */
@JvmName("toObject")
fun <T> ByteArray.jsonToObject(offset: Int, type: Class<T>): T {
    return defaultJsonSerializer.toObject(this, offset, type)
}

/**
 * json bytes -> T
 *
 * @receiver json bytes
 * @param offset    offset of bytes to be parsed
 * @param type      type of T
 * @param [T]       java type
 * @return [T]
 */
@JvmName("toObject")
fun <T> ByteArray.jsonToObject(offset: Int, type: Type): T {
    return defaultJsonSerializer.toObject(this, offset, type)
}

/**
 * json bytes -> T
 *
 * @receiver     json bytes
 * @param offset    offset of bytes to be parsed
 * @param typeRef type reference of T
 * @param [T]           java type
 * @return [T]
 */
@JvmName("toObject")
fun <T> ByteArray.jsonToObject(offset: Int, typeRef: TypeRef<T>): T {
    return defaultJsonSerializer.toObject(this, offset, typeRef)
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
@JvmName("toObject")
fun <T> ByteArray.jsonToObject(offset: Int, length: Int, type: Class<T>): T {
    return defaultJsonSerializer.toObject(this, offset, length, type)
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
@JvmName("toObject")
fun <T> ByteArray.jsonToObject(offset: Int, length: Int, type: Type): T {
    return defaultJsonSerializer.toObject(this, offset, length, type)
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
@JvmName("toObject")
fun <T> ByteArray.jsonToObject(offset: Int, length: Int, typeRef: TypeRef<T>): T {
    return defaultJsonSerializer.toObject(this, offset, length, typeRef)
}

/**
 * json stream -> T
 *
 * @receiver json stream
 * @param type       class of T
 * @param [T]        java type
 * @return [T]
 */
@JvmName("toObject")
fun <T> InputStream.jsonToObject(type: Class<T>): T {
    return defaultJsonSerializer.toObject(this, type)
}

/**
 * json stream -> T
 *
 * @receiver json stream
 * @param type       type of T
 * @param [T]        java type
 * @return [T]
 */
@JvmName("toObject")
fun <T> InputStream.jsonToObject(type: Type): T {
    return defaultJsonSerializer.toObject(this, type)
}

/**
 * json stream -> T
 *
 * @receiver    json stream
 * @param typeRef type reference of T
 * @param [T]           java type
 * @return [T]
 */
@JvmName("toObject")
fun <T> InputStream.jsonToObject(typeRef: TypeRef<T>): T {
    return defaultJsonSerializer.toObject(this, typeRef)
}

/**
 * json reader -> T
 *
 * @receiver json reader
 * @param type       class of T
 * @param [T]        java type
 * @return [T]
 */
@JvmName("toObject")
fun <T> Reader.jsonToObject(type: Class<T>): T {
    return defaultJsonSerializer.toObject(this, type)
}

/**
 * json reader -> T
 *
 * @receiver json reader
 * @param type       type of T
 * @param [T]        java type
 * @return [T]
 */
@JvmName("toObject")
fun <T> Reader.jsonToObject(type: Type): T {
    return defaultJsonSerializer.toObject(this, type)
}

/**
 * json reader -> T
 *
 * @receiver    json reader
 * @param typeRef type reference of T
 * @param [T]           java type
 * @return [T]
 */
@JvmName("toObject")
fun <T> Reader.jsonToObject(typeRef: TypeRef<T>): T {
    return defaultJsonSerializer.toObject(this, typeRef)
}

/**
 * json buffer -> T
 *
 * @receiver json buffer
 * @param type       class of T
 * @param [T]        java type
 * @return [T]
 */
@JvmName("toObject")
fun <T> ByteBuffer.jsonToObject(type: Class<T>): T {
    return defaultJsonSerializer.toObject(this, type)
}

/**
 * json buffer -> T
 *
 * @receiver json buffer
 * @param type       type of T
 * @param [T]        java type
 * @return [T]
 */
@JvmName("toObject")
fun <T> ByteBuffer.jsonToObject(type: Type): T {
    return defaultJsonSerializer.toObject(this, type)
}

/**
 * json buffer -> T
 *
 * @receiver    json buffer
 * @param typeRef type reference of T
 * @param [T]           java type
 * @return [T]
 */
@JvmName("toObject")
fun <T> ByteBuffer.jsonToObject(typeRef: TypeRef<T>): T {
    return defaultJsonSerializer.toObject(this, typeRef)
}

/**
 * json source -> T
 *
 * @receiver json source
 * @param type       class of T
 * @param [T]        java type
 * @return [T]
 */
@JvmName("toObject")
fun <T> URL.jsonToObject(type: Class<T>): T {
    return defaultJsonSerializer.toObject(this, type)
}

/**
 * json source -> T
 *
 * @receiver json source
 * @param type       type of T
 * @param [T]        java type
 * @return [T]
 */
@JvmName("toObject")
fun <T> URL.jsonToObject(type: Type): T {
    return defaultJsonSerializer.toObject(this, type)
}

/**
 * json source -> T
 *
 * @receiver    json source
 * @param typeRef type reference of T
 * @param [T]           java type
 * @return [T]
 */
@JvmName("toObject")
fun <T> URL.jsonToObject(typeRef: TypeRef<T>): T {
    return defaultJsonSerializer.toObject(this, typeRef)
}