package xyz.srclab.common.codec

import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

/**
 * @author sunqian
 */
internal object CodecKeySupport {
    fun generateKey(key: ByteArray?, algorithm: String?): SecretKey {
        return SecretKeySpec(key, algorithm)
    }
}