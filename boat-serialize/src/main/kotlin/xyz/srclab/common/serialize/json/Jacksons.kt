package xyz.srclab.common.serialize.json

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.databind.node.JsonNodeType
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import java.io.*
import java.lang.reflect.Type
import java.net.URL
import java.nio.ByteBuffer

internal val DEFAULT_OBJECT_MAPPER by lazy {
    val mapper = JsonMapper()
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    val javaTimeModule = JavaTimeModule()
    mapper.registerModule(javaTimeModule)
    mapper
}

internal class JsonSerializerImpl(private val objectMapper: ObjectMapper) : JsonSerializer {

    override fun toJsonString(javaObject: Any): String {
        return try {
            objectMapper.writeValueAsString(javaObject)
        } catch (e: Exception) {
            throw IllegalStateException(e)
        }
    }

    override fun toJsonBytes(javaObject: Any): ByteArray {
        return try {
            objectMapper.writeValueAsBytes(javaObject)
        } catch (e: Exception) {
            throw IllegalStateException(e)
        }
    }

    override fun writeJson(javaObject: Any, outputStream: OutputStream) {
        try {
            objectMapper.writeValue(outputStream, javaObject)
        } catch (e: Exception) {
            throw IllegalStateException(e)
        }
    }

    override fun writeJson(javaObject: Any, writer: Writer) {
        try {
            objectMapper.writeValue(writer, javaObject)
        } catch (e: Exception) {
            throw IllegalStateException(e)
        }
    }

    override fun toJson(jsonString: CharSequence): Json {
        return try {
            JsonImpl(
                objectMapper,
                objectMapper.readTree(jsonString.toString())
            )
        } catch (e: Exception) {
            throw IllegalStateException(e)
        }
    }

    override fun toJson(jsonBytes: ByteArray, offset: Int, length: Int): Json {
        return try {
            JsonImpl(
                objectMapper,
                objectMapper.readTree(jsonBytes, offset, length)
            )
        } catch (e: Exception) {
            throw IllegalStateException(e)
        }
    }

    override fun toJson(jsonStream: InputStream): Json {
        return try {
            JsonImpl(
                objectMapper,
                objectMapper.readTree(jsonStream)
            )
        } catch (e: Exception) {
            throw IllegalStateException(e)
        }
    }

    override fun toJson(jsonReader: Reader): Json {
        return try {
            JsonImpl(
                objectMapper,
                objectMapper.readTree(jsonReader)
            )
        } catch (e: Exception) {
            throw IllegalStateException(e)
        }
    }

    override fun toJson(jsonBuffer: ByteBuffer): Json {
        return try {
            JsonImpl(
                objectMapper,
                objectMapper.readTree(bufferToBytes(jsonBuffer))
            )
        } catch (e: Exception) {
            throw IllegalStateException(e)
        }
    }

    override fun toJson(jsonSource: URL): Json {
        return try {
            JsonImpl(
                objectMapper,
                objectMapper.readTree(jsonSource)
            )
        } catch (e: Exception) {
            throw IllegalStateException(e)
        }
    }

    override fun toJson(javaObject: Any): Json {
        if (javaObject is CharSequence) {
            return toJson(javaObject.toString())
        }
        if (javaObject is ByteArray) {
            return toJson(javaObject)
        }
        if (javaObject is InputStream) {
            return toJson(javaObject)
        }
        if (javaObject is Reader) {
            return toJson(javaObject)
        }
        if (javaObject is ByteBuffer) {
            return toJson(javaObject)
        }
        return if (javaObject is URL) {
            toJson(javaObject)
        } else try {
            val json = toJsonString(javaObject)
            JsonImpl(
                objectMapper,
                objectMapper.readTree(json)
            )
        } catch (e: Exception) {
            throw IllegalStateException(e)
        }
    }

    private fun bufferToBytes(buffer: ByteBuffer): ByteArray {
        val bytesArray = ByteArray(buffer.remaining())
        buffer[bytesArray, 0, bytesArray.size]
        return bytesArray
    }
}

internal class JsonImpl(private val objectMapper: ObjectMapper, private val jsonNode: JsonNode) : Json {

    override val type: JsonType = jsonNode.nodeType.toJsonType()

    override fun toJsonString(): String {
        return jsonNode.toString()
    }

    override fun toJsonBytes(): ByteArray {
        return try {
            objectMapper.writeValueAsBytes(jsonNode)
        } catch (e: JsonProcessingException) {
            throw IllegalStateException(e)
        }
    }

    override fun writeJson(outputStream: OutputStream) {
        try {
            objectMapper.writeValue(outputStream, jsonNode)
        } catch (e: IOException) {
            throw IllegalStateException(e)
        }
    }

    override fun writeJson(writer: Writer) {
        try {
            objectMapper.writeValue(writer, jsonNode)
        } catch (e: IOException) {
            throw IllegalStateException(e)
        }
    }

    override fun writeJson(buffer: ByteBuffer) {
        buffer.put(toJsonBytes())
    }

    override fun <T> toJavaObjectOrNull(type: Type): T? {
        return try {
            objectMapper.readValue(jsonNode.traverse(), object : TypeReference<T>() {
                override fun getType(): Type {
                    return type
                }
            })
        } catch (e: IOException) {
            throw IllegalStateException(e)
        }
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
}