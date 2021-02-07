package xyz.srclab.common.codec

import org.bouncycastle.math.ec.ECPoint
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
 *
 * @see CodecKeys
 * @see CommonCodec
 * @see CodecAlgorithm
 * @see ReversibleCipher
 * @see SymmetricCipher
 * @see AsymmetricCipher
 * @see DigestCipher
 * @see HmacDigestCipher
 */
interface Codec {

    @JvmDefault
    fun encode(algorithm: String): Codec {
        return encode(CodecAlgorithm.forName(algorithm, CodecAlgorithmType.ENCODE))
    }

    fun encode(algorithm: CodecAlgorithm): Codec

    @JvmDefault
    fun decode(algorithm: String): Codec {
        return decode(CodecAlgorithm.forName(algorithm, CodecAlgorithmType.ENCODE))
    }

    fun decode(algorithm: CodecAlgorithm): Codec

    @JvmDefault
    fun encrypt(key: SecretKey, algorithm: String): Codec {
        return encrypt(key, CodecAlgorithm.forName(algorithm))
    }

    @JvmDefault
    fun encrypt(key: SecretKey, algorithm: String, algorithmType: CodecAlgorithmType): Codec {
        return encrypt(key, CodecAlgorithm.forName(algorithm, algorithmType))
    }

    fun encrypt(key: SecretKey, algorithm: CodecAlgorithm): Codec

    @JvmDefault
    fun encrypt(key: ByteArray, algorithm: String): Codec {
        return encrypt(key, CodecAlgorithm.forName(algorithm))
    }

    @JvmDefault
    fun encrypt(key: ByteArray, algorithm: String, algorithmType: CodecAlgorithmType): Codec {
        return encrypt(key, CodecAlgorithm.forName(algorithm, algorithmType))
    }

    fun encrypt(key: ByteArray, algorithm: CodecAlgorithm): Codec

    @JvmDefault
    fun encrypt(key: CharSequence, algorithm: String): Codec {
        return encrypt(key, CodecAlgorithm.forName(algorithm))
    }

    @JvmDefault
    fun encrypt(key: CharSequence, algorithm: String, algorithmType: CodecAlgorithmType): Codec {
        return encrypt(key, CodecAlgorithm.forName(algorithm, algorithmType))
    }

    @JvmDefault
    fun encrypt(key: CharSequence, algorithm: CodecAlgorithm): Codec {
        return encrypt(key.toBytes(), algorithm)
    }

    @JvmDefault
    fun encrypt(key: Any, algorithm: String): Codec {
        return encrypt(key, CodecAlgorithm.forName(algorithm))
    }

    @JvmDefault
    fun encrypt(key: Any, algorithm: String, algorithmType: CodecAlgorithmType): Codec {
        return encrypt(key, CodecAlgorithm.forName(algorithm, algorithmType))
    }

    fun encrypt(key: Any, algorithm: CodecAlgorithm): Codec

    @JvmDefault
    fun decrypt(key: SecretKey, algorithm: String): Codec {
        return decrypt(key, CodecAlgorithm.forName(algorithm))
    }

    @JvmDefault
    fun decrypt(key: SecretKey, algorithm: String, algorithmType: CodecAlgorithmType): Codec {
        return decrypt(key, CodecAlgorithm.forName(algorithm, algorithmType))
    }

    fun decrypt(key: SecretKey, algorithm: CodecAlgorithm): Codec

    @JvmDefault
    fun decrypt(key: ByteArray, algorithm: String): Codec {
        return decrypt(key, CodecAlgorithm.forName(algorithm))
    }

    @JvmDefault
    fun decrypt(key: ByteArray, algorithm: String, algorithmType: CodecAlgorithmType): Codec {
        return decrypt(key, CodecAlgorithm.forName(algorithm, algorithmType))
    }

    fun decrypt(key: ByteArray, algorithm: CodecAlgorithm): Codec

    @JvmDefault
    fun decrypt(key: CharSequence, algorithm: String): Codec {
        return decrypt(key, CodecAlgorithm.forName(algorithm))
    }

    @JvmDefault
    fun decrypt(key: CharSequence, algorithm: String, algorithmType: CodecAlgorithmType): Codec {
        return decrypt(key, CodecAlgorithm.forName(algorithm, algorithmType))
    }

    @JvmDefault
    fun decrypt(key: CharSequence, algorithm: CodecAlgorithm): Codec {
        return decrypt(key.toBytes(), algorithm)
    }

    @JvmDefault
    fun decrypt(key: Any, algorithm: String): Codec {
        return decrypt(key, CodecAlgorithm.forName(algorithm))
    }

    @JvmDefault
    fun decrypt(key: Any, algorithm: String, algorithmType: CodecAlgorithmType): Codec {
        return decrypt(key, CodecAlgorithm.forName(algorithm, algorithmType))
    }

    fun decrypt(key: Any, algorithm: CodecAlgorithm): Codec

    @JvmDefault
    fun digest(algorithm: String): Codec {
        return digest(CodecAlgorithm.forName(algorithm))
    }

    @JvmDefault
    fun digest(algorithm: String, algorithmType: CodecAlgorithmType): Codec {
        return digest(CodecAlgorithm.forName(algorithm, algorithmType))
    }

    fun digest(algorithm: CodecAlgorithm): Codec

    @JvmDefault
    fun hmacDigest(key: SecretKey, algorithm: String): Codec {
        return hmacDigest(key, CodecAlgorithm.forName(algorithm))
    }

    @JvmDefault
    fun hmacDigest(key: SecretKey, algorithm: String, algorithmType: CodecAlgorithmType): Codec {
        return hmacDigest(key, CodecAlgorithm.forName(algorithm, algorithmType))
    }

    fun hmacDigest(key: SecretKey, algorithm: CodecAlgorithm): Codec

    @JvmDefault
    fun hmacDigest(key: ByteArray, algorithm: String): Codec {
        return hmacDigest(key, CodecAlgorithm.forName(algorithm))
    }

    @JvmDefault
    fun hmacDigest(key: ByteArray, algorithm: String, algorithmType: CodecAlgorithmType): Codec {
        return hmacDigest(key, CodecAlgorithm.forName(algorithm, algorithmType))
    }

    fun hmacDigest(key: ByteArray, algorithm: CodecAlgorithm): Codec

    @JvmDefault
    fun hmacDigest(key: CharSequence, algorithm: String): Codec {
        return hmacDigest(key, CodecAlgorithm.forName(algorithm))
    }

    @JvmDefault
    fun hmacDigest(key: CharSequence, algorithm: String, algorithmType: CodecAlgorithmType): Codec {
        return hmacDigest(key, CodecAlgorithm.forName(algorithm, algorithmType))
    }

    @JvmDefault
    fun hmacDigest(key: CharSequence, algorithm: CodecAlgorithm): Codec {
        return hmacDigest(key.toBytes(), algorithm)
    }

    @JvmDefault
    fun hmacDigest(key: Any, algorithm: String): Codec {
        return hmacDigest(key, CodecAlgorithm.forName(algorithm))
    }

    @JvmDefault
    fun hmacDigest(key: Any, algorithm: String, algorithmType: CodecAlgorithmType): Codec {
        return hmacDigest(key, CodecAlgorithm.forName(algorithm, algorithmType))
    }

    fun hmacDigest(key: Any, algorithm: CodecAlgorithm): Codec

    @JvmDefault
    fun encodeHex(): Codec {
        return encode(CodecAlgorithm.HEX)
    }

    @JvmDefault
    fun decodeHex(): Codec {
        return decode(CodecAlgorithm.HEX)
    }

    @JvmDefault
    fun encodeBase64(): Codec {
        return encode(CodecAlgorithm.BASE64)
    }

    @JvmDefault
    fun decodeBase64(): Codec {
        return decode(CodecAlgorithm.BASE64)
    }

    @JvmDefault
    fun encryptAes(key: SecretKey): Codec {
        return encrypt(key, CodecAlgorithm.AES)
    }

    @JvmDefault
    fun encryptAes(key: ByteArray): Codec {
        return encrypt(key, CodecAlgorithm.AES)
    }

    @JvmDefault
    fun encryptAes(key: CharSequence): Codec {
        return encrypt(key, CodecAlgorithm.AES)
    }

    @JvmDefault
    fun decryptAes(key: SecretKey): Codec {
        return decrypt(key, CodecAlgorithm.AES)
    }

    @JvmDefault
    fun decryptAes(key: ByteArray): Codec {
        return decrypt(key, CodecAlgorithm.AES)
    }

    @JvmDefault
    fun decryptAes(key: CharSequence): Codec {
        return decrypt(key, CodecAlgorithm.AES)
    }

    @JvmDefault
    fun digestMd2(): Codec {
        return digest(CodecAlgorithm.MD2)
    }

    @JvmDefault
    fun digestMd5(): Codec {
        return digest(CodecAlgorithm.MD5)
    }

    @JvmDefault
    fun digestSha1(): Codec {
        return digest(CodecAlgorithm.SHA1)
    }

    @JvmDefault
    fun digestSha256(): Codec {
        return digest(CodecAlgorithm.SHA256)
    }

    @JvmDefault
    fun digestSha384(): Codec {
        return digest(CodecAlgorithm.SHA384)
    }

    @JvmDefault
    fun digestSha512(): Codec {
        return digest(CodecAlgorithm.SHA512)
    }

    @JvmDefault
    fun hmacDigestMd5(key: SecretKey): Codec {
        return hmacDigest(key, CodecAlgorithm.HMAC_MD5)
    }

    @JvmDefault
    fun hmacDigestMd5(key: ByteArray): Codec {
        return hmacDigest(key, CodecAlgorithm.HMAC_MD5)
    }

    @JvmDefault
    fun hmacDigestMd5(key: CharSequence): Codec {
        return hmacDigest(key, CodecAlgorithm.HMAC_MD5)
    }

    @JvmDefault
    fun hmacDigestSha1(key: SecretKey): Codec {
        return hmacDigest(key, CodecAlgorithm.HMAC_SHA1)
    }

    @JvmDefault
    fun hmacDigestSha1(key: ByteArray): Codec {
        return hmacDigest(key, CodecAlgorithm.HMAC_SHA1)
    }

    @JvmDefault
    fun hmacDigestSha1(key: CharSequence): Codec {
        return hmacDigest(key, CodecAlgorithm.HMAC_SHA1)
    }

    @JvmDefault
    fun hmacDigestSha256(key: SecretKey): Codec {
        return hmacDigest(key, CodecAlgorithm.HMAC_SHA256)
    }

    @JvmDefault
    fun hmacDigestSha256(key: ByteArray): Codec {
        return hmacDigest(key, CodecAlgorithm.HMAC_SHA256)
    }

    @JvmDefault
    fun hmacDigestSha256(key: CharSequence): Codec {
        return hmacDigest(key, CodecAlgorithm.HMAC_SHA256)
    }

    @JvmDefault
    fun hmacDigestSha384(key: SecretKey): Codec {
        return hmacDigest(key, CodecAlgorithm.HMAC_SHA384)
    }

    @JvmDefault
    fun hmacDigestSha384(key: ByteArray): Codec {
        return hmacDigest(key, CodecAlgorithm.HMAC_SHA384)
    }

    @JvmDefault
    fun hmacDigestSha384(key: CharSequence): Codec {
        return hmacDigest(key, CodecAlgorithm.HMAC_SHA384)
    }

    @JvmDefault
    fun hmacDigestSha512(key: SecretKey): Codec {
        return hmacDigest(key, CodecAlgorithm.HMAC_SHA512)
    }

    @JvmDefault
    fun hmacDigestSha512(key: ByteArray): Codec {
        return hmacDigest(key, CodecAlgorithm.HMAC_SHA512)
    }

    @JvmDefault
    fun hmacDigestSha512(key: CharSequence): Codec {
        return hmacDigest(key, CodecAlgorithm.HMAC_SHA512)
    }

    @JvmDefault
    fun encryptRsa(publicKey: RSAPublicKey): Codec {
        return encrypt(publicKey, CodecAlgorithm.RSA)
    }

    @JvmDefault
    fun encryptRsa(publicKey: ByteArray): Codec {
        return encrypt(publicKey, CodecAlgorithm.RSA)
    }

    @JvmDefault
    fun encryptRsa(publicKey: CharSequence): Codec {
        return encrypt(publicKey, CodecAlgorithm.RSA)
    }

    @JvmDefault
    fun decryptRsa(privateKey: RSAPrivateKey): Codec {
        return decrypt(privateKey, CodecAlgorithm.RSA)
    }

    @JvmDefault
    fun decryptRsa(privateKey: ByteArray): Codec {
        return decrypt(privateKey, CodecAlgorithm.RSA)
    }

    @JvmDefault
    fun decryptRsa(privateKey: CharSequence): Codec {
        return decrypt(privateKey, CodecAlgorithm.RSA)
    }

    @JvmDefault
    fun encryptSm2(publicKey: ECPoint): Codec {
        return encrypt(publicKey, CodecAlgorithm.SM2)
    }

    @JvmDefault
    fun encryptSm2(publicKey: ByteArray): Codec {
        return encrypt(publicKey, CodecAlgorithm.SM2)
    }

    @JvmDefault
    fun encryptSm2(publicKey: CharSequence): Codec {
        return encrypt(publicKey, CodecAlgorithm.SM2)
    }

    @JvmDefault
    fun decryptSm2(privateKey: BigInteger): Codec {
        return decrypt(privateKey, CodecAlgorithm.SM2)
    }

    @JvmDefault
    fun decryptSm2(privateKey: ByteArray): Codec {
        return decrypt(privateKey, CodecAlgorithm.SM2)
    }

    @JvmDefault
    fun decryptSm2(privateKey: CharSequence): Codec {
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

        @JvmStatic
        fun forData(data: ByteArray): Codec {
            return CommonCodec(data)
        }

        @JvmStatic
        fun forData(data: CharSequence): Codec {
            return CommonCodec(data.toBytes())
        }

        @JvmStatic
        fun ByteArray.encodeHex(): ByteArray {
            return HexEncodeCipher.encode(this)
        }

        @JvmStatic
        fun CharSequence.encodeHex(): ByteArray {
            return HexEncodeCipher.encode(this)
        }

        @JvmStatic
        fun ByteArray.encodeHexString(): String {
            return HexEncodeCipher.encodeToString(this)
        }

        @JvmStatic
        fun CharSequence.encodeHexString(): String {
            return HexEncodeCipher.encodeToString(this)
        }

        @JvmStatic
        fun ByteArray.decodeHex(): ByteArray {
            return HexEncodeCipher.decode(this)
        }

        @JvmStatic
        fun CharSequence.decodeHex(): ByteArray {
            return HexEncodeCipher.decode(this)
        }

        @JvmStatic
        fun ByteArray.decodeHexString(): String {
            return HexEncodeCipher.decodeToString(this)
        }

        @JvmStatic
        fun CharSequence.decodeHexString(): String {
            return HexEncodeCipher.decodeToString(this)
        }

        @JvmStatic
        fun ByteArray.encodeBase64(): ByteArray {
            return Base64EncodeCipher.encode(this)
        }

        @JvmStatic
        fun CharSequence.encodeBase64(): ByteArray {
            return Base64EncodeCipher.encode(this)
        }

        @JvmStatic
        fun ByteArray.encodeBase64String(): String {
            return Base64EncodeCipher.encodeToString(this)
        }

        @JvmStatic
        fun CharSequence.encodeBase64String(): String {
            return Base64EncodeCipher.encodeToString(this)
        }

        @JvmStatic
        fun ByteArray.decodeBase64(): ByteArray {
            return Base64EncodeCipher.decode(this)
        }

        @JvmStatic
        fun CharSequence.decodeBase64(): ByteArray {
            return Base64EncodeCipher.decode(this)
        }

        @JvmStatic
        fun ByteArray.decodeBase64String(): String {
            return Base64EncodeCipher.decodeToString(this)
        }

        @JvmStatic
        fun CharSequence.decodeBase64String(): String {
            return Base64EncodeCipher.decodeToString(this)
        }

        @JvmStatic
        fun aesCipher(): SymmetricCipher<SecretKey> {
            return symmetricCipher(CodecAlgorithm.AES)
        }

        @JvmStatic
        fun md2Cipher(): DigestCipher {
            return digestCipher(CodecAlgorithm.MD2)
        }

        @JvmStatic
        fun md5Cipher(): DigestCipher {
            return digestCipher(CodecAlgorithm.MD5)
        }

        @JvmStatic
        fun sha1Cipher(): DigestCipher {
            return digestCipher(CodecAlgorithm.SHA1)
        }

        @JvmStatic
        fun sha256Cipher(): DigestCipher {
            return digestCipher(CodecAlgorithm.SHA256)
        }

        @JvmStatic
        fun sha384Cipher(): DigestCipher {
            return digestCipher(CodecAlgorithm.SHA384)
        }

        @JvmStatic
        fun sha512Cipher(): DigestCipher {
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
        fun symmetricCipher(algorithm: String): SecretKeySymmetricCipher {
            return symmetricCipher(CodecAlgorithm.forName(algorithm, CodecAlgorithmType.SYMMETRIC))
        }

        @JvmStatic
        fun symmetricCipher(algorithm: CodecAlgorithm): SecretKeySymmetricCipher {
            return SymmetricCipher.forAlgorithm(algorithm)
        }

        @JvmStatic
        fun digestCipher(algorithm: String): DigestCipher {
            return digestCipher(CodecAlgorithm.forName(algorithm, CodecAlgorithmType.DIGEST))
        }

        @JvmStatic
        fun digestCipher(algorithm: CodecAlgorithm): DigestCipher {
            return DigestCipher.forAlgorithm(algorithm)
        }

        @JvmStatic
        fun hmacDigestCipher(algorithm: String): SecretKeyHmacDigestCipher {
            return hmacDigestCipher(CodecAlgorithm.forName(algorithm, CodecAlgorithmType.HMAC))
        }

        @JvmStatic
        fun hmacDigestCipher(algorithm: CodecAlgorithm): SecretKeyHmacDigestCipher {
            return HmacDigestCipher.forAlgorithm(algorithm)
        }
    }
}

open class CommonCodec(private var data: ByteArray) : Codec {

    override fun encode(algorithm: CodecAlgorithm): Codec {
        data = getEncoder(algorithm).encode(data)
        return this
    }

    override fun decode(algorithm: CodecAlgorithm): Codec {
        data = getEncoder(algorithm).decode(data)
        return this
    }

    private fun getEncoder(algorithm: CodecAlgorithm): EncodeCipher {
        return when (algorithm) {
            CodecAlgorithm.HEX -> HexEncodeCipher
            CodecAlgorithm.BASE64 -> Base64EncodeCipher
            else -> throw CodecAlgorithmNotFound(algorithm)
        }
    }

    override fun encrypt(key: SecretKey, algorithm: CodecAlgorithm): Codec {
        data = when (algorithm) {
            CodecAlgorithm.RSA -> Codec.rsaCipher().encrypt(key.encoded, data)
            CodecAlgorithm.SM2 -> Codec.sm2Cipher().encrypt(key.encoded, data)
            else -> {
                if (algorithm.type == CodecAlgorithmType.SYMMETRIC) {
                    Codec.symmetricCipher(algorithm).encrypt(key, data)
                } else {
                    throw CodecAlgorithmNotFound(algorithm)
                }
            }
        }
        return this
    }

    override fun encrypt(key: ByteArray, algorithm: CodecAlgorithm): Codec {
        data = when (algorithm) {
            CodecAlgorithm.RSA -> Codec.rsaCipher().encrypt(key, data)
            CodecAlgorithm.SM2 -> Codec.sm2Cipher().encrypt(key, data)
            else -> {
                if (algorithm.type == CodecAlgorithmType.SYMMETRIC) {
                    Codec.symmetricCipher(algorithm).encrypt(key, data)
                } else {
                    throw CodecAlgorithmNotFound(algorithm)
                }
            }
        }
        return this
    }

    override fun encrypt(key: Any, algorithm: CodecAlgorithm): Codec {
        data = when (algorithm) {
            CodecAlgorithm.RSA -> {
                when (key) {
                    is RSAPublicKey -> Codec.rsaCipher().encrypt(key, data)
                    is ByteArray -> Codec.rsaCipher().encrypt(key, data)
                    is CharSequence -> Codec.rsaCipher().encrypt(key, data)
                    else -> throw CodecAlgorithmNotFound(algorithm)
                }
            }
            CodecAlgorithm.SM2 -> {
                when (key) {
                    is ECPoint -> Codec.sm2Cipher().encrypt(key, data)
                    is ByteArray -> Codec.sm2Cipher().encrypt(key, data)
                    is CharSequence -> Codec.sm2Cipher().encrypt(key, data)
                    else -> throw CodecAlgorithmNotFound(algorithm)
                }
            }
            else -> {
                if (algorithm.type != CodecAlgorithmType.SYMMETRIC) {
                    throw CodecAlgorithmNotFound(algorithm)
                }
                when (key) {
                    is SecretKey -> {
                        Codec.symmetricCipher(algorithm).encrypt(key, data)
                    }
                    is ByteArray -> {
                        Codec.symmetricCipher(algorithm).encrypt(key, data)
                    }
                    is CharSequence -> {
                        Codec.symmetricCipher(algorithm).encrypt(key, data)
                    }
                    else -> throw CodecAlgorithmNotFound(algorithm)
                }
            }
        }
        return this
    }

    override fun decrypt(key: SecretKey, algorithm: CodecAlgorithm): Codec {
        data = when (algorithm) {
            CodecAlgorithm.RSA -> Codec.rsaCipher().decrypt(key.encoded, data)
            CodecAlgorithm.SM2 -> Codec.sm2Cipher().decrypt(key.encoded, data)
            else -> {
                if (algorithm.type == CodecAlgorithmType.SYMMETRIC) {
                    Codec.symmetricCipher(algorithm).decrypt(key, data)
                } else {
                    throw CodecAlgorithmNotFound(algorithm)
                }
            }
        }
        return this
    }

    override fun decrypt(key: ByteArray, algorithm: CodecAlgorithm): Codec {
        data = when (algorithm) {
            CodecAlgorithm.RSA -> Codec.rsaCipher().decrypt(key, data)
            CodecAlgorithm.SM2 -> Codec.sm2Cipher().decrypt(key, data)
            else -> {
                if (algorithm.type == CodecAlgorithmType.SYMMETRIC) {
                    Codec.symmetricCipher(algorithm).decrypt(key, data)
                } else {
                    throw CodecAlgorithmNotFound(algorithm)
                }
            }
        }
        return this
    }

    override fun decrypt(key: Any, algorithm: CodecAlgorithm): Codec {
        data = when (algorithm) {
            CodecAlgorithm.RSA -> {
                when (key) {
                    is RSAPrivateKey -> Codec.rsaCipher().decrypt(key, data)
                    is ByteArray -> Codec.rsaCipher().decrypt(key, data)
                    is CharSequence -> Codec.rsaCipher().decrypt(key, data)
                    else -> throw CodecAlgorithmNotFound(algorithm)
                }
            }
            CodecAlgorithm.SM2 -> {
                when (key) {
                    is BigInteger -> Codec.sm2Cipher().decrypt(key, data)
                    is ByteArray -> Codec.sm2Cipher().decrypt(key, data)
                    is CharSequence -> Codec.sm2Cipher().decrypt(key, data)
                    else -> throw CodecAlgorithmNotFound(algorithm)
                }
            }
            else -> {
                if (algorithm.type != CodecAlgorithmType.SYMMETRIC) {
                    throw CodecAlgorithmNotFound(algorithm)
                }
                when (key) {
                    is SecretKey -> {
                        Codec.symmetricCipher(algorithm).decrypt(key, data)
                    }
                    is ByteArray -> {
                        Codec.symmetricCipher(algorithm).decrypt(key, data)
                    }
                    is CharSequence -> {
                        Codec.symmetricCipher(algorithm).decrypt(key, data)
                    }
                    else -> throw CodecAlgorithmNotFound(algorithm)
                }
            }
        }
        return this
    }

    override fun digest(algorithm: CodecAlgorithm): Codec {
        data = Codec.digestCipher(algorithm).digest(data)
        return this
    }

    override fun hmacDigest(key: SecretKey, algorithm: CodecAlgorithm): Codec {
        data = Codec.hmacDigestCipher(algorithm).digest(key, data)
        return this
    }

    override fun hmacDigest(key: ByteArray, algorithm: CodecAlgorithm): Codec {
        data = Codec.hmacDigestCipher(algorithm).digest(key, data)
        return this
    }

    override fun hmacDigest(key: Any, algorithm: CodecAlgorithm): Codec {
        data = when (key) {
            is SecretKey -> {
                Codec.hmacDigestCipher(algorithm).digest(key, data)
            }
            is ByteArray -> {
                Codec.hmacDigestCipher(algorithm).digest(key, data)
            }
            is CharSequence -> {
                Codec.hmacDigestCipher(algorithm).digest(key, data)
            }
            else -> throw CodecAlgorithmNotFound(algorithm)
        }
        return this
    }

    override fun doFinal(): ByteArray {
        return data.clone()
    }
}

private class CodecAlgorithmNotFound(
    algorithm: CodecAlgorithm
) : RuntimeException("${algorithm.name}: ${algorithm.type}")