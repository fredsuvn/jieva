package xyz.srclab.common.codec

/**
 * Represents codec algorithm.
 */
interface CodecAlgorithm {

    val name: String

    val type: CodecAlgorithmType

    companion object {

        //@JvmField
        //val PLAIN_NAME = "PLAIN"
        //
        //@JvmField
        //val HEX_NAME = "HEX"
        //
        //@JvmField
        //val BASE64_NAME = "BASE64"

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
        val AES_NAME = "AES"

        @JvmField
        val RSA_NAME = "RSA"

        @JvmField
        val SM2_NAME = "SM2"

        @JvmField
        val SHA1PRNG_NAME = "SHA1PRNG"

        //@JvmField
        //val PLAIN = newAlgorithm(PLAIN_NAME, CodecAlgorithmType.ENCODE)
        //
        //@JvmField
        //val HEX = newAlgorithm(HEX_NAME, CodecAlgorithmType.ENCODE)
        //
        //@JvmField
        //val BASE64 = newAlgorithm(BASE64_NAME, CodecAlgorithmType.ENCODE)

        @JvmField
        val MD2 = of(MD2_NAME, CodecAlgorithmType.DIGEST)

        @JvmField
        val MD5 = of(MD5_NAME, CodecAlgorithmType.DIGEST)

        @JvmField
        val SHA1 = of(SHA1_NAME, CodecAlgorithmType.DIGEST)

        @JvmField
        val SHA256 = of(SHA256_NAME, CodecAlgorithmType.DIGEST)

        @JvmField
        val SHA384 = of(SHA384_NAME, CodecAlgorithmType.DIGEST)

        @JvmField
        val SHA512 = of(SHA512_NAME, CodecAlgorithmType.DIGEST)

        @JvmField
        val HMAC_MD5 = of(HMAC_MD5_NAME, CodecAlgorithmType.HMAC)

        @JvmField
        val HMAC_SHA1 = of(HMAC_SHA1_NAME, CodecAlgorithmType.HMAC)

        @JvmField
        val HMAC_SHA256 = of(HMAC_SHA256_NAME, CodecAlgorithmType.HMAC)

        @JvmField
        val HMAC_SHA384 = of(HMAC_SHA384_NAME, CodecAlgorithmType.HMAC)

        @JvmField
        val HMAC_SHA512 = of(HMAC_SHA512_NAME, CodecAlgorithmType.HMAC)

        @JvmField
        val AES = of(AES_NAME, CodecAlgorithmType.CIPHER)

        @JvmField
        val RSA = of(RSA_NAME, CodecAlgorithmType.CIPHER)

        @JvmField
        val SM2 = of(SM2_NAME, CodecAlgorithmType.CIPHER)

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
            return of(this.toString(), algorithmType)
        }

        @JvmStatic
        fun of(name: CharSequence, type: CodecAlgorithmType): CodecAlgorithm {
            return CodecAlgorithmImpl(name.toString(), type)
        }

        private fun CharSequence.searchPrepared(): CodecAlgorithm? {
            return when (this.toString()) {
                //PLAIN_NAME -> PLAIN
                //HEX_NAME -> HEX
                //BASE64_NAME -> BASE64
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
                AES_NAME -> AES
                RSA_NAME -> RSA
                SM2_NAME -> SM2
                else -> null
            }
        }

        private class CodecAlgorithmImpl(
            override val name: String,
            override val type: CodecAlgorithmType
        ) : CodecAlgorithm {

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
                return "$name($type)"
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

@JvmSynthetic
fun codecAlgorithm(name: CharSequence, type: CodecAlgorithmType): CodecAlgorithm {
    return CodecAlgorithm.of(name, type)
}