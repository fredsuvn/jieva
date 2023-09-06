package test;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.FsString;
import xyz.srclab.common.io.FsIO;
import xyz.srclab.common.security.FsCipher;
import xyz.srclab.common.security.FsCrypto;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;

public class SecurityTest {

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
        FsCipher cipher = FsCipher.getCipher(cryptoAlgorithm);
        byte[] enBytes = cipher.prepare(data).blockSize(enBlockSize).key(publicKey).encrypt().doFinal();
        byte[] deBytes = cipher.prepare(enBytes).blockSize(deBlockSize).key(privateKey).decrypt().doFinal();
        Assert.assertEquals(data, deBytes);
        enBytes = cipher.prepare(ByteBuffer.wrap(data)).blockSize(enBlockSize).key(publicKey).encrypt().doFinal();
        deBytes = cipher.prepare(ByteBuffer.wrap(enBytes)).blockSize(deBlockSize).key(privateKey).decrypt().doFinal();
        Assert.assertEquals(data, deBytes);
        enBytes = cipher.prepare(FsIO.toInputStream(data)).blockSize(enBlockSize).key(publicKey).encrypt().doFinal();
        deBytes = cipher.prepare(FsIO.toInputStream(enBytes)).blockSize(deBlockSize).key(privateKey).decrypt().doFinal();
        Assert.assertEquals(data, deBytes);
        enBytes = FsIO.readBytes(
            cipher.prepare(FsIO.toInputStream(data)).blockSize(enBlockSize).key(publicKey).encrypt().doFinalStream());
        deBytes = FsIO.readBytes(
            cipher.prepare(FsIO.toInputStream(enBytes)).blockSize(deBlockSize).key(privateKey).decrypt().doFinalStream());
        Assert.assertEquals(data, deBytes);
    }

    private void testCipherSymmetric(
        int dataSize, int enBlockSize, int deBlockSize, String keyAlgorithm, String cryptoAlgorithm) throws Exception {
        byte[] data = TestUtil.buildRandomBytes(dataSize);
        KeyGenerator keyGenerator = KeyGenerator.getInstance(keyAlgorithm);
        SecretKey key = keyGenerator.generateKey();
        FsCipher cipher = FsCipher.getCipher(cryptoAlgorithm);
        byte[] enBytes = cipher.prepare(data).blockSize(enBlockSize).key(key).encrypt().doFinal();
        byte[] deBytes = cipher.prepare(enBytes).blockSize(deBlockSize).key(key).decrypt().doFinal();
        Assert.assertEquals(data, deBytes);
        enBytes = cipher.prepare(ByteBuffer.wrap(data)).blockSize(enBlockSize).key(key).encrypt().doFinal();
        deBytes = cipher.prepare(ByteBuffer.wrap(enBytes)).blockSize(deBlockSize).key(key).decrypt().doFinal();
        Assert.assertEquals(data, deBytes);
        enBytes = cipher.prepare(FsIO.toInputStream(data)).blockSize(enBlockSize).key(key).encrypt().doFinal();
        deBytes = cipher.prepare(FsIO.toInputStream(enBytes)).blockSize(deBlockSize).key(key).decrypt().doFinal();
        Assert.assertEquals(data, deBytes);
        enBytes = FsIO.readBytes(
            cipher.prepare(FsIO.toInputStream(data)).blockSize(enBlockSize).key(key).encrypt().doFinalStream());
        deBytes = FsIO.readBytes(
            cipher.prepare(FsIO.toInputStream(enBytes)).blockSize(deBlockSize).key(key).decrypt().doFinalStream());
        Assert.assertEquals(data, deBytes);
    }

    @Test
    public void testCrypto() throws Exception {
        byte[] data = DATA.getBytes(FsString.CHARSET);
        byte[] data2 = TestUtil.buildRandomBytes(150);
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");

        //encrypt
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        System.out.println(cipher.getBlockSize());
        System.out.println(cipher.getOutputSize(0));
        ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
        FsCrypto.encrypt(cipher, publicKey, new ByteArrayInputStream(data), outBytes, 245, null);
        byte[] enBytes = outBytes.toByteArray();
        ByteBuffer outBuffer = ByteBuffer.allocate(enBytes.length);
        FsCrypto.encrypt(cipher, publicKey, ByteBuffer.wrap(data), outBuffer, 245, null);
        outBuffer.flip();
        byte[] enBuffer = FsIO.getBytes(outBuffer);
        outBytes.reset();
        FsCrypto.encrypt(cipher, publicKey, new ByteArrayInputStream(data2), outBytes, 0, null);
        byte[] enBytes2 = outBytes.toByteArray();
        outBuffer.clear();
        FsCrypto.encrypt(cipher, publicKey, ByteBuffer.wrap(data2), outBuffer, 0, null);
        outBuffer.flip();
        byte[] enBuffer2 = FsIO.getBytes(outBuffer);

        //decrypt
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        System.out.println(cipher.getBlockSize());
        System.out.println(cipher.getOutputSize(0));
        outBytes.reset();
        FsCrypto.decrypt(cipher, privateKey, new ByteArrayInputStream(enBytes), outBytes, 256, null);
        byte[] deBytes = outBytes.toByteArray();
        Assert.assertEquals(deBytes, data);
        outBuffer.clear();
        FsCrypto.decrypt(cipher, privateKey, ByteBuffer.wrap(enBuffer), outBuffer, 256, null);
        outBuffer.flip();
        byte[] deBuffer = FsIO.getBytes(outBuffer);
        Assert.assertEquals(deBuffer, data);
        outBytes.reset();
        FsCrypto.decrypt(cipher, privateKey, new ByteArrayInputStream(enBytes2), outBytes, 0, null);
        byte[] deBytes2 = outBytes.toByteArray();
        Assert.assertEquals(deBytes2, data2);
        outBuffer.clear();
        FsCrypto.decrypt(cipher, privateKey, ByteBuffer.wrap(enBuffer2), outBuffer, 0, null);
        outBuffer.flip();
        byte[] deBuffer2 = FsIO.getBytes(outBuffer);
        Assert.assertEquals(deBuffer2, data2);
    }

    @Test
    public void testJavaCipher() throws Exception {
        byte[] data = TestUtil.buildRandomBytes(150);
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        out.write(cipher.doFinal(data, 0, 100));
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        out.write(cipher.doFinal(data, 100, 50));
        byte[] enBytes = out.toByteArray();
        System.out.println(enBytes.length);
        out.reset();
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        out.write(cipher.doFinal(enBytes, 0, 256));
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        out.write(cipher.doFinal(enBytes, 256, 256));
        byte[] deBytes = out.toByteArray();
        System.out.println(deBytes.length);
        Assert.assertEquals(data, deBytes);
    }
}
