package xyz.srclab.common.codec

import xyz.srclab.common.codec.Codec.Companion.toCodecKey
import xyz.srclab.common.codec.rsa.RsaCodec
import xyz.srclab.common.codec.sm2.Sm2Codec
import xyz.srclab.common.codec.sm2.Sm2Params
import xyz.srclab.common.lang.toBytes
import xyz.srclab.common.lang.toChars
import java.io.OutputStream
import javax.crypto.Cipher

/**
 * Reversible cipher codec which support `encrypt` and `decrypt` such as `AES`.
 *
 * @author sunqian
 *
 * @see RsaCodec
 * @see Sm2Codec
 * @see AsymmetricCryptCodec
 */
interface CryptCodec : Codec {

    @JvmDefault
    fun encrypt(key: Any, data: ByteArray): ByteArray {
        return encrypt(key, data, 0)
    }

    @JvmDefault
    fun encrypt(key: Any, data: ByteArray, offset: Int): ByteArray {
        return encrypt(key, data, offset, data.size - offset)
    }

    fun encrypt(key: Any, data: ByteArray, offset: Int, length: Int): ByteArray

    @JvmDefault
    fun encrypt(key: Any, data: ByteArray, output: OutputStream): Int {
        return encrypt(key, data, 0, output)
    }

    @JvmDefault
    fun encrypt(key: Any, data: ByteArray, offset: Int, output: OutputStream): Int {
        return encrypt(key, data, offset, data.size - offset, output)
    }

    @JvmDefault
    fun encrypt(key: Any, data: ByteArray, offset: Int, length: Int, output: OutputStream): Int {
        val bytes = encrypt(key, data, offset, length)
        output.write(bytes)
        return bytes.size
    }

    @JvmDefault
    fun encrypt(key: Any, data: CharSequence): ByteArray {
        return encrypt(key, data.toBytes())
    }

    @JvmDefault
    fun encrypt(key: Any, data: CharSequence, output: OutputStream): Int {
        return encrypt(key, data.toBytes(), output)
    }

    @JvmDefault
    fun encryptToString(key: Any, data: ByteArray): String {
        return encrypt(key, data).toChars()
    }

    @JvmDefault
    fun encryptToString(key: Any, data: ByteArray, offset: Int): String {
        return encrypt(key, data, offset).toChars()
    }

    @JvmDefault
    fun encryptToString(key: Any, data: ByteArray, offset: Int, length: Int): String {
        return encrypt(key, data, offset, length).toChars()
    }

    @JvmDefault
    fun encryptToString(key: Any, data: CharSequence): String {
        return encrypt(key, data).toChars()
    }

    @JvmDefault
    fun decrypt(key: Any, data: ByteArray): ByteArray {
        return decrypt(key, data, 0)
    }

    @JvmDefault
    fun decrypt(key: Any, data: ByteArray, offset: Int): ByteArray {
        return decrypt(key, data, offset, data.size - offset)
    }

    fun decrypt(key: Any, data: ByteArray, offset: Int, length: Int): ByteArray

    @JvmDefault
    fun decrypt(key: Any, data: ByteArray, output: OutputStream): Int {
        return decrypt(key, data, 0, output)
    }

    @JvmDefault
    fun decrypt(key: Any, data: ByteArray, offset: Int, output: OutputStream): Int {
        return decrypt(key, data, offset, data.size - offset, output)
    }

    @JvmDefault
    fun decrypt(key: Any, data: ByteArray, offset: Int, length: Int, output: OutputStream): Int {
        val bytes = decrypt(key, data, offset, length)
        output.write(bytes)
        return bytes.size
    }

    @JvmDefault
    fun decrypt(key: Any, data: CharSequence): ByteArray {
        return decrypt(key, data.toBytes())
    }

    @JvmDefault
    fun decrypt(key: Any, data: CharSequence, output: OutputStream): Int {
        return decrypt(key, data.toBytes(), output)
    }

    @JvmDefault
    fun decryptToString(key: Any, data: ByteArray): String {
        return decrypt(key, data).toChars()
    }

    @JvmDefault
    fun decryptToString(key: Any, data: ByteArray, offset: Int): String {
        return decrypt(key, data, offset).toChars()
    }

    @JvmDefault
    fun decryptToString(key: Any, data: ByteArray, offset: Int, length: Int): String {
        return decrypt(key, data, offset, length).toChars()
    }

    @JvmDefault
    fun decryptToString(key: Any, data: CharSequence): String {
        return decrypt(key, data).toChars()
    }

    companion object {

        @JvmStatic
        fun aes(): CryptCodec {
            return withAlgorithm(CodecAlgorithm.AES_NAME)
        }

        @JvmStatic
        fun rsa(): RsaCodec {
            return RsaCodec()
        }

        @JvmStatic
        fun rsa(
            encryptBlockSize: Int,
            decryptBlockSize: Int
        ): RsaCodec {
            return RsaCodec(encryptBlockSize, decryptBlockSize)
        }

        @JvmStatic
        fun sm2(sm2Params: Sm2Params): Sm2Codec {
            return Sm2Codec(sm2Params)
        }

        @JvmStatic
        fun withAlgorithm(
            algorithm: CharSequence
        ): CryptCodec {
            return CryptCodecImpl(algorithm.toString())
        }

        private class CryptCodecImpl(
            override val algorithm: String
        ) : CryptCodec {

            private val cipher: Cipher by lazy {
                Cipher.getInstance(algorithm)
            }

            override fun encrypt(key: Any, data: ByteArray, offset: Int, length: Int): ByteArray {
                cipher.init(Cipher.ENCRYPT_MODE, key.toCodecKey(algorithm))
                //cipher.update(data, offset, length)
                return cipher.doFinal(data, offset, length)
            }

            //override fun encrypt(key: Any, data: ByteArray, offset: Int, length: Int, output: OutputStream): Int {
            //    cipher.init(Cipher.ENCRYPT_MODE, key.toCodecKey(algorithm))
            //    //cipher.update(data, offset, length)
            //     cipher.doFinal(data, offset, length)
            //}

            override fun decrypt(key: Any, data: ByteArray, offset: Int, length: Int): ByteArray {
                cipher.init(Cipher.DECRYPT_MODE, key.toCodecKey(algorithm))
                //cipher.update(data, offset, length)
                return cipher.doFinal(data, offset, length)
            }
        }
    }
}