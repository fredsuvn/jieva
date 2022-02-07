package test.xyz.srclab.common.codec;

import org.testng.annotations.Test;
import xyz.srclab.common.codec.BCodec;
import xyz.srclab.common.codec.BKey;
import xyz.srclab.common.codec.CodecAlgorithm;
import xyz.srclab.common.codec.aes.BAes;
import xyz.srclab.common.codec.gm.BGM;
import xyz.srclab.common.codec.rsa.BRsa;

import java.security.Key;
import java.security.KeyPair;

/**
 * @author sunqian
 */
public class CodecTest {

    @Test
    public void testDigest() {
        TestUtil.testDigest(BCodec.md5());
        TestUtil.testDigest(BCodec.sha256());
        TestUtil.testDigest(BCodec.sm3());
    }

    @Test
    public void testHmac() {
        String password = "123";
        TestUtil.testHmac(BCodec.hmacMd5(), BKey.passphraseToKey(password, CodecAlgorithm.HMAC_MD5_NAME, 128));
        TestUtil.testHmac(BCodec.hmacSha256(), BKey.passphraseToKey(password, CodecAlgorithm.HMAC_SHA256_NAME, 128));
    }

    @Test
    public void testSign() {
        KeyPair rsaKeys = BRsa.generateKeyPair();
        TestUtil.testSign(BCodec.sha256WithRsa(), rsaKeys.getPublic(), rsaKeys.getPrivate());
        KeyPair sm2Keys = BGM.generateSm2KeyPair();
        TestUtil.testSign(BCodec.sm3WithSm2(), sm2Keys.getPublic(), sm2Keys.getPrivate());
        TestUtil.testSign(BCodec.sha256WithSm2(), sm2Keys.getPublic(), sm2Keys.getPrivate());
    }

    @Test
    public void testCipher() throws Exception {
        String password = "123";
        Key key = BAes.passphraseToKey(password);
        TestUtil.testCipher(BCodec.aes(), key, key);
        Key sm4Key = BGM.passphraseToSm4Key(password);
        TestUtil.testCipher(BCodec.sm4(), sm4Key, sm4Key);
        KeyPair rsaKeys = BRsa.generateKeyPair();
        TestUtil.testCipher(BCodec.rsa(), rsaKeys.getPublic(), rsaKeys.getPrivate());
        KeyPair sm2Keys = BGM.generateSm2KeyPair();
        TestUtil.testCipher(BCodec.sm2(), sm2Keys.getPublic(), sm2Keys.getPrivate());
    }
}
