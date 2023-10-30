package test;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.fsgek.common.base.GekChars;
import xyz.fsgek.common.io.GekBuffer;
import xyz.fsgek.common.io.GekIO;
import xyz.fsgek.common.security.*;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.security.*;
import java.util.Arrays;

public class CodecTest {

    private static final String DATA = TestUtil.buildRandomString(256, 256);

    @Test
    public void testCipher() throws Exception {
        testCipherAsymmetric(150, 88, 256, "RSA", "RSA/ECB/PKCS1Padding");
        testCipherAsymmetric(1500, 188, 256, "RSA", "RSA");
        testCipherSymmetric(150, 999, 9999, "AES", "AES");
        testCipherSymmetric(1500, 16, 32, "AES", "AES");
    }

    private void testCipherAsymmetric(
        int dataSize, int enBlockSize, int deBlockSize, String keyAlgorithm, String cryptoAlgorithm) throws Exception {
        byte[] data = TestUtil.buildRandomBytes(dataSize);
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(keyAlgorithm);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();
        GekCipher cipher = GekCipher.getInstance(cryptoAlgorithm);
        byte[] enBytes = cipher.prepare(data).blockSize(enBlockSize).key(publicKey).encrypt().doFinal();
        byte[] deBytes = cipher.prepare(enBytes).blockSize(deBlockSize).key(privateKey).decrypt().doFinal();
        Assert.assertEquals(data, deBytes);
        enBytes = cipher.prepare(ByteBuffer.wrap(data)).blockSize(enBlockSize).key(publicKey).encrypt().doFinal();
        deBytes = cipher.prepare(ByteBuffer.wrap(enBytes)).blockSize(deBlockSize).key(privateKey).decrypt().doFinal();
        Assert.assertEquals(data, deBytes);
        enBytes = cipher.prepare(GekIO.toInputStream(data)).blockSize(enBlockSize).key(publicKey).encrypt().doFinal();
        deBytes = cipher.prepare(GekIO.toInputStream(enBytes)).blockSize(deBlockSize).key(privateKey).decrypt().doFinal();
        Assert.assertEquals(data, deBytes);
        enBytes = GekIO.readBytes(
            cipher.prepare(GekIO.toInputStream(data)).blockSize(enBlockSize).key(publicKey).encrypt().doFinalStream());
        deBytes = GekIO.readBytes(
            cipher.prepare(GekIO.toInputStream(enBytes)).blockSize(deBlockSize).key(privateKey).decrypt().doFinalStream());
        Assert.assertEquals(data, deBytes);
        byte[] enDest = new byte[dataSize * 10];
        int destSize = cipher.prepare(data, 2, data.length - 2).blockSize(enBlockSize).key(publicKey).encrypt().doFinal(enDest, 1);
        enBytes = Arrays.copyOfRange(enDest, 1, destSize + 1);
        deBytes = cipher.prepare(GekIO.toInputStream(enBytes)).blockSize(deBlockSize).key(privateKey).decrypt().doFinal();
        Assert.assertEquals(Arrays.copyOfRange(data, 2, data.length), deBytes);
    }

    private void testCipherSymmetric(
        int dataSize, int enBlockSize, int deBlockSize, String keyAlgorithm, String cryptoAlgorithm) throws Exception {
        byte[] data = TestUtil.buildRandomBytes(dataSize);
        KeyGenerator keyGenerator = KeyGenerator.getInstance(keyAlgorithm);
        SecretKey key = keyGenerator.generateKey();
        GekCipher cipher = GekCipher.getInstance(cryptoAlgorithm);
        byte[] enBytes = cipher.prepare(data).blockSize(enBlockSize).key(key).encrypt().doFinal();
        byte[] deBytes = cipher.prepare(enBytes).blockSize(deBlockSize).key(key).decrypt().doFinal();
        Assert.assertEquals(data, deBytes);
        enBytes = cipher.prepare(ByteBuffer.wrap(data)).blockSize(enBlockSize).key(key).encrypt().doFinal();
        deBytes = cipher.prepare(ByteBuffer.wrap(enBytes)).blockSize(deBlockSize).key(key).decrypt().doFinal();
        Assert.assertEquals(data, deBytes);
        enBytes = cipher.prepare(GekIO.toInputStream(data)).blockSize(enBlockSize).key(key).encrypt().doFinal();
        deBytes = cipher.prepare(GekIO.toInputStream(enBytes)).blockSize(deBlockSize).key(key).decrypt().doFinal();
        Assert.assertEquals(data, deBytes);
        enBytes = GekIO.readBytes(
            cipher.prepare(GekIO.toInputStream(data)).blockSize(enBlockSize).key(key).encrypt().doFinalStream());
        deBytes = GekIO.readBytes(
            cipher.prepare(GekIO.toInputStream(enBytes)).blockSize(deBlockSize).key(key).decrypt().doFinalStream());
        Assert.assertEquals(data, deBytes);
        byte[] enDest = new byte[dataSize * 10];
        int destSize = cipher.prepare(data, 2, data.length - 2).blockSize(enBlockSize).key(key).encrypt().doFinal(enDest, 1);
        enBytes = Arrays.copyOfRange(enDest, 1, destSize + 1);
        deBytes = cipher.prepare(GekIO.toInputStream(enBytes)).blockSize(deBlockSize).key(key).decrypt().doFinal();
        Assert.assertEquals(Arrays.copyOfRange(data, 2, data.length), deBytes);
    }
}
