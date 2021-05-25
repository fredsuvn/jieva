@file:JvmName("JsonSerials")

package xyz.srclab.common.serialize.json

import xyz.srclab.common.reflect.TypeRef
import java.io.InputStream
import java.io.Reader
import java.lang.reflect.Type
import java.net.URL
import java.nio.ByteBuffer

private val jsonSerializer: JsonSerializer = JsonSerializer.DEFAULT

fun Any?.toJson(): Json {
    return jsonSerializer.toJson(this)
}

@JvmOverloads
fun ByteArray.toJson(offset: Int = 0, length: Int = this.size - offset): Json {
    return jsonSerializer.deserialize(this, offset, length)
}

fun CharSequence.toJson(chars: CharSequence): Json {
    return jsonSerializer.deserialize(this)
}

fun InputStream.toJson(input: InputStream): Json {
    return jsonSerializer.deserialize(this)
}

fun Reader.toJson(reader: Reader): Json {
    return jsonSerializer.deserialize(this)
}

fun ByteBuffer.toJson(byteBuffer: ByteBuffer): Json {
    return jsonSerializer.deserialize(this)
}

fun URL.toJson(url: URL): Json {
    return jsonSerializer.deserialize(this)
}

fun Any?.toJsonString(): String {
    return toJson().toJsonString()
}

fun Any?.toJsonBytes(): ByteArray {
    return toJson().toJsonBytes()
}

fun CharSequence.stringify(): String {
    return jsonSerializer.serialize(this).toJsonString()
}

/**
 * Chars -> Object.
 */
@JvmName("toObject")
fun <T> CharSequence.jsonToObject(chars: CharSequence, type: Class<T>): T {
    return jsonSerializer.deserialize(this).toObject(type)
}

/**
 * Chars -> Object.
 */
@JvmName("toObject")
fun <T> CharSequence.jsonToObject(chars: CharSequence, type: Type): T {
    return jsonSerializer.deserialize(this).toObject(type)
}

/**
 * Chars -> Object.
 */
@JvmName("toObject")
fun <T> CharSequence.jsonToObject(chars: CharSequence, type: TypeRef<T>): T {
    return jsonSerializer.deserialize(this).toObject(type)
}