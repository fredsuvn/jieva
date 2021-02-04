package xyz.srclab.common.codec

import org.apache.commons.codec.binary.Base64
import org.apache.commons.codec.binary.Hex
import xyz.srclab.common.base.toBytes
import xyz.srclab.common.base.toChars
import xyz.srclab.common.codec.rsa.RsaCipher
import xyz.srclab.common.codec.sm2.Sm2Cipher
import java.math.BigInteger
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import javax.crypto.SecretKey

/**
 * Codec core class, provides static method:
 * ```
 * byte[] base64 = Codec.encodeBase64(data);
 * byte[] result = Codec.encodeAes(key, base64);
 * ```
 *
 * And chaining-call:
 * ```
 * byte[] result = Codec
 * .forData(data)
 * .encodeBase64
 * .encodeAes(key)
 * .doFinal();
 * ```
 *
 * @author sunqian
 * @see CodecAlgorithm
 */
interface Codec {

    fun encodeHex(): Codec

    fun decodeHex(): Codec

    fun encodeBase64(): Codec

    fun decodeBase64(): Codec

    fun encryptAes(secretKey: SecretKey): Codec {
        return encrypt(secretKey, CodecAlgorithm.AES!!)
    }

    fun encryptAes(secretKey: ByteArray): Codec {
        return encrypt(secretKey, CodecAlgorithm.AES!!)
    }

    fun decryptAes(secretKey: SecretKey): Codec {
        return decrypt(secretKey, CodecAlgorithm.AES!!)
    }

    fun decryptAes(secretKey: ByteArray): Codec {
        return decrypt(secretKey, CodecAlgorithm.AES!!)
    }

    fun encryptRsa(publicKey: SecretKey): Codec {
        return encrypt(publicKey, CodecAlgorithm.RSA!!)
    }

    fun encryptRsa(publicKey: ByteArray): Codec {
        return encrypt(publicKey, CodecAlgorithm.RSA!!)
    }

    fun encryptRsa(publicKey: RSAPublicKey): Codec {
        return encrypt(publicKey, CodecAlgorithm.RSA)
    }

    fun decryptRsa(privateKey: SecretKey): Codec {
        return decrypt(privateKey, CodecAlgorithm.RSA!!)
    }

    fun decryptRsa(privateKey: ByteArray): Codec {
        return decrypt(privateKey, CodecAlgorithm.RSA!!)
    }

    fun decryptRsa(privateKey: RSAPrivateKey): Codec {
        return decrypt(privateKey, CodecAlgorithm.RSA)
    }

    fun encryptSm2(publicKey: SecretKey): Codec {
        return encrypt(publicKey, CodecAlgorithm.SM2!!)
    }

    fun encryptSm2(publicKey: ByteArray): Codec {
        return encrypt(publicKey, CodecAlgorithm.SM2!!)
    }

    fun encryptSm2(publicKey: ECPoint): Codec {
        return encrypt(publicKey, CodecAlgorithm.SM2!!)
    }

    fun decryptSm2(privateKey: SecretKey): Codec {
        return decrypt(privateKey, CodecAlgorithm.SM2!!)
    }

    fun decryptSm2(privateKey: ByteArray): Codec {
        return decrypt(privateKey, CodecAlgorithm.SM2!!)
    }

    fun decryptSm2(privateKey: BigInteger): Codec {
        return decrypt(privateKey, CodecAlgorithm.SM2)
    }

    fun digestMd5(): Codec {
        return digest(CodecAlgorithm.MD5)
    }

    fun hmacDigestSha1(secretKey: SecretKey): Codec {
        return hmacDigest(secretKey, CodecAlgorithm.HMAC_SHA1)
    }

    fun hmacDigestSha1(secretKey: ByteArray): Codec {
        return hmacDigest(secretKey, CodecAlgorithm.HMAC_SHA1)
    }

    fun encode(algorithm: String): Codec {
        return encode(CodecAlgorithm.forName(algorithm))
    }

    fun encode(algorithm: CodecAlgorithm): Codec
    fun decode(algorithm: String): Codec {
        return decode(CodecAlgorithm.forName(algorithm))
    }

    fun decode(algorithm: CodecAlgorithm): Codec
    fun encrypt(secretKey: SecretKey, algorithm: String): Codec {
        return encrypt(secretKey, CodecAlgorithm.forName(algorithm))
    }

    fun encrypt(secretKey: SecretKey, algorithm: CodecAlgorithm): Codec
    fun encrypt(secretKey: ByteArray, algorithm: String): Codec {
        return encrypt(secretKey, CodecAlgorithm.forName(algorithm))
    }

    fun encrypt(secretKey: ByteArray, algorithm: CodecAlgorithm): Codec
    fun encrypt(secretKey: Any, algorithm: String): Codec {
        return encrypt(secretKey, CodecAlgorithm.forName(algorithm))
    }

    fun encrypt(secretKey: Any, algorithm: CodecAlgorithm): Codec
    fun decrypt(secretKey: SecretKey, algorithm: String): Codec {
        return decrypt(secretKey, CodecAlgorithm.forName(algorithm))
    }

    fun decrypt(secretKey: SecretKey, algorithm: CodecAlgorithm): Codec
    fun decrypt(secretKey: ByteArray, algorithm: String): Codec {
        return decrypt(secretKey, CodecAlgorithm.forName(algorithm))
    }

    fun decrypt(secretKey: ByteArray, algorithm: CodecAlgorithm): Codec
    fun decrypt(secretKey: Any, algorithm: String): Codec {
        return decrypt(secretKey, CodecAlgorithm.forName(algorithm))
    }

    fun decrypt(secretKey: Any, algorithm: CodecAlgorithm): Codec
    fun digest(algorithm: String): Codec {
        return digest(CodecAlgorithm.forName(algorithm))
    }

    fun digest(algorithm: CodecAlgorithm): Codec
    fun hmacDigest(secretKey: SecretKey, algorithm: String): Codec {
        return hmacDigest(secretKey, CodecAlgorithm.forName(algorithm))
    }

    fun hmacDigest(secretKey: SecretKey, algorithm: CodecAlgorithm): Codec
    fun hmacDigest(secretKey: ByteArray, algorithm: String): Codec {
        return hmacDigest(secretKey, CodecAlgorithm.forName(algorithm))
    }

    fun hmacDigest(secretKey: ByteArray, algorithm: CodecAlgorithm): Codec
    fun hmacDigest(secretKey: Any, algorithm: String): Codec {
        return hmacDigest(secretKey, CodecAlgorithm.forName(algorithm))
    }

    fun hmacDigest(secretKey: Any, algorithm: CodecAlgorithm): Codec
    fun doFinal(): ByteArray
    fun doFinalString(): String {
        return CodecBytes.toString(doFinal())
    }

    fun doFinalHexString(): String {
        return encodeHexString(doFinal())
    }

    fun doFinalBase64String(): String {
        return encodeBase64String(doFinal())
    }

    companion object {

        @JvmStatic
        fun forData(data: ByteArray): Codec {
            return CodecImpl(data)
        }

        @JvmStatic
        fun forData(data: String): Codec {
            return CodecImpl(CodecBytes.toBytes(data))
        }

        @JvmStatic
        fun secretKey(key: ByteArray, algorithm: String): SecretKey {
            return key.toSecretKey(algorithm)
        }

        @JvmStatic
        fun secretKey(key: String, algorithm: String): SecretKey {
            return secretKey(key.toBytes(), algorithm)
        }

        @JvmStatic
        fun secretKey(key: ByteArray, algorithm: CodecAlgorithm): SecretKey {
            return key.toSecretKey(algorithm.name())
        }

        @JvmStatic
        fun secretKey(key: String, algorithm: CodecAlgorithm): SecretKey {
            return secretKey(key.toBytes(), algorithm)
        }

        @JvmStatic
        fun ByteArray.encodeHex(): ByteArray {
            val chars = Hex.encodeHex(this)
            return chars.toBytes()
        }

        @JvmStatic
        fun String.encodeHex(): ByteArray {
            val chars = Hex.encodeHex(this.toBytes())
            return chars.toBytes()
        }

        @JvmStatic
        fun ByteArray.encodeHexString(): String {
            return encodeHex().toChars()
        }

        @JvmStatic
        fun String.encodeHexString(): String {
            return encodeHex().toChars()
        }

        @JvmStatic
        fun ByteArray.decodeHex(): ByteArray {
            return try {
                Hex.decodeHex(this.toChars())
            } catch (e: Exception) {
                throw e
            }
        }

        @JvmStatic
        fun String.decodeHex(): ByteArray {
            return try {
                Hex.decodeHex(this)
            } catch (e: Exception) {
                throw e
            }
        }

        @JvmStatic
        fun ByteArray.decodeHexString(): String {
            return decodeHex().toChars()
        }

        @JvmStatic
        fun String.decodeHexString(): String {
            return decodeHex().toChars()
        }

        @JvmStatic
        fun ByteArray.encodeBase64(): ByteArray {
            return Base64.encodeBase64(this)
        }

        @JvmStatic
        fun String.encodeBase64(): ByteArray {
            return Base64.encodeBase64(this.toBytes())
        }

        @JvmStatic
        fun ByteArray.encodeBase64String(): String {
            return encodeBase64().toChars()
        }

        @JvmStatic
        fun String.encodeBase64String(): String {
            return encodeBase64().toChars()
        }

        @JvmStatic
        fun ByteArray.decodeBase64(): ByteArray {
            return Base64.decodeBase64(this)
        }

        @JvmStatic
        fun String.decodeBase64(): ByteArray {
            return Base64.decodeBase64(this)
        }

        @JvmStatic
        fun ByteArray.decodeBase64String(): String {
            return decodeBase64().toChars()
        }

        @JvmStatic
        fun String.decodeBase64String(): String {
            return decodeBase64().toChars()
        }

        @JvmStatic
        fun encryptAes(secretKey: SecretKey, data: ByteArray): ByteArray {
            return symmetricCipher(CodecAlgorithm.AES).encrypt(secretKey, data)
        }

        @JvmStatic
        fun encryptAes(secretKey: ByteArray, data: ByteArray): ByteArray {
            return symmetricCipher(CodecAlgorithm.AES).encrypt(secretKey, data)
        }

        @JvmStatic
        fun decryptAes(secretKey: SecretKey, encrypted: ByteArray): ByteArray {
            return symmetricCipher(CodecAlgorithm.AES).decrypt(secretKey, encrypted)
        }

        @JvmStatic
        fun decryptAes(secretKey: ByteArray, encrypted: ByteArray): ByteArray {
            return symmetricCipher(CodecAlgorithm.AES).decrypt(secretKey, encrypted)
        }

        @JvmStatic
        fun md5(data: ByteArray): ByteArray {
            return digestCipher(CodecAlgorithm.MD5).digest(data)
        }

        @JvmStatic
        fun hmacSha1(secretKey: SecretKey, data: ByteArray): ByteArray {
            return hmacDigestCipher(CodecAlgorithm.HMAC_SHA1).digest(secretKey, data)
        }

        @JvmStatic
        fun hmacSha1(secretKey: ByteArray, data: ByteArray): ByteArray {
            return hmacDigestCipher(CodecAlgorithm.HMAC_SHA1).digest(secretKey, data)
        }

        @JvmStatic
        fun aesCipher(): RsaCipher {
            return RsaCipher()
        }

        @JvmStatic
        fun rsaCipher(): RsaCipher {
            return RsaCipher()
        }

        @JvmStatic
        fun sm2Cipher(): Sm2Cipher {
            return Sm2Cipher()
        }

        @JvmStatic
        fun symmetricCipher(algorithm: String): SymmetricCipher<SecretKey> {
            return symmetricCipher(CodecAlgorithm.forName(algorithm, CodecAlgorithmType.SYMMETRIC))
        }

        @JvmStatic
        fun symmetricCipher(algorithm: CodecAlgorithm): SymmetricCipher<SecretKey> {
            return SymmetricCipher.forAlgorithm(algorithm)
        }

        @JvmStatic
        fun digestCipher(algorithm: String): DigestCipher {
            return digestCipher(CodecAlgorithm.forName(algorithm,CodecAlgorithmType.DIGEST))
        }

        @JvmStatic
        fun digestCipher(algorithm: CodecAlgorithm): DigestCipher {
            return DigestCipher.forAlgorithm(algorithm)
        }

        @JvmStatic
        fun hmacDigestCipher(algorithm: String): HmacDigestCipher<SecretKey> {
            return hmacDigestCipher(CodecAlgorithm.forName(algorithm,CodecAlgorithmType.HMAC))
        }

        @JvmStatic
        fun hmacDigestCipher(algorithm: CodecAlgorithm): HmacDigestCipher<SecretKey> {
            return HmacDigestCipher.forAlgorithm(algorithm)
        }
    }
}