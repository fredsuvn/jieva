@file:JvmName("BJson")

package xyz.srclab.common.data.json

import xyz.srclab.common.base.remainingLength
import xyz.srclab.common.reflect.TypeRef
import java.io.InputStream
import java.lang.reflect.Type
import java.nio.ByteBuffer

private val jsonParser: JsonParser
    get() {
        return JsonParser.defaultParser()
    }

fun Any?.toJson(): Json {
    return jsonParser.toJson(this)
}

fun Any?.toJsonString(): String {
    return jsonParser.toString(this)
}

fun Any?.toJsonBytes(): ByteArray {
    return jsonParser.toBytes(this)
}

fun CharSequence.jsonToMap(): Map<String, Any?> {
    return jsonParser.parse(this).toMap()
}

@JvmOverloads
fun ByteArray.jsonToMap(offset: Int = 0, length: Int = remainingLength(this.size, offset)): Map<String, Any?> {
    return jsonParser.parse(this, offset, length).toMap()
}

@JvmName("parse")
fun CharSequence.parseJson(): Json {
    return jsonParser.parse(this)
}

@JvmName("parse")
@JvmOverloads
fun ByteArray.parseJson(offset: Int = 0, length: Int = remainingLength(this.size, offset)): Json {
    return jsonParser.parse(this, offset, length)
}

@JvmName("parse")
fun InputStream.parseJson(): Json {
    return jsonParser.parse(this)
}

@JvmName("parse")
fun ByteBuffer.parseJson(): Json {
    return jsonParser.parse(this)
}

@JvmName("parse")
fun <T> CharSequence.parseJson(type: Class<T>): T {
    return jsonParser.parse(this).toObject(type)
}

@JvmName("parse")
@JvmOverloads
fun <T> ByteArray.parseJson(type: Class<T>, offset: Int = 0, length: Int = remainingLength(this.size, offset)): T {
    return jsonParser.parse(this, offset, length).toObject(type)
}

@JvmName("parse")
fun <T> InputStream.parseJson(type: Class<T>): T {
    return jsonParser.parse(this).toObject(type)
}

@JvmName("parse")
fun <T> ByteBuffer.parseJson(type: Class<T>): T {
    return jsonParser.parse(this).toObject(type)
}

@JvmName("parse")
fun <T> CharSequence.parseJson(type: Type): T {
    return jsonParser.parse(this).toObject(type)
}

@JvmName("parse")
@JvmOverloads
fun <T> ByteArray.parseJson(type: Type, offset: Int = 0, length: Int = remainingLength(this.size, offset)): T {
    return jsonParser.parse(this, offset, length).toObject(type)
}

@JvmName("parse")
fun <T> InputStream.parseJson(type: Type): T {
    return jsonParser.parse(this).toObject(type)
}

@JvmName("parse")
fun <T> ByteBuffer.parseJson(type: Type): T {
    return jsonParser.parse(this).toObject(type)
}

@JvmName("parse")
fun <T> CharSequence.parseJson(type: TypeRef<T>): T {
    return jsonParser.parse(this).toObject(type)
}

@JvmName("parse")
@JvmOverloads
fun <T> ByteArray.parseJson(type: TypeRef<T>, offset: Int = 0, length: Int = remainingLength(this.size, offset)): T {
    return jsonParser.parse(this, offset, length).toObject(type)
}

@JvmName("parse")
fun <T> InputStream.parseJson(type: TypeRef<T>): T {
    return jsonParser.parse(this).toObject(type)
}

@JvmName("parse")
fun <T> ByteBuffer.parseJson(type: TypeRef<T>): T {
    return jsonParser.parse(this).toObject(type)
}