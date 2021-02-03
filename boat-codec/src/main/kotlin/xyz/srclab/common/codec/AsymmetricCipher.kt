package xyz.srclab.common.codec

/**
 * Asymmetric cipher.
 *
 * @param [EK] encrypt key
 * @param [DK] decrypt key
 * @author sunqian
 * @see xyz.srclab.common.codec.rsa.RsaCipher
 * @see xyz.srclab.common.codec.sm2.Sm2Cipher
 */
interface AsymmetricCipher<EK, DK> : ReversibleCipher<EK, DK> {

    /**
     * Generates key pair.
     *
     * @return key pair
     */
    fun generateKeyPair(): CodecKeyPair<EK, DK>
}