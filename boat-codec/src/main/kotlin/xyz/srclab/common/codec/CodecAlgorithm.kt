package xyz.srclab.common.codec

import xyz.srclab.common.lang.INAPPLICABLE_JVM_NAME

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

        @JvmField
        val PLAIN = newAlgorithm(PLAIN_NAME, CodecAlgorithmType.ENCODE)

        @JvmField
        val HEX = newAlgorithm(HEX_NAME, CodecAlgorithmType.ENCODE)

        @JvmField
        val BASE64 = newAlgorithm(BASE64_NAME, CodecAlgorithmType.ENCODE)

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
        val HMAC_MD5 = newAlgorithm(HMAC_MD5_NAME, CodecAlgorithmType.MAC)

        @JvmField
        val HMAC_SHA1 = newAlgorithm(HMAC_SHA1_NAME, CodecAlgorithmType.MAC)

        @JvmField
        val HMAC_SHA256 = newAlgorithm(HMAC_SHA256_NAME, CodecAlgorithmType.MAC)

        @JvmField
        val HMAC_SHA384 = newAlgorithm(HMAC_SHA384_NAME, CodecAlgorithmType.MAC)

        @JvmField
        val HMAC_SHA512 = newAlgorithm(HMAC_SHA512_NAME, CodecAlgorithmType.MAC)

        @JvmField
        val AES = newAlgorithm(AES_NAME, CodecAlgorithmType.CIPHER)

        @JvmField
        val RSA = newAlgorithm(RSA_NAME, CodecAlgorithmType.ASYMMETRIC)

        @JvmField
        val SM2 = newAlgorithm(SM2_NAME, CodecAlgorithmType.ASYMMETRIC)

        @JvmName("forName")
        @JvmStatic
        fun CharSequence.toCodecAlgorithm(): CodecAlgorithm {
            return toCodecAlgorithmOrNull()
                ?: throw IllegalArgumentException("Codec algorithm not found: $this.")
        }

        @JvmName("forName")
        @JvmStatic
        fun CharSequence.toCodecAlgorithm(algorithmType: CodecAlgorithmType): CodecAlgorithm {
            val pre = toCodecAlgorithmOrNull()
            if (pre !== null && pre.type == algorithmType) {
                return pre
            }
            return newAlgorithm(this.toString(), algorithmType)
        }

        private fun CharSequence.toCodecAlgorithmOrNull(): CodecAlgorithm? {
            return when (this.toString()) {
                PLAIN_NAME -> PLAIN
                HEX_NAME -> HEX
                BASE64_NAME -> BASE64
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

        @JvmStatic
        fun newAlgorithm(name: String, type: CodecAlgorithmType): CodecAlgorithm {
            return CodecAlgorithmImpl(name, type)
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
                return "Algorithm: $name"
            }
        }
    }
}

enum class CodecAlgorithmType {
    ENCODE, DIGEST, MAC, CIPHER, ASYMMETRIC,
}