package xyz.srclab.common.serialize.jackson

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.node.JsonNodeType
import com.fasterxml.jackson.datatype.jsr310.PackageVersion
import xyz.srclab.common.io.toInputStream
import xyz.srclab.common.io.toReader
import xyz.srclab.common.serialize.json.Json
import xyz.srclab.common.serialize.json.JsonSerializer
import xyz.srclab.common.serialize.json.JsonType
import java.io.InputStream
import java.io.OutputStream
import java.io.Reader
import java.io.Writer
import java.lang.reflect.Type
import java.nio.charset.Charset

open class JacksonJsonSerializer(
    private val objectMapper: ObjectMapper
) : JsonSerializer {

    init {
        objectMapper.registerModule(JsonImplModule())
    }

    override fun serialize(any: Any?): Json {
        if (any is Json) {
            return any
        }
        return JsonImpl(objectMapper.convertValue(any, JsonNode::class.java))
    }

    override fun deserialize(input: InputStream): Json {
        return JsonImpl(objectMapper.readTree(input))
    }

    override fun deserialize(reader: Reader): Json {
        return JsonImpl(objectMapper.readTree(reader))
    }

    override fun deserialize(bytes: ByteArray, offset: Int, length: Int): Json {
        return JsonImpl(objectMapper.readTree(bytes, offset, length))
    }

    inner class JsonImpl(
        val jsonNode: JsonNode
    ) : Json {

        override val type: JsonType = jsonNode.nodeType.toJsonType()

        override fun toInputStream(): InputStream {
            return toBytes().toInputStream()
        }

        override fun toReader(): Reader {
            return toText().toReader()
        }

        override fun toReader(charset: Charset): Reader {
            return toReader()
        }

        override fun toBytes(): ByteArray {
            return objectMapper.writeValueAsBytes(jsonNode)
        }

        override fun toText(): String {
            return objectMapper.writeValueAsString(jsonNode)
        }

        override fun toText(charset: Charset): String {
            return toText()
        }

        override fun writeTo(outputStream: OutputStream) {
            objectMapper.writeValue(outputStream, jsonNode)
        }

        override fun writeTo(writer: Writer) {
            objectMapper.writeValue(writer, jsonNode)
        }

        override fun toJsonString(): String {
            return objectMapper.writeValueAsString(jsonNode)
        }

        override fun toJsonBytes(): ByteArray {
            return toBytes()
        }

        override fun <T> parseOrNull(type: Type): T? {
            return objectMapper.readValue(jsonNode.traverse(), object : TypeReference<T>() {
                override fun getType(): Type {
                    return type
                }
            })
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

        override fun toString(): String {
            return jsonNode.toString()
        }
    }

    private inner class JsonImplModule : SimpleModule(PackageVersion.VERSION) {

        init {
            addSerializer(JsonImpl::class.java, JsonImplSerializer())
            addDeserializer(Json::class.java, JsonImplDeserializer())
        }

        private inner class JsonImplSerializer : com.fasterxml.jackson.databind.JsonSerializer<JsonImpl>() {

            override fun handledType(): Class<JsonImpl> {
                return JsonImpl::class.java
            }

            override fun serialize(value: JsonImpl, gen: JsonGenerator, serializers: SerializerProvider) {
                gen.writeObject(value.jsonNode)
            }
        }

        private inner class JsonImplDeserializer : JsonDeserializer<Json>() {

            override fun handledType(): Class<*> {
                return Json::class.java
            }

            override fun deserialize(parser: JsonParser, ctxt: DeserializationContext): Json {
                return JsonImpl(
                    objectMapper.readValue(parser, JsonNode::class.java)
                )
            }
        }
    }
}