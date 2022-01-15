package xyz.srclab.common.serialize

import xyz.srclab.common.io.asInputStream
import xyz.srclab.common.io.asReader
import xyz.srclab.common.serialize.json.JsonSerializer
import java.io.InputStream
import java.io.Reader
import java.net.URL
import java.nio.ByteBuffer

/**
 * Serializer interface, to serialize and deserialize Java object or binary sequence to [S].
 *
 * @see Serial
 * @see JsonSerializer
 */
interface Serializer<S : Serial> {

    // Serialize methods: Java Object -> Serial

    fun serialize(any: Any?): S

    // Deserialize methods: Binary Sequence -> Serial

    fun deserialize(input: InputStream): S

    fun deserialize(reader: Reader): S

    fun deserialize(bytes: ByteArray): S {
        return deserialize(bytes, 0)
    }

    fun deserialize(bytes: ByteArray, offset: Int): S {
        return deserialize(bytes, offset, bytes.size - offset)
    }

    fun deserialize(bytes: ByteArray, offset: Int, length: Int): S

    fun deserialize(byteBuffer: ByteBuffer): S {
        return deserialize(byteBuffer.asInputStream())
    }

    fun deserialize(chars: CharSequence): S {
        return deserialize(chars.asReader())
    }

    fun deserialize(url: URL): S {
        return deserialize(url.openStream())
    }
}