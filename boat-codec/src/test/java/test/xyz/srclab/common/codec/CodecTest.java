package test.xyz.srclab.common.codec;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.BRandom;
import xyz.srclab.common.codec.*;
import xyz.srclab.common.codec.rsa.BRsa;
import xyz.srclab.common.codec.sm2.BSm2;
import xyz.srclab.common.io.BBuffer;
import xyz.srclab.common.io.BIO;
import xyz.srclab.common.io.BytesAppender;

import java.nio.ByteBuffer;
import java.security.Key;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

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

    private void testDigest(DigestCodec digestCodec) {
        int offset = 6;
        int length = 998;
        byte[] data = BRandom.randomString(1888).getBytes();
        ByteBuffer dataBuffer = ByteBuffer.allocate(data.length);
        dataBuffer.put(data, offset, length);
        dataBuffer.flip();
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
        ByteBuffer dataBuffer = ByteBuffer.allocate(data.length);
        dataBuffer.put(data, offset, length);
        dataBuffer.flip();
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
        ByteBuffer dataBuffer = ByteBuffer.allocate(data.length);
        dataBuffer.put(data, offset, length);
        dataBuffer.flip();
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
}
