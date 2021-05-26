package xyz.srclab.common.serialize

import xyz.srclab.common.serialize.json.JsonSerializer
import java.io.InputStream
import java.io.Reader
import java.net.URL
import java.nio.ByteBuffer

/**
 * Serializer interface, to serialize and deserialize [T].
 *
 * @see Serial
 * @see JsonSerializer
 */
interface Serializer<S : Serial> {

    fun serialize(any: Any?): S

    @JvmDefault
    fun deserialize(bytes: ByteArray): S {
        return deserialize(bytes, 0)
    }

    @JvmDefault
    fun deserialize(bytes: ByteArray, offset: Int): S {
        return deserialize(bytes, offset, bytes.size - offset)
    }

    @JvmDefault
    fun deserialize(bytes: ByteArray, offset: Int, length: Int): S

    fun deserialize(chars: CharSequence): S

    fun deserialize(input: InputStream): S

    fun deserialize(reader: Reader): S

    fun deserialize(byteBuffer: ByteBuffer): S

    fun deserialize(url: URL): S
}