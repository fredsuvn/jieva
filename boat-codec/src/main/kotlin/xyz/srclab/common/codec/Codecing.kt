package xyz.srclab.common.codec

import xyz.srclab.common.cache.Cache
import xyz.srclab.common.cache.Cache.Companion.toCache
import xyz.srclab.common.lang.toBytes
import xyz.srclab.common.lang.toChars
import java.io.OutputStream
import java.util.*
import javax.crypto.SecretKey

/**
 * Codec processing for chain operation.
 *
 * @see EncodeCodec
 * @see DigestCodec
 * @see MacCodec
 * @see CipherCodec
 * @see AsymmetricCipherCodec
 */
interface Codecing {

    fun encode(algorithm: CodecAlgorithm): Codecing

    fun encode(algorithm: CharSequence): Codecing {
        return encode(algorithm.toCodecAlgorithm())
    }

    fun decode(algorithm: CodecAlgorithm): Codecing

    fun decode(algorithm: CharSequence): Codecing {
        return decode(algorithm.toCodecAlgorithm())
    }

    fun digest(algorithm: CodecAlgorithm): Codecing

    fun digest(algorithm: CharSequence): Codecing {
        return digest(algorithm.toCodecAlgorithm())
    }

    fun digest(algorithm: CodecAlgorithm, key: Any): Codecing

    fun digest(algorithm: CharSequence, key: Any): Codecing {
        return digest(algorithm.toCodecAlgorithm(), key)
    }

    fun encrypt(algorithm: CodecAlgorithm, key: Any): Codecing

    fun encrypt(algorithm: CharSequence, key: Any): Codecing {
        return encrypt(algorithm.toCodecAlgorithm(), key)
    }

    fun decrypt(algorithm: CodecAlgorithm, key: Any): Codecing

    fun decrypt(algorithm: CharSequence, key: Any): Codecing {
        return decrypt(algorithm.toCodecAlgorithm(), key)
    }

    fun doFinal(): ByteArray

    fun doFinal(output: OutputStream)

    @JvmDefault
    fun doFinalString(): String {
        return doFinal().toChars()
    }

    @JvmDefault
    fun encodeHex(): Codecing {
        return encode(CodecAlgorithm.HEX)
    }

    @JvmDefault
    fun decodeHex(): Codecing {
        return decode(CodecAlgorithm.HEX)
    }

    @JvmDefault
    fun encodeBase64(): Codecing {
        return encode(CodecAlgorithm.BASE64)
    }

    @JvmDefault
    fun decodeBase64(): Codecing {
        return decode(CodecAlgorithm.BASE64)
    }

    @JvmDefault
    fun encryptAes(key: Any): Codecing {
        return encrypt(CodecAlgorithm.AES, key)
    }

    @JvmDefault
    fun decryptAes(key: Any): Codecing {
        return decrypt(CodecAlgorithm.AES, key)
    }

    @JvmDefault
    fun digestMd2(): Codecing {
        return digest(CodecAlgorithm.MD2)
    }

    @JvmDefault
    fun digestMd5(): Codecing {
        return digest(CodecAlgorithm.MD5)
    }

    @JvmDefault
    fun digestSha1(): Codecing {
        return digest(CodecAlgorithm.SHA1)
    }

    @JvmDefault
    fun digestSha256(): Codecing {
        return digest(CodecAlgorithm.SHA256)
    }

    @JvmDefault
    fun digestSha384(): Codecing {
        return digest(CodecAlgorithm.SHA384)
    }

    @JvmDefault
    fun digestSha512(): Codecing {
        return digest(CodecAlgorithm.SHA512)
    }

    @JvmDefault
    fun hmacDigestMd5(key: Any): Codecing {
        return digest(CodecAlgorithm.HMAC_MD5, key)
    }

    @JvmDefault
    fun hmacDigestSha1(key: SecretKey): Codecing {
        return digest(CodecAlgorithm.HMAC_SHA1, key)
    }

    @JvmDefault
    fun hmacDigestSha256(key: SecretKey): Codecing {
        return digest(CodecAlgorithm.HMAC_SHA256, key)
    }

    @JvmDefault
    fun hmacDigestSha384(key: CharSequence): Codecing {
        return digest(CodecAlgorithm.HMAC_SHA384, key)
    }

    @JvmDefault
    fun hmacDigestSha512(key: CharSequence): Codecing {
        return digest(CodecAlgorithm.HMAC_SHA512, key)
    }

    @JvmDefault
    fun encryptRsa(publicKey: Any): Codecing {
        return encrypt(CodecAlgorithm.RSA, publicKey)
    }

    @JvmDefault
    fun decryptRsa(privateKey: Any): Codecing {
        return decrypt(CodecAlgorithm.RSA, privateKey)
    }

    @JvmDefault
    fun encryptSm2(publicKey: Any): Codecing {
        return encrypt(CodecAlgorithm.SM2, publicKey)
    }

    @JvmDefault
    fun decryptSm2(privateKey: Any): Codecing {
        return decrypt(CodecAlgorithm.SM2, privateKey)
    }

    interface CodecSupplier {

        fun get(algorithm: CodecAlgorithm): Codec
    }

    companion object {

        @JvmField
        val DEFAULT_CODEC_SUPPLIER: CodecSupplier = object : CodecSupplier {
            override fun get(algorithm: CodecAlgorithm): Codec {
                val cipher = when (algorithm) {
                    CodecAlgorithm.HEX -> HexCodec
                    CodecAlgorithm.BASE64 -> Base64Codec
                    CodecAlgorithm.RSA -> rsaCodec()
                    CodecAlgorithm.SM2 -> sm2Codec()
                    CodecAlgorithm.PLAIN -> PlainCodec
                    else -> null
                }
                if (cipher !== null) {
                    return cipher
                }
                return when (algorithm.type) {
                    CodecAlgorithmType.DIGEST -> algorithm.toDigestCodec()
                    CodecAlgorithmType.CIPHER -> algorithm.toCipherCodec()
                    else -> throw UnsupportedOperationException("Unsupported algorithm: $algorithm")
                }
            }
        }

        @JvmName("forData")
        @JvmOverloads
        @JvmStatic
        fun ByteArray.startCodec(codecSupplier: CodecSupplier = DEFAULT_CODEC_SUPPLIER): Codecing {
            return CodecingImpl(this, codecSupplier)
        }

        @JvmName("forData")
        @JvmOverloads
        @JvmStatic
        fun CharSequence.startCodec(codecSupplier: CodecSupplier = DEFAULT_CODEC_SUPPLIER): Codecing {
            return this.toBytes().startCodec(codecSupplier)
        }

        private class CodecingImpl(
            initData: ByteArray,
            private val codecSupplier: CodecSupplier = DEFAULT_CODEC_SUPPLIER
        ) : Codecing {

            private var data: ByteArray = initData

            private val codecCache: Cache<CodecAlgorithm, Codec> = WeakHashMap<CodecAlgorithm, Codec>().toCache()

            private fun getCodec(algorithm: CodecAlgorithm): Codec {
                return codecCache.getOrLoad(algorithm) { codecSupplier.get(algorithm) }
            }

            override fun encode(algorithm: CodecAlgorithm): Codecing {
                val codec = getCodec(algorithm)
                data = when (codec) {
                    is EncodeCodec -> codec.encode(data)
                    is DigestCodec -> codec.digest(data)
                    else -> throw UnsupportedOperationException("Unsupported encode or digest algorithm: $algorithm")
                }
                return this
            }

            override fun decode(algorithm: CodecAlgorithm): Codecing {
                val codec = getCodec(algorithm)
                data = when (codec) {
                    is EncodeCodec -> codec.decode(data)
                    else -> throw UnsupportedOperationException("Unsupported decode algorithm: $algorithm")
                }
                return this
            }

            override fun digest(algorithm: CodecAlgorithm): Codecing {
                val codec = getCodec(algorithm)
                data = when (codec) {
                    is DigestCodec -> codec.digest(data)
                    else -> throw UnsupportedOperationException("Unsupported digest algorithm: $algorithm")
                }
                return this
            }

            override fun digest(algorithm: CodecAlgorithm, key: Any): Codecing {
                val codec = getCodec(algorithm)
                data = when (codec) {
                    is MacCodec -> codec.digest(key, data)
                    else -> throw UnsupportedOperationException("Unsupported MAC digest algorithm: $algorithm")
                }
                return this
            }

            override fun encrypt(algorithm: CodecAlgorithm, key: Any): Codecing {
                val codec = getCodec(algorithm)
                data = when (codec) {
                    is CipherCodec -> codec.encrypt(key, data)
                    else -> throw UnsupportedOperationException("Unsupported cipher encrypt: $algorithm")
                }
                return this
            }

            override fun decrypt(algorithm: CodecAlgorithm, key: Any): Codecing {
                val codec = getCodec(algorithm)
                data = when (codec) {
                    is CipherCodec -> codec.decrypt(key, data)
                    else -> throw UnsupportedOperationException("Unsupported cipher decrypt: $algorithm")
                }
                return this
            }

            override fun doFinal(): ByteArray {
                return data.clone()
            }

            override fun doFinal(output: OutputStream) {
                output.write(data)
            }

            override fun encodeHex(): Codecing {
                data = HexCodec.encode(data)
                return this
            }

            override fun decodeHex(): Codecing {
                data = HexCodec.decode(data)
                return this
            }

            override fun encodeBase64(): Codecing {
                data = Base64Codec.encode(data)
                return this
            }

            override fun decodeBase64(): Codecing {
                data = Base64Codec.decode(data)
                return this
            }
        }
    }
}