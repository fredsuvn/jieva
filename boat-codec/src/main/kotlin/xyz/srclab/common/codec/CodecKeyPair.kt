package xyz.srclab.common.codec

import xyz.srclab.common.codec.Codec.Companion.toBase64String
import java.security.Key

/**
 * Key pair of codec.
 *
 * @param [PUB] public key
 * @param [PRI] private key
 */
interface CodecKeyPair<PUB : Key, PRI : Key> {

    val publicKey: PUB
    val privateKey: PRI

    val publicKeyBase64String: String
        get() = publicKey.encoded.toBase64String()

    val privateKeyBase64String: String
        get() = privateKey.encoded.toBase64String()
}