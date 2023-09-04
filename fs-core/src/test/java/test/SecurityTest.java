package test;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.FsString;
import xyz.srclab.common.io.FsIO;
import xyz.srclab.common.security.FsCrypto;

import javax.crypto.Cipher;
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

    @Test
    public void testCrypto() throws Exception {
        byte[] data = DATA.getBytes(FsString.CHARSET);
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
        System.out.println(data.length);
        System.out.println(enBytes.length);
        ByteBuffer outBuffer = ByteBuffer.allocate(enBytes.length);
        FsCrypto.encrypt(cipher, publicKey, ByteBuffer.wrap(data), outBuffer, 245, null);
        outBuffer.flip();
        byte[] enBuffer = FsIO.getBytes(outBuffer);
        System.out.println(data.length);
        System.out.println(enBuffer.length);

        //decrypt
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        System.out.println(cipher.getBlockSize());
        System.out.println(cipher.getOutputSize(0));
        outBytes.reset();
        FsCrypto.decrypt(cipher, privateKey, new ByteArrayInputStream(enBytes), outBytes, 256, null);
        byte[] deBytes = outBytes.toByteArray();
        System.out.println(data.length);
        System.out.println(deBytes.length);
        Assert.assertEquals(deBytes, data);
        outBuffer.flip();
        FsCrypto.decrypt(cipher, privateKey, ByteBuffer.wrap(enBuffer), outBuffer, 256, null);
        outBuffer.flip();
        byte[] deBuffer = FsIO.getBytes(outBuffer);
        System.out.println(data.length);
        System.out.println(deBuffer.length);
        Assert.assertEquals(deBuffer, data);
    }
}
