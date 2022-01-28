package test.xyz.srclab.common.codec;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.BRandom;
import xyz.srclab.common.codec.*;
import xyz.srclab.common.codec.aes.BAes;
import xyz.srclab.common.codec.rsa.BRsa;
import xyz.srclab.common.codec.sm2.BSm2;
import xyz.srclab.common.io.BBuffer;
import xyz.srclab.common.io.BIO;
import xyz.srclab.common.io.BytesAppender;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import java.nio.ByteBuffer;
import java.security.Key;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;

/**
 * @author sunqian
 */
public class CodecTest {

    @Test
    public void testDigest() {
        testDigest(BCodec.md5());
        testDigest(BCodec.sha256());
        testDigest(BCodec.sm3());
    }

    @Test
    public void testHmac() {
        String password = "123";
        testHmac(BCodec.hmacMd5(), BKey.passphraseToKey(password, CodecAlgorithm.HMAC_MD5_NAME, 128));
        testHmac(BCodec.hmacSha256(), BKey.passphraseToKey(password, CodecAlgorithm.HMAC_SHA256_NAME, 128));
    }

    @Test
    public void testSign() {
        KeyPair rsa = BRsa.generateKeyPair();
        testSign(BCodec.sha256WithRsa(), rsa.getPublic(), rsa.getPrivate());
        KeyPair sm2 = BSm2.generateKeyPair();
        testSign(BCodec.sm3WithSm2(), sm2.getPublic(), sm2.getPrivate());
    }

    @Test
    public void testCipher() {
        String password = "123";
        Key key = BAes.passphraseToKey(password);
        testCipher(BCodec.aes(), key, key);
    }

    private void testDigest(DigestCodec digestCodec) {
        int offset = 6;
        int length = 998;
        byte[] data = BRandom.randomString(1888).getBytes();
        ByteBuffer dataBuffer = BBuffer.toBuffer(data, offset, length);
        byte[] d1 = digestCodec.digest(data, offset, length).doFinal();
        byte[] d2 = digestCodec.digest(dataBuffer).doFinal();
        Assert.assertEquals(d1, d2);
        ByteBuffer destBuffer = ByteBuffer.allocateDirect(2222);
        dataBuffer.position(0);
        digestCodec.digest(dataBuffer).doFinal(destBuffer);
        destBuffer.flip();
        Assert.assertEquals(BBuffer.toBytes(destBuffer), d2);
        dataBuffer.position(0);
        BytesAppender appender = new BytesAppender();
        digestCodec.digest(dataBuffer).doFinal(appender);
        Assert.assertEquals(appender.toBytes(), d2);
        byte[] d3 = digestCodec.digest(BIO.asInputStream(data, offset, length)).doFinal();
        Assert.assertEquals(d3, d1);
    }

    private void testHmac(HmacCodec hmacCodec, Key key) {
        int offset = 6;
        int length = 998;
        byte[] data = BRandom.randomString(1888).getBytes();
        ByteBuffer dataBuffer = BBuffer.toBuffer(data, offset, length);
        byte[] d1 = hmacCodec.hmac(key, data, offset, length).doFinal();
        byte[] d2 = hmacCodec.hmac(key, dataBuffer).doFinal();
        Assert.assertEquals(d1, d2);
        ByteBuffer destBuffer = ByteBuffer.allocateDirect(2222);
        dataBuffer.position(0);
        hmacCodec.hmac(key, dataBuffer).doFinal(destBuffer);
        destBuffer.flip();
        Assert.assertEquals(BBuffer.toBytes(destBuffer), d2);
        dataBuffer.position(0);
        BytesAppender appender = new BytesAppender();
        hmacCodec.hmac(key, dataBuffer).doFinal(appender);
        Assert.assertEquals(appender.toBytes(), d2);
    }

    private void testSign(SignCodec signCodec, PublicKey publicKey, PrivateKey privateKey) {
        int offset = 6;
        int length = 998;
        byte[] data = BRandom.randomString(1888).getBytes();
        ByteBuffer dataBuffer = BBuffer.toBuffer(data, offset, length);
        byte[] d1 = signCodec.sign(privateKey, data, offset, length).doFinal();
        byte[] d2 = signCodec.sign(privateKey, dataBuffer).doFinal();
        //Assert.assertEquals(d1, d2);
        ByteBuffer destBuffer = ByteBuffer.allocateDirect(2222);
        dataBuffer.position(0);
        signCodec.sign(privateKey, dataBuffer).doFinal(destBuffer);
        destBuffer.flip();
        //Assert.assertEquals(BBuffer.toBytes(destBuffer), d2);
        dataBuffer.position(0);
        BytesAppender appender = new BytesAppender();
        signCodec.sign(privateKey, dataBuffer).doFinal(appender);
        //Assert.assertEquals(appender.toBytes(), d2);

        dataBuffer.position(0);
        Assert.assertTrue(signCodec.verify(publicKey, data, offset, length).verify(d1));
        Assert.assertTrue(signCodec.verify(publicKey, dataBuffer).verify(d2));
        destBuffer.position(0);
        Assert.assertTrue(signCodec.verify(publicKey, BIO.asInputStream(data, offset, length)).verify(destBuffer));
    }

    private void testCipher(CipherCodec cipherCodec, Key publicKey, Key privateKey) {
        int offset = 777;
        int length = 55555;
        int destOffset = 66;
        byte[] data = BRandom.randomString(66666).getBytes();

        // bytes -> bytes
        byte[] en1 = cipherCodec.encrypt(privateKey, data, offset, length).doFinal();
        byte[] de1 = cipherCodec.decrypt(publicKey, en1).doFinal();
        Assert.assertEquals(de1, Arrays.copyOfRange(data, offset, offset + length));

        // bytes -> dest bytes
        byte[] dest2 = new byte[111111];
        int outLength2 = cipherCodec.encrypt(privateKey, data, offset, length).doFinal(dest2, destOffset);
        byte[] de2 = new byte[length];
        cipherCodec.decrypt(publicKey, dest2, destOffset, outLength2).doFinal(de2, 0);
        Assert.assertEquals(de2, Arrays.copyOfRange(data, offset, offset + length));

        // bytes -> buffer
        ByteBuffer dest3 = ByteBuffer.allocateDirect(111111);
        int outLength3 = cipherCodec.encrypt(privateKey, data, offset, length).doFinal(dest3);
        ByteBuffer de3 = ByteBuffer.allocateDirect(length);
        dest3.flip();
        cipherCodec.decrypt(publicKey, dest3).doFinal(de3);
        de3.flip();
        Assert.assertEquals(BBuffer.toBytes(de3), Arrays.copyOfRange(data, offset, offset + length));
        Assert.assertEquals(outLength3, outLength2);

        // bytes -> output
        BytesAppender dest4 = new BytesAppender();
        int outLength4 = cipherCodec.encrypt(privateKey, data, offset, length).doFinal(dest4);
        byte[] de4 = new byte[length];
        cipherCodec.decrypt(publicKey, dest4.toBytes()).doFinal(de4, 0);
        Assert.assertEquals(de4, Arrays.copyOfRange(data, offset, offset + length));
        Assert.assertEquals(outLength4, outLength3);
    }

    @Test
    public void testAes() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128);
        Key key = keyGenerator.generateKey();
        Cipher cipher = Cipher.getInstance("AES");
        byte[] data = "data".getBytes();
        cipher.init(Cipher.ENCRYPT_MODE, key);
        cipher.update(data);
        byte[] en = cipher.doFinal();

        // success for byte[] type
        byte[] destBytes = new byte[data.length];
        cipher.init(Cipher.DECRYPT_MODE, key);
        cipher.update(en);
        cipher.doFinal(destBytes, 0);
        // OK
        Assert.assertEquals(destBytes, data);

        // error: Need at least 16 bytes of space in output buffer
        // If replace data.length with en.length, success:
        // destBuffer = ByteBuffer.allocate(en.length);
        ByteBuffer destBuffer = ByteBuffer.allocate(data.length);
        cipher.init(Cipher.DECRYPT_MODE, key);
        cipher.doFinal(ByteBuffer.wrap(en), destBuffer);
        destBuffer.flip();
        Assert.assertEquals(Arrays.copyOfRange(destBuffer.array(), 0, data.length), data);
    }
}
