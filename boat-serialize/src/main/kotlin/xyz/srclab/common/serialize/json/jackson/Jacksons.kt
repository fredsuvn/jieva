@file:JvmName("Jacksons")

package xyz.srclab.common.serialize.json.jackson

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import xyz.srclab.common.serialize.json.JsonSerializer

@JvmField
val JAVA_TIME_MODULE = JavaTimeModule()

/**
 * Returns [JsonSerializer] by Jackson implementation.
 *
 * Following module and configure will be set:
 * * [DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES] : false
 * * [JavaTimeModule]
 * * [KotlinModule]
 */
@JvmName("newJsonSerializer")
fun ObjectMapper.toJsonSerializer(): JsonSerializer {
    this.registerModule(JAVA_TIME_MODULE)
    this.registerKotlinModule()
    this.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    return JacksonJsonSerializer(this)
}