@file:JvmName("BProtobuf")

package xyz.srclab.common.protobuf

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.json.JsonMapper
import com.hubspot.jackson.datatype.protobuf.ProtobufModule
import xyz.srclab.common.bean.BeanResolver
import xyz.srclab.common.bean.BeanResolver.Companion.extend
import xyz.srclab.common.convert.BeanConvertHandler
import xyz.srclab.common.convert.ConvertHandler
import xyz.srclab.common.convert.Converter
import xyz.srclab.common.data.jackson.setCommon
import xyz.srclab.common.data.jackson.toJsonSerializer
import xyz.srclab.common.data.json.JsonParser

private val defaultProtobufConverter: Converter = run {
    val handlers = ConvertHandler.DEFAULT_HANDLERS.toMutableList()
    handlers[handlers.size - 1] = BeanConvertHandler(
        ProtobufBeanCreator, BeanResolver.defaultResolver().extendProtobuf())
    Converter.newConverter(handlers)
}

private val defaultProtobufJsonParser: JsonParser = JsonMapper().setProtobuf().toJsonSerializer()

fun defaultProtobufConverter(): Converter {
    return defaultProtobufConverter
}

fun defaultProtobufBeanResolver(): BeanResolver {
    return BeanResolver.defaultResolver().extendProtobuf()
}

fun BeanResolver.extendProtobuf(): BeanResolver {
    return this.extend(ProtobufBeanResolveHandler)
}

fun defaultProtobufJsonParser(): JsonParser {
    return defaultProtobufJsonParser
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
 *
 * Note it will call [setCommon] at first.
 */
fun ObjectMapper.setProtobuf() = apply {
    this.setCommon()
    this.configure(JsonGenerator.Feature.IGNORE_UNKNOWN, false)
    this.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false)
    this.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    this.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
    val protobufModule = ProtobufModule()
    this.registerModule(protobufModule)
}