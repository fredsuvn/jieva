@file:JvmName("ProtobufJsons")

package xyz.srclab.common.protobuf

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.hubspot.jackson.datatype.protobuf.ProtobufModule
import xyz.srclab.common.data.json.JsonParser
import xyz.srclab.common.data.json.jackson.JAVA_TIME_MODULE
import xyz.srclab.common.data.json.jackson.toJsonSerializer

/**
 * Default [JsonParser] supports protobuf types.
 */
@JvmField
val PROTOBUF_JSON_SERIALIZER: JsonParser = run {
    val objectMapper = ObjectMapper()
    objectMapper.configureForProtobuf(true)
    objectMapper.toJsonSerializer()
}

/**
 * Configures given [ObjectMapper]:
 *
 * * [JsonGenerator.Feature.IGNORE_UNKNOWN] : false
 * * [MapperFeature.DEFAULT_VIEW_INCLUSION] : false
 * * [DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES] : false
 * * [SerializationFeature.WRITE_DATES_AS_TIMESTAMPS] : false
 *
 * Adds module:
 *
 * * [ProtobufModule]
 */
fun ObjectMapper.configureForProtobuf(addJavaTimeModule: Boolean) {
    this.configure(JsonGenerator.Feature.IGNORE_UNKNOWN, false)
    this.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false)
    this.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    this.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
    val protobufModule = ProtobufModule()
    this.registerModule(protobufModule)
    if (addJavaTimeModule) {
        this.registerModule(JAVA_TIME_MODULE)
    }
}