package xyz.srclab.common.security;

import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.base.FsArray;

import javax.crypto.Cipher;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;

/**
 * Crypto utilities.
 *
 * @author fredsuvn
 */
public class FsCrypto {

    /**
     * Encrypts data from given input stream into given output stream with specified cipher,
     * returns the written bytes count.
     *
     * @param cipher specified cipher
     * @param key    key for encrypting
     * @param in     given input stream
     * @param out    given output stream
     * @param params algorithm parameters
     */
    public static long encrypt(
        Cipher cipher, Key key, InputStream in, OutputStream out, int blockSize, @Nullable AlgorithmParams params) {
        return doEncrypt(cipher, Cipher.ENCRYPT_MODE, key, in, out, blockSize, params);
    }

    /**
     * Decrypts data from given input stream into given output stream with specified cipher,
     * returns the written bytes count.
     *
     * @param cipher specified cipher
     * @param key    key for encrypting
     * @param in     given input stream
     * @param out    given output stream
     * @param params algorithm parameters
     */
    public static long decrypt(
        Cipher cipher, Key key, InputStream in, OutputStream out, int blockSize, @Nullable AlgorithmParams params) {
        return doEncrypt(cipher, Cipher.DECRYPT_MODE, key, in, out, blockSize, params);
    }

    private static long doEncrypt(
        Cipher cipher, int mode, Key key, InputStream in, OutputStream out, int blockSize, @Nullable AlgorithmParams params) {
        try {
            initCipher(cipher, mode, key, params);
            byte[] srcBuffer = new byte[blockSize];
            long writeCount = 0;
            while (true) {
                int readSize = in.read(srcBuffer);
                if (readSize == -1) {
                    break;
                }
                byte[] decrypt = cipher.doFinal(srcBuffer, 0, readSize);
                if (FsArray.isNotEmpty(decrypt)) {
                    out.write(decrypt);
                    writeCount += decrypt.length;
                }
            }
            return writeCount;
        } catch (Exception e) {
            throw new FsSecurityException(e);
        }
    }

    /**
     * Encrypts data from given input buffer into given output buffer with specified cipher,
     * returns the written bytes count.
     *
     * @param cipher specified cipher
     * @param key    key for encrypting
     * @param in     given input buffer
     * @param out    given output buffer
     * @param params algorithm parameters
     */
    public static int encrypt(
        Cipher cipher, Key key, ByteBuffer in, ByteBuffer out, int blockSize, @Nullable AlgorithmParams params) {
        return doEncrypt(cipher, Cipher.ENCRYPT_MODE, key, in, out, blockSize, params);
    }

    /**
     * Decrypts data from given input buffer into given output buffer with specified cipher,
     * returns the written bytes count.
     *
     * @param cipher specified cipher
     * @param key    key for encrypting
     * @param in     given input buffer
     * @param out    given output buffer
     * @param params algorithm parameters
     */
    public static int decrypt(
        Cipher cipher, Key key, ByteBuffer in, ByteBuffer out, int blockSize, @Nullable AlgorithmParams params) {
        return doEncrypt(cipher, Cipher.DECRYPT_MODE, key, in, out, blockSize, params);
    }

    private static int doEncrypt(
        Cipher cipher, int mode, Key key, ByteBuffer in, ByteBuffer out, int blockSize, @Nullable AlgorithmParams params) {
        try {
            initCipher(cipher, mode, key, params);
            int total = in.remaining();
            if (total <= blockSize) {
                return cipher.doFinal(in, out);
            }
            ByteBuffer srcBuffer = in.slice();
            int writeCount = 0;
            int remaining = total;
            while (remaining > 0) {
                srcBuffer.limit(srcBuffer.position() + Math.min(remaining, blockSize));
                int writeSize = cipher.doFinal(srcBuffer, out);
                writeCount += writeSize;
                remaining -= blockSize;
            }
            return writeCount;
        } catch (Exception e) {
            throw new FsSecurityException(e);
        }
    }

    private static void initCipher(
        Cipher cipher, int mode, Key key, @Nullable AlgorithmParams params
    ) throws InvalidKeyException, InvalidAlgorithmParameterException {
        if (params == null) {
            cipher.init(mode, key);
            return;
        }
        if (params.getSecureRandom() != null) {
            if (params.getCertificate() != null) {
                cipher.init(mode, params.getCertificate(), params.getSecureRandom());
            } else if (params.getParameters() != null) {
                cipher.init(mode, key, params.getParameters(), params.getSecureRandom());
            } else if (params.getParameterSpec() != null) {
                cipher.init(mode, key, params.getParameterSpec(), params.getSecureRandom());
            } else {
                cipher.init(mode, key, params.getSecureRandom());
            }
        } else {
            if (params.getCertificate() != null) {
                cipher.init(mode, params.getCertificate());
            } else if (params.getParameters() != null) {
                cipher.init(mode, key, params.getParameters());
            } else if (params.getParameterSpec() != null) {
                cipher.init(mode, key, params.getParameterSpec());
            } else {
                cipher.init(mode, key);
            }
        }
    }
}
