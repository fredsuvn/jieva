package xyz.srclab.common.codec

/**
 * Represents codec algorithm.
 */
open class CodecAlgorithm(
    val name: String,
    val type: CodecAlgorithmType
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CodecAlgorithm) return false
        if (name != other.name) return false
        if (type != other.type) return false
        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + type.hashCode()
        return result
    }

    override fun toString(): String {
        return "$name[$type]"
    }

    companion object {

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
        val SM3_NAME = "SM3"

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
        val AES_NAME = "AES"

        @JvmField
        val RSA_NAME = "RSA"

        @JvmField
        val SM2_NAME = "SM2"

        @JvmField
        val SM4_NAME = "SM4"

        @JvmField
        val SHA1PRNG_NAME = "SHA1PRNG"

        @JvmField
        val SHA1_WITH_RSA_NAME = "SHA1WithRSA"

        @JvmField
        val SHA256_WITH_RSA_NAME = "SHA256WithRSA"

        @JvmField
        val SHA256_WITH_SM2_NAME = "SHA256WithSM2"

        @JvmField
        val SM3_WITH_SM2_NAME = "SM3WithSM2"

        @JvmField
        val MD2 = CodecAlgorithm(MD2_NAME, CodecAlgorithmType.DIGEST)

        @JvmField
        val MD5 = CodecAlgorithm(MD5_NAME, CodecAlgorithmType.DIGEST)

        @JvmField
        val SHA1 = CodecAlgorithm(SHA1_NAME, CodecAlgorithmType.DIGEST)

        @JvmField
        val SHA256 = CodecAlgorithm(SHA256_NAME, CodecAlgorithmType.DIGEST)

        @JvmField
        val SHA384 = CodecAlgorithm(SHA384_NAME, CodecAlgorithmType.DIGEST)

        @JvmField
        val SHA512 = CodecAlgorithm(SHA512_NAME, CodecAlgorithmType.DIGEST)

        @JvmField
        val SM3 = CodecAlgorithm(SM3_NAME, CodecAlgorithmType.DIGEST)

        @JvmField
        val HMAC_MD5 = CodecAlgorithm(HMAC_MD5_NAME, CodecAlgorithmType.HMAC)

        @JvmField
        val HMAC_SHA1 = CodecAlgorithm(HMAC_SHA1_NAME, CodecAlgorithmType.HMAC)

        @JvmField
        val HMAC_SHA256 = CodecAlgorithm(HMAC_SHA256_NAME, CodecAlgorithmType.HMAC)

        @JvmField
        val HMAC_SHA384 = CodecAlgorithm(HMAC_SHA384_NAME, CodecAlgorithmType.HMAC)

        @JvmField
        val HMAC_SHA512 = CodecAlgorithm(HMAC_SHA512_NAME, CodecAlgorithmType.HMAC)

        @JvmField
        val AES = CodecAlgorithm(AES_NAME, CodecAlgorithmType.CIPHER)

        @JvmField
        val RSA = CodecAlgorithm(RSA_NAME, CodecAlgorithmType.CIPHER)

        @JvmField
        val SM2 = CodecAlgorithm(SM2_NAME, CodecAlgorithmType.CIPHER)

        @JvmField
        val SM4 = CodecAlgorithm(SM4_NAME, CodecAlgorithmType.CIPHER)

        @JvmField
        val SHA1_WITH_RSA = CodecAlgorithm(SHA1_WITH_RSA_NAME, CodecAlgorithmType.SIGN)

        @JvmField
        val SHA256_WITH_RSA = CodecAlgorithm(SHA256_WITH_RSA_NAME, CodecAlgorithmType.SIGN)

        @JvmField
        val SHA256_WITH_SM2 = CodecAlgorithm(SHA256_WITH_SM2_NAME, CodecAlgorithmType.SIGN)

        @JvmField
        val SM3_WITH_SM2 = CodecAlgorithm(SM3_WITH_SM2_NAME, CodecAlgorithmType.SIGN)

        @JvmStatic
        fun CharSequence.toCodecAlgorithm(): CodecAlgorithm {
            return this.searchPrepared()
                ?: throw IllegalArgumentException("Codec algorithm not found: $this.")
        }

        @JvmStatic
        fun CharSequence.toCodecAlgorithm(algorithmType: CodecAlgorithmType): CodecAlgorithm {
            val result = this.searchPrepared()
            if (result !== null && result.type == algorithmType) {
                return result
            }
            return CodecAlgorithm(this.toString(), algorithmType)
        }

        private fun CharSequence.searchPrepared(): CodecAlgorithm? {
            return when (this.toString()) {
                MD2_NAME -> MD2
                MD5_NAME -> MD5
                SHA1_NAME -> SHA1
                SHA256_NAME -> SHA256
                SHA384_NAME -> SHA384
                SHA512_NAME -> SHA512
                SM3_NAME -> SM3
                HMAC_MD5_NAME -> HMAC_MD5
                HMAC_SHA1_NAME -> HMAC_SHA1
                HMAC_SHA256_NAME -> HMAC_SHA256
                HMAC_SHA384_NAME -> HMAC_SHA384
                HMAC_SHA512_NAME -> HMAC_SHA512
                AES_NAME -> AES
                RSA_NAME -> RSA
                SM2_NAME -> SM2
                SM4_NAME -> SM4
                SHA1_WITH_RSA_NAME -> SHA1_WITH_RSA
                SHA256_WITH_RSA_NAME -> SHA256_WITH_RSA
                SHA256_WITH_SM2_NAME -> SHA256_WITH_SM2
                SM3_WITH_SM2_NAME -> SM3_WITH_SM2
                else -> null
            }
        }
    }
}

/**
 * Algorithm type.
 */
enum class CodecAlgorithmType {
    DIGEST, HMAC, CIPHER, SIGN,
}