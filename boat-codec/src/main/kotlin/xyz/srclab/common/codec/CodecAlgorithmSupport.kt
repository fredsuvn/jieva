package xyz.srclab.common.codec

import cn.com.essence.galaxy.annotation.Nullable
import java.util.*

/**
 * @author sunqian
 */
internal object CodecAlgorithmSupport {
    // Note there is no thread-safe problem if we only read from the HashMap, without modification.
    // And this HashMap will never be modified after initializing.
    private val algorithmMap: MutableMap<String, CodecAlgorithm?> = HashMap()
    private fun putAlgorithmMap(algorithm: CodecAlgorithm?) {
        algorithmMap[algorithm!!.name().toUpperCase()] = algorithm
    }

    fun newCodecAlgorithm(name: String, type: CodecAlgorithm.Type): CodecAlgorithm {
        return CodecAlgorithmImpl(name, type)
    }

    @Nullable
    fun find(algorithmName: String): CodecAlgorithm? {
        if (algorithmMap.isEmpty()) {
            synchronized(algorithmMap) {
                if (algorithmMap.isEmpty()) {
                    initAlgorithmMap()
                }
            }
        }
        return algorithmMap[algorithmName.toUpperCase()]
    }

    private fun initAlgorithmMap() {
        putAlgorithmMap(CodecAlgorithm.Companion.PLAIN)
        putAlgorithmMap(CodecAlgorithm.Companion.HEX)
        putAlgorithmMap(CodecAlgorithm.Companion.BASE64)
        putAlgorithmMap(CodecAlgorithm.Companion.AES)
        putAlgorithmMap(CodecAlgorithm.Companion.MD2)
        putAlgorithmMap(CodecAlgorithm.Companion.MD5)
        putAlgorithmMap(CodecAlgorithm.Companion.SHA1)
        putAlgorithmMap(CodecAlgorithm.Companion.SHA256)
        putAlgorithmMap(CodecAlgorithm.Companion.SHA384)
        putAlgorithmMap(CodecAlgorithm.Companion.SHA512)
        putAlgorithmMap(CodecAlgorithm.Companion.HMAC_MD5)
        putAlgorithmMap(CodecAlgorithm.Companion.HMAC_SHA1)
        putAlgorithmMap(CodecAlgorithm.Companion.HMAC_SHA256)
        putAlgorithmMap(CodecAlgorithm.Companion.HMAC_SHA384)
        putAlgorithmMap(CodecAlgorithm.Companion.HMAC_SHA512)
        putAlgorithmMap(CodecAlgorithm.Companion.RSA)
        putAlgorithmMap(CodecAlgorithm.Companion.SM2)
    }

    private class CodecAlgorithmImpl(private val name: String, private val type: CodecAlgorithm.Type) : CodecAlgorithm {
        override fun name(): String {
            return name
        }

        override fun type(): CodecAlgorithm.Type {
            return type
        }

        override fun equals(`object`: Any?): Boolean {
            if (this === `object`) return true
            if (`object` !is CodecAlgorithm) return false
            val that = `object`
            return name == that.name() &&
                    type == that.type()
        }

        override fun hashCode(): Int {
            return Objects.hash(name, type)
        }

        override fun toString(): String {
            return name()
        }
    }
}