package test.xyz.srclab.common.codec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.jetbrains.annotations.NotNull;
import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.codec.*;
import xyz.srclab.common.codec.aes.AesKeys;
import xyz.srclab.common.codec.rsa.RsaCodec;
import xyz.srclab.common.codec.rsa.RsaKeyPair;
import xyz.srclab.common.codec.sm2.Sm2Codec;
import xyz.srclab.common.codec.sm2.Sm2KeyPair;
import xyz.srclab.common.lang.Chars;
import xyz.srclab.common.test.TestLogger;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import java.security.Key;
import java.security.MessageDigest;
import java.util.Random;

/**
 * @author sunqian
 */
public class CodecTest {

    private static final TestLogger logger = TestLogger.DEFAULT;

    private static final String CHARS = "0123456789qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM";

    @Test
    public void testRsa() {
        RsaCodec rsaCodec = Codec.rsaCodec();
        RsaKeyPair rsaKeyPair = rsaCodec.newKeyPair();
        String data = random(512);
        byte[] dataBytes = Chars.toBytes(data);
        byte[] encrypt1 = rsaCodec.encrypt(rsaKeyPair.publicKey(), dataBytes);
        byte[] decrypt1 = rsaCodec.decrypt(rsaKeyPair.privateKey(), encrypt1);
        byte[] encrypt2 = Codec.forData(data).encryptRsa(rsaKeyPair.publicKey()).doFinal();
        byte[] decrypt2 = Codec.forData(encrypt2).decryptRsa(rsaKeyPair.privateKey()).doFinal();
        Assert.assertEquals(decrypt1, dataBytes);
        Assert.assertEquals(decrypt2, dataBytes);

        String decryptData = Codec.forData(encrypt1).decryptRsa(rsaKeyPair.privateKeyBytes()).doFinalString();
        Assert.assertEquals(decryptData, data);

        //Test empty string
        String emptyData = "";
        byte[] encryptEmpty = Codec.forData(emptyData).encryptRsa(rsaKeyPair.publicKey()).doFinal();
        String decryptEmptyString = Codec.forData(encryptEmpty).decryptRsa(rsaKeyPair.privateKey()).doFinalString();
        Assert.assertEquals(decryptEmptyString, emptyData);
    }

    @Test
    public void testSm2() {
        Sm2Codec sm2Codec = Codec.sm2Codec();
        Sm2KeyPair sm2KeyPair = sm2Codec.newKeyPair();
        String data = random(512);
        byte[] dataBytes = Chars.toBytes(data);
        byte[] encrypt1 = sm2Codec.encrypt(sm2KeyPair.publicKey(), dataBytes);
        byte[] decrypt1 = sm2Codec.decrypt(sm2KeyPair.privateKey(), encrypt1);
        byte[] encrypt2 = Codec.forData(data).encryptSm2(sm2KeyPair.publicKey()).doFinal();
        byte[] decrypt2 = Codec.forData(encrypt2).decryptSm2(sm2KeyPair.privateKey()).doFinal();
        Assert.assertEquals(decrypt1, dataBytes);
        Assert.assertEquals(decrypt2, dataBytes);

        String decryptData = Codec.forData(encrypt1).decryptSm2(sm2KeyPair.privateKeyBytes()).doFinalString();
        Assert.assertEquals(decryptData, data);

        //Test empty string
        String emptyData = "";
        byte[] encryptEmpty = Codec.forData(emptyData).encryptSm2(sm2KeyPair.publicKey()).doFinal();
        String decryptEmptyString = Codec.forData(encryptEmpty).decryptSm2(sm2KeyPair.privateKey()).doFinalString();
        Assert.assertEquals(decryptEmptyString, emptyData);
    }

    @Test
    public void testAes() {
        String key = "a";
        SecretKey secretKey = AesKeys.newKey(key);
        CipherCodec aesCodec = Codec.aesCodec();
        String data = random(512);
        byte[] dataBytes = Chars.toBytes(data);
        byte[] encrypt = aesCodec.encrypt(secretKey, dataBytes);
        byte[] decrypt = aesCodec.decrypt(secretKey, encrypt);
        Assert.assertEquals(decrypt, dataBytes);

        String decryptData = Codec.forData(encrypt).decryptAes(secretKey.getEncoded()).doFinalString();
        Assert.assertEquals(decryptData, data);

        //Test empty string
        String emptyData = "";
        byte[] encryptEmpty = Codec.forData(emptyData).encryptAes(secretKey).doFinal();
        String decryptEmptyString = Codec.forData(encryptEmpty).decryptAes(secretKey).doFinalString();
        Assert.assertEquals(decryptEmptyString, emptyData);

        //Repeat use aes codec
        SecretKey aesKey1 = AesKeys.newKey(random(32));
        byte[] aesEncrypt1 = aesCodec.encrypt(aesKey1, data);
        byte[] aesDecrypt1 = aesCodec.decrypt(aesKey1, aesEncrypt1);
        Assert.assertEquals(aesDecrypt1, dataBytes);
        SecretKey aesKey2 = AesKeys.newKey(random(32));
        byte[] aesEncrypt2 = aesCodec.encrypt(aesKey2, data);
        byte[] aesDecrypt2 = aesCodec.decrypt(aesKey2, aesEncrypt2);
        Assert.assertEquals(aesDecrypt2, dataBytes);
        SecretKey aesKey3 = AesKeys.newKey(random(32));
        byte[] aesEncrypt3 = aesCodec.encrypt(aesKey3, data);
        byte[] aesDecrypt3 = aesCodec.decrypt(aesKey3, aesEncrypt3);
        Assert.assertEquals(aesDecrypt3, dataBytes);
    }

    @Test
    public void testCodec() {
        String data = random(512);
        logger.log("data: " + data);
        Sm2Codec sm2Codec = Codec.sm2Codec();
        Sm2KeyPair sm2KeyPair = sm2Codec.newKeyPair();
        RsaCodec rsaCodec = Codec.rsaCodec();
        RsaKeyPair rsaKeyPair = rsaCodec.newKeyPair();
        SecretKey aesKey = Codec.secretKey("0123456789012345", CodecAlgorithm.AES_NAME);

        //加密
        byte[] encrypt = Codec.aesCodec().encrypt(aesKey.getEncoded(), Chars.toBytes(data));
        encrypt = rsaCodec.encrypt(rsaKeyPair.publicKey(), encrypt);
        encrypt = sm2Codec.encrypt(sm2KeyPair.publicKey(), encrypt);
        encrypt = EncodeCodec.base64().encode(encrypt);

        //解密
        String origin = Codec.forData(encrypt)
            .decodeBase64()
            .decryptSm2(sm2KeyPair.privateKeyBytes())
            .decryptRsa(rsaKeyPair.privateKeyBytes())
            .decryptAes(aesKey)
            .doFinalString();
        logger.log("origin: " + origin);
        Assert.assertEquals(origin, data);
    }

    @Test
    public void testEncode() {
        logger.log(Codec.hexString("123456789"));
        logger.log(Codec.base64String("123456789"));
        Assert.assertEquals(
            Codec.hexString("123456789"),
            Hex.encodeHexString("123456789".getBytes())
        );
        Assert.assertEquals(
            Codec.base64String("123456789"),
            Base64.encodeBase64String("123456789".getBytes())
        );
    }

    @Test
    public void testDigest() throws Exception {
        String message = "123456789";
        DigestCodec md5 = Codec.md5Codec();
        Assert.assertEquals(
            md5.digest(message),
            MessageDigest.getInstance("Md5").digest(message.getBytes())
        );
        Assert.assertEquals(//test reset
            md5.digest(message),
            MessageDigest.getInstance("Md5").digest(message.getBytes())
        );
        MacCodec macCodec = Codec.hmacMd5Codec();
        Key aesKey = AesKeys.newKey("12345678");
        Mac mac1 = Mac.getInstance("HmacMD5");
        mac1.init(aesKey);
        Assert.assertEquals(
            macCodec.digest(aesKey, message),
            mac1.doFinal(message.getBytes())
        );
        Mac mac2 = Mac.getInstance("HmacMD5");
        mac2.init(aesKey);
        Assert.assertEquals(
            macCodec.digest(aesKey, message),
            mac2.doFinal(message.getBytes())
        );
    }

    @Test
    public void testAlgorithm() {
        String key = "123";
        String password = "666666666666666666666666";
        logger.log("{}: {}", CodecAlgorithm.PLAIN, Codec.plainCodec().encodeToString(password));
        logger.log("{}: {}", CodecAlgorithm.HEX, Codec.hexCodec().encodeToString(password));
        logger.log("{}: {}", CodecAlgorithm.BASE64, Codec.base64Codec().encodeToString(password));
        logger.log("{}: {}", CodecAlgorithm.MD2,
            EncodeCodec.base64().encodeToString(Codec.md2Codec().digestToString(password)));
        logger.log("{}: {}", CodecAlgorithm.MD5,
            EncodeCodec.base64().encodeToString(Codec.md5Codec().digestToString(password)));
        logger.log("{}: {}", CodecAlgorithm.SHA1,
            EncodeCodec.base64().encodeToString(Codec.sha1Codec().digestToString(password)));
        logger.log("{}: {}", CodecAlgorithm.SHA256,
            EncodeCodec.base64().encodeToString(Codec.sha256Codec().digestToString(password)));
        logger.log("{}: {}", CodecAlgorithm.SHA384,
            EncodeCodec.base64().encodeToString(Codec.sha384Codec().digestToString(password)));
        logger.log("{}: {}", CodecAlgorithm.SHA512,
            EncodeCodec.base64().encodeToString(Codec.sha512Codec().digestToString(password)));
        logger.log("{}: {}", CodecAlgorithm.HMAC_MD5,
            EncodeCodec.base64().encodeToString(Codec.hmacMd5Codec().digestToString(key, password)));
        logger.log("{}: {}", CodecAlgorithm.HMAC_SHA1,
            EncodeCodec.base64().encodeToString(Codec.hmacSha1Codec().digestToString(key, password)));
        logger.log("{}: {}", CodecAlgorithm.HMAC_SHA256,
            EncodeCodec.base64().encodeToString(Codec.hmacSha256Codec().digestToString(key, password)));
        logger.log("{}: {}", CodecAlgorithm.HMAC_SHA384,
            EncodeCodec.base64().encodeToString(Codec.hmacSha384Codec().digestToString(key, password)));
        logger.log("{}: {}", CodecAlgorithm.HMAC_SHA512,
            EncodeCodec.base64().encodeToString(Codec.hmacSha512Codec().digestToString(key, password)));
        SecretKey aesKey = AesKeys.newKey(key);
        logger.log("{}: {}", CodecAlgorithm.AES,
            EncodeCodec.base64().encodeToString(Codec.aesCodec().encryptToString(aesKey, password)));
        RsaKeyPair rsaKeyPair = Codec.rsaCodec().newKeyPair();
        logger.log("{}: {}", CodecAlgorithm.RSA,
            EncodeCodec.base64().encodeToString(Codec.rsaCodec().encryptToString(rsaKeyPair.publicKey(), password)));
        Sm2KeyPair sm2KeyPair = Codec.sm2Codec().newKeyPair();
        logger.log("{}: {}", CodecAlgorithm.SM2,
            EncodeCodec.base64().encodeToString(Codec.sm2Codec().encryptToString(sm2KeyPair.publicKey(), password)));
    }

    @Test
    public void testPassword() {
        String password = "some password";
        SecretKey key = AesKeys.newKey("123");
        logger.log("password: {}, aes: {}",
            password, Codec.forData(password).encryptAes(key).encodeBase64().doFinalString());
    }

    private String random(int length) {
        char[] result = new char[length];
        for (int i = 0; i < result.length; i++) {
            result[i] = CHARS.charAt(new Random().nextInt(CHARS.length()));
        }
        return new String(result);
    }

    public static class Xxx implements Codec {

        @NotNull
        @Override
        public String algorithm() {
            return null;
        }
    }
}
