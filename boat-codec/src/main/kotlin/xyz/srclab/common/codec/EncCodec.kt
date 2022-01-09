package xyz.srclab.common.codec

import org.bouncycastle.util.Strings
import org.bouncycastle.util.encoders.*
import xyz.srclab.common.base.*
import java.io.InputStream
import java.io.OutputStream

/**
 * To encode/decoder such as Hex, Base64 and Plain.
 */
interface EncCodec : Codec {

    //encode:

    fun getEncodedLength(sourceLength:Int):Int

    fun encode(source: ByteArray): ByteArray {
        return encode(source, 0, source.size)
    }

    fun encode(source: ByteArray, offset: Int): ByteArray {
        return encode(source, offset, remainingLength(source.size, offset))
    }

    fun encode(source: ByteArray, offset: Int, length: Int): ByteArray

    fun encode(source: InputStream): ByteArray

    fun encodeTo(source: ByteArray, dest: ByteArray): Int

    fun encodeTo(source: ByteArray, dest: BytesRef): Int

    fun encodeTo(source: ByteArray, dest: OutputStream): Int

    fun encodeTo(source: BytesRef, dest: ByteArray): Int

    fun encodeTo(source: BytesRef, dest: BytesRef): Int

    fun encodeTo(source: BytesRef, dest: OutputStream): Int

    fun encodeTo(source: InputStream, dest: ByteArray): Int

    fun encodeTo(source: InputStream, dest: BytesRef): Int

    fun encodeTo(source: InputStream, dest: OutputStream): Int

    //decode:

    fun getMaxDecodedLength(encodedLength:Int):Int

    fun decode(encoded: ByteArray): ByteArray {
        return decode(encoded, 0, encoded.size)
    }

    fun decode(encoded: ByteArray, offset: Int): ByteArray {
        return decode(encoded, offset, remainingLength(encoded.size, offset))
    }

    fun decode(encoded: ByteArray, offset: Int, length: Int): ByteArray

    fun decode(encoded: InputStream): ByteArray

    fun decodeTo(encoded: ByteArray, dest: ByteArray): Int

    fun decodeTo(encoded: ByteArray, dest: BytesRef): Int

    fun decodeTo(encoded: ByteArray, dest: OutputStream): Int

    fun decodeTo(encoded: BytesRef, dest: ByteArray): Int

    fun decodeTo(encoded: BytesRef, dest: BytesRef): Int

    fun decodeTo(encoded: BytesRef, dest: OutputStream): Int

    fun decodeTo(encoded: InputStream, dest: ByteArray): Int

    fun decodeTo(encoded: InputStream, dest: BytesRef): Int

    fun decodeTo(encoded: InputStream, dest: OutputStream): Int

    //utils

    fun encodeToString(source: CharSequence): String {

    }

    fun encodeToString(source: InputStream): String

    fun decodeToString(encoded: CharSequence): String

    fun decodeToString(encoded: InputStream): String

    fun bytesToString(bytes:ByteArray):String {
        return Strings.fromByteArray(bytes)
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
            private val encoder: Encoder,
            override val algorithm: String
        ) : EncCodec {

            override fun getEncodedLength(sourceLength: Int): Int {
                TODO("Not yet implemented")
            }

            override fun encode(source: ByteArray, offset: Int, length: Int): ByteArray {
                encoder.getMaxDecodedLength()
                Hex
                Base64
                val l = encoder.getEncodedLength(length)
            }

            override fun encode(source: InputStream): ByteArray {
                TODO("Not yet implemented")
            }

            override fun encode(source: CharSequence): String {
                TODO("Not yet implemented")
            }

            override fun encodeTo(source: ByteArray, offset: Int, length: Int, outputStream: OutputStream): Int {
                TODO("Not yet implemented")
            }

            override fun encodeTo(source: InputStream, outputStream: OutputStream): Int {
                TODO("Not yet implemented")
            }

            override fun decode(encoded: ByteArray, offset: Int, length: Int): ByteArray {
                TODO("Not yet implemented")
            }

            override fun decode(encoded: InputStream): ByteArray {
                TODO("Not yet implemented")
            }

            override fun decode(encoded: CharSequence): String {
                TODO("Not yet implemented")
            }

            override fun decodeTo(encoded: ByteArray, offset: Int, length: Int, outputStream: OutputStream): Int {
                TODO("Not yet implemented")
            }

            override fun decodeTo(encoded: InputStream, outputStream: OutputStream): Int {
                TODO("Not yet implemented")
            }
        }

        object PlainEncoder : EncCodec {

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