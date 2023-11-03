package test;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.fsgek.common.base.GekString;
import xyz.fsgek.common.codec.GekCodec;
import xyz.fsgek.common.io.GekBuffer;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;

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
        Assert.assertEquals(GekBuffer.getBytes(comBuffer), data);

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
        deResultSize = GekCodec.doCipher(cipher, new ByteArrayInputStream(GekBuffer.getBytes(outBuffer)), comBuffer, deBlockSize);
        Assert.assertEquals(deResultSize, dataSize);
        comBuffer.flip();
        Assert.assertEquals(GekBuffer.getBytes(comBuffer), data);

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

//    private void testCodecDigest(int dataSize, String algorithm) throws Exception {
//        byte[] data = GekString.encode(TestUtil.buildRandomString(dataSize, 0));
//        MessageDigest digest = MessageDigest.getInstance(algorithm);
//
//        //buffer -> buffer
//        ByteBuffer inBuffer = ByteBuffer.wrap(data);
//        ByteBuffer outBuffer = ByteBuffer.allocate(digest.getDigestLength());
//        byte[] result = GekCodec.doDigest(inBuffer);
//        Assert.assertEquals(enResultSize, enOutSize);
//        outBuffer.flip();
//        cipher.init(Cipher.DECRYPT_MODE, decryptKey);
//        System.out.println(cipher.getOutputSize(deBlockSize) + ", " + cipher.getBlockSize());
//        ByteBuffer comBuffer = ByteBuffer.allocate(decodeSize(cipher, data.length, deBlockSize));
//        long deResultSize = GekCodec.doCipher(cipher, outBuffer, comBuffer, deBlockSize);
//        Assert.assertEquals(deResultSize, dataSize);
//        comBuffer.flip();
//        Assert.assertEquals(GekBuffer.getBytes(comBuffer), data);
//
//        //buffer -> stream
//        cipher.init(Cipher.ENCRYPT_MODE, encryptKey);
//        inBuffer = ByteBuffer.wrap(data);
//        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
//        enResultSize = GekCodec.doCipher(cipher, inBuffer, outStream, enBlockSize);
//        Assert.assertEquals(enResultSize, enOutSize);
//        cipher.init(Cipher.DECRYPT_MODE, decryptKey);
//        ByteArrayOutputStream comStream = new ByteArrayOutputStream();
//        deResultSize = GekCodec.doCipher(cipher, ByteBuffer.wrap(outStream.toByteArray()), comStream, deBlockSize);
//        Assert.assertEquals(deResultSize, dataSize);
//        Assert.assertEquals(comStream.toByteArray(), data);
//
//        //stream -> buffer
//        cipher.init(Cipher.ENCRYPT_MODE, encryptKey);
//        ByteArrayInputStream inStream = new ByteArrayInputStream(data);
//        outBuffer.clear();
//        enResultSize = GekCodec.doCipher(cipher, inStream, outBuffer, enBlockSize);
//        Assert.assertEquals(enResultSize, enOutSize);
//        outBuffer.flip();
//        cipher.init(Cipher.DECRYPT_MODE, decryptKey);
//        comBuffer.clear();
//        deResultSize = GekCodec.doCipher(cipher, new ByteArrayInputStream(GekBuffer.getBytes(outBuffer)), comBuffer, deBlockSize);
//        Assert.assertEquals(deResultSize, dataSize);
//        comBuffer.flip();
//        Assert.assertEquals(GekBuffer.getBytes(comBuffer), data);
//
//        //stream -> stream
//        cipher.init(Cipher.ENCRYPT_MODE, encryptKey);
//        inStream = new ByteArrayInputStream(data);
//        outStream.reset();
//        enResultSize = GekCodec.doCipher(cipher, inStream, outStream, enBlockSize);
//        Assert.assertEquals(enResultSize, enOutSize);
//        cipher.init(Cipher.DECRYPT_MODE, decryptKey);
//        comStream.reset();
//        deResultSize = GekCodec.doCipher(cipher, ByteBuffer.wrap(outStream.toByteArray()), comStream, deBlockSize);
//        Assert.assertEquals(deResultSize, dataSize);
//        Assert.assertEquals(comStream.toByteArray(), data);
//    }
}
