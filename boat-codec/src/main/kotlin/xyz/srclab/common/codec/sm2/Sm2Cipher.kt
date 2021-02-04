package xyz.srclab.common.codec.sm2

/**
 * @author sunqian
 */
class Sm2Cipher @JvmOverloads constructor(
    sm2Params: Sm2Params = Sm2Params.defaultParams()
) : Sm2CipherJavaImpl(sm2Params) {
    override val name: String = super.name()
}