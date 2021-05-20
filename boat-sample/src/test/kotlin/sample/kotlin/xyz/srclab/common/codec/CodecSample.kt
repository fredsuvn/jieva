package sample.kotlin.xyz.srclab.common.codec

import org.testng.annotations.Test
import xyz.srclab.common.codec.Coding
import xyz.srclab.common.codec.Coding.Companion.decodeBase64String
import xyz.srclab.common.codec.aes.toAesKey
import xyz.srclab.common.test.TestLogger

class CodecSample {

    @Test
    fun testCodec() {
        val password = "hei, xiongdi, womenhaojiubujiannizainali"
        val messageBase64 = "aGVpLCBwZW5neW91LCBydWd1b3poZW5kZXNoaW5pcWluZ2Rhemhhb2h1"
        val secretKey = password.toAesKey()

        //Use static
        val message: String = messageBase64.decodeBase64String()
        var encrypt = Coding.aesCipher().encrypt(secretKey, message)
        var decrypt = Coding.aesCipher().decryptToString(secretKey, encrypt)
        //hei, pengyou, ruguozhendeshiniqingdazhaohu
        logger.log("decrypt: {}", decrypt)

        //Use chain
        encrypt = Coding.forData(messageBase64).decodeBase64().encryptAes(secretKey).doFinal()
        decrypt = Coding.forData(encrypt).decryptAes(secretKey).doFinalToString()
        //hei, pengyou, ruguozhendeshiniqingdazhaohu
        logger.log("decrypt: {}", decrypt)
    }

    companion object {
        private val logger = TestLogger.DEFAULT
    }
}