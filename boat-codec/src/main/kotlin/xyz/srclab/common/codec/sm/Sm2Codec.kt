package xyz.srclab.common.codec.sm

import xyz.srclab.common.codec.CipherCodec
import xyz.srclab.common.codec.CodecAlgorithm
import xyz.srclab.common.codec.bcprov.DEFAULT_BCPROV_PROVIDER
import javax.crypto.Cipher

open class Sm2Codec(
    mode: Int = MODE_C1C3C2
) : CipherCodec {

    override val algorithm: CodecAlgorithm = CodecAlgorithm.SM2
    override val cipher: Cipher = Cipher.getInstance(algorithm.name, DEFAULT_BCPROV_PROVIDER)

    companion object {
        const val MODE_C1C2C3 = 1
        const val MODE_C1C3C2 = 2
    }
}