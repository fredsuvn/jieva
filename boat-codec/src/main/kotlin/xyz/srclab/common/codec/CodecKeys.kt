@file:JvmName("CodecKeys")
@file:JvmMultifileClass

package xyz.srclab.common.codec

import xyz.srclab.common.lang.toBytes
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

@JvmName("newKey")
fun ByteArray.toSecretKey(algorithm: String): SecretKey {
    return SecretKeySpec(this, algorithm)
}

@JvmName("newKeyBytes")
fun ByteArray.toSecretKeyBytes(algorithm: String): ByteArray {
    return toSecretKey(algorithm).encoded
}

@JvmName("newKey")
fun CharSequence.toSecretKey(algorithm: String): SecretKey {
    return this.toBytes().toSecretKey(algorithm)
}

@JvmName("newKeyBytes")
fun CharSequence.toSecretKeyBytes(algorithm: String): ByteArray {
    return this.toBytes().toSecretKeyBytes(algorithm)
}