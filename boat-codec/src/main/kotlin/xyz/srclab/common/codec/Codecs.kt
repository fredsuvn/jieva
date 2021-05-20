package xyz.srclab.common.codec

import org.bouncycastle.math.ec.ECPoint
import xyz.srclab.common.codec.CodecAlgorithm.Companion.toCodecAlgorithm
import xyz.srclab.common.codec.DigestCodec.Companion.toDigestCipher
import xyz.srclab.common.codec.EncodeCodec.Companion.toEncodeCipher
import xyz.srclab.common.codec.MacDigestCipher.Companion.toMacDigestCipher
import xyz.srclab.common.codec.SymmetricCipher.Companion.toSecretKeySymmetricCipher
import xyz.srclab.common.codec.rsa.RsaCipher
import xyz.srclab.common.codec.sm2.Sm2Cipher
import xyz.srclab.common.lang.asAny
import xyz.srclab.common.lang.toBytes
import xyz.srclab.common.lang.toChars
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
 *
 * @see CodecKeys
 * @see CommonCodec
 * @see CodecAlgorithm
 * @see ReversibleCipher
 * @see SymmetricCipher
 * @see AsymmetricCipher
 * @see DigestCodec
 * @see MacDigestCipher
 */
interface Coding {

    @JvmDefault
    fun encode(algorithm: String): Coding {
        return encode(algorithm.toCodecAlgorithm(CodecAlgorithmType.ENCODE))
    }

    fun encode(algorithm: CodecAlgorithm): Coding

    @JvmDefault
    fun decode(algorithm: String): Coding {
        return decode(algorithm.toCodecAlgorithm(CodecAlgorithmType.ENCODE))
    }

    fun decode(algorithm: CodecAlgorithm): Coding

    @JvmDefault
    fun encrypt(key: SecretKey, algorithm: String): Coding {
        return encrypt(key, algorithm.toCodecAlgorithm())
    }

    @JvmDefault
    fun encrypt(key: SecretKey, algorithm: String, algorithmType: CodecAlgorithmType): Coding {
        return encrypt(key, algorithm.toCodecAlgorithm(algorithmType))
    }

    fun encrypt(key: SecretKey, algorithm: CodecAlgorithm): Coding

    @JvmDefault
    fun encrypt(key: ByteArray, algorithm: String): Coding {
        return encrypt(key, algorithm.toCodecAlgorithm())
    }

    @JvmDefault
    fun encrypt(key: ByteArray, algorithm: String, algorithmType: CodecAlgorithmType): Coding {
        return encrypt(key, algorithm.toCodecAlgorithm(algorithmType))
    }

    fun encrypt(key: ByteArray, algorithm: CodecAlgorithm): Coding

    @JvmDefault
    fun encrypt(key: CharSequence, algorithm: String): Coding {
        return encrypt(key, algorithm.toCodecAlgorithm())
    }

    @JvmDefault
    fun encrypt(key: CharSequence, algorithm: String, algorithmType: CodecAlgorithmType): Coding {
        return encrypt(key, algorithm.toCodecAlgorithm(algorithmType))
    }

    @JvmDefault
    fun encrypt(key: CharSequence, algorithm: CodecAlgorithm): Coding {
        return encrypt(key.toBytes(), algorithm)
    }

    @JvmDefault
    fun encrypt(key: Any, algorithm: String): Coding {
        return encrypt(key, algorithm.toCodecAlgorithm())
    }

    @JvmDefault
    fun encrypt(key: Any, algorithm: String, algorithmType: CodecAlgorithmType): Coding {
        return encrypt(key, algorithm.toCodecAlgorithm(algorithmType))
    }

    fun encrypt(key: Any, algorithm: CodecAlgorithm): Coding

    @JvmDefault
    fun decrypt(key: SecretKey, algorithm: String): Coding {
        return decrypt(key, algorithm.toCodecAlgorithm())
    }

    @JvmDefault
    fun decrypt(key: SecretKey, algorithm: String, algorithmType: CodecAlgorithmType): Coding {
        return decrypt(key, algorithm.toCodecAlgorithm(algorithmType))
    }

    fun decrypt(key: SecretKey, algorithm: CodecAlgorithm): Coding

    @JvmDefault
    fun decrypt(key: ByteArray, algorithm: String): Coding {
        return decrypt(key, algorithm.toCodecAlgorithm())
    }

    @JvmDefault
    fun decrypt(key: ByteArray, algorithm: String, algorithmType: CodecAlgorithmType): Coding {
        return decrypt(key, algorithm.toCodecAlgorithm(algorithmType))
    }

    fun decrypt(key: ByteArray, algorithm: CodecAlgorithm): Coding

    @JvmDefault
    fun decrypt(key: CharSequence, algorithm: String): Coding {
        return decrypt(key, algorithm.toCodecAlgorithm())
    }

    @JvmDefault
    fun decrypt(key: CharSequence, algorithm: String, algorithmType: CodecAlgorithmType): Coding {
        return decrypt(key, algorithm.toCodecAlgorithm(algorithmType))
    }

    @JvmDefault
    fun decrypt(key: CharSequence, algorithm: CodecAlgorithm): Coding {
        return decrypt(key.toBytes(), algorithm)
    }

    @JvmDefault
    fun decrypt(key: Any, algorithm: String): Coding {
        return decrypt(key, algorithm.toCodecAlgorithm())
    }

    @JvmDefault
    fun decrypt(key: Any, algorithm: String, algorithmType: CodecAlgorithmType): Coding {
        return decrypt(key, algorithm.toCodecAlgorithm(algorithmType))
    }

    fun decrypt(key: Any, algorithm: CodecAlgorithm): Coding

    @JvmDefault
    fun digest(algorithm: String): Coding {
        return digest(algorithm.toCodecAlgorithm())
    }

    @JvmDefault
    fun digest(algorithm: String, algorithmType: CodecAlgorithmType): Coding {
        return digest(algorithm.toCodecAlgorithm(algorithmType))
    }

    fun digest(algorithm: CodecAlgorithm): Coding

    @JvmDefault
    fun hmacDigest(key: SecretKey, algorithm: String): Coding {
        return hmacDigest(key, algorithm.toCodecAlgorithm())
    }

    @JvmDefault
    fun hmacDigest(key: SecretKey, algorithm: String, algorithmType: CodecAlgorithmType): Coding {
        return hmacDigest(key, algorithm.toCodecAlgorithm(algorithmType))
    }

    fun hmacDigest(key: SecretKey, algorithm: CodecAlgorithm): Coding

    @JvmDefault
    fun hmacDigest(key: ByteArray, algorithm: String): Coding {
        return hmacDigest(key, algorithm.toCodecAlgorithm())
    }

    @JvmDefault
    fun hmacDigest(key: ByteArray, algorithm: String, algorithmType: CodecAlgorithmType): Coding {
        return hmacDigest(key, algorithm.toCodecAlgorithm(algorithmType))
    }

    fun hmacDigest(key: ByteArray, algorithm: CodecAlgorithm): Coding

    @JvmDefault
    fun hmacDigest(key: CharSequence, algorithm: String): Coding {
        return hmacDigest(key, algorithm.toCodecAlgorithm())
    }

    @JvmDefault
    fun hmacDigest(key: CharSequence, algorithm: String, algorithmType: CodecAlgorithmType): Coding {
        return hmacDigest(key, algorithm.toCodecAlgorithm(algorithmType))
    }

    @JvmDefault
    fun hmacDigest(key: CharSequence, algorithm: CodecAlgorithm): Coding {
        return hmacDigest(key.toBytes(), algorithm)
    }

    @JvmDefault
    fun hmacDigest(key: Any, algorithm: String): Coding {
        return hmacDigest(key, algorithm.toCodecAlgorithm())
    }

    @JvmDefault
    fun hmacDigest(key: Any, algorithm: String, algorithmType: CodecAlgorithmType): Coding {
        return hmacDigest(key, algorithm.toCodecAlgorithm(algorithmType))
    }

    fun hmacDigest(key: Any, algorithm: CodecAlgorithm): Coding

    @JvmDefault
    fun encodeHex(): Coding {
        return encode(CodecAlgorithm.HEX)
    }

    @JvmDefault
    fun decodeHex(): Coding {
        return decode(CodecAlgorithm.HEX)
    }

    @JvmDefault
    fun encodeBase64(): Coding {
        return encode(CodecAlgorithm.BASE64)
    }

    @JvmDefault
    fun decodeBase64(): Coding {
        return decode(CodecAlgorithm.BASE64)
    }

    @JvmDefault
    fun encryptAes(key: SecretKey): Coding {
        return encrypt(key, CodecAlgorithm.AES)
    }

    @JvmDefault
    fun encryptAes(key: ByteArray): Coding {
        return encrypt(key, CodecAlgorithm.AES)
    }

    @JvmDefault
    fun encryptAes(key: CharSequence): Coding {
        return encrypt(key, CodecAlgorithm.AES)
    }

    @JvmDefault
    fun decryptAes(key: SecretKey): Coding {
        return decrypt(key, CodecAlgorithm.AES)
    }

    @JvmDefault
    fun decryptAes(key: ByteArray): Coding {
        return decrypt(key, CodecAlgorithm.AES)
    }

    @JvmDefault
    fun decryptAes(key: CharSequence): Coding {
        return decrypt(key, CodecAlgorithm.AES)
    }

    @JvmDefault
    fun digestMd2(): Coding {
        return digest(CodecAlgorithm.MD2)
    }

    @JvmDefault
    fun digestMd5(): Coding {
        return digest(CodecAlgorithm.MD5)
    }

    @JvmDefault
    fun digestSha1(): Coding {
        return digest(CodecAlgorithm.SHA1)
    }

    @JvmDefault
    fun digestSha256(): Coding {
        return digest(CodecAlgorithm.SHA256)
    }

    @JvmDefault
    fun digestSha384(): Coding {
        return digest(CodecAlgorithm.SHA384)
    }

    @JvmDefault
    fun digestSha512(): Coding {
        return digest(CodecAlgorithm.SHA512)
    }

    @JvmDefault
    fun hmacDigestMd5(key: SecretKey): Coding {
        return hmacDigest(key, CodecAlgorithm.HMAC_MD5)
    }

    @JvmDefault
    fun hmacDigestMd5(key: ByteArray): Coding {
        return hmacDigest(key, CodecAlgorithm.HMAC_MD5)
    }

    @JvmDefault
    fun hmacDigestMd5(key: CharSequence): Coding {
        return hmacDigest(key, CodecAlgorithm.HMAC_MD5)
    }

    @JvmDefault
    fun hmacDigestSha1(key: SecretKey): Coding {
        return hmacDigest(key, CodecAlgorithm.HMAC_SHA1)
    }

    @JvmDefault
    fun hmacDigestSha1(key: ByteArray): Coding {
        return hmacDigest(key, CodecAlgorithm.HMAC_SHA1)
    }

    @JvmDefault
    fun hmacDigestSha1(key: CharSequence): Coding {
        return hmacDigest(key, CodecAlgorithm.HMAC_SHA1)
    }

    @JvmDefault
    fun hmacDigestSha256(key: SecretKey): Coding {
        return hmacDigest(key, CodecAlgorithm.HMAC_SHA256)
    }

    @JvmDefault
    fun hmacDigestSha256(key: ByteArray): Coding {
        return hmacDigest(key, CodecAlgorithm.HMAC_SHA256)
    }

    @JvmDefault
    fun hmacDigestSha256(key: CharSequence): Coding {
        return hmacDigest(key, CodecAlgorithm.HMAC_SHA256)
    }

    @JvmDefault
    fun hmacDigestSha384(key: SecretKey): Coding {
        return hmacDigest(key, CodecAlgorithm.HMAC_SHA384)
    }

    @JvmDefault
    fun hmacDigestSha384(key: ByteArray): Coding {
        return hmacDigest(key, CodecAlgorithm.HMAC_SHA384)
    }

    @JvmDefault
    fun hmacDigestSha384(key: CharSequence): Coding {
        return hmacDigest(key, CodecAlgorithm.HMAC_SHA384)
    }

    @JvmDefault
    fun hmacDigestSha512(key: SecretKey): Coding {
        return hmacDigest(key, CodecAlgorithm.HMAC_SHA512)
    }

    @JvmDefault
    fun hmacDigestSha512(key: ByteArray): Coding {
        return hmacDigest(key, CodecAlgorithm.HMAC_SHA512)
    }

    @JvmDefault
    fun hmacDigestSha512(key: CharSequence): Coding {
        return hmacDigest(key, CodecAlgorithm.HMAC_SHA512)
    }

    @JvmDefault
    fun encryptRsa(publicKey: RSAPublicKey): Coding {
        return encrypt(publicKey, CodecAlgorithm.RSA)
    }

    @JvmDefault
    fun encryptRsa(publicKey: ByteArray): Coding {
        return encrypt(publicKey, CodecAlgorithm.RSA)
    }

    @JvmDefault
    fun encryptRsa(publicKey: CharSequence): Coding {
        return encrypt(publicKey, CodecAlgorithm.RSA)
    }

    @JvmDefault
    fun decryptRsa(privateKey: RSAPrivateKey): Coding {
        return decrypt(privateKey, CodecAlgorithm.RSA)
    }

    @JvmDefault
    fun decryptRsa(privateKey: ByteArray): Coding {
        return decrypt(privateKey, CodecAlgorithm.RSA)
    }

    @JvmDefault
    fun decryptRsa(privateKey: CharSequence): Coding {
        return decrypt(privateKey, CodecAlgorithm.RSA)
    }

    @JvmDefault
    fun encryptSm2(publicKey: ECPoint): Coding {
        return encrypt(publicKey, CodecAlgorithm.SM2)
    }

    @JvmDefault
    fun encryptSm2(publicKey: ByteArray): Coding {
        return encrypt(publicKey, CodecAlgorithm.SM2)
    }

    @JvmDefault
    fun encryptSm2(publicKey: CharSequence): Coding {
        return encrypt(publicKey, CodecAlgorithm.SM2)
    }

    @JvmDefault
    fun decryptSm2(privateKey: BigInteger): Coding {
        return decrypt(privateKey, CodecAlgorithm.SM2)
    }

    @JvmDefault
    fun decryptSm2(privateKey: ByteArray): Coding {
        return decrypt(privateKey, CodecAlgorithm.SM2)
    }

    @JvmDefault
    fun decryptSm2(privateKey: CharSequence): Coding {
        return decrypt(privateKey, CodecAlgorithm.SM2)
    }

    fun doFinal(): ByteArray

    @JvmDefault
    fun doFinalToString(): String {
        return doFinal().toChars()
    }

    @JvmDefault
    fun doFinalToHexString(): String {
        return doFinal().encodeHexString()
    }

    @JvmDefault
    fun doFinalToBase64String(): String {
        return doFinal().encodeBase64String()
    }

    companion object {

        private val DEFAULT_CIPHER_SUPPLIER: (CodecAlgorithm) -> Codec = label@{
            val cipher = when (it) {
                CodecAlgorithm.RSA -> Coding.rsaCipher()
                CodecAlgorithm.SM2 -> Coding.sm2Cipher()
                else -> null
            }
            if (cipher !== null) {
                return@label cipher
            }
            when (it.type) {
                CodecAlgorithmType.ENCODE -> Coding.encodeCipher(it)
                CodecAlgorithmType.DIGEST -> Coding.digestCipher(it)
                CodecAlgorithmType.SYMMETRIC -> Coding.symmetricCipher(it)
                else -> throw CodecAlgorithmNotFound(it)
            }
        }

        @JvmOverloads
        @JvmStatic
        fun forData(
            data: ByteArray,
            cipherSupplier: (CodecAlgorithm) -> Codec = DEFAULT_CIPHER_SUPPLIER
        ): Coding {
            return CommonCodec(data, cipherSupplier)
        }

        @JvmOverloads
        @JvmStatic
        fun forData(
            data: CharSequence,
            cipherSupplier: (CodecAlgorithm) -> Codec = DEFAULT_CIPHER_SUPPLIER
        ): Coding {
            return CommonCodec(data.toBytes(), cipherSupplier)
        }

        @JvmStatic
        fun ByteArray.encodeHex(): ByteArray {
            return HexCodec.encode(this)
        }

        @JvmStatic
        fun CharSequence.encodeHex(): ByteArray {
            return HexCodec.encode(this)
        }

        @JvmStatic
        fun ByteArray.encodeHexString(): String {
            return HexCodec.encodeToString(this)
        }

        @JvmStatic
        fun CharSequence.encodeHexString(): String {
            return HexCodec.encodeToString(this)
        }

        @JvmStatic
        fun ByteArray.decodeHex(): ByteArray {
            return HexCodec.decode(this)
        }

        @JvmStatic
        fun CharSequence.decodeHex(): ByteArray {
            return HexCodec.decode(this)
        }

        @JvmStatic
        fun ByteArray.decodeHexString(): String {
            return HexCodec.decodeToString(this)
        }

        @JvmStatic
        fun CharSequence.decodeHexString(): String {
            return HexCodec.decodeToString(this)
        }

        @JvmStatic
        fun ByteArray.encodeBase64(): ByteArray {
            return Base64Codec.encode(this)
        }

        @JvmStatic
        fun CharSequence.encodeBase64(): ByteArray {
            return Base64Codec.encode(this)
        }

        @JvmStatic
        fun ByteArray.encodeBase64String(): String {
            return Base64Codec.encodeToString(this)
        }

        @JvmStatic
        fun CharSequence.encodeBase64String(): String {
            return Base64Codec.encodeToString(this)
        }

        @JvmStatic
        fun ByteArray.decodeBase64(): ByteArray {
            return Base64Codec.decode(this)
        }

        @JvmStatic
        fun CharSequence.decodeBase64(): ByteArray {
            return Base64Codec.decode(this)
        }

        @JvmStatic
        fun ByteArray.decodeBase64String(): String {
            return Base64Codec.decodeToString(this)
        }

        @JvmStatic
        fun CharSequence.decodeBase64String(): String {
            return Base64Codec.decodeToString(this)
        }

        @JvmStatic
        fun aesCipher(): SymmetricCipher<SecretKey> {
            return symmetricCipher(CodecAlgorithm.AES)
        }

        @JvmStatic
        fun md2Cipher(): DigestCodec {
            return digestCipher(CodecAlgorithm.MD2)
        }

        @JvmStatic
        fun md5Cipher(): DigestCodec {
            return digestCipher(CodecAlgorithm.MD5)
        }

        @JvmStatic
        fun sha1Cipher(): DigestCodec {
            return digestCipher(CodecAlgorithm.SHA1)
        }

        @JvmStatic
        fun sha256Cipher(): DigestCodec {
            return digestCipher(CodecAlgorithm.SHA256)
        }

        @JvmStatic
        fun sha384Cipher(): DigestCodec {
            return digestCipher(CodecAlgorithm.SHA384)
        }

        @JvmStatic
        fun sha512Cipher(): DigestCodec {
            return digestCipher(CodecAlgorithm.SHA512)
        }

        @JvmStatic
        fun hmacMd5Cipher(): SecretKeyHmacDigestCipher {
            return hmacDigestCipher(CodecAlgorithm.HMAC_MD5)
        }

        @JvmStatic
        fun hmacSha1Cipher(): SecretKeyHmacDigestCipher {
            return hmacDigestCipher(CodecAlgorithm.HMAC_SHA1)
        }

        @JvmStatic
        fun hmacSha256Cipher(): SecretKeyHmacDigestCipher {
            return hmacDigestCipher(CodecAlgorithm.HMAC_SHA256)
        }

        @JvmStatic
        fun hmacSha384Cipher(): SecretKeyHmacDigestCipher {
            return hmacDigestCipher(CodecAlgorithm.HMAC_SHA384)
        }

        @JvmStatic
        fun hmacSha512Cipher(): SecretKeyHmacDigestCipher {
            return hmacDigestCipher(CodecAlgorithm.HMAC_SHA512)
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
        fun encodeCipher(algorithm: String): EncodeCodec {
            return encodeCipher(algorithm.toCodecAlgorithm())
        }

        @JvmStatic
        fun encodeCipher(algorithm: CodecAlgorithm): EncodeCodec {
            return algorithm.toEncodeCipher()
        }

        @JvmStatic
        fun digestCipher(algorithm: String): DigestCodec {
            return digestCipher(algorithm.toCodecAlgorithm(CodecAlgorithmType.DIGEST))
        }

        @JvmStatic
        fun digestCipher(algorithm: CodecAlgorithm): DigestCodec {
            return algorithm.toDigestCipher()
        }

        @JvmStatic
        fun hmacDigestCipher(algorithm: String): SecretKeyHmacDigestCipher {
            return hmacDigestCipher(algorithm.toCodecAlgorithm(CodecAlgorithmType.HMAC))
        }

        @JvmStatic
        fun hmacDigestCipher(algorithm: CodecAlgorithm): SecretKeyHmacDigestCipher {
            return algorithm.toMacDigestCipher()
        }

        @JvmStatic
        fun symmetricCipher(algorithm: String): SecretKeySymmetricCipher {
            return symmetricCipher(algorithm.toCodecAlgorithm(CodecAlgorithmType.SYMMETRIC))
        }

        @JvmStatic
        fun symmetricCipher(algorithm: CodecAlgorithm): SecretKeySymmetricCipher {
            return algorithm.toSecretKeySymmetricCipher()
        }
    }
}

private class CommonCodec(
    private var data: ByteArray,
    private val cipherSupplier: (CodecAlgorithm) -> Codec
) : Coding {

    override fun encode(algorithm: CodecAlgorithm): Coding {
        data = cipherSupplier(algorithm).asAny<EncodeCodec>().encode(data)
        return this
    }

    override fun decode(algorithm: CodecAlgorithm): Coding {
        data = cipherSupplier(algorithm).asAny<EncodeCodec>().decode(data)
        return this
    }

    override fun encrypt(key: SecretKey, algorithm: CodecAlgorithm): Coding {
        return encrypt(key as Any, algorithm)
    }

    override fun encrypt(key: ByteArray, algorithm: CodecAlgorithm): Coding {
        val cipher = cipherSupplier(algorithm)
        data = when (cipher) {
            is ReversibleCipher<*, *> -> cipher.encrypt(key, data)
            is MacDigestCipher<*> -> cipher.digest(key, data)
            else -> throw CodecAlgorithmNotFound(algorithm)
        }
        return this
    }

    override fun encrypt(key: Any, algorithm: CodecAlgorithm): Coding {
        val cipher = cipherSupplier(algorithm)
        data = when (cipher) {
            is ReversibleCipher<*, *> -> cipher.encryptWithAny(key, data)
            is MacDigestCipher<*> -> cipher.digestWithAny(key, data)
            else -> throw CodecAlgorithmNotFound(algorithm)
        }
        return this
    }

    override fun decrypt(key: SecretKey, algorithm: CodecAlgorithm): Coding {
        return decrypt(key as Any, algorithm)
    }

    override fun decrypt(key: ByteArray, algorithm: CodecAlgorithm): Coding {
        val cipher = cipherSupplier(algorithm)
        data = when (cipher) {
            is ReversibleCipher<*, *> -> cipher.decrypt(key, data)
            else -> throw CodecAlgorithmNotFound(algorithm)
        }
        return this
    }

    override fun decrypt(key: Any, algorithm: CodecAlgorithm): Coding {
        val cipher = cipherSupplier(algorithm)
        data = when (cipher) {
            is ReversibleCipher<*, *> -> cipher.decryptWithAny(key, data)
            else -> throw CodecAlgorithmNotFound(algorithm)
        }
        return this
    }

    override fun digest(algorithm: CodecAlgorithm): Coding {
        data = cipherSupplier(algorithm).asAny<DigestCodec>().digest(data)
        return this
    }

    override fun hmacDigest(key: SecretKey, algorithm: CodecAlgorithm): Coding {
        return hmacDigest(key as Any, algorithm)
    }

    override fun hmacDigest(key: ByteArray, algorithm: CodecAlgorithm): Coding {
        data = cipherSupplier(algorithm).asAny<MacDigestCipher<*>>().digest(key, data)
        return this
    }

    override fun hmacDigest(key: Any, algorithm: CodecAlgorithm): Coding {
        data = cipherSupplier(algorithm).asAny<MacDigestCipher<*>>().digestWithAny(key, data)
        return this
    }

    override fun doFinal(): ByteArray {
        return data.clone()
    }
}

private class CodecAlgorithmNotFound(
    algorithm: CodecAlgorithm
) : RuntimeException("${algorithm.name}: ${algorithm.type}")