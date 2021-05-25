package xyz.srclab.common.serialize.json

import xyz.srclab.common.serialize.Serializer
import java.io.InputStream
import java.io.Reader
import java.net.URL
import java.nio.ByteBuffer

/**
 * Json serialization of [Serializer].
 *
 * @author sunqian
 *
 * @see Json
 * @see Serializer
 */
interface JsonSerializer : Serializer<Json> {

    /**
     * Serialize or deserialize operation, source -> [Json], supports:
     *
     * * [CharSequence]
     * * [ByteArray];
     * * [InputStream];
     * * [Reader];
     * * [ByteBuffer];
     * * [URL];
     * * Other type seem as bean;
     *
     * @param source any object
     * @return [Json]
     */
    @JvmDefault
    fun toJson(source: Any?): Json {
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

    @JvmDefault
    fun toJsonString(source: Any?): String {
        return toJson(source).toJsonString()
    }

    companion object {

        @JvmField
        val DEFAULT = DEFAULT_OBJECT_MAPPER.toJsonSerializer()
    }
}