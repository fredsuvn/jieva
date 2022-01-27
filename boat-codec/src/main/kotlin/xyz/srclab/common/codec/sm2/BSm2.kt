@file:JvmName("BSm2")

package xyz.srclab.common.codec.sm2

import xyz.srclab.common.codec.bcprov.DEFAULT_BCPROV_PROVIDER
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.SecureRandom

import java.security.spec.ECGenParameterSpec

@JvmName("generateKeyPair")
@JvmOverloads
fun generateSm2KeyPair(size: Int = 2048): KeyPair {
    val sm2Spec = ECGenParameterSpec("sm2p256v1")
    val keyPairGenerator = KeyPairGenerator.getInstance("EC", DEFAULT_BCPROV_PROVIDER)
    keyPairGenerator.initialize(sm2Spec, SecureRandom())
    return keyPairGenerator.generateKeyPair()
}