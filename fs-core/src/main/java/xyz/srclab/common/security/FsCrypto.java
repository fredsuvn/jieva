package xyz.srclab.common.security;

import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.base.FsArray;
import xyz.srclab.common.io.FsIO;

import javax.crypto.Cipher;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.security.Key;

/**
 * Crypto utilities.
 *
 * @author fredsuvn
 */
public class FsCrypto {

    /**
     * Initializes given cipher.
     *
     * @param cipher given cipher
     * @param mode   cipher mode
     * @param key    key for encrypting/decrypting
     * @param params algorithm parameters
     */
    public static void initCipher(Cipher cipher, int mode, Key key, @Nullable AlgorithmParams params) {
        try {
            if (params == null) {
                cipher.init(mode, key);
                return;
            }
            if (params.getSecureRandom() != null) {
                if (params.getCertificate() != null) {
                    cipher.init(mode, params.getCertificate(), params.getSecureRandom());
                } else if (params.getAlgorithmParameters() != null) {
                    cipher.init(mode, key, params.getAlgorithmParameters(), params.getSecureRandom());
                } else if (params.getAlgorithmParameterSpec() != null) {
                    cipher.init(mode, key, params.getAlgorithmParameterSpec(), params.getSecureRandom());
                } else {
                    cipher.init(mode, key, params.getSecureRandom());
                }
            } else {
                if (params.getCertificate() != null) {
                    cipher.init(mode, params.getCertificate());
                } else if (params.getAlgorithmParameters() != null) {
                    cipher.init(mode, key, params.getAlgorithmParameters());
                } else if (params.getAlgorithmParameterSpec() != null) {
                    cipher.init(mode, key, params.getAlgorithmParameterSpec());
                } else {
                    cipher.init(mode, key);
                }
            }
        } catch (Exception e) {
            throw new FsSecurityException(e);
        }
    }

    /**
     * Encrypts data from given input stream into given output stream with specified cipher,
     * returns the written bytes count.
     * If {@code blockSize} > 0, this method will submit the data in blocks of the specified size to the cipher for
     * encryption, otherwise, it will submit all the data to the cipher at once.
     *
     * @param cipher    specified cipher
     * @param key       key for encrypting
     * @param in        given input stream
     * @param out       given output stream
     * @param blockSize block size for each encrypting
     * @param params    algorithm parameters
     */
    public static long encrypt(
        Cipher cipher, Key key, InputStream in, OutputStream out, int blockSize, @Nullable AlgorithmParams params) {
        return doEncrypt(cipher, Cipher.ENCRYPT_MODE, key, in, out, blockSize, params);
    }

    /**
     * Decrypts data from given input stream into given output stream with specified cipher,
     * returns the written bytes count.
     * If {@code blockSize} > 0, this method will submit the data in blocks of the specified size to the cipher for
     * encryption, otherwise, it will submit all the data to the cipher at once.
     *
     * @param cipher    specified cipher
     * @param key       key for encrypting
     * @param in        given input stream
     * @param out       given output stream
     * @param blockSize block size for each decrypting
     * @param params    algorithm parameters
     */
    public static long decrypt(
        Cipher cipher, Key key, InputStream in, OutputStream out, int blockSize, @Nullable AlgorithmParams params) {
        return doEncrypt(cipher, Cipher.DECRYPT_MODE, key, in, out, blockSize, params);
    }

    private static long doEncrypt(
        Cipher cipher, int mode, Key key, InputStream in, OutputStream out, int blockSize, @Nullable AlgorithmParams params) {
        try {
            initCipher(cipher, mode, key, params);
            if (blockSize <= 0) {
                byte[] src = FsIO.readBytes(in);
                byte[] de = cipher.doFinal(src);
                out.write(de);
                return de.length;
            }
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
        } catch (FsSecurityException e) {
            throw e;
        } catch (Exception e) {
            throw new FsSecurityException(e);
        }
    }

    /**
     * Encrypts data from given input buffer into given output buffer with specified cipher,
     * returns the written bytes count.
     * If {@code blockSize} > 0, this method will submit the data in blocks of the specified size to the cipher for
     * encryption, otherwise, it will submit all the data to the cipher at once.
     *
     * @param cipher    specified cipher
     * @param key       key for encrypting
     * @param in        given input buffer
     * @param out       given output buffer
     * @param blockSize block size for each encrypting
     * @param params    algorithm parameters
     */
    public static int encrypt(
        Cipher cipher, Key key, ByteBuffer in, ByteBuffer out, int blockSize, @Nullable AlgorithmParams params) {
        return doEncrypt(cipher, Cipher.ENCRYPT_MODE, key, in, out, blockSize, params);
    }

    /**
     * Decrypts data from given input buffer into given output buffer with specified cipher,
     * returns the written bytes count.
     * If {@code blockSize} > 0, this method will submit the data in blocks of the specified size to the cipher for
     * encryption, otherwise, it will submit all the data to the cipher at once.
     *
     * @param cipher    specified cipher
     * @param key       key for encrypting
     * @param in        given input buffer
     * @param out       given output buffer
     * @param blockSize block size for each decrypting
     * @param params    algorithm parameters
     */
    public static int decrypt(
        Cipher cipher, Key key, ByteBuffer in, ByteBuffer out, int blockSize, @Nullable AlgorithmParams params) {
        return doEncrypt(cipher, Cipher.DECRYPT_MODE, key, in, out, blockSize, params);
    }

    private static int doEncrypt(
        Cipher cipher, int mode, Key key, ByteBuffer in, ByteBuffer out, int blockSize, @Nullable AlgorithmParams params) {
        try {
            initCipher(cipher, mode, key, params);
            if (blockSize <= 0) {
                return cipher.doFinal(in, out);
            }
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
        } catch (FsSecurityException e) {
            throw e;
        } catch (Exception e) {
            throw new FsSecurityException(e);
        }
    }
}
