package xyz.srclab.common.data.jackson

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.node.JsonNodeType
import com.fasterxml.jackson.datatype.jsr310.PackageVersion
import xyz.srclab.common.data.json.Json
import xyz.srclab.common.data.json.JsonType
import xyz.srclab.common.io.asInputStream
import xyz.srclab.common.io.asReader
import java.io.InputStream
import java.io.Serializable
import java.lang.reflect.Type
import java.nio.ByteBuffer


open class JacksonJsonParser(
    private val objectMapper: ObjectMapper
) : xyz.srclab.common.data.json.JsonParser {

    init {
        objectMapper.registerModule(JsonImplModule())
    }

    override fun toString(obj: Any?): String {
        return objectMapper.writeValueAsString(obj)
    }

    override fun toBytes(obj: Any?): ByteArray {
        return objectMapper.writeValueAsBytes(obj)
    }

    override fun toDataElement(obj: Any?): Json {
        val jsonNode = objectMapper.convertValue(obj, JsonNode::class.java)
        return JsonImpl(jsonNode)
    }

    override fun parse(chars: CharSequence): Json {
        val jsonNode = if (chars is String) objectMapper.readTree(chars) else objectMapper.readTree(chars.asReader())
        return JsonImpl(jsonNode)
    }

    override fun parse(bytes: ByteArray, offset: Int, length: Int): Json {
        val jsonNode = objectMapper.readTree(bytes, offset, length)
        return JsonImpl(jsonNode)
    }

    override fun parse(input: InputStream): Json {
        val jsonNode = objectMapper.readTree(input)
        return JsonImpl(jsonNode)
    }

    override fun parse(byteBuffer: ByteBuffer): Json {
        val jsonNode = objectMapper.readTree(byteBuffer.asInputStream())
        return JsonImpl(jsonNode)
    }

    private inner class JsonImpl(
        val jsonNode: JsonNode
    ) : Json, Serializable {

        override val type: JsonType = jsonNode.nodeType.toJsonType()

        override fun toString(): String {
            return jsonNode.toString()
        }

        override fun toBytes(): ByteArray {
            jsonNode.binaryValue()
            return objectMapper.writeValueAsBytes(jsonNode)
        }

        override fun <T> toObjectOrNull(type: Type): T? {
            //return objectMapper.readValue(jsonNode.traverse(), object : TypeReference<T>() {
            //    override fun getType(): Type {
            //        return type
            //    }
            //})
            return objectMapper.convertValue(jsonNode, object : TypeReference<T>() {
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

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is JsonImpl) return false
            return this.jsonNode == other.jsonNode
        }

        override fun hashCode(): Int {
            return jsonNode.hashCode()
        }
    }

    private inner class JsonImplModule : SimpleModule(PackageVersion.VERSION) {

        init {
            addSerializer(JsonImpl::class.java, JsonImplSerializer())
            addDeserializer(Json::class.java, JsonImplDeserializer())
        }

        private inner class JsonImplSerializer : JsonSerializer<JsonImpl>() {

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

            override fun deserialize(
                parser: com.fasterxml.jackson.core.JsonParser,
                ctxt: DeserializationContext
            ): Json {
                return JsonImpl(
                    objectMapper.readValue(parser, JsonNode::class.java)
                )
            }
        }
    }
}