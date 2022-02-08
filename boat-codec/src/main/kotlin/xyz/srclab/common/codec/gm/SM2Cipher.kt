package xyz.srclab.common.codec.gm

import org.bouncycastle.crypto.engines.SM2Engine
import org.bouncycastle.jcajce.provider.asymmetric.ec.GMCipherSpi
import xyz.srclab.common.codec.BoatCodecProvider
import xyz.srclab.common.codec.CodecAlgorithm
import xyz.srclab.common.codec.bc.DEFAULT_BCPROV_PROVIDER
import javax.crypto.Cipher

open class SM2Cipher @JvmOverloads constructor(
    mode: Int = MODE_C1C3C2
) : Cipher(createGMCipherSpi(mode), DEFAULT_BCPROV_PROVIDER, "SM2C1C3C2") {

    companion object {

        const val MODE_C1C2C3 = 1
        const val MODE_C1C3C2 = 2

        private fun createGMCipherSpi(mode: Int): GMCipherSpi {
            val engine = SM2Engine(
                if (mode == MODE_C1C2C3) {
                    SM2Engine.Mode.C1C2C3
                } else {
                    SM2Engine.Mode.C1C3C2
                }
            )
            return GMCipherSpi(engine)
        }
    }
}