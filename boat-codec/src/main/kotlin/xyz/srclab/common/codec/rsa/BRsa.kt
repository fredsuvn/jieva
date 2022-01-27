@file:JvmName("BRsa")

package xyz.srclab.common.codec.rsa

import xyz.srclab.common.codec.CodecAlgorithm
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.SecureRandom

@JvmName("generateKeyPair")
@JvmOverloads
fun generateRsaKeyPair(size: Int = 2048): KeyPair {
    val keyPairGenerator = KeyPairGenerator.getInstance(CodecAlgorithm.RSA_NAME)
    keyPairGenerator.initialize(size, SecureRandom())
    return keyPairGenerator.generateKeyPair()
}