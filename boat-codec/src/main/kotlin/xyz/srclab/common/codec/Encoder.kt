package xyz.srclab.common.codec

import org.bouncycastle.util.encoders.Base64Encoder
import org.bouncycastle.util.encoders.HexEncoder
import xyz.srclab.common.base.toBytes
import xyz.srclab.common.base.toChars
import xyz.srclab.common.codec.Codec.Companion.toCodecAlgorithm
import xyz.srclab.common.codec.Encoder.Companion.HexCodec
import xyz.srclab.common.io.toInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.io.Reader

/**
 * To encode/decoder such as Hex, Base64 and Plain.
 *
 * @see PlainCodec
 * @see HexCodec
 * @see Base64Codec
 */
interface Encoder : Codec {

    //encode:

    fun encode(data: ByteArray): ByteArray {
        return encode(data, 0)
    }

    fun encode(data: ByteArray, offset: Int): ByteArray {
        return encode(data, offset, data.size - offset)
    }

    fun encode(data: ByteArray, offset: Int, length: Int): ByteArray {
        val outputStream = ByteArrayOutputStream()
        encode(data, offset, length, outputStream)
        return outputStream.toByteArray()
    }

    fun encode(data: ByteArray, output: OutputStream): Int {
        return encode(data, 0, output)
    }

    fun encode(data: ByteArray, offset: Int, output: OutputStream): Int {
        return encode(data, offset, data.size - offset, output)
    }

    fun encode(data: ByteArray, offset: Int, length: Int, output: OutputStream): Int

    fun encode(data: InputStream, output: OutputStream): Int

    fun encode(data: CharSequence): ByteArray {
        val outputStream = ByteArrayOutputStream()
        encode(data.toBytes(), outputStream)
        return outputStream.toByteArray()
    }

    fun encode(data: CharSequence, output: OutputStream): Int

    fun encode(data: Reader, output: OutputStream): Int

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

    //decode:

    fun decode(data: ByteArray): ByteArray {
        return decode(data, 0)
    }

    fun decode(data: ByteArray, offset: Int): ByteArray {
        return decode(data, offset, data.size - offset)
    }

    fun decode(data: ByteArray, offset: Int, length: Int): ByteArray {
        val outputStream = ByteArrayOutputStream()
        decode(data, offset, length, outputStream)
        return outputStream.toByteArray()
    }

    fun decode(data: ByteArray, output: OutputStream): Int {
        return decode(data, 0, output)
    }

    fun decode(data: ByteArray, offset: Int, output: OutputStream): Int {
        return decode(data, offset, data.size - offset, output)
    }

    fun decode(data: ByteArray, offset: Int, length: Int, output: OutputStream): Int

    fun decode(data: InputStream, output: OutputStream): Int

    fun decode(data: CharSequence): ByteArray {
        val outputStream = ByteArrayOutputStream()
        decode(data.toBytes(), outputStream)
        return outputStream.toByteArray()
    }

    fun decode(data: CharSequence, output: OutputStream): Int

    fun decode(data: Reader, output: OutputStream): Int

    fun decodeToString(data: ByteArray): String {
        return decode(data).toChars()
    }

    fun decodeToString(data: ByteArray, offset: Int): String {
        return decode(data, offset).toChars()
    }

    fun decodeToString(data: ByteArray, offset: Int, length: Int): String {
        return decode(data, offset, length).toChars()
    }

    fun decodeToString(data: CharSequence): String {
        return decode(data).toChars()
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
        fun withAlgorithm(algorithm: CharSequence): xyz.srclab.common.codec.Encoder {
            return withAlgorithm(algorithm.toCodecAlgorithm())
        }

        @JvmStatic
        fun withAlgorithm(algorithm: CodecAlgorithm): xyz.srclab.common.codec.Encoder {
            return when (algorithm.name) {
                CodecAlgorithm.PLAIN_NAME -> PlainCodec
                CodecAlgorithm.HEX_NAME -> HexCodec
                CodecAlgorithm.BASE64_NAME -> Base64Codec
                else -> throw IllegalArgumentException("Unknown algorithm: ${algorithm.name}")
            }
        }

        private class BcprovEncoder(
            private val encoder: org.bouncycastle.util.encoders.Encoder,
            override val algorithm: String
        ) : Encoder {

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
object PlainCodec : xyz.srclab.common.codec.Encoder {

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

