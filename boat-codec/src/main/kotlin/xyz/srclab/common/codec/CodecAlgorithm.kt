package xyz.srclab.common.codec

import xyz.srclab.common.base.INAPPLICABLE_JVM_NAME

/**
 * codec algorithm.
 *
 * @author sunqian
 */
interface CodecAlgorithm {

    @Suppress(INAPPLICABLE_JVM_NAME)
    val name: String
        @JvmName("name") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    val type: CodecAlgorithmType
        @JvmName("type") get

    companion object {

        @JvmField
        val PLAIN_NAME = "PLAIN"

        @JvmField
        val HEX_NAME = "HEX"

        @JvmField
        val BASE64_NAME = "BASE64"

        @JvmField
        val AES_NAME = "AES"

        @JvmField
        val MD2_NAME = "MD2"

        @JvmField
        val MD5_NAME = "MD5"

        @JvmField
        val SHA1_NAME = "SHA-1"

        @JvmField
        val SHA256_NAME = "SHA-256"

        @JvmField
        val SHA384_NAME = "SHA-384"

        @JvmField
        val SHA512_NAME = "SHA-512"

        @JvmField
        val HMAC_MD5_NAME = "HmacMD5"

        @JvmField
        val HMAC_SHA1_NAME = "HmacSHA1"

        @JvmField
        val HMAC_SHA256_NAME = "HmacSHA256"

        @JvmField
        val HMAC_SHA384_NAME = "HmacSHA384"

        @JvmField
        val HMAC_SHA512_NAME = "HmacSHA512"

        @JvmField
        val RSA_NAME = "RSA"

        @JvmField
        val SM2_NAME = "SM2"

        @JvmField
        val SHA1PRNG_NAME = "SHA1PRNG"

        @JvmField
        val PLAIN = newAlgorithm(PLAIN_NAME, CodecAlgorithmType.ENCODE)

        @JvmField
        val HEX = newAlgorithm(HEX_NAME, CodecAlgorithmType.ENCODE)

        @JvmField
        val BASE64 = newAlgorithm(BASE64_NAME, CodecAlgorithmType.ENCODE)

        @JvmField
        val AES = newAlgorithm(AES_NAME, CodecAlgorithmType.SYMMETRIC)

        @JvmField
        val MD2 = newAlgorithm(MD2_NAME, CodecAlgorithmType.DIGEST)

        @JvmField
        val MD5 = newAlgorithm(MD5_NAME, CodecAlgorithmType.DIGEST)

        @JvmField
        val SHA1 = newAlgorithm(SHA1_NAME, CodecAlgorithmType.DIGEST)

        @JvmField
        val SHA256 = newAlgorithm(SHA256_NAME, CodecAlgorithmType.DIGEST)

        @JvmField
        val SHA384 = newAlgorithm(SHA384_NAME, CodecAlgorithmType.DIGEST)

        @JvmField
        val SHA512 = newAlgorithm(SHA512_NAME, CodecAlgorithmType.DIGEST)

        @JvmField
        val HMAC_MD5 = newAlgorithm(HMAC_MD5_NAME, CodecAlgorithmType.HMAC)

        @JvmField
        val HMAC_SHA1 = newAlgorithm(HMAC_SHA1_NAME, CodecAlgorithmType.HMAC)

        @JvmField
        val HMAC_SHA256 = newAlgorithm(HMAC_SHA256_NAME, CodecAlgorithmType.HMAC)

        @JvmField
        val HMAC_SHA384 = newAlgorithm(HMAC_SHA384_NAME, CodecAlgorithmType.HMAC)

        @JvmField
        val HMAC_SHA512 = newAlgorithm(HMAC_SHA512_NAME, CodecAlgorithmType.HMAC)

        @JvmField
        val RSA = newAlgorithm(RSA_NAME, CodecAlgorithmType.ASYMMETRIC)

        @JvmField
        val SM2 = newAlgorithm(SM2_NAME, CodecAlgorithmType.ASYMMETRIC)

        @JvmStatic
        fun forName(algorithmName: String): CodecAlgorithm {
            return forNameOrNull(algorithmName)
                ?: throw IllegalArgumentException("Codec algorithm not found: $algorithmName.")
        }

        @JvmStatic
        fun forName(algorithmName: String, algorithmType: CodecAlgorithmType): CodecAlgorithm {
            val pre = forNameOrNull(algorithmName)
            if (pre !== null && pre.type == algorithmType) {
                return pre
            }
            return newAlgorithm(algorithmName, algorithmType)
        }

        private fun forNameOrNull(algorithmName: String): CodecAlgorithm? {
            return when (algorithmName) {
                PLAIN_NAME -> PLAIN
                HEX_NAME -> HEX
                BASE64_NAME -> BASE64
                AES_NAME -> AES
                MD2_NAME -> MD2
                MD5_NAME -> MD5
                SHA1_NAME -> SHA1
                SHA256_NAME -> SHA256
                SHA384_NAME -> SHA384
                SHA512_NAME -> SHA512
                HMAC_MD5_NAME -> HMAC_MD5
                HMAC_SHA1_NAME -> HMAC_SHA1
                HMAC_SHA256_NAME -> HMAC_SHA256
                HMAC_SHA384_NAME -> HMAC_SHA384
                HMAC_SHA512_NAME -> HMAC_SHA512
                RSA_NAME -> RSA
                SM2_NAME -> SM2
                else -> null
            }
        }

        @JvmStatic
        fun newAlgorithm(name: String, type: CodecAlgorithmType): CodecAlgorithm {
            return object : CodecAlgorithm {
                override val name: String = name
                override val type: CodecAlgorithmType = type
            }
        }
    }
}

enum class CodecAlgorithmType {
    SYMMETRIC, DIGEST, HMAC, ASYMMETRIC, ENCODE
}