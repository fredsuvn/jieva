package xyz.srclab.common.codec

import xyz.srclab.common.codec.gm.SM2Cipher
import xyz.srclab.common.codec.rsa.RsaCodec

/**
 * Codec interface, represents a type of codec way.
 *
 * @see DigestCodec
 * @see HmacCodec
 * @see CipherCodec
 * @see CodecAlgorithm
 * @see RsaCodec
 * @see SM2Cipher
 */
interface Codec {

    val algorithm: CodecAlgorithm
}