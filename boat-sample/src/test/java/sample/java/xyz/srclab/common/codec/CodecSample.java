package sample.java.xyz.srclab.common.codec;

import org.testng.annotations.Test;
import xyz.srclab.common.codec.Codec;
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
        String message = Codec.decodeBase64String(messageBase64);
        byte[] encrypt = Codec.aesCipher().encrypt(secretKey, message);
        String decrypt = Codec.aesCipher().decryptToString(secretKey, encrypt);
        //hei, pengyou, ruguozhendeshiniqingdazhaohu
        logger.log("decrypt: {}", decrypt);

        //Use chain
        encrypt = Codec.forData(messageBase64).decodeBase64().encryptAes(secretKey).doFinal();
        decrypt = Codec.forData(encrypt).decryptAes(secretKey).doFinalToString();
        //hei, pengyou, ruguozhendeshiniqingdazhaohu
        logger.log("decrypt: {}", decrypt);
    }
}
