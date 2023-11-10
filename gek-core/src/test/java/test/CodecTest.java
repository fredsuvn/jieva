package test;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.fsgek.annotations.Nullable;
import xyz.fsgek.common.base.GekString;
import xyz.fsgek.common.codec.CipherProcess;
import xyz.fsgek.common.codec.GekCodec;
import xyz.fsgek.common.io.GekIO;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.security.*;
import java.util.Arrays;

public class CodecTest {

    @Test
    public void testCodec() throws Exception {
        testCodecCipher(1111, "RSA", "RSA",
            245, 256, 1280, true);
        testCodecCipher(3, "RSA", "RSA/ECB/PKCS1Padding",
            245, 256, 256, true);
        testCodecCipher(33333, "RSA", "RSA/ECB/PKCS1Padding",
            245, 256, 35072, true);
        testCodecCipher(88888, "AES", "AES",
            16, 32, 177776, false);
        testCodecCipher(1, "AES", "AES",
            16, 32, 16, false);
        testCodecCipher(88888, "AES", "AES",
            0, 0, 88896, false);
        testCodecCipher(1, "AES", "AES",
            0, 0, 16, false);
        testCodecCipher(15, "AES", "AES",
            0, 0, 16, false);
        testCodecCipher(17, "AES", "AES",
            0, 0, 32, false);
    }

    private void testCodecCipher(
        int dataSize, String keyAlgorithm, String dataAlgorithm,
        int enBlockSize, int deBlockSize, long enOutSize, boolean isAsymmetric
    ) throws Exception {
        byte[] data = GekString.encode(TestUtil.buildRandomString(dataSize, 0));
        Cipher cipher = Cipher.getInstance(dataAlgorithm);
        Key encryptKey;
        Key decryptKey;
        if (isAsymmetric) {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(keyAlgorithm);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            encryptKey = keyPair.getPublic();
            decryptKey = keyPair.getPrivate();
        } else {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(keyAlgorithm);
            SecretKey key = keyGenerator.generateKey();
            encryptKey = key;
            decryptKey = key;
        }

        //buffer -> buffer
        cipher.init(Cipher.ENCRYPT_MODE, encryptKey);
        System.out.println(cipher.getOutputSize(enBlockSize) + ", " + cipher.getBlockSize());
        ByteBuffer inBuffer = ByteBuffer.wrap(data);
        ByteBuffer outBuffer = ByteBuffer.allocate(decodeSize(cipher, data.length, deBlockSize));
        long enResultSize = GekCodec.doCipher(cipher, inBuffer, outBuffer, enBlockSize);
        Assert.assertEquals(enResultSize, enOutSize);
        outBuffer.flip();
        cipher.init(Cipher.DECRYPT_MODE, decryptKey);
        System.out.println(cipher.getOutputSize(deBlockSize) + ", " + cipher.getBlockSize());
        ByteBuffer comBuffer = ByteBuffer.allocate(decodeSize(cipher, data.length, deBlockSize));
        long deResultSize = GekCodec.doCipher(cipher, outBuffer, comBuffer, deBlockSize);
        Assert.assertEquals(deResultSize, dataSize);
        comBuffer.flip();
        Assert.assertEquals(GekIO.read(comBuffer), data);

        //buffer -> stream
        cipher.init(Cipher.ENCRYPT_MODE, encryptKey);
        inBuffer = ByteBuffer.wrap(data);
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        enResultSize = GekCodec.doCipher(cipher, inBuffer, outStream, enBlockSize);
        Assert.assertEquals(enResultSize, enOutSize);
        cipher.init(Cipher.DECRYPT_MODE, decryptKey);
        ByteArrayOutputStream comStream = new ByteArrayOutputStream();
        deResultSize = GekCodec.doCipher(cipher, ByteBuffer.wrap(outStream.toByteArray()), comStream, deBlockSize);
        Assert.assertEquals(deResultSize, dataSize);
        Assert.assertEquals(comStream.toByteArray(), data);

        //stream -> buffer
        cipher.init(Cipher.ENCRYPT_MODE, encryptKey);
        ByteArrayInputStream inStream = new ByteArrayInputStream(data);
        outBuffer.clear();
        enResultSize = GekCodec.doCipher(cipher, inStream, outBuffer, enBlockSize);
        Assert.assertEquals(enResultSize, enOutSize);
        outBuffer.flip();
        cipher.init(Cipher.DECRYPT_MODE, decryptKey);
        comBuffer.clear();
        deResultSize = GekCodec.doCipher(cipher, new ByteArrayInputStream(GekIO.read(outBuffer)), comBuffer, deBlockSize);
        Assert.assertEquals(deResultSize, dataSize);
        comBuffer.flip();
        Assert.assertEquals(GekIO.read(comBuffer), data);

        //stream -> stream
        cipher.init(Cipher.ENCRYPT_MODE, encryptKey);
        inStream = new ByteArrayInputStream(data);
        outStream.reset();
        enResultSize = GekCodec.doCipher(cipher, inStream, outStream, enBlockSize);
        Assert.assertEquals(enResultSize, enOutSize);
        cipher.init(Cipher.DECRYPT_MODE, decryptKey);
        comStream.reset();
        deResultSize = GekCodec.doCipher(cipher, ByteBuffer.wrap(outStream.toByteArray()), comStream, deBlockSize);
        Assert.assertEquals(deResultSize, dataSize);
        Assert.assertEquals(comStream.toByteArray(), data);
    }

    private int decodeSize(Cipher cipher, int dataSize, int deBlockSize) {
        int max = Math.max(dataSize * 10, deBlockSize);
        return Math.max(max, cipher.getBlockSize());
    }

    @Test
    public void testCipher() throws Exception {
        testCipherAsymmetric(150, 88, 256, "RSA", "RSA/ECB/PKCS1Padding", null);
        testCipherAsymmetric(1500, 188, 256, "RSA", "RSA", null);
        testCipherSymmetric(150, 999, 9999, "AES", "AES", null);
        testCipherSymmetric(1500, 16, 32, "AES", "AES", null);
    }

    private void testCipherAsymmetric(
        int dataSize, int enBlockSize, int deBlockSize, String keyAlgorithm, String cryptoAlgorithm, @Nullable Provider provider) throws Exception {
        byte[] data = TestUtil.buildRandomBytes(dataSize);
        KeyPairGenerator keyPairGenerator = provider == null ?
            KeyPairGenerator.getInstance(keyAlgorithm) : KeyPairGenerator.getInstance(keyAlgorithm, provider);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();
        Cipher c = provider == null ? Cipher.getInstance(cryptoAlgorithm) : Cipher.getInstance(cryptoAlgorithm, provider);
        CipherProcess cipher = GekCodec.cipher(c);
        byte[] enBytes = cipher.input(data).blockSize(enBlockSize).key(publicKey).encrypt().finalBytes();
        byte[] deBytes = cipher.input(enBytes).blockSize(deBlockSize).key(privateKey).decrypt().finalBytes();
        Assert.assertEquals(data, deBytes);
        enBytes = cipher.input(ByteBuffer.wrap(data)).blockSize(enBlockSize).key(publicKey).encrypt().finalBytes();
        deBytes = cipher.input(ByteBuffer.wrap(enBytes)).blockSize(deBlockSize).key(privateKey).decrypt().finalBytes();
        Assert.assertEquals(data, deBytes);
        enBytes = cipher.input(GekIO.toInputStream(data)).blockSize(enBlockSize).key(publicKey).encrypt().finalBytes();
        deBytes = cipher.input(GekIO.toInputStream(enBytes)).blockSize(deBlockSize).key(privateKey).decrypt().finalBytes();
        Assert.assertEquals(data, deBytes);
        enBytes = GekIO.read(
            cipher.input(GekIO.toInputStream(data)).blockSize(enBlockSize).key(publicKey).encrypt().finalStream());
        deBytes = GekIO.read(
            cipher.input(GekIO.toInputStream(enBytes)).blockSize(deBlockSize).key(privateKey).decrypt().finalStream());
        Assert.assertEquals(data, deBytes);
        byte[] enDest = new byte[dataSize * 10];
        int destSize = (int) cipher.input(data, 2, data.length - 2).blockSize(enBlockSize).key(publicKey).encrypt().output(enDest, 1).doFinal();
        enBytes = Arrays.copyOfRange(enDest, 1, destSize + 1);
        deBytes = cipher.input(GekIO.toInputStream(enBytes)).blockSize(deBlockSize).key(privateKey).decrypt().finalBytes();
        Assert.assertEquals(Arrays.copyOfRange(data, 2, data.length), deBytes);
    }

    private void testCipherSymmetric(
        int dataSize, int enBlockSize, int deBlockSize, String keyAlgorithm, String cryptoAlgorithm, @Nullable Provider provider) throws Exception {
        byte[] data = TestUtil.buildRandomBytes(dataSize);
        KeyGenerator keyGenerator = provider == null ?
            KeyGenerator.getInstance(keyAlgorithm) : KeyGenerator.getInstance(keyAlgorithm, provider);
        SecretKey key = keyGenerator.generateKey();
        Cipher c = provider == null ? Cipher.getInstance(cryptoAlgorithm) : Cipher.getInstance(cryptoAlgorithm, provider);
        CipherProcess cipher = GekCodec.cipher(c);
        byte[] enBytes = cipher.input(data).blockSize(enBlockSize).key(key).encrypt().finalBytes();
        byte[] deBytes = cipher.input(enBytes).blockSize(deBlockSize).key(key).decrypt().finalBytes();
        Assert.assertEquals(data, deBytes);
        enBytes = cipher.input(ByteBuffer.wrap(data)).blockSize(enBlockSize).key(key).encrypt().finalBytes();
        deBytes = cipher.input(ByteBuffer.wrap(enBytes)).blockSize(deBlockSize).key(key).decrypt().finalBytes();
        Assert.assertEquals(data, deBytes);
        enBytes = cipher.input(GekIO.toInputStream(data)).blockSize(enBlockSize).key(key).encrypt().finalBytes();
        deBytes = cipher.input(GekIO.toInputStream(enBytes)).blockSize(deBlockSize).key(key).decrypt().finalBytes();
        Assert.assertEquals(data, deBytes);
        enBytes = GekIO.read(
            cipher.input(GekIO.toInputStream(data)).blockSize(enBlockSize).key(key).encrypt().finalStream());
        deBytes = GekIO.read(
            cipher.input(GekIO.toInputStream(enBytes)).blockSize(deBlockSize).key(key).decrypt().finalStream());
        Assert.assertEquals(data, deBytes);
        byte[] enDest = new byte[dataSize * 10];
        int destSize = (int) cipher.input(data, 2, data.length - 2).blockSize(enBlockSize).key(key).encrypt().output(enDest, 1).doFinal();
        enBytes = Arrays.copyOfRange(enDest, 1, destSize + 1);
        deBytes = cipher.input(GekIO.toInputStream(enBytes)).blockSize(deBlockSize).key(key).decrypt().finalBytes();
        Assert.assertEquals(Arrays.copyOfRange(data, 2, data.length), deBytes);
    }
}
