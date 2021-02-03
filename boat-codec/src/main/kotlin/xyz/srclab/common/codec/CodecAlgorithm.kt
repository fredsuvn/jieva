package xyz.srclab.common.codec

import cn.com.essence.galaxy.annotation.Nullable

/**
 * codec algorithm.
 *
 * @author sunqian
 */
interface CodecAlgorithm {
    fun name(): String
    fun type(): Type
    enum class Type {
        SYMMETRIC, DIGEST, HMAC, ASYMMETRIC, ENCODE
    }

    companion object {
        fun of(algorithmName: String?, type: Type): CodecAlgorithm? {
            return CodecAlgorithmSupport.newCodecAlgorithm(algorithmName!!, type)
        }

        fun forName(algorithmName: String): CodecAlgorithm {
            return CodecAlgorithmSupport.find(algorithmName)
                ?: throw NoSuchElementException(algorithmName)
        }

        val PLAIN = of(CodecAlgorithmConstants.PLAIN, Type.ENCODE)
        val HEX = of(CodecAlgorithmConstants.HEX, Type.ENCODE)
        val BASE64 = of(CodecAlgorithmConstants.BASE64, Type.ENCODE)
        val AES = of(CodecAlgorithmConstants.AES, Type.SYMMETRIC)
        val MD2 = of(CodecAlgorithmConstants.MD2, Type.DIGEST)
        val MD5 = of(CodecAlgorithmConstants.MD5, Type.DIGEST)
        val SHA1 = of(CodecAlgorithmConstants.SHA1, Type.DIGEST)
        val SHA256 = of(CodecAlgorithmConstants.SHA256, Type.DIGEST)
        val SHA384 = of(CodecAlgorithmConstants.SHA384, Type.DIGEST)
        val SHA512 = of(CodecAlgorithmConstants.SHA512, Type.DIGEST)
        val HMAC_MD5 = of(CodecAlgorithmConstants.HMAC_MD5, Type.HMAC)
        val HMAC_SHA1 = of(CodecAlgorithmConstants.HMAC_SHA1, Type.HMAC)
        val HMAC_SHA256 = of(CodecAlgorithmConstants.HMAC_SHA256, Type.HMAC)
        val HMAC_SHA384 = of(CodecAlgorithmConstants.HMAC_SHA384, Type.HMAC)
        val HMAC_SHA512 = of(CodecAlgorithmConstants.HMAC_SHA512, Type.HMAC)
        @kotlin.jvm.JvmField
        val RSA = of(CodecAlgorithmConstants.RSA, Type.ASYMMETRIC)
        val SM2 = of(CodecAlgorithmConstants.SM2, Type.ASYMMETRIC)
    }
}