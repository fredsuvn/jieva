package xyz.srclab.common.codec

import xyz.srclab.common.codec.rsa.RsaCipher
import java.lang.IllegalArgumentException
import java.lang.UnsupportedOperationException
import java.math.BigInteger
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey

/**
 * @author sunqian
 */
internal class CodecImpl(private var data: ByteArray?) : Codec {
    override fun encodeHex(): Codec {
        data = Codec.Companion.encodeHex(data)
        return this
    }

    override fun decodeHex(): Codec {
        data = Codec.Companion.decodeHex(data)
        return this
    }

    override fun encodeBase64(): Codec {
        data = Codec.Companion.encodeBase64(data)
        return this
    }

    override fun decodeBase64(): Codec {
        data = Codec.Companion.decodeBase64(data)
        return this
    }

    override fun encode(algorithm: CodecAlgorithm): Codec? {
        require(algorithm.type() == CodecAlgorithm.Type.ENCODE) { "Algorithm is not encode: $algorithm" }
        if (algorithm == CodecAlgorithm.Companion.PLAIN) {
            return this
        }
        if (algorithm == CodecAlgorithm.Companion.HEX) {
            return encodeHex()
        }
        if (algorithm == CodecAlgorithm.Companion.BASE64) {
            return encodeBase64()
        }
        throw IllegalArgumentException("Unknown encode algorithm: $algorithm")
    }

    override fun decode(algorithm: CodecAlgorithm): Codec? {
        require(algorithm.type() == CodecAlgorithm.Type.ENCODE) { "Algorithm is not encode: $algorithm" }
        if (algorithm == CodecAlgorithm.Companion.PLAIN) {
            return this
        }
        if (algorithm == CodecAlgorithm.Companion.HEX) {
            return decodeHex()
        }
        if (algorithm == CodecAlgorithm.Companion.BASE64) {
            return decodeBase64()
        }
        throw IllegalArgumentException("Unknown decode algorithm: $algorithm")
    }

    override fun encrypt(secretKey: SecretKey, algorithm: CodecAlgorithm): Codec {
        when (algorithm.type()) {
            CodecAlgorithm.Type.SYMMETRIC -> {
                val symmetricCipher: SymmetricCipher<SecretKey?> = Codec.Companion.symmetricCipher(algorithm)
                data = symmetricCipher.encrypt(secretKey, data)
            }
            CodecAlgorithm.Type.HMAC -> {
                val hmacDigestCipher: HmacDigestCipher<SecretKey?> = Codec.Companion.hmacDigestCipher(algorithm)
                data = hmacDigestCipher.digest(secretKey, data)
            }
            CodecAlgorithm.Type.ASYMMETRIC -> when (algorithm.name()) {
                CodecAlgorithmConstants.RSA -> {
                    val rsaCipher: RsaCipher = Codec.Companion.rsaCipher()
                    data = if (secretKey is RSAPublicKey) {
                        rsaCipher.encrypt(secretKey as RSAPublicKey, data)
                    } else {
                        rsaCipher.encrypt(secretKey.getEncoded(), data)
                    }
                }
                CodecAlgorithmConstants.SM2 -> {
                    val sm2Cipher: Sm2Cipher = Codec.Companion.sm2Cipher()
                    data = sm2Cipher.encrypt(secretKey.getEncoded(), data)
                }
            }
            else -> throw IllegalArgumentException(
                "Algorithm should be symmetric or hmac-digest or RSA or SM2: $algorithm"
            )
        }
        return this
    }

    override fun encrypt(secretKey: ByteArray?, algorithm: CodecAlgorithm): Codec {
        when (algorithm.type()) {
            CodecAlgorithm.Type.SYMMETRIC -> {
                val symmetricCipher: SymmetricCipher<SecretKey?> = Codec.Companion.symmetricCipher(algorithm)
                data = symmetricCipher.encrypt(secretKey, data)
            }
            CodecAlgorithm.Type.HMAC -> {
                val hmacDigestCipher: HmacDigestCipher<SecretKey?> = Codec.Companion.hmacDigestCipher(algorithm)
                data = hmacDigestCipher.digest(secretKey, data)
            }
            CodecAlgorithm.Type.ASYMMETRIC -> when (algorithm.name()) {
                CodecAlgorithmConstants.RSA -> {
                    val rsaCipher: RsaCipher = Codec.Companion.rsaCipher()
                    data = rsaCipher.encrypt(secretKey, data)
                }
                CodecAlgorithmConstants.SM2 -> {
                    val sm2Cipher: Sm2Cipher = Codec.Companion.sm2Cipher()
                    data = sm2Cipher.encrypt(secretKey, data)
                }
            }
            else -> throw IllegalArgumentException(
                "Algorithm should be symmetric or hmac-digest or RSA or SM2: $algorithm"
            )
        }
        return this
    }

    override fun encrypt(secretKey: Any, algorithm: CodecAlgorithm): Codec {
        if (secretKey is RSAPublicKey
            && CodecAlgorithm.Type.ASYMMETRIC == algorithm.type() && CodecAlgorithmConstants.RSA == algorithm.name()
        ) {
            val rsaCipher: RsaCipher = Codec.Companion.rsaCipher()
            data = rsaCipher.encrypt(secretKey, data)
            return this
        }
        if (secretKey is ECPoint
            && CodecAlgorithm.Type.ASYMMETRIC == algorithm.type() && CodecAlgorithmConstants.SM2 == algorithm.name()
        ) {
            val sm2Cipher: Sm2Cipher = Codec.Companion.sm2Cipher()
            data = sm2Cipher.encrypt(secretKey as ECPoint, data)
            return this
        }
        if (secretKey is SecretKey) {
            return encrypt(secretKey as SecretKey, algorithm)
        }
        if (secretKey is ByteArray) {
            return encrypt(secretKey, algorithm)
        }
        throw IllegalArgumentException(
            "Unknown secret key: $secretKey"
        )
    }

    override fun decrypt(secretKey: SecretKey, algorithm: CodecAlgorithm): Codec {
        when (algorithm.type()) {
            CodecAlgorithm.Type.SYMMETRIC -> {
                val symmetricCipher: SymmetricCipher<SecretKey?> = Codec.Companion.symmetricCipher(algorithm)
                data = symmetricCipher.decrypt(secretKey, data)
            }
            CodecAlgorithm.Type.ASYMMETRIC -> when (algorithm.name()) {
                CodecAlgorithmConstants.RSA -> {
                    val rsaCipher: RsaCipher = Codec.Companion.rsaCipher()
                    data = if (secretKey is RSAPrivateKey) {
                        rsaCipher.decrypt(secretKey as RSAPrivateKey, data)
                    } else {
                        rsaCipher.decrypt(secretKey.getEncoded(), data)
                    }
                }
                CodecAlgorithmConstants.SM2 -> {
                    val sm2Cipher: Sm2Cipher = Codec.Companion.sm2Cipher()
                    data = sm2Cipher.decrypt(secretKey.getEncoded(), data)
                }
            }
            else -> throw IllegalArgumentException(
                "Algorithm should be symmetric or RSA or SM2: $algorithm"
            )
        }
        return this
    }

    override fun decrypt(secretKey: ByteArray?, algorithm: CodecAlgorithm): Codec {
        when (algorithm.type()) {
            CodecAlgorithm.Type.SYMMETRIC -> {
                val symmetricCipher: SymmetricCipher<SecretKey?> = Codec.Companion.symmetricCipher(algorithm)
                data = symmetricCipher.decrypt(secretKey, data)
            }
            CodecAlgorithm.Type.ASYMMETRIC -> when (algorithm.name()) {
                CodecAlgorithmConstants.RSA -> {
                    val rsaCipher: RsaCipher = Codec.Companion.rsaCipher()
                    data = rsaCipher.decrypt(secretKey, data)
                }
                CodecAlgorithmConstants.SM2 -> {
                    val sm2Cipher: Sm2Cipher = Codec.Companion.sm2Cipher()
                    data = sm2Cipher.decrypt(secretKey, data)
                }
            }
            else -> throw IllegalArgumentException(
                "Algorithm should be symmetric or RSA or SM2: $algorithm"
            )
        }
        return this
    }

    override fun decrypt(secretKey: Any, algorithm: CodecAlgorithm): Codec {
        if (secretKey is RSAPrivateKey
            && CodecAlgorithm.Type.ASYMMETRIC == algorithm.type() && CodecAlgorithmConstants.RSA == algorithm.name()
        ) {
            val rsaCipher: RsaCipher = Codec.Companion.rsaCipher()
            data = rsaCipher.decrypt(secretKey, data)
            return this
        }
        if (secretKey is BigInteger
            && CodecAlgorithm.Type.ASYMMETRIC == algorithm.type() && CodecAlgorithmConstants.SM2 == algorithm.name()
        ) {
            val sm2Cipher: Sm2Cipher = Codec.Companion.sm2Cipher()
            data = sm2Cipher.decrypt(secretKey, data)
            return this
        }
        if (secretKey is SecretKey) {
            return decrypt(secretKey as SecretKey, algorithm)
        }
        if (secretKey is ByteArray) {
            return decrypt(secretKey, algorithm)
        }
        throw IllegalArgumentException(
            "Unknown secret key: $secretKey"
        )
    }

    override fun digest(algorithm: CodecAlgorithm?): Codec? {
        val digestCipher: DigestCipher = Codec.Companion.digestCipher(algorithm)
        data = digestCipher.digest(data)
        return this
    }

    override fun hmacDigest(secretKey: SecretKey?, algorithm: CodecAlgorithm?): Codec {
        val hmacDigestCipher: HmacDigestCipher<SecretKey?> = Codec.Companion.hmacDigestCipher(algorithm)
        data = hmacDigestCipher.digest(secretKey, data)
        return this
    }

    override fun hmacDigest(secretKey: ByteArray?, algorithm: CodecAlgorithm?): Codec {
        val hmacDigestCipher: HmacDigestCipher<SecretKey?> = Codec.Companion.hmacDigestCipher(algorithm)
        data = hmacDigestCipher.digest(secretKey, data)
        return this
    }

    override fun hmacDigest(secretKey: Any?, algorithm: CodecAlgorithm?): Codec? {
        throw UnsupportedOperationException(
            "Use hmacDigest(SecretKey, algorithm) or hmacDigest(byte[] secretKey, algorithm)"
        )
    }

    override fun doFinal(): ByteArray? {
        return data!!.clone()
    }
}