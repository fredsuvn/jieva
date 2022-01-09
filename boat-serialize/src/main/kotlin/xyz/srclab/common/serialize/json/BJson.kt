@file:JvmName("Jsons")

package xyz.srclab.common.serialize.json

import xyz.srclab.common.reflect.TypeRef
import java.io.InputStream
import java.io.Reader
import java.lang.reflect.Type
import java.net.URL
import java.nio.ByteBuffer

private val jsonSerializer: JsonSerializer
    get() {
        return JsonSerializer.defaultSerializer()
    }

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
 * @see [JsonSerializer.parse]
 */
@JvmName("parse")
fun Any?.parseJson(): Json {
    return jsonSerializer.parse(this)
}

fun Any?.toJson(): Json {
    return jsonSerializer.serialize(this)
}

@JvmName("parse")
@JvmOverloads
fun ByteArray.parseJson(offset: Int = 0, length: Int = this.size - offset): Json {
    return jsonSerializer.deserialize(this, offset, length)
}

@JvmName("parse")
fun CharSequence.parseJson(): Json {
    return jsonSerializer.deserialize(this)
}

@JvmName("parse")
fun InputStream.parseJson(): Json {
    return jsonSerializer.deserialize(this)
}

@JvmName("parse")
fun Reader.parseJson(): Json {
    return jsonSerializer.deserialize(this)
}

@JvmName("parse")
fun ByteBuffer.parseJson(): Json {
    return jsonSerializer.deserialize(this)
}

@JvmName("parse")
fun URL.parseJson(): Json {
    return jsonSerializer.deserialize(this)
}

fun Any?.toJsonString(): String {
    return parseJson().toJsonString()
}

fun Any?.toJsonBytes(): ByteArray {
    return parseJson().toJsonBytes()
}

@JvmName("parse")
fun <T> CharSequence.parseJson(type: Class<T>): T {
    return jsonSerializer.deserialize(this).parse(type)
}

@JvmName("parse")
fun <T> CharSequence.parseJson(type: Type): T {
    return jsonSerializer.deserialize(this).parse(type)
}

@JvmName("parse")
fun <T> CharSequence.parseJson(type: TypeRef<T>): T {
    return jsonSerializer.deserialize(this).parse(type)
}