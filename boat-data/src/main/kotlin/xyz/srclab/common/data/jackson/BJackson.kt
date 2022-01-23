@file:JvmName("BJackson")

package xyz.srclab.common.data.jackson

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import xyz.srclab.common.data.json.JsonParser

@JvmField
val JAVA_TIME_MODULE = JavaTimeModule()

/**
 * Adds following modules and configurations:
 *
 * * [DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES] : false
 * * [JavaTimeModule]
 * * [KotlinModule]
 */
fun ObjectMapper.addCommonSettings() = apply {
    this.registerModule(JAVA_TIME_MODULE)
    this.registerKotlinModule()
    this.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
}

fun ObjectMapper.toJsonSerializer(): JsonParser {
    return JacksonJsonParser(this)
}