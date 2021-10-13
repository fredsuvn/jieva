package xyz.srclab.common.codec

import org.bouncycastle.util.encoders.Base64Encoder
import org.bouncycastle.util.encoders.Encoder
import org.bouncycastle.util.encoders.HexEncoder
import xyz.srclab.common.base.toBytes
import xyz.srclab.common.base.toChars
import xyz.srclab.common.codec.Codec.Companion.toCodecAlgorithm
import xyz.srclab.common.io.toInputStream
import java.io.ByteArrayOutputStream
import java.io.OutputStream

/**
 * Encode codec such as Hex, Base64 and Plain.
 *
 * @see PlainCodec
 * @see HexCodec
 * @see Base64Codec
 */
interface EncodeCodec : Codec {

    fun encode(data: ByteArray): ByteArray {
        return encode(data, 0)
    }

    fun encode(data: ByteArray, offset: Int): ByteArray {
        return encode(data, offset, data.size - offset)
    }

    fun encode(data: ByteArray, offset: Int, length: Int): ByteArray

    fun encode(data: ByteArray, output: OutputStream): Int {
        return encode(data, 0, output)
    }

    fun encode(data: ByteArray, offset: Int, output: OutputStream): Int {
        return encode(data, offset, data.size - offset, output)
    }

    fun encode(data: ByteArray, offset: Int, length: Int, output: OutputStream): Int

    fun encode(data: CharSequence): ByteArray

    fun encode(data: CharSequence, output: OutputStream): Int

    fun decode(encoded: ByteArray): ByteArray {
        return decode(encoded, 0)
    }

    fun decode(encoded: ByteArray, offset: Int): ByteArray {
        return decode(encoded, 0, encoded.size - offset)
    }

    fun decode(encoded: ByteArray, offset: Int, length: Int): ByteArray

    fun decode(encoded: ByteArray, output: OutputStream): Int {
        return decode(encoded, 0, output)
    }

    fun decode(encoded: ByteArray, offset: Int, output: OutputStream): Int {
        return decode(encoded, 0, encoded.size - offset, output)
    }

    fun decode(encoded: ByteArray, offset: Int, length: Int, output: OutputStream): Int

    fun decode(encoded: CharSequence): ByteArray

    fun decode(encoded: CharSequence, output: OutputStream): Int

    fun encodeToString(data: ByteArray): String {
        return encode(data).toChars()
    }

    fun encodeToString(data: ByteArray, offset: Int): String {
        return encode(data, offset).toChars()
    }

    fun encodeToString(data: ByteArray, offset: Int, length: Int): String {
        return encode(data, offset, length).toChars()
    }

    fun encodeToString(data: CharSequence): String {
        return encode(data).toChars()
    }

    fun decodeToString(encoded: ByteArray): String {
        return decode(encoded).toChars()
    }

    fun decodeToString(encoded: ByteArray, offset: Int): String {
        return decode(encoded, offset).toChars()
    }

    fun decodeToString(encoded: ByteArray, offset: Int, length: Int): String {
        return decode(encoded, offset, length).toChars()
    }

    fun decodeToString(encoded: CharSequence): String {
        return decode(encoded).toChars()
    }

    companion object {

        /**
         * Codec for hex encoding.
         */
        val HexCodec = BcprovEncodeCodec(
            HexEncoder(),
            CodecAlgorithm.HEX_NAME
        )

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

        @JvmStatic
        fun withAlgorithm(algorithm: CharSequence): EncodeCodec {
            return withAlgorithm(algorithm.toCodecAlgorithm())
        }

        @JvmStatic
        fun withAlgorithm(algorithm: CodecAlgorithm): EncodeCodec {
            return when (algorithm.name) {
                CodecAlgorithm.PLAIN_NAME -> PlainCodec
                CodecAlgorithm.HEX_NAME -> HexCodec
                CodecAlgorithm.BASE64_NAME -> Base64Codec
                else -> throw IllegalArgumentException("Unknown algorithm: ${algorithm.name}")
            }
        }

        private class BcprovEncodeCodec(
            private val encoder: Encoder,
            override val algorithm: String
        ) : EncodeCodec {

            override fun encode(data: ByteArray, offset: Int, length: Int): ByteArray {
                val output = ByteArrayOutputStream()
                encode(data, offset, length, output)
                return output.toByteArray()
            }

            override fun encode(data: ByteArray, offset: Int, length: Int, output: OutputStream): Int {
                return encoder.encode(data, offset, length, output)
            }

            override fun encode(data: CharSequence): ByteArray {
                return encode(data.toBytes())
            }

            override fun encode(data: CharSequence, output: OutputStream): Int {
                return encode(data.toBytes(), output)
            }

            override fun decode(encoded: ByteArray, offset: Int, length: Int): ByteArray {
                val output = ByteArrayOutputStream()
                decode(encoded, offset, length, output)
                return output.toByteArray()
            }

            override fun decode(encoded: ByteArray, offset: Int, length: Int, output: OutputStream): Int {
                return encoder.decode(encoded, offset, length, output)
            }

            override fun decode(encoded: CharSequence): ByteArray {
                val output = ByteArrayOutputStream()
                decode(encoded, output)
                return output.toByteArray()
            }

            override fun decode(encoded: CharSequence, output: OutputStream): Int {
                return encoder.decode(encoded.toString(), output)
            }
        }
    }
}

/**
 * A `NOP` Codec just return plain bytes/text.
 */
object PlainCodec : EncodeCodec {

    override val algorithm: String = CodecAlgorithm.PLAIN_NAME

    override fun encode(data: ByteArray, offset: Int, length: Int): ByteArray {
        return data.sliceArray(offset until length)
    }

    override fun encode(data: ByteArray, offset: Int, length: Int, output: OutputStream): Int {
        return data.toInputStream(offset, length).copyTo(output).toInt()
    }

    override fun encode(data: CharSequence): ByteArray {
        return data.toBytes()
    }

    override fun encode(data: CharSequence, output: OutputStream): Int {
        return encode(encode(data), output)
    }

    override fun decode(encoded: ByteArray, offset: Int, length: Int): ByteArray {
        return encode(encoded, offset, length)
    }

    override fun decode(encoded: ByteArray, offset: Int, length: Int, output: OutputStream): Int {
        return encode(encoded, offset, length, output)
    }

    override fun decode(encoded: CharSequence): ByteArray {
        return encode(encoded)
    }

    override fun decode(encoded: CharSequence, output: OutputStream): Int {
        return encode(encoded, output)
    }

    override fun encodeToString(data: CharSequence): String {
        return data.toString()
    }

    override fun decodeToString(encoded: CharSequence): String {
        return encoded.toString()
    }
}



/**
 * Codec for base64 encoding.
 */
object Base64Codec : BcprovEncodeCodec(
    Base64Encoder(),
    CodecAlgorithm.BASE64_NAME
)

