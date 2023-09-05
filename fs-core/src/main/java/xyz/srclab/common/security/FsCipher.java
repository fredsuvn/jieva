package xyz.srclab.common.security;

import xyz.srclab.annotations.Nullable;

import javax.crypto.Cipher;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.security.Provider;

/**
 * Denotes cipher for encrypting/decrypting, maybe has a backed {@link Cipher}.
 *
 * @author fredsuvn
 * @see Cipher
 */
public interface FsCipher {

    /**
     * Returns new instance of specified algorithm.
     *
     * @param algorithm specified algorithm
     */
    static FsCipher getCipher(String algorithm) {
        return new CipherImpl(() -> {
            try {
                return Cipher.getInstance(algorithm);
            } catch (Exception e) {
                throw new FsSecurityException(e);
            }
        });
    }

    /**
     * Returns new instance of specified algorithm and provider.
     *
     * @param algorithm specified algorithm
     * @param provider  specified provider
     */
    static FsCipher getCipher(String algorithm, String provider) {
        return new CipherImpl(() -> {
            try {
                return Cipher.getInstance(algorithm, provider);
            } catch (Exception e) {
                throw new FsSecurityException(e);
            }
        });
    }

    /**
     * Returns new instance of specified algorithm and provider.
     *
     * @param algorithm specified algorithm
     * @param provider  specified provider
     */
    static FsCipher getCipher(String algorithm, Provider provider) {
        return new CipherImpl(() -> {
            try {
                return Cipher.getInstance(algorithm, provider);
            } catch (Exception e) {
                throw new FsSecurityException(e);
            }
        });
    }

    /**
     * Returns backed {@link Cipher} if it has, or null if it doesn't have one.
     * The backed {@link Cipher} maybe thread-local, that is, returned value may be not only one instance.
     */
    @Nullable
    Cipher getCipher();

    /**
     * Prepares to encrypt/decrypt source array.
     *
     * @param source source array
     */
    default CryptoProcess prepare(byte[] source) {
        return prepare(source, 0, source.length);
    }

    /**
     * Prepares to encrypt/decrypt array of specified length from offset index.
     *
     * @param source source array
     * @param offset offset index
     * @param length specified length
     */
    CryptoProcess prepare(byte[] source, int offset, int length);

    /**
     * Prepares to encrypt/decrypt source buffer.
     *
     * @param source source buffer
     */
    CryptoProcess prepare(ByteBuffer source);

    /**
     * Prepares to encrypt/decrypt source stream.
     *
     * @param source source stream
     */
    CryptoProcess prepare(InputStream source);
}
