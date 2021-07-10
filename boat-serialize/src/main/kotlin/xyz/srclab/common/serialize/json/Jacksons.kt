@file:JvmName("Jacksons")

package xyz.srclab.common.serialize.json

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.databind.node.JsonNodeType
import com.fasterxml.jackson.databind.node.NullNode
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import java.io.*
import java.lang.reflect.Type
import java.net.URL
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

@JvmField
val DEFAULT_OBJECT_MAPPER = run {
    val mapper = JsonMapper()
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    val javaTimeModule = JavaTimeModule()
    mapper.registerModule(javaTimeModule)
    mapper
}

@JvmName("newJsonSerializer")
fun ObjectMapper.toJsonSerializer(): JsonSerializer {
    return JsonSerializerImpl(this)
}

private class JsonSerializerImpl(
    private val objectMapper: ObjectMapper
) : JsonSerializer {

    override fun serialize(any: Any?): Json {
        if (any === null) {
            return JsonImpl.NULL
        }
        return try {
            JsonImpl(
                objectMapper,
                objectMapper.convertValue(any, JsonNode::class.java)
            )
        } catch (e: Exception) {
            throw e
        }
    }

    override fun deserialize(chars: CharSequence): Json {
        return try {
            JsonImpl(
                objectMapper,
                objectMapper.readTree(chars.toString())
            )
        } catch (e: Exception) {
            throw e
        }
    }

    override fun deserialize(bytes: ByteArray, offset: Int, length: Int): Json {
        return try {
            JsonImpl(
                objectMapper,
                objectMapper.readTree(bytes, offset, length)
            )
        } catch (e: Exception) {
            throw e
        }
    }

    override fun deserialize(input: InputStream): Json {
        return try {
            JsonImpl(
                objectMapper,
                objectMapper.readTree(input)
            )
        } catch (e: Exception) {
            throw e
        }
    }

    override fun deserialize(reader: Reader): Json {
        return try {
            JsonImpl(
                objectMapper,
                objectMapper.readTree(reader)
            )
        } catch (e: Exception) {
            throw e
        }
    }

    override fun deserialize(byteBuffer: ByteBuffer): Json {
        return try {
            JsonImpl(
                objectMapper,
                objectMapper.readTree(bufferToBytes(byteBuffer))
            )
        } catch (e: Exception) {
            throw e
        }
    }

    override fun deserialize(url: URL): Json {
        return try {
            JsonImpl(
                objectMapper,
                objectMapper.readTree(url)
            )
        } catch (e: Exception) {
            throw e
        }
    }

    private fun bufferToBytes(buffer: ByteBuffer): ByteArray {
        val bytesArray = ByteArray(buffer.remaining())
        buffer[bytesArray, 0, bytesArray.size]
        return bytesArray
    }
}

private class JsonImpl(
    private val objectMapper: ObjectMapper,
    private val jsonNode: JsonNode
) : Json {

    override val type: JsonType = jsonNode.nodeType.toJsonType()

    override fun <T : OutputStream> writeTo(outputStream: T): T {
        try {
            objectMapper.writeValue(outputStream as OutputStream, jsonNode)
            return outputStream
        } catch (e: IOException) {
            throw e
        }
    }

    override fun <T : Writer> writeTo(writer: T): T {
        try {
            objectMapper.writeValue(writer, jsonNode)
            return writer
        } catch (e: IOException) {
            throw e
        }
    }

    override fun toBytes(): ByteArray {
        return try {
            objectMapper.writeValueAsBytes(jsonNode)
        } catch (e: JsonProcessingException) {
            throw e
        }
    }

    override fun toInputStream(): InputStream {
        return ByteArrayInputStream(toBytes())
    }

    override fun <T> toObjectOrNull(type: Type): T? {
        return try {
            objectMapper.readValue(jsonNode.traverse(), object : TypeReference<T>() {
                override fun getType(): Type {
                    return type
                }
            })
        } catch (e: IOException) {
            throw e
        }
    }

    override fun toJsonString(): String {
        return jsonNode.toString()
    }

    override fun toJsonBytes(): ByteArray {
        return toJsonString().toByteArray(StandardCharsets.UTF_8)
    }

    override fun toObjectString(): String {
        return toObject(String::class.java)
    }

    override fun toString(): String {
        return toJsonString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is JsonImpl) return false
        return jsonNode == other.jsonNode
    }

    override fun hashCode(): Int {
        return jsonNode.hashCode()
    }

    private fun JsonNodeType.toJsonType(): JsonType {
        return when (this) {
            JsonNodeType.NULL -> return JsonType.NULL
            JsonNodeType.POJO, JsonNodeType.OBJECT -> return JsonType.OBJECT
            JsonNodeType.ARRAY -> return JsonType.ARRAY
            JsonNodeType.NUMBER -> return JsonType.NUMBER
            JsonNodeType.STRING -> return JsonType.STRING
            JsonNodeType.BOOLEAN -> return JsonType.BOOLEAN
            JsonNodeType.BINARY -> return JsonType.BINARY
            else -> JsonType.MISSING
        }
    }

    companion object {

        val NULL = JsonImpl(DEFAULT_OBJECT_MAPPER, NullNode.getInstance())
    }
}