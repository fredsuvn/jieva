package xyz.fs404.common.security;

import xyz.fs404.annotations.Nullable;
import xyz.fs404.common.base.FsArray;
import xyz.fs404.common.io.FsIO;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.security.*;

/**
 * Crypto utilities.
 *
 * @author fredsuvn
 */
public class FsCrypto {

    /**
     * Initializes given {@link Cipher}.
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
     * Initializes given {@link Mac}.
     *
     * @param mac    given MAC
     * @param key    key for generating MAC
     * @param params algorithm parameters
     */
    public static void initMac(Mac mac, Key key, @Nullable AlgorithmParams params) {
        try {
            if (params == null) {
                mac.init(key);
                return;
            }
            if (params.getAlgorithmParameterSpec() != null) {
                mac.init(key, params.getAlgorithmParameterSpec());
                return;
            }
            mac.init(key);
        } catch (Exception e) {
            throw new FsSecurityException(e);
        }
    }

    /**
     * Initializes given {@link Signature}.
     *
     * @param signature given signature
     * @param key       key for generating MAC
     * @param params    algorithm parameters
     */
    public static void initSignature(
        Signature signature, boolean forSign, @Nullable Key key, @Nullable AlgorithmParams params) {
        try {
            if (forSign) {
                if (params == null) {
                    signature.initSign(asPrivateKey(key));
                    return;
                }
                if (params.getSecureRandom() != null) {
                    signature.initSign(asPrivateKey(key), params.getSecureRandom());
                }
            } else {
                if (params == null) {
                    signature.initVerify(asPublicKey(key));
                    return;
                }
                if (params.getCertificate() != null) {
                    signature.initVerify(params.getCertificate());
                }
            }
        } catch (Exception e) {
            throw new FsSecurityException(e);
        }
    }

    private static PrivateKey asPrivateKey(Key key) {
        if (key instanceof PrivateKey) {
            return (PrivateKey) key;
        }
        throw new FsSecurityException("Not a PrivateKey: " + key);
    }

    private static PublicKey asPublicKey(Key key) {
        if (key instanceof PublicKey) {
            return (PublicKey) key;
        }
        throw new FsSecurityException("Not a PublicKey: " + key);
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

    /**
     * Encrypts/Decrypts data from given input stream into given output stream with specified cipher,
     * returns the written bytes count.
     * If {@code blockSize} > 0, this method will submit the data in blocks of the specified size to the cipher for
     * encryption, otherwise, it will submit all the data to the cipher at once.
     *
     * @param cipher    specified cipher
     * @param mode      encryption mode: ({@link Cipher#ENCRYPT_MODE}/{@link Cipher#DECRYPT_MODE})
     * @param key       key for encrypting
     * @param in        given input stream
     * @param out       given output stream
     * @param blockSize block size for each decrypting
     * @param params    algorithm parameters
     */
    public static long doEncrypt(
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

    /**
     * Encrypts/Decrypts data from given input buffer into given output buffer with specified cipher,
     * returns the written bytes count.
     * If {@code blockSize} > 0, this method will submit the data in blocks of the specified size to the cipher for
     * encryption, otherwise, it will submit all the data to the cipher at once.
     *
     * @param cipher    specified cipher
     * @param mode      encryption mode: ({@link Cipher#ENCRYPT_MODE}/{@link Cipher#DECRYPT_MODE})
     * @param key       key for encrypting
     * @param in        given input buffer
     * @param out       given output buffer
     * @param blockSize block size for each decrypting
     * @param params    algorithm parameters
     */
    public static int doEncrypt(
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

    /**
     * Generates MAC for given input stream.
     *
     * @param mac        MAC generator
     * @param key        key for generating
     * @param in         given input stream
     * @param bufferSize buffer size
     * @param params     other parameters
     */
    public static byte[] generateMac(
        Mac mac, Key key, InputStream in, int bufferSize, @Nullable AlgorithmParams params) {
        try {
            initMac(mac, key, params);
            if (bufferSize <= 0) {
                byte[] src = FsIO.readBytes(in);
                return mac.doFinal(src);
            }
            byte[] buffer = new byte[bufferSize];
            while (true) {
                int readSize = in.read(buffer);
                if (readSize == -1) {
                    return mac.doFinal();
                }
                mac.update(buffer);
            }
        } catch (FsSecurityException e) {
            throw e;
        } catch (Exception e) {
            throw new FsSecurityException(e);
        }
    }

    /**
     * Generates MAC for given buffer.
     *
     * @param mac    MAC generator
     * @param key    key for generating
     * @param in     given buffer
     * @param params other parameters
     */
    public static byte[] generateMac(Mac mac, Key key, ByteBuffer in, @Nullable AlgorithmParams params) {
        try {
            initMac(mac, key, params);
            mac.update(in);
            return mac.doFinal();
        } catch (FsSecurityException e) {
            throw e;
        } catch (Exception e) {
            throw new FsSecurityException(e);
        }
    }

    /**
     * Digests for given input stream.
     *
     * @param digest     MAC generator
     * @param in         given input stream
     * @param bufferSize buffer size
     */
    public static byte[] digest(MessageDigest digest, InputStream in, int bufferSize) {
        try {
            if (bufferSize <= 0) {
                byte[] src = FsIO.readBytes(in);
                return digest.digest(src);
            }
            byte[] buffer = new byte[bufferSize];
            while (true) {
                int readSize = in.read(buffer);
                if (readSize == -1) {
                    return digest.digest();
                }
                digest.update(buffer);
            }
        } catch (FsSecurityException e) {
            throw e;
        } catch (Exception e) {
            throw new FsSecurityException(e);
        }
    }

    /**
     * Digests for given buffer.
     *
     * @param mac MAC generator
     * @param in  given buffer
     */
    public static byte[] digest(MessageDigest mac, ByteBuffer in) {
        try {
            mac.update(in);
            return mac.digest();
        } catch (FsSecurityException e) {
            throw e;
        } catch (Exception e) {
            throw new FsSecurityException(e);
        }
    }

    /**
     * Generates signature for given input stream.
     *
     * @param signature  signature generator
     * @param key        key for generating
     * @param in         given input stream
     * @param bufferSize buffer size
     * @param params     other parameters
     */
    public static byte[] sign(
        Signature signature, Key key, InputStream in, int bufferSize, @Nullable AlgorithmParams params) {
        try {
            initSignature(signature, true, key, params);
            if (bufferSize <= 0) {
                byte[] src = FsIO.readBytes(in);
                signature.update(src);
                return signature.sign();
            }
            byte[] buffer = new byte[bufferSize];
            while (true) {
                int readSize = in.read(buffer);
                if (readSize == -1) {
                    return signature.sign();
                }
                signature.update(buffer);
            }
        } catch (FsSecurityException e) {
            throw e;
        } catch (Exception e) {
            throw new FsSecurityException(e);
        }
    }

    /**
     * Generates signature for given buffer.
     *
     * @param signature signature generator
     * @param key       key for generating
     * @param in        given buffer
     * @param params    other parameters
     */
    public static byte[] sign(Signature signature, Key key, ByteBuffer in, @Nullable AlgorithmParams params) {
        try {
            initSignature(signature, true, key, params);
            signature.update(in);
            return signature.sign();
        } catch (FsSecurityException e) {
            throw e;
        } catch (Exception e) {
            throw new FsSecurityException(e);
        }
    }

    /**
     * Verifies signature for given input stream.
     *
     * @param signature  signature generator
     * @param key        key for generating
     * @param in         given input stream
     * @param bufferSize buffer size
     * @param sign       sign to be verified
     * @param signOffset start offset of sign
     * @param signLength length of sign
     * @param params     other parameters
     */
    public static boolean verify(
        Signature signature, Key key, InputStream in, int bufferSize,
        byte[] sign, int signOffset, int signLength, @Nullable AlgorithmParams params
    ) {
        try {
            initSignature(signature, false, key, params);
            if (bufferSize <= 0) {
                byte[] src = FsIO.readBytes(in);
                signature.update(src);
                return signature.verify(sign, signOffset, signLength);
            }
            byte[] buffer = new byte[bufferSize];
            while (true) {
                int readSize = in.read(buffer);
                if (readSize == -1) {
                    return signature.verify(sign, signOffset, signLength);
                }
                signature.update(buffer);
            }
        } catch (FsSecurityException e) {
            throw e;
        } catch (Exception e) {
            throw new FsSecurityException(e);
        }
    }

    /**
     * Verifies signature for given buffer.
     *
     * @param signature  signature generator
     * @param key        key for generating
     * @param in         given buffer
     * @param sign       sign to be verified
     * @param signOffset start offset of sign
     * @param signLength length of sign
     * @param params     other parameters
     */
    public static boolean verify(
        Signature signature, Key key, ByteBuffer in,
        byte[] sign, int signOffset, int signLength, @Nullable AlgorithmParams params
    ) {
        try {
            initSignature(signature, false, key, params);
            signature.update(in);
            return signature.verify(sign, signOffset, signLength);
        } catch (FsSecurityException e) {
            throw e;
        } catch (Exception e) {
            throw new FsSecurityException(e);
        }
    }
}
