package xyz.srclab.common.codec

import org.bouncycastle.util.encoders.Base64Encoder
import org.bouncycastle.util.encoders.HexEncoder
import xyz.srclab.common.codec.CodecAlgorithm.Companion.toCodecAlgorithm
import xyz.srclab.common.lang.toBytes
import xyz.srclab.common.lang.toChars
import java.io.ByteArrayOutputStream
import java.io.OutputStream

/**
 * Encode codec for such as Hex, Base64 and Plain bytes/text.
 *
 * @see PlainCodec
 * @see HexCodec
 * @see Base64Codec
 */
interface EncodeCodec : Codec {

    @JvmDefault
    fun encode(data: ByteArray): ByteArray {
        return encode(data, 0)
    }

    @JvmDefault
    fun encode(data: ByteArray, offset: Int): ByteArray {
        return encode(data, offset, data.size - offset)
    }

    fun encode(data: ByteArray, offset: Int, length: Int): ByteArray

    @JvmDefault
    fun encode(data: ByteArray, output: OutputStream): Int {
        return encode(data, 0, output)
    }

    @JvmDefault
    fun encode(data: ByteArray, offset: Int, output: OutputStream): Int {
        return encode(data, offset, data.size - offset, output)
    }

    @JvmDefault
    fun encode(data: ByteArray, offset: Int, length: Int, output: OutputStream): Int {
        val bytes = encode(data, offset, length)
        output.write(bytes)
        return bytes.size
    }

    @JvmDefault
    fun encode(data: CharSequence): ByteArray {
        return encode(data.toBytes())
    }

    @JvmDefault
    fun encode(data: CharSequence, output: OutputStream): Int {
        return encode(data.toBytes(), output)
    }

    @JvmDefault
    fun decode(encoded: ByteArray): ByteArray {
        return decode(encoded, 0)
    }

    @JvmDefault
    fun decode(encoded: ByteArray, offset: Int): ByteArray {
        return decode(encoded, 0, encoded.size - offset)
    }

    fun decode(encoded: ByteArray, offset: Int, length: Int): ByteArray

    @JvmDefault
    fun decode(encoded: ByteArray, output: OutputStream): Int {
        return decode(encoded, 0, output)
    }

    @JvmDefault
    fun decode(encoded: ByteArray, offset: Int, output: OutputStream): Int {
        return decode(encoded, 0, encoded.size - offset, output)
    }

    @JvmDefault
    fun decode(encoded: ByteArray, offset: Int, length: Int, output: OutputStream): Int {
        val bytes = decode(encoded, offset, length)
        output.write(bytes)
        return bytes.size
    }

    @JvmDefault
    fun decode(encoded: CharSequence): ByteArray {
        return decode(encoded.toBytes())
    }

    @JvmDefault
    fun decode(encoded: CharSequence, output: OutputStream): Int {
        return decode(encoded.toBytes(), output)
    }

    @JvmDefault
    fun encodeToString(data: ByteArray): String {
        return encode(data).toChars()
    }

    @JvmDefault
    fun encodeToString(data: ByteArray, offset: Int): String {
        return encode(data, offset).toChars()
    }

    @JvmDefault
    fun encodeToString(data: ByteArray, offset: Int, length: Int): String {
        return encode(data, offset, length).toChars()
    }

    @JvmDefault
    fun encodeToString(data: CharSequence): String {
        return encode(data).toChars()
    }

    @JvmDefault
    fun decodeToString(encoded: ByteArray): String {
        return decode(encoded).toChars()
    }

    @JvmDefault
    fun decodeToString(encoded: ByteArray, offset: Int): String {
        return decode(encoded, offset).toChars()
    }

    @JvmDefault
    fun decodeToString(encoded: ByteArray, offset: Int, length: Int): String {
        return decode(encoded, offset, length).toChars()
    }

    @JvmDefault
    fun decodeToString(encoded: CharSequence): String {
        return decode(encoded).toChars()
    }

    companion object {

        @JvmStatic
        fun plain(): PlainCodec {
            return PlainCodec
        }

        @JvmStatic
        fun hex(): HexCodec {
            return HexCodec
        }

        @JvmStatic
        fun base64(): Base64Codec {
            return Base64Codec
        }

        @JvmName("withAlgorithm")
        @JvmStatic
        fun CharSequence.toEncodeCodec(): EncodeCodec {
            return this.toCodecAlgorithm().toEncodeCodec()
        }

        @JvmName("withAlgorithm")
        @JvmStatic
        fun CodecAlgorithm.toEncodeCodec(): EncodeCodec {
            return when (this.name) {
                CodecAlgorithm.PLAIN_NAME -> PlainCodec
                CodecAlgorithm.HEX_NAME -> HexCodec
                CodecAlgorithm.BASE64_NAME -> Base64Codec
                else -> throw IllegalArgumentException("Unknown algorithm: ${this.name}")
            }
        }

        @JvmStatic
        fun ByteArray.toHexString(): String {
            return HexCodec.encodeToString(this)
        }

        @JvmStatic
        fun ByteArray.toBase64String(): String {
            return Base64Codec.encodeToString(this)
        }
    }
}

/**
 * A `NOP` Codec just return raw, plain bytes/text.
 */
object PlainCodec : EncodeCodec {

    override val name: String = CodecAlgorithm.PLAIN_NAME

    override fun encode(data: ByteArray): ByteArray {
        return data.clone()
    }

    override fun encode(data: ByteArray, offset: Int): ByteArray {
        return data.sliceArray(offset until data.size)
    }

    override fun encode(data: ByteArray, offset: Int, length: Int): ByteArray {
        return data.sliceArray(offset until offset + length)
    }

    override fun encode(data: CharSequence): ByteArray {
        return data.toBytes()
    }

    override fun decode(encoded: ByteArray): ByteArray {
        return encoded.clone()
    }

    override fun decode(encoded: ByteArray, offset: Int): ByteArray {
        return encoded.sliceArray(offset until encoded.size)
    }

    override fun decode(encoded: ByteArray, offset: Int, length: Int): ByteArray {
        return encoded.sliceArray(offset until offset + length)
    }

    override fun decode(encoded: CharSequence): ByteArray {
        return encoded.toBytes()
    }

    override fun encodeToString(data: CharSequence): String {
        return data.toString()
    }

    override fun decodeToString(encoded: CharSequence): String {
        return encoded.toString()
    }
}

/**
 * Codec for hex encoding.
 */
object HexCodec : EncodeCodec {

    override val name: String = CodecAlgorithm.HEX_NAME

    private val encoder = HexEncoder()

    override fun encode(data: ByteArray, offset: Int, length: Int): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        encoder.encode(data, offset, length, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }

    override fun encode(data: ByteArray, offset: Int, length: Int, output: OutputStream): Int {
        return encoder.encode(data, offset, length, output)
    }

    override fun decode(encoded: ByteArray, offset: Int, length: Int): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        encoder.decode(encoded, offset, length, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }

    override fun decode(encoded: ByteArray, offset: Int, length: Int, output: OutputStream): Int {
        return encoder.decode(encoded, offset, length, output)
    }

    override fun decode(encoded: CharSequence, output: OutputStream): Int {
        return encoder.decode(encoded.toString(), output)
    }
}

/**
 * Codec for base64 encoding.
 */
object Base64Codec : EncodeCodec {

    override val name: String = CodecAlgorithm.BASE64_NAME

    private val encoder = Base64Encoder()

    override fun encode(data: ByteArray, offset: Int, length: Int): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        encoder.encode(data, offset, length, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }

    override fun encode(data: ByteArray, offset: Int, length: Int, output: OutputStream): Int {
        return encoder.encode(data, offset, length, output)
    }

    override fun decode(encoded: ByteArray, offset: Int, length: Int): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        encoder.decode(encoded, offset, length, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }

    override fun decode(encoded: ByteArray, offset: Int, length: Int, output: OutputStream): Int {
        return encoder.decode(encoded, offset, length, output)
    }

    override fun decode(encoded: CharSequence, output: OutputStream): Int {
        return encoder.decode(encoded.toString(), output)
    }
}