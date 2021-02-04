package xyz.srclab.common.codec

/**
 * Asymmetric cipher.
 *
 * @param [PUB] public key
 * @param [PRI] private key
 * @author sunqian
 * @see xyz.srclab.common.codec.rsa.RsaCipher
 * @see xyz.srclab.common.codec.sm2.Sm2CipherJavaImpl
 */
interface AsymmetricCipher<PUB, PRI> : ReversibleCipher<PUB, PRI> {

    /**
     * Generates key pair.
     *
     * @return key pair
     */
    fun generateKeyPair(): CodecKeyPair<PUB, PRI>

    /**
     * Generates key pair.
     *
     * @return key pair
     */
    fun generateKeyPair(size: Int): CodecKeyPair<PUB, PRI>
}