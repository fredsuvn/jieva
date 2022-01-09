package xyz.srclab.common.serialize.json

import com.fasterxml.jackson.databind.json.JsonMapper
import xyz.srclab.common.serialize.Serializer
import xyz.srclab.common.serialize.jackson.addCommonSettings
import xyz.srclab.common.serialize.jackson.toJsonSerializer
import java.io.InputStream
import java.io.Reader
import java.net.URL
import java.nio.ByteBuffer

/**
 * Json [Serializer].
 *
 * It is thread-safe if it uses [ObjectMapper] as underlying implementation.
 *
 * @see Json
 * @see Serializer
 */
interface JsonSerializer : Serializer<Json> {

    /**
     * Serialize or deserialize operation, source -> [Json].
     *
     * Deserialize:
     *
     * * [CharSequence]
     * * [ByteArray];
     * * [InputStream];
     * * [Reader];
     * * [ByteBuffer];
     * * [URL];
     *
     * Serialize:
     *
     * * Other types;
     *
     * @param source any object
     * @return [Json]
     */
    fun parse(source: Any?): Json {
        return when (source) {
            is CharSequence -> deserialize(source)
            is ByteArray -> deserialize(source)
            is InputStream -> deserialize(source)
            is Reader -> deserialize(source)
            is ByteBuffer -> deserialize(source)
            is URL -> deserialize(source)
            else -> serialize(source)
        }
    }

    companion object {

        private var defaultSerializer: JsonSerializer = JsonMapper().addCommonSettings().toJsonSerializer()

        @JvmStatic
        fun defaultSerializer(): JsonSerializer {
            return defaultSerializer
        }

        @JvmStatic
        fun setDefaultSerializer(defaultSerializer: JsonSerializer) {
            this.defaultSerializer = defaultSerializer
        }
    }
}