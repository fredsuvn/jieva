package test.xyz.srclab.common.codec;

import org.testng.Assert;
import xyz.srclab.common.base.BRandom;
import xyz.srclab.common.codec.CipherCodec;
import xyz.srclab.common.codec.DigestCodec;
import xyz.srclab.common.codec.HmacCodec;
import xyz.srclab.common.codec.SignCodec;
import xyz.srclab.common.io.BBuffer;
import xyz.srclab.common.io.BIO;
import xyz.srclab.common.io.BytesAppender;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;

public class CipherTester {

    static void testDigest(DigestCodec digestCodec) {
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
        Assert.assertEquals(BBuffer.getBytes(destBuffer), d2);
        dataBuffer.position(0);
        BytesAppender appender = new BytesAppender();
        digestCodec.digest(dataBuffer).doFinal(appender);
        Assert.assertEquals(appender.toBytes(), d2);
        byte[] d3 = digestCodec.digest(BIO.asInputStream(data, offset, length)).doFinal();
        Assert.assertEquals(d3, d1);
    }

    static void testHmac(HmacCodec hmacCodec, Key key) {
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
        Assert.assertEquals(BBuffer.getBytes(destBuffer), d2);
        dataBuffer.position(0);
        BytesAppender appender = new BytesAppender();
        hmacCodec.hmac(key, dataBuffer).doFinal(appender);
        Assert.assertEquals(appender.toBytes(), d2);
    }

    static void testSign(SignCodec signCodec, PublicKey publicKey, PrivateKey privateKey) {
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

    static void testCipher(CipherCodec cipherCodec, Key publicKey, Key privateKey) throws Exception {
        int offset = 777;
        int length = 55555;
        int destOffset = 66;
        byte[] data = BRandom.randomString(66666).getBytes();
        ByteBuffer dataBuffer = BBuffer.toBuffer(data, offset, length);
        InputStream dataStream = BIO.asInputStream(data, offset, length);

        //1 bytes -> bytes
        byte[] en1 = cipherCodec.encrypt(publicKey, data, offset, length).doFinal();
        byte[] de1 = cipherCodec.decrypt(privateKey, en1).doFinal();
        Assert.assertEquals(de1, Arrays.copyOfRange(data, offset, offset + length));

        //2 bytes -> dest bytes
        byte[] dest2 = new byte[111111];
        int enLength2 = cipherCodec.encrypt(publicKey, data, offset, length).doFinal(dest2, destOffset);
        byte[] de2 = new byte[dest2.length];
        int deLength2 = cipherCodec.decrypt(privateKey, dest2, destOffset, enLength2).doFinal(de2, 0);
        Assert.assertEquals(
            Arrays.copyOfRange(de2, 0, deLength2),
            Arrays.copyOfRange(data, offset, offset + length)
        );

        //3 bytes -> buffer
        ByteBuffer dest3 = ByteBuffer.allocateDirect(111111);
        int enLength3 = cipherCodec.encrypt(publicKey, data, offset, length).doFinal(dest3);
        ByteBuffer de3 = ByteBuffer.allocate(dest3.capacity());
        dest3.flip();
        int deLength3 = cipherCodec.decrypt(privateKey, BBuffer.getBytes(BBuffer.getBuffer(dest3, enLength3))).doFinal(de3);
        de3.flip();
        Assert.assertEquals(
            BBuffer.getBytes(BBuffer.getBuffer(de3, deLength3)),
            Arrays.copyOfRange(data, offset, offset + length)
        );

        //4 bytes -> output
        BytesAppender dest4 = new BytesAppender();
        long enLength4 = cipherCodec.encrypt(publicKey, data, offset, length).doFinal(dest4);
        BytesAppender de4 = new BytesAppender();
        long deLength4 = cipherCodec.decrypt(privateKey, dest4.toBytes()).doFinal(de4);
        Assert.assertEquals(de4.toBytes(), Arrays.copyOfRange(data, offset, offset + length));
        Assert.assertEquals(enLength4, enLength3);
        Assert.assertEquals(deLength4, deLength3);

        //5 buffer -> bytes
        byte[] en5 = cipherCodec.encrypt(publicKey, dataBuffer).doFinal();
        dataBuffer.flip();
        byte[] de5 = cipherCodec.decrypt(privateKey, en5).doFinal();
        Assert.assertEquals(de5, Arrays.copyOfRange(data, offset, offset + length));

        //6 buffer -> dest bytes
        byte[] dest6 = new byte[111111];
        int enLength6 = cipherCodec.encrypt(publicKey, dataBuffer).doFinal(dest6, destOffset);
        dataBuffer.flip();
        byte[] de6 = new byte[dest6.length];
        int deLength6 = cipherCodec.decrypt(privateKey, dest6, destOffset, enLength6).doFinal(de6, 0);
        Assert.assertEquals(
            Arrays.copyOfRange(de6, 0, deLength6),
            Arrays.copyOfRange(data, offset, offset + length)
        );

        //7 buffer -> buffer
        ByteBuffer dest7 = ByteBuffer.allocateDirect(111111);
        int enLength7 = cipherCodec.encrypt(publicKey, dataBuffer).doFinal(dest7);
        dataBuffer.flip();
        ByteBuffer de7 = ByteBuffer.allocate(dest7.capacity());
        dest7.flip();
        int deLength7 = cipherCodec.decrypt(privateKey, BBuffer.getBytes(BBuffer.getBuffer(dest7, enLength7))).doFinal(de7);
        de7.flip();
        Assert.assertEquals(
            BBuffer.getBytes(BBuffer.getBuffer(de7, deLength7)),
            Arrays.copyOfRange(data, offset, offset + length)
        );

        //8 buffer -> output
        BytesAppender dest8 = new BytesAppender();
        long enLength8 = cipherCodec.encrypt(publicKey, dataBuffer).doFinal(dest8);
        dataBuffer.flip();
        BytesAppender de8 = new BytesAppender();
        long deLength8 = cipherCodec.decrypt(privateKey, dest8.toBytes()).doFinal(de8);
        Assert.assertEquals(de8.toBytes(), Arrays.copyOfRange(data, offset, offset + length));
        Assert.assertEquals(enLength8, enLength7);
        Assert.assertEquals(deLength8, deLength7);

        //a stream -> bytes
        byte[] enA = cipherCodec.encrypt(publicKey, dataStream).doFinal();
        dataStream.reset();
        byte[] deA = cipherCodec.decrypt(privateKey, enA).doFinal();
        Assert.assertEquals(deA, Arrays.copyOfRange(data, offset, offset + length));

        //b stream -> dest bytes
        byte[] destB = new byte[111111];
        int enLengthB = cipherCodec.encrypt(publicKey, dataStream).doFinal(destB, destOffset);
        dataStream.reset();
        byte[] deB = new byte[dest6.length];
        int deLengthB = cipherCodec.decrypt(privateKey, destB, destOffset, enLengthB).doFinal(deB, 0);
        Assert.assertEquals(
            Arrays.copyOfRange(deB, 0, deLengthB),
            Arrays.copyOfRange(data, offset, offset + length)
        );

        //c stream -> buffer
        ByteBuffer destC = ByteBuffer.allocateDirect(111111);
        int enLengthC = cipherCodec.encrypt(publicKey, dataStream).doFinal(destC);
        dataStream.reset();
        ByteBuffer deC = ByteBuffer.allocate(destC.capacity());
        destC.flip();
        int deLengthC = cipherCodec.decrypt(privateKey, BBuffer.getBytes(BBuffer.getBuffer(destC, enLengthC))).doFinal(deC);
        deC.flip();
        Assert.assertEquals(
            BBuffer.getBytes(BBuffer.getBuffer(deC, deLengthC)),
            Arrays.copyOfRange(data, offset, offset + length)
        );

        //d stream -> output
        BytesAppender destD = new BytesAppender();
        long enLengthD = cipherCodec.encrypt(publicKey, dataStream).doFinal(destD);
        dataStream.reset();
        BytesAppender deD = new BytesAppender();
        long deLengthD = cipherCodec.decrypt(privateKey, destD.toBytes()).doFinal(deD);
        Assert.assertEquals(deD.toBytes(), Arrays.copyOfRange(data, offset, offset + length));
        Assert.assertEquals(enLengthD, enLength7);
        Assert.assertEquals(deLengthD, deLength7);
    }
}
