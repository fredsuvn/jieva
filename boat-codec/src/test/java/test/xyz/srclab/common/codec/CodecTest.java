package test.xyz.srclab.common.codec;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.Chars;
import xyz.srclab.common.codec.Codec;
import xyz.srclab.common.codec.CodecAlgorithm;
import xyz.srclab.common.codec.aes.AesKeys;
import xyz.srclab.common.codec.rsa.RsaCipher;
import xyz.srclab.common.codec.rsa.RsaKeyPair;
import xyz.srclab.common.codec.sm2.Sm2Cipher;
import xyz.srclab.common.codec.sm2.Sm2KeyPair;
import xyz.srclab.common.test.TestLogger;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Random;

/**
 * @author sunqian
 */
public class CodecTest {

    private static final TestLogger logger = TestLogger.DEFAULT;

    private static final String CHARS = "0123456789qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM";

    @Test
    public void testRsa() {
        RsaCipher rsaCipher = Codec.rsaCipher();
        RsaKeyPair rsaKeyPair = rsaCipher.newKeyPair();
        String data = random(512);
        byte[] dataBytes = Chars.toBytes(data);
        byte[] encrypt1 = rsaCipher.encrypt(rsaKeyPair.getPublicKey(), dataBytes);
        byte[] decrypt1 = rsaCipher.decrypt(rsaKeyPair.getPrivateKey(), encrypt1);
        byte[] encrypt2 = Codec.forData(data).encryptRsa(rsaKeyPair.getPublicKey()).doFinal();
        byte[] decrypt2 = Codec.forData(encrypt2).decryptRsa(rsaKeyPair.getPrivateKey()).doFinal();
        Assert.assertEquals(decrypt1, dataBytes);
        Assert.assertEquals(decrypt2, dataBytes);

        String decryptData = Codec.forData(encrypt1).decryptRsa(rsaKeyPair.getPrivateKeyBytes()).doFinalToString();
        Assert.assertEquals(decryptData, data);

        //Test empty string
        String emptyData = "";
        byte[] encryptEmpty = Codec.forData(emptyData).encryptRsa(rsaKeyPair.getPublicKey()).doFinal();
        String decryptEmptyString = Codec.forData(encryptEmpty).decryptRsa(rsaKeyPair.getPrivateKey()).doFinalToString();
        Assert.assertEquals(decryptEmptyString, emptyData);
    }

    @Test
    public void testSm2() {
        Sm2Cipher sm2Cipher = Codec.sm2Cipher();
        Sm2KeyPair sm2KeyPair = sm2Cipher.newKeyPair();
        String data = random(512);
        byte[] dataBytes = Chars.toBytes(data);
        byte[] encrypt1 = sm2Cipher.encrypt(sm2KeyPair.getPublicKey(), dataBytes);
        byte[] decrypt1 = sm2Cipher.decrypt(sm2KeyPair.getPrivateKey(), encrypt1);
        byte[] encrypt2 = Codec.forData(data).encryptSm2(sm2KeyPair.getPublicKey()).doFinal();
        byte[] decrypt2 = Codec.forData(encrypt2).decryptSm2(sm2KeyPair.getPrivateKey()).doFinal();
        Assert.assertEquals(decrypt1, dataBytes);
        Assert.assertEquals(decrypt2, dataBytes);

        String decryptData = Codec.forData(encrypt1).decryptSm2(sm2KeyPair.getPrivateKeyBytes()).doFinalToString();
        Assert.assertEquals(decryptData, data);

        //Test empty string
        String emptyData = "";
        byte[] encryptEmpty = Codec.forData(emptyData).encryptSm2(sm2KeyPair.getPublicKey()).doFinal();
        String decryptEmptyString = Codec.forData(encryptEmpty).decryptSm2(sm2KeyPair.getPrivateKey()).doFinalToString();
        Assert.assertEquals(decryptEmptyString, emptyData);
    }

    @Test
    public void testAes() {
        String key = "a";
        SecretKey secretKey = AesKeys.newKey(key);
        String data = random(512);
        byte[] dataBytes = Chars.toBytes(data);
        byte[] encrypt = Codec.aesCipher().encrypt(secretKey, dataBytes);
        byte[] decrypt = Codec.aesCipher().decrypt(secretKey, encrypt);
        Assert.assertEquals(decrypt, dataBytes);

        String decryptData = Codec.forData(encrypt).decryptAes(secretKey.getEncoded()).doFinalToString();
        Assert.assertEquals(decryptData, data);

        //Test empty string
        String emptyData = "";
        byte[] encryptEmpty = Codec.forData(emptyData).encryptAes(secretKey).doFinal();
        String decryptEmptyString = Codec.forData(encryptEmpty).decryptAes(secretKey).doFinalToString();
        Assert.assertEquals(decryptEmptyString, emptyData);
    }

    @Test
    public void testCodec() {
        String data = random(512);
        logger.log("data: " + data);
        Sm2Cipher sm2Cipher = Codec.sm2Cipher();
        Sm2KeyPair sm2KeyPair = sm2Cipher.newKeyPair();
        RsaCipher rsaCipher = Codec.rsaCipher();
        RsaKeyPair rsaKeyPair = rsaCipher.newKeyPair();
        SecretKey aesKey = Codec.secretKey("0123456789012345", CodecAlgorithm.AES);

        //加密
        byte[] encrypt = Codec.aesCipher().encrypt(aesKey.getEncoded(), data.getBytes(StandardCharsets.UTF_8));
        encrypt = rsaCipher.encrypt(rsaKeyPair.getPublicKey(), encrypt);
        encrypt = sm2Cipher.encrypt(sm2KeyPair.getPublicKey(), encrypt);
        encrypt = Codec.encodeBase64(encrypt);

        //解密
        String origin = Codec.forData(encrypt)
                .decodeBase64()
                .decryptSm2(sm2KeyPair.getPrivateKeyBytes())
                .decryptRsa(rsaKeyPair.getPrivateKeyBytes())
                .decryptAes(aesKey)
                .doFinalToString();
        logger.log("origin: " + origin);
        Assert.assertEquals(origin, data);
    }

    private String random(int length) {
        char[] result = new char[length];
        for (int i = 0; i < result.length; i++) {
            result[i] = CHARS.charAt(new Random().nextInt(CHARS.length()));
        }
        return new String(result);
    }
}
