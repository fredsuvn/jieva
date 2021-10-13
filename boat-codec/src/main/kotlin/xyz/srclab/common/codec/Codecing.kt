package xyz.srclab.common.codec

import xyz.srclab.common.cache.Cache
import xyz.srclab.common.codec.Codec.Companion.toCodecAlgorithm
import xyz.srclab.common.lang.toChars
import java.io.OutputStream
import javax.crypto.SecretKey

/**
 * Codec processing supports chain operation.
 *
 * Note this class **may not thread-safe**.
 *
 * @see Encoder
 * @see DigestCodec
 * @see MacCodec
 * @see CipherCodec
 * @see AsymmetricCipherCodec
 */
abstract class Codecing(
    protected open var data: ByteArray
) {

    protected abstract fun getCodec(algorithm: CodecAlgorithm): Codec

    open fun encode(algorithm: CodecAlgorithm): Codecing {
        val codec = getCodec(algorithm)
        data = when (codec) {
            is Encoder -> codec.encode(data)
            is DigestCodec -> codec.digest(data)
            else -> throw UnsupportedOperationException("Unsupported encode or digest algorithm: $algorithm")
        }
        return this
    }

    open fun encode(algorithm: CharSequence): Codecing {
        return encode(algorithm.toCodecAlgorithm())
    }

    open fun decode(algorithm: CodecAlgorithm): Codecing {
        val codec = getCodec(algorithm)
        data = when (codec) {
            is Encoder -> codec.decode(data)
            else -> throw UnsupportedOperationException("Unsupported decode algorithm: $algorithm")
        }
        return this
    }

    open fun decode(algorithm: CharSequence): Codecing {
        return decode(algorithm.toCodecAlgorithm())
    }

    open fun digest(algorithm: CodecAlgorithm): Codecing {
        val codec = getCodec(algorithm)
        data = when (codec) {
            is DigestCodec -> codec.digest(data)
            else -> throw UnsupportedOperationException("Unsupported digest algorithm: $algorithm")
        }
        return this
    }

    open fun digest(algorithm: CharSequence): Codecing {
        return digest(algorithm.toCodecAlgorithm())
    }

    open fun digest(algorithm: CodecAlgorithm, key: Any): Codecing {
        val codec = getCodec(algorithm)
        data = when (codec) {
            is MacCodec -> codec.digest(key, data)
            else -> throw UnsupportedOperationException("Unsupported MAC digest algorithm: $algorithm")
        }
        return this
    }

    open fun digest(algorithm: CharSequence, key: Any): Codecing {
        return digest(algorithm.toCodecAlgorithm(), key)
    }

    open fun encrypt(algorithm: CodecAlgorithm, key: Any): Codecing {
        val codec = getCodec(algorithm)
        data = when (codec) {
            is CipherCodec -> codec.encrypt(key, data)
            else -> throw UnsupportedOperationException("Unsupported cipher encrypt: $algorithm")
        }
        return this
    }

    open fun encrypt(algorithm: CharSequence, key: Any): Codecing {
        return encrypt(algorithm.toCodecAlgorithm(), key)
    }

    open fun decrypt(algorithm: CodecAlgorithm, key: Any): Codecing {
        val codec = getCodec(algorithm)
        data = when (codec) {
            is CipherCodec -> codec.decrypt(key, data)
            else -> throw UnsupportedOperationException("Unsupported cipher decrypt: $algorithm")
        }
        return this
    }

    open fun decrypt(algorithm: CharSequence, key: Any): Codecing {
        return decrypt(algorithm.toCodecAlgorithm(), key)
    }

    open fun doFinal(): ByteArray {
        return data.clone()
    }

    open fun doFinal(output: OutputStream) {
        output.write(data)
    }

    open fun doFinalString(): String {
        return doFinal().toChars()
    }

    open fun encodeHex(): Codecing {
        data = HexCodec.encode(data)
        return this
    }

    open fun decodeHex(): Codecing {
        data = HexCodec.decode(data)
        return this
    }

    open fun encodeBase64(): Codecing {
        data = Base64Codec.encode(data)
        return this
    }

    open fun decodeBase64(): Codecing {
        data = Base64Codec.decode(data)
        return this
    }

    open fun encryptAes(key: Any): Codecing {
        return encrypt(CodecAlgorithm.AES, key)
    }

    open fun decryptAes(key: Any): Codecing {
        return decrypt(CodecAlgorithm.AES, key)
    }

    open fun digestMd2(): Codecing {
        return digest(CodecAlgorithm.MD2)
    }

    open fun digestMd5(): Codecing {
        return digest(CodecAlgorithm.MD5)
    }

    open fun digestSha1(): Codecing {
        return digest(CodecAlgorithm.SHA1)
    }

    open fun digestSha256(): Codecing {
        return digest(CodecAlgorithm.SHA256)
    }

    open fun digestSha384(): Codecing {
        return digest(CodecAlgorithm.SHA384)
    }

    open fun digestSha512(): Codecing {
        return digest(CodecAlgorithm.SHA512)
    }

    open fun hmacDigestMd5(key: Any): Codecing {
        return digest(CodecAlgorithm.HMAC_MD5, key)
    }

    open fun hmacDigestSha1(key: SecretKey): Codecing {
        return digest(CodecAlgorithm.HMAC_SHA1, key)
    }

    open fun hmacDigestSha256(key: SecretKey): Codecing {
        return digest(CodecAlgorithm.HMAC_SHA256, key)
    }

    open fun hmacDigestSha384(key: CharSequence): Codecing {
        return digest(CodecAlgorithm.HMAC_SHA384, key)
    }

    open fun hmacDigestSha512(key: CharSequence): Codecing {
        return digest(CodecAlgorithm.HMAC_SHA512, key)
    }

    open fun encryptRsa(publicKey: Any): Codecing {
        return encrypt(CodecAlgorithm.RSA, publicKey)
    }

    open fun decryptRsa(privateKey: Any): Codecing {
        return decrypt(CodecAlgorithm.RSA, privateKey)
    }

    open fun encryptSm2(publicKey: Any): Codecing {
        return encrypt(CodecAlgorithm.SM2, publicKey)
    }

    open fun decryptSm2(privateKey: Any): Codecing {
        return decrypt(CodecAlgorithm.SM2, privateKey)
    }

    companion object {

        @JvmStatic
        fun newCodecing(
            data: ByteArray,
            codecSupplier: (CodecAlgorithm) -> Codec
        ): Codecing {
            return CodecingImpl(data, codecSupplier)
        }

        private class CodecingImpl(
            override var data: ByteArray,
            private val codecSupplier: (CodecAlgorithm) -> Codec
        ) : Codecing(data) {

            val codecCache: Cache<CodecAlgorithm, Codec> = Cache.newFastCache()

            override fun getCodec(algorithm: CodecAlgorithm): Codec {
                return codecCache.getOrLoad(algorithm) { codecSupplier(algorithm) }
            }
        }
    }
}