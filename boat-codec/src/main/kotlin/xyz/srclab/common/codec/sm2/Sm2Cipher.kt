package xyz.srclab.common.codec.sm2

class Sm2Cipher constructor(
    sm2Params: Sm2Params = Sm2Params.defaultParams()
) : Sm2CipherJavaImpl(sm2Params)