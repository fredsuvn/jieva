package xyz.srclab.common.codec

import xyz.srclab.common.codec.rsa.RsaCodec
import xyz.srclab.common.codec.sm2.Sm2Codec

/**
 * Codec interface, represents a type of codec way.
 *
 * @see DigestCodec
 * @see HmacCodec
 * @see CipherCodec
 * @see CodecAlgorithm
 * @see RsaCodec
 * @see Sm2Codec
 */
interface Codec {

    val algorithm: CodecAlgorithm
}