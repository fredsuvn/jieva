@file:JvmName("AesKeys")

package xyz.srclab.common.codec.aes

import xyz.srclab.common.codec.CodecAlgorithm
import xyz.srclab.common.codec.toSecretKey
import xyz.srclab.common.lang.toBytes
import javax.crypto.SecretKey

private const val MIN_AES_KEY_SIZE = 128

@JvmOverloads
fun CharSequence.toAesKey(minKeySize: Int = MIN_AES_KEY_SIZE): SecretKey {
    return this.toBytes().toAesKey(minKeySize)
}

@JvmOverloads
fun ByteArray.toAesKey(minKeySize: Int = MIN_AES_KEY_SIZE): SecretKey {
    return this.toSecretKey(CodecAlgorithm.AES_NAME, minKeySize)
}