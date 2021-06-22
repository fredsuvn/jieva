package sample.kotlin.xyz.srclab.codec

import org.testng.annotations.Test
import xyz.srclab.common.codec.*
import xyz.srclab.common.codec.aes.toAesKey
import xyz.srclab.common.test.TestLogger

class CodecSample {

    @Test
    fun testCodec() {
        val password = "hei, xiongdi, womenhaojiubujiannizainali"
        val messageBase64 = "aGVpLCBwZW5neW91LCBydWd1b3poZW5kZXNoaW5pcWluZ2Rhemhhb2h1"
        val secretKey = password.toAesKey()

        //Use static
        val message: String = Base64Codec.decodeToString(messageBase64)
        var encrypt = aesCodec().encrypt(secretKey, message)
        var decrypt = aesCodec().decryptToString(secretKey, encrypt)
        //hei, pengyou, ruguozhendeshiniqingdazhaohu
        logger.log("decrypt: {}", decrypt)

        //Use chain
        encrypt = messageBase64.codec().decodeBase64().encryptAes(secretKey).doFinal()
        decrypt = encrypt.codec().decryptAes(secretKey).doFinalString()
        //hei, pengyou, ruguozhendeshiniqingdazhaohu
        logger.log("decrypt: {}", decrypt)
    }

    @Test
    fun testEncode() {
        logger.log("123456789".toHexString())
        logger.log("123456789".toBase64String())
    }

    companion object {
        private val logger = TestLogger.DEFAULT
    }
}