package xyz.srclab.common.codec

import xyz.srclab.common.codec.rsa.RsaCodec
import xyz.srclab.common.codec.sm2.Sm2Codec
import xyz.srclab.common.codec.sm2.Sm2Params
import xyz.srclab.common.lang.toBytes
import java.security.Key
import java.security.SecureRandom
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

/**
 * Codec interface, represents a type of codec way.
 *
 * Note [Codec] **may not thread-safe**.
 *
 * @see EncCodec
 * @see Digester
 * @see MacCodec
 * @see CipherCodec
 * @see CodecAlgorithm
 */
interface Codec {

    val algorithm: String

    companion object {

        /**
         * Default [Codec] supplier function, supports:
         *
         * * [CodecAlgorithm.HEX]
         * * [CodecAlgorithm.BASE64]
         * * [CodecAlgorithm.RSA]
         * * [CodecAlgorithm.SM2]
         * * [CodecAlgorithm.HEX]
         * * [CodecAlgorithm.PLAIN]
         * * [CodecAlgorithmType.DIGEST]
         * * [CodecAlgorithmType.CIPHER]
         */
        @JvmField
        val DEFAULT_CODEC_SUPPLIER: (CodecAlgorithm) -> Codec = codec@{
            val cipher = when (it) {
                CodecAlgorithm.HEX -> HexCodec
                CodecAlgorithm.BASE64 -> Base64Codec
                CodecAlgorithm.RSA -> rsaCodec()
                CodecAlgorithm.SM2 -> sm2Codec()
                CodecAlgorithm.PLAIN -> PlainCodec
                else -> null
            }
            if (cipher !== null) {
                return@codec cipher
            }
            when (it.type) {
                CodecAlgorithmType.DIGEST -> it.toDigestCodec()
                CodecAlgorithmType.CIPHER -> it.toCipherCodec()
                else -> throw UnsupportedOperationException("Unsupported algorithm: $it")
            }
        }

        @JvmName("forData")
        @JvmOverloads
        @JvmStatic
        fun ByteArray.codec(codecSupplier: (CodecAlgorithm) -> Codec = DEFAULT_CODEC_SUPPLIER): Codecing {
            return Codecing.newCodecing(this, codecSupplier)
        }

        @JvmName("forData")
        @JvmOverloads
        @JvmStatic
        fun CharSequence.codec(codecSupplier: (CodecAlgorithm) -> Codec = DEFAULT_CODEC_SUPPLIER): Codecing {
            return this.toBytes().codec(codecSupplier)
        }

        @JvmName("secretKey")
        @JvmStatic
        fun ByteArray.toSecretKey(algorithm: String): SecretKey {
            return SecretKeySpec(this, algorithm)
        }

        @JvmName("secretKey")
        @JvmStatic
        fun CharSequence.toSecretKey(algorithm: String): SecretKey {
            return this.toBytes().toSecretKey(algorithm)
        }

        @JvmName("secretKey")
        @JvmStatic
        fun Any.toSecretKey(algorithm: String): SecretKey {
            return when (this) {
                is SecretKey -> this
                is ByteArray -> toSecretKey(algorithm)
                is CharSequence -> toSecretKey(algorithm)
                else -> throw UnsupportedOperationException("Unsupported key: $this")
            }
        }

        @JvmName("codecKey")
        @JvmStatic
        fun Any.toCodecKey(algorithm: String): Key {
            if (this is Key) {
                return this
            }
            return this.toSecretKey(algorithm)
        }

        /**
         * Create a new [SecretKey] with [algorithm] and min key size ([minKeySize]).
         * If [minKeySize] <= 0, return simply like:
         *
         * ```
         * return new SecretKeySpec(bytes, algorithm)
         * ```
         */
        @JvmName("secretKey")
        @JvmStatic
        fun ByteArray.toSecretKey(algorithm: String, minKeySize: Int): SecretKey {
            if (minKeySize <= 0) {
                return SecretKeySpec(this, algorithm)
            }
            val random = SecureRandom.getInstance(CodecAlgorithm.SHA1PRNG_NAME)
            random.setSeed(this)
            val keyGenerator = KeyGenerator.getInstance(algorithm)
            keyGenerator.init(minKeySize, random)
            val secretKey = keyGenerator.generateKey()
            return SecretKeySpec(secretKey.encoded, algorithm)
        }

        @JvmName("codecAlgorithm")
        @JvmStatic
        fun CharSequence.toCodecAlgorithm(): CodecAlgorithm {
            return CodecAlgorithm.forName(this)
        }

        @JvmName("codecAlgorithm")
        @JvmStatic
        fun CharSequence.toCodecAlgorithm(type: CodecAlgorithmType): CodecAlgorithm {
            return CodecAlgorithm.forName(this, type)
        }

        @JvmName("encodeCodec")
        @JvmStatic
        fun CharSequence.toEncodeCodec(): EncCodec {
            return EncCodec.withAlgorithm(this)
        }

        @JvmName("encodeCodec")
        @JvmStatic
        fun CodecAlgorithm.toEncodeCodec(): EncCodec {
            return EncCodec.withAlgorithm(this)
        }

        @JvmName("digestCodec")
        @JvmStatic
        fun CharSequence.toDigestCodec(): Digester {
            return Digester.withAlgorithm(this)
        }

        @JvmName("digestCodec")
        @JvmStatic
        fun CodecAlgorithm.toDigestCodec(): Digester {
            return Digester.withAlgorithm(this.name)
        }

        @JvmName("macCodec")
        @JvmStatic
        fun CharSequence.toMacCodec(): MacCodec {
            return MacCodec.withAlgorithm(this)
        }

        @JvmName("macCodec")
        @JvmStatic
        fun CodecAlgorithm.toMacCodec(): MacCodec {
            return MacCodec.withAlgorithm(this.name)
        }

        @JvmName("cipherCodec")
        @JvmStatic
        fun CharSequence.toCipherCodec(): CipherCodec {
            return CipherCodec.withAlgorithm(this)
        }

        @JvmName("cipherCodec")
        @JvmStatic
        fun CodecAlgorithm.toCipherCodec(): CipherCodec {
            return CipherCodec.withAlgorithm(this.name)
        }

        @JvmStatic
        fun plainCodec(): EncCodec {
            return EncCodec.plain()
        }

        @JvmStatic
        fun hexCodec(): EncCodec {
            return EncCodec.hex()
        }

        @JvmStatic
        fun base64Codec(): EncCodec {
            return EncCodec.base64()
        }

        @JvmStatic
        fun md2Codec(): Digester {
            return Digester.md2()
        }

        @JvmStatic
        fun md5Codec(): Digester {
            return Digester.md5()
        }

        @JvmStatic
        fun sha1Codec(): Digester {
            return Digester.sha1()
        }

        @JvmStatic
        fun sha256Codec(): Digester {
            return Digester.sha256()
        }

        @JvmStatic
        fun sha384Codec(): Digester {
            return Digester.sha384()
        }

        @JvmStatic
        fun sha512Codec(): Digester {
            return Digester.sha512()
        }

        @JvmStatic
        fun hmacMd5Codec(): MacCodec {
            return MacCodec.hmacMd5()
        }

        @JvmStatic
        fun hmacSha1Codec(): MacCodec {
            return MacCodec.hmacSha1()
        }

        @JvmStatic
        fun hmacSha256Codec(): MacCodec {
            return MacCodec.hmacSha256()
        }

        @JvmStatic
        fun hmacSha384Codec(): MacCodec {
            return MacCodec.hmacSha384()
        }

        @JvmStatic
        fun hmacSha512Codec(): MacCodec {
            return MacCodec.hmacSha512()
        }

        @JvmStatic
        fun aesCodec(): CipherCodec {
            return CipherCodec.aes()
        }

        @JvmStatic
        fun rsaCodec(): RsaCodec {
            return CipherCodec.rsa()
        }

        @JvmStatic
        fun rsaCodec(
            encryptBlockSize: Int,
            decryptBlockSize: Int
        ): RsaCodec {
            return CipherCodec.rsa(encryptBlockSize, decryptBlockSize)
        }

        @JvmOverloads
        @JvmStatic
        fun sm2Codec(sm2Params: Sm2Params = Sm2Params.DEFAULT): Sm2Codec {
            return CipherCodec.sm2(sm2Params)
        }

        @JvmName("hexString")
        @JvmStatic
        fun ByteArray.toHexString(): String {
            return hexCodec().encodeToString(this)
        }

        @JvmName("hexString")
        @JvmStatic
        fun CharSequence.toHexString(): String {
            return hexCodec().encodeToString(this)
        }

        @JvmName("base64String")
        @JvmStatic
        fun ByteArray.toBase64String(): String {
            return base64Codec().encodeToString(this)
        }

        @JvmName("base64String")
        @JvmStatic
        fun CharSequence.toBase64String(): String {
            return base64Codec().encodeToString(this)
        }
    }
}