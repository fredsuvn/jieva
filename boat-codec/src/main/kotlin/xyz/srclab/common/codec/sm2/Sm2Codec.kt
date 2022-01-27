package xyz.srclab.common.codec.sm2

import xyz.srclab.common.codec.CipherCodec
import xyz.srclab.common.codec.CodecAlgorithm
import xyz.srclab.common.codec.bcprov.DEFAULT_BCPROV_PROVIDER
import javax.crypto.Cipher

open class Sm2Codec : CipherCodec {

    override val cipher: Cipher = Cipher.getInstance("SM2", DEFAULT_BCPROV_PROVIDER)
    override val algorithm: CodecAlgorithm = CodecAlgorithm.SM2
}