@file:JvmName("ProtobufJsons")

package xyz.srclab.common.protobuf

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.hubspot.jackson.datatype.protobuf.ProtobufModule
import xyz.srclab.common.serialize.json.JsonSerializer
import xyz.srclab.common.serialize.json.JsonSerializer.Companion.toJsonSerializer

@JvmField
val PROTOBUF_OBJECT_MAPPER: JsonMapper = run {
    val mapper = JsonMapper()
    mapper.configure(JsonGenerator.Feature.IGNORE_UNKNOWN, false)
    mapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false)
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)

    val javaTimeModule = JavaTimeModule()
    mapper.registerModule(javaTimeModule)
    val protobufModule = ProtobufModule()
    mapper.registerModule(protobufModule)
    mapper
}

@JvmField
val PROTOBUF_JSON_SERIALIZER: JsonSerializer = PROTOBUF_OBJECT_MAPPER.toJsonSerializer()