package xyz.srclab.common.security;

import xyz.srclab.annotations.Nullable;

import javax.crypto.Cipher;
import java.security.Provider;

/**
 * Denotes cipher for encrypting/decrypting, maybe has a back {@link Cipher}.
 * <p>
 * It is recommended to use method-chaining:
 * <pre>
 *     byte[] enBytes = cipher.prepare(bytes).key(key).blockSize(size).encrypt().doFinal();
 * </pre>
 *
 * @author fredsuvn
 * @see Cipher
 */
public interface FsCipher extends Prepareable {

    /**
     * Returns new instance of specified algorithm.
     * Returned instance has a back thread-local cipher which supplied with {@link Cipher#getInstance(String)}.
     *
     * @param algorithm specified algorithm
     */
    static FsCipher getInstance(String algorithm) {
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
     * Returned instance has a back thread-local cipher which supplied with {@link Cipher#getInstance(String, String)}.
     *
     * @param algorithm specified algorithm
     * @param provider  specified provider
     */
    static FsCipher getInstance(String algorithm, String provider) {
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
     * Returned instance has a back thread-local cipher which supplied with {@link Cipher#getInstance(String, Provider)}.
     *
     * @param algorithm specified algorithm
     * @param provider  specified provider
     */
    static FsCipher getInstance(String algorithm, Provider provider) {
        return new CipherImpl(() -> {
            try {
                return Cipher.getInstance(algorithm, provider);
            } catch (Exception e) {
                throw new FsSecurityException(e);
            }
        });
    }

    /**
     * Returns back {@link Cipher} if it has, or null if it doesn't have one.
     * The back {@link Cipher} maybe thread-local, that is, returned value may be not only one instance.
     */
    @Nullable
    Cipher getInstance();
}
