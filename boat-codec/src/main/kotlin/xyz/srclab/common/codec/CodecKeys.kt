@file:JvmName("CodecKeys")
@file:JvmMultifileClass

package xyz.srclab.common.codec

import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

@JvmName("newKey")
fun ByteArray.toSecretKey(algorithm: String): SecretKey {
    return SecretKeySpec(this, algorithm)
}