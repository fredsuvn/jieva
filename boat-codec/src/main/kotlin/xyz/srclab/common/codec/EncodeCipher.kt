package xyz.srclab.common.codec

import org.apache.commons.codec.binary.Base64
import org.apache.commons.codec.binary.Hex
import xyz.srclab.common.base.toBytes
import xyz.srclab.common.base.toChars
import xyz.srclab.common.codec.Codec.Companion.encodeBase64String
import xyz.srclab.common.codec.Codec.Companion.encodeHexString
import xyz.srclab.common.codec.CodecAlgorithm.Companion.toCodecAlgorithm

/**
 * @see HexEncodeCipher
 * @see Base64EncodeCipher
 */
interface EncodeCipher : CodecCipher {

    fun encode(data: ByteArray): ByteArray

    fun encode(data: CharSequence): ByteArray

    @JvmDefault
    fun encodeToString(data: ByteArray): String {
        return encode(data).toChars()
    }

    @JvmDefault
    fun encodeToString(data: CharSequence): String {
        return encode(data).toChars()
    }

    @JvmDefault
    fun encodeToHexString(data: ByteArray): String {
        return encode(data).encodeHexString()
    }

    @JvmDefault
    fun encodeToHexString(data: CharSequence): String {
        return encode(data).encodeHexString()
    }

    @JvmDefault
    fun encodeToBase64String(data: ByteArray): String {
        return encode(data).encodeBase64String()
    }

    @JvmDefault
    fun encodeToBase64String(data: CharSequence): String {
        return encode(data).encodeBase64String()
    }

    fun decode(encoded: ByteArray): ByteArray

    fun decode(encoded: CharSequence): ByteArray

    @JvmDefault
    fun decodeToString(encoded: ByteArray): String {
        return decode(encoded).toChars()
    }

    @JvmDefault
    fun decodeToString(encoded: CharSequence): String {
        return decode(encoded).toChars()
    }

    @JvmDefault
    fun decodeToHexString(encoded: ByteArray): String {
        return decode(encoded).encodeHexString()
    }

    @JvmDefault
    fun decodeToHexString(encoded: CharSequence): String {
        return decode(encoded).encodeHexString()
    }

    @JvmDefault
    fun decodeToBase64String(encoded: ByteArray): String {
        return decode(encoded).encodeBase64String()
    }

    @JvmDefault
    fun decodeToBase64String(encoded: CharSequence): String {
        return decode(encoded).encodeBase64String()
    }

    companion object {

        @JvmName("withAlgorithm")
        @JvmStatic
        fun CharSequence.toEncodeCipher(): EncodeCipher {
            return this.toCodecAlgorithm().toEncodeCipher()
        }

        @JvmName("withAlgorithm")
        @JvmStatic
        fun CodecAlgorithm.toEncodeCipher(): EncodeCipher {
            return when (this.name) {
                CodecAlgorithm.HEX_NAME -> HexEncodeCipher
                CodecAlgorithm.BASE64_NAME -> Base64EncodeCipher
                else -> throw IllegalArgumentException("Unknown algorithm: ${this.name}")
            }
        }
    }
}

object HexEncodeCipher : EncodeCipher {

    override val name = CodecAlgorithm.HEX_NAME

    override fun encode(data: ByteArray): ByteArray {
        return Hex.encodeHex(data).toBytes()
    }

    override fun encode(data: CharSequence): ByteArray {
        return encode(data.toBytes())
    }

    override fun encodeToString(data: ByteArray): String {
        return Hex.encodeHex(data).toChars()
    }

    override fun encodeToString(data: CharSequence): String {
        return encodeToString(data.toBytes())
    }

    override fun decode(encoded: ByteArray): ByteArray {
        return Hex.decodeHex(encoded.toChars())
    }

    override fun decode(encoded: CharSequence): ByteArray {
        return Hex.decodeHex(encoded.toString())
    }
}

object Base64EncodeCipher : EncodeCipher {

    override val name = CodecAlgorithm.HEX_NAME

    override fun encode(data: ByteArray): ByteArray {
        return Base64.encodeBase64(data)
    }

    override fun encode(data: CharSequence): ByteArray {
        return Base64.encodeBase64(data.toBytes())
    }

    override fun decode(encoded: ByteArray): ByteArray {
        return Base64.decodeBase64(encoded)
    }

    override fun decode(encoded: CharSequence): ByteArray {
        return Base64.decodeBase64(encoded.toString())
    }
}