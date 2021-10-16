package xyz.srclab.common.codec

import org.bouncycastle.util.encoders.Base64Encoder
import org.bouncycastle.util.encoders.HexEncoder
import xyz.srclab.common.base.ByteArrayRef
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
        val outputStream = ByteArrayOutputStream()
        encode(data, outputStream)
        return outputStream.toByteArray()
    }

    fun encode(data: ByteArrayRef): ByteArray {
        val outputStream = ByteArrayOutputStream()
        encode(data, outputStream)
        return outputStream.toByteArray()
    }

    fun encode(data: InputStream): ByteArray {
        return encode(data.readBytes())
    }

    fun encode(data:CharSequence):ByteArray {
        return encode(data.toBytes())
    }

    fun encode(data:Reader):ByteArray {
        return encode(data.readText())
    }

    fun encode(date: ByteArray, output: ByteArray):Int {
        return encode(date,0,date.size,output,0)
    }

    fun encode(date: ByteArrayRef, output: ByteArray):Int {
        return encode(date.array,date.offset,date.length,output,0)
    }

    fun encode(date: InputStream, output: ByteArray):Int {
        return encode(date.readBytes(),output)
    }

    fun encode(date: CharSequence, output: ByteArray):Int {
        return encode(date.toBytes(),output)
    }

    fun encode(date: Reader, output: ByteArray):Int {
        return encode(date.readText(),output)
    }

    fun encode(date: ByteArrayRef, output: ByteArrayRef):Int {
        return encode(date.array,date.offset,date.length,output.array,output.offset,output.length)
    }

    fun encode(date: ByteArrayRef, output: ByteArrayRef):Int {
        return encode(date.array,date.offset,date.length,output.array,output.offset,output.length)
    }

    fun encode(date: ByteArrayRef, output: ByteArrayRef):Int {
        return encode(date.array,date.offset,date.length,output.array,output.offset,output.length)
    }

    fun encode(date: ByteArrayRef, output: ByteArrayRef):Int {
        return encode(date.array,date.offset,date.length,output.array,output.offset,output.length)
    }

    fun encode(date: ByteArrayRef, output: ByteArrayRef):Int {
        return encode(date.array,date.offset,date.length,output.array,output.offset,output.length)
    }

    fun encode(data: ByteArray, output: OutputStream): Int {
        return encode(data,0,data.size,output)
    }

    fun encode(data: ByteArrayRef, output: OutputStream): Int{
        return encode(data.array,data.offset,data.length,output)
    }

    fun encode(
        data: ByteArray, dataOffset: Int, dataLength: Int,
        output: ByteArray, outputOffset: Int
    ):Int

    fun encode(
        data: ByteArray, dataOffset: Int, dataLength: Int,
        output: OutputStream
    ):Int

    fun encode(data: InputStream, output: OutputStream):Int



    fun encodeToString(data: ByteArray): String {
        return encode(data).toChars()
    }

    fun encodeToString(data: ByteArrayRef): String {
        return encode(data).toChars()
    }

    fun encodeToString(data: CharSequence): String {
        return encode(data).toChars()
    }

    //decode:

    fun decode(data: ByteArray): ByteArray {
        return decode(ByteArrayRef.of(data))
    }

    fun decode(data: ByteArrayRef): ByteArray {
        val outputStream = ByteArrayOutputStream()
        decode(data, outputStream)
        return outputStream.toByteArray()
    }

    fun decode(data: ByteArray, output: OutputStream): Int {
        return decode(ByteArrayRef.of(data), output)
    }

    fun decode(data: ByteArrayRef, output: OutputStream): Int

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

    fun decodeToString(data: ByteArrayRef): String {
        return decode(data).toChars()
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

            override fun encode(data: ByteArrayRef, output: OutputStream): Int {
                return encoder.encode(data, offset, length, output)
            }

            override fun encode(data: CharSequence, output: OutputStream): Int {
                return encode(data.toBytes(), output)
            }

            override fun decode(data: ByteArrayRef, output: OutputStream): Int {
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