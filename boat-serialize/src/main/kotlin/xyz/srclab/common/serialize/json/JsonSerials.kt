@file:JvmName("JsonSerials")

package xyz.srclab.common.serialize.json

import xyz.srclab.common.reflect.TypeRef
import java.io.InputStream
import java.io.Reader
import java.lang.reflect.Type
import java.net.URL
import java.nio.ByteBuffer

private val jsonSerializer: JsonSerializer = JsonSerializer.DEFAULT

/**
 * Serialize or deserialize operation, source -> [Json].
 *
 * Deserialize:
 *
 * * [CharSequence]
 * * [ByteArray];
 * * [InputStream];
 * * [Reader];
 * * [ByteBuffer];
 * * [URL];
 *
 * Serialize:
 *
 * * Other types;
 *
 * @see [JsonSerializer.toJson]
 */
fun Any?.toJson(): Json {
    return jsonSerializer.toJson(this)
}

@JvmOverloads
fun ByteArray.toJson(offset: Int = 0, length: Int = this.size - offset): Json {
    return jsonSerializer.deserialize(this, offset, length)
}

fun CharSequence.toJson(): Json {
    return jsonSerializer.deserialize(this)
}

fun InputStream.toJson(): Json {
    return jsonSerializer.deserialize(this)
}

fun Reader.toJson(): Json {
    return jsonSerializer.deserialize(this)
}

fun ByteBuffer.toJson(): Json {
    return jsonSerializer.deserialize(this)
}

fun URL.toJson(): Json {
    return jsonSerializer.deserialize(this)
}

fun Any?.toJsonString(): String {
    return toJson().toJsonString()
}

fun Any?.toJsonBytes(): ByteArray {
    return toJson().toJsonBytes()
}

/**
 * Serializes [this] to json string.
 */
@JvmName("stringify")
fun Any?.stringifyJson(): String {
    return jsonSerializer.serialize(this).toJsonString()
}

/**
 * Chars -> Object.
 */
@JvmName("toObject")
fun <T> CharSequence.jsonToObject(type: Class<T>): T {
    return jsonSerializer.deserialize(this).toObject(type)
}

/**
 * Chars -> Object.
 */
@JvmName("toObject")
fun <T> CharSequence.jsonToObject(type: Type): T {
    return jsonSerializer.deserialize(this).toObject(type)
}

/**
 * Chars -> Object.
 */
@JvmName("toObject")
fun <T> CharSequence.jsonToObject(type: TypeRef<T>): T {
    return jsonSerializer.deserialize(this).toObject(type)
}