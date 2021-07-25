@file:JvmName("Jacksons")

package xyz.srclab.common.serialize.json.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import xyz.srclab.common.serialize.json.JsonSerializer

@JvmField
val JAVA_TIME_MODULE = JavaTimeModule()

/**
 * Returns [JsonSerializer] by Jackson implementation.
 */
@JvmName("newJsonSerializer")
@JvmOverloads
fun ObjectMapper.toJsonSerializer(withJavaTimeModule: Boolean = true): JsonSerializer {
    if (withJavaTimeModule) {
        this.registerModule(JAVA_TIME_MODULE)
    }
    return JacksonJsonSerializer(this)
}