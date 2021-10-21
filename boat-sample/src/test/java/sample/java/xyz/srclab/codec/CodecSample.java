package sample.java.xyz.srclab.codec;

import org.testng.annotations.Test;
import xyz.srclab.common.codec.Codecs;
import xyz.srclab.common.codec.Encoder;
import xyz.srclab.common.codec.aes.AesKeys;
import xyz.srclab.common.test.TestLogger;

import javax.crypto.SecretKey;

public class CodecSample {

    private static final TestLogger logger = TestLogger.DEFAULT;

    @Test
    public void testCodec() {
        String password = "hei, xiongdi, womenhaojiubujiannizainali";
        String messageBase64 = "aGVpLCBwZW5neW91LCBydWd1b3poZW5kZXNoaW5pcWluZ2Rhemhhb2h1";
        SecretKey secretKey = AesKeys.newKey(password);

        //Use static
        String message = Encoder.base64().decodeToString(messageBase64);
        byte[] encrypt = Codecs.aesCodec().encrypt(secretKey, message);
        String decrypt = Codecs.aesCodec().decryptToString(secretKey, encrypt);
        //hei, pengyou, ruguozhendeshiniqingdazhaohu
        logger.log("decrypt: {}", decrypt);

        //Use chain
        encrypt = Codecs.codec(messageBase64).decodeBase64().encryptAes(secretKey).doFinal();
        decrypt = Codecs.codec(encrypt).decryptAes(secretKey).doFinalString();
        //hei, pengyou, ruguozhendeshiniqingdazhaohu
        logger.log("decrypt: {}", decrypt);
    }

    @Test
    public void testEncode() {
        logger.log(Codecs.hexString("123456789"));
        logger.log(Codecs.base64String("123456789"));
    }
}
