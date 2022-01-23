package xyz.srclab.common.codec

import xyz.srclab.common.codec.rsa.RsaCodec
import xyz.srclab.common.codec.sm2.Sm2Codec

/**
 * Asymmetric cipher codec which support `encrypt` and `decrypt` with a pair of keys.
 *
 * @see RsaCodec
 * @see Sm2Codec
 */
interface AsymmetricCryptCodec<PUB, PRI> : CryptCodec {

    fun newKeyPair(): CodecKeyPair<PUB, PRI>

    fun newKeyPair(size: Int): CodecKeyPair<PUB, PRI>
}