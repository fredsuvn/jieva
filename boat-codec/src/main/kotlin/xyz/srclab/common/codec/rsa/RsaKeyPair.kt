package xyz.srclab.common.codec.rsa

import xyz.srclab.common.codec.CodecKeyPair
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey

/**
 * RSA key pair.
 *
 * @author sunqian
 */
class RsaKeyPair(
    override val publicKey: RSAPublicKey,
    override val privateKey: RSAPrivateKey
) : CodecKeyPair<RSAPublicKey, RSAPrivateKey> {
    override val publicKeyBytes: ByteArray = publicKey.encoded
    override val privateKeyBytes: ByteArray = privateKey.encoded
}