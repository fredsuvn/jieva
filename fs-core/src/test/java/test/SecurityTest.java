package test;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.FsString;
import xyz.srclab.common.io.FsIO;
import xyz.srclab.common.security.*;

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
        FsCipher cipher = FsCipher.getInstance(cryptoAlgorithm);
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
        byte[] enDest = new byte[dataSize * 10];
        int destSize = cipher.prepare(data, 2, data.length - 2).blockSize(enBlockSize).key(publicKey).encrypt().doFinal(enDest, 1);
        enBytes = Arrays.copyOfRange(enDest, 1, destSize + 1);
        deBytes = cipher.prepare(FsIO.toInputStream(enBytes)).blockSize(deBlockSize).key(privateKey).decrypt().doFinal();
        Assert.assertEquals(Arrays.copyOfRange(data, 2, data.length), deBytes);
    }

    private void testCipherSymmetric(
        int dataSize, int enBlockSize, int deBlockSize, String keyAlgorithm, String cryptoAlgorithm) throws Exception {
        byte[] data = TestUtil.buildRandomBytes(dataSize);
        KeyGenerator keyGenerator = KeyGenerator.getInstance(keyAlgorithm);
        SecretKey key = keyGenerator.generateKey();
        FsCipher cipher = FsCipher.getInstance(cryptoAlgorithm);
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
        byte[] enDest = new byte[dataSize * 10];
        int destSize = cipher.prepare(data, 2, data.length - 2).blockSize(enBlockSize).key(key).encrypt().doFinal(enDest, 1);
        enBytes = Arrays.copyOfRange(enDest, 1, destSize + 1);
        deBytes = cipher.prepare(FsIO.toInputStream(enBytes)).blockSize(deBlockSize).key(key).decrypt().doFinal();
        Assert.assertEquals(Arrays.copyOfRange(data, 2, data.length), deBytes);
    }

    @Test
    public void testMac() throws Exception {
        byte[] data = DATA.getBytes(FsString.CHARSET);
        KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA256");
        Key macKey = keyGenerator.generateKey();
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(macKey);
        byte[] macBytes = mac.doFinal(data);
        System.out.println(macBytes.length + ", " + mac.getMacLength());
        FsMac fsMac = FsMac.getInstance("HmacSHA256");
        byte[] fsMacBytes = fsMac.prepare(data).key(macKey).doFinal();
        Assert.assertEquals(macBytes, fsMacBytes);
        byte[] enDest = new byte[mac.getMacLength() + 8];
        int destSize = fsMac.prepare(data, 2, data.length - 2).bufferSize(1).key(macKey).doFinal(enDest, 8);
        macBytes = mac.doFinal(Arrays.copyOfRange(data, 2, data.length));
        Assert.assertEquals(macBytes, Arrays.copyOfRange(enDest, 8, 8 + destSize));
        Assert.assertEquals(destSize, mac.getMacLength());
        macBytes = mac.doFinal(data);
        InputStream enStream = fsMac.prepare(data).key(macKey).doFinalStream();
        Assert.assertEquals(macBytes, FsIO.readBytes(enStream));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        long outSize = fsMac.prepare(ByteBuffer.wrap(data)).key(macKey).doFinal(out);
        Assert.assertEquals(outSize, mac.getMacLength());
        Assert.assertEquals(macBytes, out.toByteArray());
        ByteBuffer buffer = ByteBuffer.allocate(macBytes.length);
        int bufferSize = fsMac.prepare(FsIO.toInputStream(data)).key(macKey).doFinal(buffer);
        Assert.assertEquals(bufferSize, mac.getMacLength());
        buffer.flip();
        Assert.assertEquals(macBytes, FsIO.getBytes(buffer));
        System.out.println(destSize);
    }

    @Test
    public void testDigest() throws Exception {
        byte[] data = DATA.getBytes(FsString.CHARSET);
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] mdBytes = md.digest(data);
        System.out.println(mdBytes.length + ", " + md.getDigestLength());
        FsDigest fd = FsDigest.getInstance("MD5");
        byte[] fdBytes = fd.prepare(data).doFinal();
        Assert.assertEquals(mdBytes, fdBytes);
        byte[] enDest = new byte[md.getDigestLength() + 8];
        int destSize = fd.prepare(data, 2, data.length - 2).bufferSize(1).doFinal(enDest, 8);
        mdBytes = md.digest(Arrays.copyOfRange(data, 2, data.length));
        Assert.assertEquals(mdBytes, Arrays.copyOfRange(enDest, 8, 8 + destSize));
        Assert.assertEquals(destSize, md.getDigestLength());
        mdBytes = md.digest(data);
        InputStream enStream = fd.prepare(data).doFinalStream();
        Assert.assertEquals(mdBytes, FsIO.readBytes(enStream));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        long outSize = fd.prepare(ByteBuffer.wrap(data)).doFinal(out);
        Assert.assertEquals(outSize, md.getDigestLength());
        Assert.assertEquals(mdBytes, out.toByteArray());
        ByteBuffer buffer = ByteBuffer.allocate(mdBytes.length);
        int bufferSize = fd.prepare(FsIO.toInputStream(data)).doFinal(buffer);
        Assert.assertEquals(bufferSize, md.getDigestLength());
        buffer.flip();
        Assert.assertEquals(mdBytes, FsIO.getBytes(buffer));
        System.out.println(destSize);
    }

    @Test
    public void testSign() throws Exception {
        byte[] data = DATA.getBytes(FsString.CHARSET);
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        //sign
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(data);
        byte[] signBytes = signature.sign();
        System.out.println(signBytes.length);
        FsSign fsSign = FsSign.getInstance("SHA256withRSA");
        byte[] fsMacBytes = fsSign.prepare(data).key(privateKey).doFinal();
        Assert.assertEquals(signBytes, fsMacBytes);
        byte[] enDest = new byte[signBytes.length + 8];
        int destSize = fsSign.prepare(data, 2, data.length - 2).bufferSize(1).key(privateKey).doFinal(enDest, 8);
        signature.update(Arrays.copyOfRange(data, 2, data.length));
        signBytes = signature.sign();
        Assert.assertEquals(signBytes, Arrays.copyOfRange(enDest, 8, 8 + destSize));
        Assert.assertEquals(destSize, signBytes.length);
        signature.update(data);
        signBytes = signature.sign();
        InputStream enStream = fsSign.prepare(data).key(privateKey).doFinalStream();
        Assert.assertEquals(signBytes, FsIO.readBytes(enStream));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        long outSize = fsSign.prepare(ByteBuffer.wrap(data)).key(privateKey).doFinal(out);
        Assert.assertEquals(outSize, signBytes.length);
        Assert.assertEquals(signBytes, out.toByteArray());
        ByteBuffer buffer = ByteBuffer.allocate(signBytes.length);
        int bufferSize = fsSign.prepare(FsIO.toInputStream(data)).key(privateKey).doFinal(buffer);
        Assert.assertEquals(bufferSize, signBytes.length);
        buffer.flip();
        Assert.assertEquals(signBytes, FsIO.getBytes(buffer));
        System.out.println(destSize);

        //verify
        signature.initSign(privateKey);
        signature.update(data);
        signBytes = signature.sign();
        signature.initVerify(publicKey);
        signature.update(data);
        Assert.assertTrue(signature.verify(signBytes));
        Assert.assertTrue(fsSign.prepare(data).key(publicKey).verify(signBytes));
        signature.initVerify(publicKey);
        signature.update(data);
        Assert.assertTrue(signature.verify(out.toByteArray()));
        Assert.assertTrue(fsSign.prepare(FsIO.toInputStream(data)).key(publicKey).verify(signBytes));
    }

    @Test
    public void testKeyGen() throws Exception {
        FsKeyGen keyGen = FsKeyGen.getInstance("AES");
        Key key = keyGen.generateKey();
        byte[] keyBytes = key.getEncoded();
        Assert.assertEquals(key, FsKeyGen.generate("AES", keyBytes));

        FsKeyPairGen keyPairGen = FsKeyPairGen.getInstance("RSA");
        KeyPair keyPair = keyPairGen.generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();
        byte[] publicBytes = publicKey.getEncoded();
        byte[] privateBytes = privateKey.getEncoded();
        FsKeyFactory keyFactory = FsKeyFactory.getInstance("RSA");
        PublicKey newPublicKey = keyFactory.generatePublic(FsKeyPairGen.generateX509(publicBytes));
        PrivateKey newPrivateKey = keyFactory.generatePrivate(FsKeyPairGen.generatePkcs8(privateBytes));
        Assert.assertEquals(publicKey, newPublicKey);
        Assert.assertEquals(privateKey, newPrivateKey);
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
        //System.out.println(cipher.getBlockSize());
        //System.out.println(cipher.getOutputSize(0));
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
        //System.out.println(cipher.getBlockSize());
        //System.out.println(cipher.getOutputSize(0));
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
