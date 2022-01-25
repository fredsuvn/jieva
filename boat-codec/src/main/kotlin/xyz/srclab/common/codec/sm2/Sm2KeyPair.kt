package xyz.srclab.common.codec.sm2

import org.bouncycastle.math.ec.ECPoint
import java.math.BigInteger

/**
 * SM2 key pair.
 *
 * @author sunqian
 */
class Sm2KeyPair(
    override val publicKey: ECPoint,
    override val privateKey: BigInteger
) : CodecKeyPair<ECPoint?, BigInteger?> {
    override val publicKeyBytes: ByteArray = publicKey.getEncoded(false)
    override val privateKeyBytes: ByteArray = privateKey.toByteArray()
}