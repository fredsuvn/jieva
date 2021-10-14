package xyz.srclab.common.codec

import org.bouncycastle.util.encoders.Base64Encoder
import org.bouncycastle.util.encoders.HexEncoder
import xyz.srclab.common.base.toBytes
import xyz.srclab.common.base.toChars
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.io.Reader

/**
 * To encode/decoder such as Hex, Base64 and Plain.
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

    fun encode(data: InputStream, output: OutputStream): Int {
        return encode(data.readBytes(), output)
    }

    fun encode(data: CharSequence): ByteArray {
        val outputStream = ByteArrayOutputStream()
        encode(data.toBytes(), outputStream)
        return outputStream.toByteArray()
    }

    fun encode(data: CharSequence, output: OutputStream): Int

    fun encode(data: Reader, output: OutputStream): Int {
        return encode(data.readText(), output)
    }

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

    fun decode(data: InputStream, output: OutputStream): Int {
        return decode(data.readBytes(), output)
    }

    fun decode(data: CharSequence): ByteArray {
        val outputStream = ByteArrayOutputStream()
        decode(data.toBytes(), outputStream)
        return outputStream.toByteArray()
    }

    fun decode(data: CharSequence, output: OutputStream): Int

    fun decode(data: Reader, output: OutputStream): Int {
        return decode(data.readText(), output)
    }

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

        @JvmField
        val PLAIN = PlainEncoder

        @JvmField
        val HEX = BcprovEncoder(
            HexEncoder(),
            CodecAlgorithm.HEX_NAME
        )

        @JvmField
        val BASE64 = BcprovEncoder(
            Base64Encoder(),
            CodecAlgorithm.BASE64_NAME
        )

        class BcprovEncoder(
            private val encoder: org.bouncycastle.util.encoders.Encoder,
            override val algorithm: String
        ) : Encoder {

            override fun encode(data: ByteArray, offset: Int, length: Int, output: OutputStream): Int {
                return encoder.encode(data, offset, length, output)
            }

            override fun encode(data: CharSequence, output: OutputStream): Int {
                return encode(data.toBytes(), output)
            }

            override fun decode(data: ByteArray, offset: Int, length: Int, output: OutputStream): Int {
                return encoder.decode(data, offset, length, output)
            }

            override fun decode(data: CharSequence, output: OutputStream): Int {
                return encoder.decode(data.toString(), output)
            }
        }

        object PlainEncoder : Encoder {

            override val algorithm: String = CodecAlgorithm.PLAIN_NAME

            override fun encode(data: ByteArray, offset: Int, length: Int): ByteArray {
                return data.copyOfRange(offset, offset + length)
            }

            override fun encode(data: CharSequence): ByteArray {
                return data.toBytes()
            }

            override fun encodeToString(data: CharSequence): String {
                return data.toString()
            }

            override fun decode(data: ByteArray, offset: Int, length: Int): ByteArray {
                return encode(data, offset, length)
            }

            override fun decode(data: CharSequence): ByteArray {
                return encode(data)
            }

            override fun decodeToString(data: CharSequence): String {
                return encodeToString(data)
            }

            override fun encode(data: ByteArray, offset: Int, length: Int, output: OutputStream): Int {
                output.write(data, offset, length)
                return length
            }

            override fun encode(data: InputStream, output: OutputStream): Int {
                return data.copyTo(output).toInt()
            }

            override fun encode(data: CharSequence, output: OutputStream): Int {
                return encode(data.toBytes(), output)
            }

            override fun decode(data: ByteArray, offset: Int, length: Int, output: OutputStream): Int {
                return encode(data, offset, length, output)
            }

            override fun decode(data: CharSequence, output: OutputStream): Int {
                return encode(data, output)
            }
        }
    }
}