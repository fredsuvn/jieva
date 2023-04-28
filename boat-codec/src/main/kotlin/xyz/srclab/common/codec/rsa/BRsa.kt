@file:JvmName("BRsa")

package xyz.srclab.common.codec.rsa

import xyz.srclab.common.codec.CodecAlgorithm
import xyz.srclab.common.codec.generateKeyPair
import java.security.KeyPair
import java.security.Provider

@JvmName("generateKeyPair")
@JvmOverloads
fun generateRsaKeyPair(size: Int = 2048, provider: Provider? = null): KeyPair {
    return generateKeyPair(CodecAlgorithm.RSA_NAME, size, provider)
}