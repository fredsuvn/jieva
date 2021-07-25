@file:JvmName("Jacksons")

package xyz.srclab.common.serialize.json.jackson

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import xyz.srclab.common.serialize.json.JsonSerializer

@JvmField
val JAVA_TIME_MODULE = JavaTimeModule()

/**
 * Returns [JsonSerializer] by Jackson implementation.
 *
 * Note it will configure:
 *
 * * [DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES] : false
 */
@JvmName("newJsonSerializer")
@JvmOverloads
fun ObjectMapper.toJsonSerializer(addJavaTimeModule: Boolean = true): JsonSerializer {
    if (addJavaTimeModule) {
        this.registerModule(JAVA_TIME_MODULE)
    }
    this.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    return JacksonJsonSerializer(this)
}