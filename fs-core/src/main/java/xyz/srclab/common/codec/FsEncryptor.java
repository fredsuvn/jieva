package xyz.srclab.common.codec;

import xyz.srclab.annotations.concurrent.ThreadSafe;
import xyz.srclab.common.io.FsIO;

import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.Key;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Encryptor interface for encrypting and decrypting.
 *
 * @author fredsuvn
 * @see javax.crypto.Cipher
 */
@ThreadSafe
public interface FsEncryptor {

    /**
     * Returns encryptor of specified algorithm name from {@link FsCodecProvider#defaultProvider()}.
     *
     * @param algorithmName specified algorithm name
     */
    static FsEncryptor getEncryptor(String algorithmName) {
        return FsCodecProvider.defaultProvider().getEncryptor(algorithmName);
    }

    /**
     * Returns encryptor of specified algorithm name from given codec provider.
     *
     * @param algorithmName specified algorithm name
     * @param provider      given codec provider
     */
    static FsEncryptor getEncryptor(String algorithmName, FsCodecProvider provider) {
        return provider.getEncryptor(algorithmName);
    }

    /**
     * Returns RSA encryptor.
     */
    static FsEncryptor rsa() {
        return getEncryptor(FsAlgorithm.RSA.getName());
    }

    /**
     * Returns algorithm name.
     */
    String algorithmName();

    //    /**
    //     * Initializes this encryptor with parameter specification.
    //     *
    //     * @param spec parameter specification
    //     */
    //    void init(AlgorithmParameterSpec spec);
    //
    //    /**
    //     * Initializes this encryptor with given parameters.
    //     *
    //     * @param params given parameters
    //     */
    //    void init(AlgorithmParameters params);
    //
    //    /**
    //     * Initializes this encryptor with secure random.
    //     *
    //     * @param secureRandom secure random
    //     */
    //    void init(SecureRandom secureRandom);
    //
    //    /**
    //     * Initializes this encryptor with parameter specification and secure random.
    //     *
    //     * @param spec         parameter specification
    //     * @param secureRandom secure random
    //     */
    //    void init(@Nullable AlgorithmParameterSpec spec, @Nullable SecureRandom secureRandom);
    //
    //    /**
    //     * Initializes this encryptor with given parameters and secure random.
    //     *
    //     * @param params       given parameters
    //     * @param secureRandom secure random
    //     */
    //    void init(@Nullable AlgorithmParameters params, @Nullable SecureRandom secureRandom);

    /**
     * Creates a pair of key.
     *
     * @param params key pair parameters
     */
    KeyPair generateKeyPair(FsEncryptParams params);

    /**
     * Converts key bytes to {@link Key} instance.
     *
     * @param keyBytes key bytes
     */
    default Key toKey(byte[] keyBytes) {
        return toKey(keyBytes, 0, keyBytes.length);
    }

    /**
     * Converts key bytes of specified length from offset index to {@link Key} instance.
     *
     * @param keyBytes key bytes
     * @param offset   offset index
     * @param length   specified length
     */
    default Key toKey(byte[] keyBytes, int offset, int length) {
        return new SecretKeySpec(keyBytes, offset, length, algorithmName());
    }

    /**
     * Converts given key buffer to {@link Key} instance.
     *
     * @param keyBuffer given key buffer
     */
    default Key toKey(ByteBuffer keyBuffer) {
        return toKey(FsIO.getBytes(keyBuffer));
    }

    /**
     * Converts key bytes to {@link PublicKey} instance.
     *
     * @param keyBytes key bytes
     */
    default PublicKey toPublicKey(byte[] keyBytes) {
        return toPublicKey(keyBytes, 0, keyBytes.length);
    }

    /**
     * Converts key bytes of specified length from offset index to {@link PublicKey} instance.
     *
     * @param keyBytes key bytes
     * @param offset   offset index
     * @param length   specified length
     */
    PublicKey toPublicKey(byte[] keyBytes, int offset, int length);

    /**
     * Converts given key buffer to {@link PublicKey} instance.
     *
     * @param keyBuffer given key buffer
     */
    default PublicKey toPublicKey(ByteBuffer keyBuffer) {
        return toPublicKey(FsIO.getBytes(keyBuffer));
    }

    /**
     * Converts key bytes to {@link PrivateKey} instance.
     *
     * @param keyBytes key bytes
     */
    default PrivateKey toPrivateKey(byte[] keyBytes) {
        return toPrivateKey(keyBytes, 0, keyBytes.length);
    }

    /**
     * Converts key bytes of specified length from offset index to {@link PrivateKey} instance.
     *
     * @param keyBytes key bytes
     * @param offset   offset index
     * @param length   specified length
     */
    PrivateKey toPrivateKey(byte[] keyBytes, int offset, int length);

    /**
     * Converts given key buffer to {@link PrivateKey} instance.
     *
     * @param keyBuffer given key buffer
     */
    default PrivateKey toPrivateKey(ByteBuffer keyBuffer) {
        return toPrivateKey(FsIO.getBytes(keyBuffer));
    }

    // /**
    //  * Encrypts source array.
    //  *
    //  * @param source source array
    //  */
    // default byte[] encrypt(byte[] source) {
    //     return encrypt(source, 0, source.length);
    // }
    //
    // /**
    //  * Encrypts source array of specified length from offset index.
    //  *
    //  * @param source source array
    //  * @param offset offset index
    //  * @param length specified length
    //  */
    // byte[] encrypt(byte[] source, int offset, int length);
    //
    // /**
    //  * Encrypts source array into dest array, return number of bytes written.
    //  *
    //  * @param source source array
    //  * @param dest   dest array
    //  */
    // default int encrypt(byte[] source, byte[] dest) {
    //     return encrypt(source, 0, dest, 0, source.length);
    // }
    //
    // /**
    //  * Encrypts source array of specified length from source offset index,
    //  * into dest array from dest offset index, return number of bytes written.
    //  *
    //  * @param source       source array
    //  * @param sourceOffset source offset index
    //  * @param dest         dest array
    //  * @param destOffset   dest offset index
    //  * @param length       specified length
    //  */
    // default int encrypt(byte[] source, int sourceOffset, byte[] dest, int destOffset, int length) {
    //     try {
    //         ByteBuffer in = ByteBuffer.wrap(source, sourceOffset, length);
    //         ByteBuffer out = ByteBuffer.wrap(dest, destOffset, dest.length - destOffset);
    //         return encrypt(in, out);
    //     } catch (FsCodecException e) {
    //         throw e;
    //     } catch (Exception e) {
    //         throw new FsCodecException(e);
    //     }
    // }
    //
    // /**
    //  * Encrypts source byte buffer.
    //  * The returned buffer's position will be set to 0 and limit is length of result bytes.
    //  *
    //  * @param source source byte buffer
    //  */
    // default ByteBuffer encrypt(ByteBuffer source) {
    //     try {
    //         byte[] src = FsIO.getBytes(source);
    //         return ByteBuffer.wrap(encrypt(src));
    //     } catch (FsCodecException e) {
    //         throw e;
    //     } catch (Exception e) {
    //         throw new FsCodecException(e);
    //     }
    // }
    //
    // /**
    //  * Encrypts source byte buffer into dest byte buffer, return number of bytes written.
    //  *
    //  * @param source source byte buffer
    //  * @param dest   dest byte buffer
    //  */
    // default int encrypt(ByteBuffer source, ByteBuffer dest) {
    //     try {
    //         OutputStream out = FsIO.toOutputStream(dest);
    //         int result = (int) encrypt(FsIO.toInputStream(source), out);
    //         out.flush();
    //         return result;
    //     } catch (FsCodecException e) {
    //         throw e;
    //     } catch (Exception e) {
    //         throw new FsCodecException(e);
    //     }
    // }
    //
    // /**
    //  * Encrypts source stream into dest stream, return number of bytes written.
    //  *
    //  * @param source source stream
    //  * @param dest   dest stream
    //  */
    // long encrypt(InputStream source, OutputStream dest);
    //
    // /**
    //  * Encrypts source array and build encoding result to string with {@link StandardCharsets#ISO_8859_1}.
    //  *
    //  * @param source source array
    //  */
    // default String encryptToString(byte[] source) {
    //     return new String(encrypt(source), StandardCharsets.ISO_8859_1);
    // }
    //
    // /**
    //  * Resolves source string with {@link FsString#CHARSET},
    //  * and build encoding result to string with {@link StandardCharsets#ISO_8859_1}.
    //  *
    //  * @param source source string
    //  */
    // default String encryptToString(String source) {
    //     return encryptToString(source.getBytes(FsString.CHARSET));
    // }
    //
    // /**
    //  * Returns block size for encoding.
    //  */
    // int encryptBlockSize();
    //
    // /**
    //  * Decrypts source array.
    //  *
    //  * @param source source array
    //  */
    // default byte[] decrypt(byte[] source) {
    //     return decrypt(source, 0, source.length);
    // }
    //
    // /**
    //  * Decrypts source array of specified length from offset index.
    //  *
    //  * @param source source array
    //  * @param offset offset index
    //  * @param length specified length
    //  */
    // byte[] decrypt(byte[] source, int offset, int length);
    //
    // /**
    //  * Decrypts source array into dest array, return number of bytes written.
    //  *
    //  * @param source source array
    //  * @param dest   dest array
    //  */
    // default int decrypt(byte[] source, byte[] dest) {
    //     return decrypt(source, 0, dest, 0, source.length);
    // }
    //
    // /**
    //  * Decrypts source array of specified length from source offset index,
    //  * into dest array from dest offset index, return number of bytes written.
    //  *
    //  * @param source       source array
    //  * @param sourceOffset source offset index
    //  * @param dest         dest array
    //  * @param destOffset   dest offset index
    //  * @param length       specified length
    //  */
    // default int decrypt(byte[] source, int sourceOffset, byte[] dest, int destOffset, int length) {
    //     try {
    //         ByteBuffer in = ByteBuffer.wrap(source, sourceOffset, length);
    //         ByteBuffer out = ByteBuffer.wrap(dest, destOffset, dest.length - destOffset);
    //         return decrypt(in, out);
    //     } catch (FsCodecException e) {
    //         throw e;
    //     } catch (Exception e) {
    //         throw new FsCodecException(e);
    //     }
    // }
    //
    // /**
    //  * Decrypts source byte buffer.
    //  * The returned buffer's position will be set to 0 and limit is length of result bytes.
    //  *
    //  * @param source source byte buffer
    //  */
    // default ByteBuffer decrypt(ByteBuffer source) {
    //     try {
    //         byte[] src = FsIO.getBytes(source);
    //         return ByteBuffer.wrap(decrypt(src));
    //     } catch (FsCodecException e) {
    //         throw e;
    //     } catch (Exception e) {
    //         throw new FsCodecException(e);
    //     }
    // }
    //
    // /**
    //  * Decrypts source byte buffer into dest byte buffer, return number of bytes written.
    //  *
    //  * @param source source byte buffer
    //  * @param dest   dest byte buffer
    //  */
    // default int decrypt(ByteBuffer source, ByteBuffer dest) {
    //     try {
    //         OutputStream out = FsIO.toOutputStream(dest);
    //         int result = (int) decrypt(FsIO.toInputStream(source), out);
    //         out.flush();
    //         return result;
    //     } catch (FsCodecException e) {
    //         throw e;
    //     } catch (Exception e) {
    //         throw new FsCodecException(e);
    //     }
    // }
    //
    // /**
    //  * Decrypts source stream into dest stream, return number of bytes written.
    //  *
    //  * @param source source stream
    //  * @param dest   dest stream
    //  */
    // long decrypt(InputStream source, OutputStream dest);
    //
    // /**
    //  * Decrypts source array and build encoding result to string with {@link FsString#CHARSET}.
    //  *
    //  * @param source source array
    //  */
    // default String decryptToString(byte[] source) {
    //     return new String(decrypt(source), FsString.CHARSET);
    // }
    //
    // /**
    //  * Resolves source string with {@link StandardCharsets#ISO_8859_1},
    //  * and build encoding result to string with {@link FsString#CHARSET}.
    //  *
    //  * @param source source string
    //  */
    // default String decrypt(String source) {
    //     return new String(decrypt(source.getBytes(StandardCharsets.ISO_8859_1)), FsString.CHARSET);
    // }
    //
    // /**
    //  * Returns block size for decoding.
    //  */
    // int decryptBlockSize();
}
