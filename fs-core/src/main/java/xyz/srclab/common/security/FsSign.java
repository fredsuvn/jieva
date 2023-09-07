package xyz.srclab.common.security;

import xyz.srclab.annotations.Nullable;

import java.security.Provider;
import java.security.Signature;

/**
 * Denotes cipher for signature, maybe has a back {@link Signature}.
 * <p>
 * It is recommended to use method-chaining:
 * <pre>
 *     byte[] sign = signer.prepare(bytes).key(key).bufferSize(size).sign().doFinal();
 *     boolean verify = signer.prepare(bytes).key(key).bufferSize(size).verify(sign);
 * </pre>
 *
 * @author fredsuvn
 * @see Signature
 */
public interface FsSign extends Prepareable {

    /**
     * Returns new instance of specified algorithm.
     * Returned instance has a back thread-local mac which supplied with {@link Signature#getInstance(String)}.
     *
     * @param algorithm specified algorithm
     */
    static FsSign getInstance(String algorithm) {
        return new SignImpl(() -> {
            try {
                return Signature.getInstance(algorithm);
            } catch (Exception e) {
                throw new FsSecurityException(e);
            }
        });
    }

    /**
     * Returns new instance of specified algorithm and provider.
     * Returned instance has a back thread-local mac which supplied with {@link Signature#getInstance(String, String)}.
     *
     * @param algorithm specified algorithm
     * @param provider  specified provider
     */
    static FsSign getInstance(String algorithm, String provider) {
        return new SignImpl(() -> {
            try {
                return Signature.getInstance(algorithm, provider);
            } catch (Exception e) {
                throw new FsSecurityException(e);
            }
        });
    }

    /**
     * Returns new instance of specified algorithm and provider.
     * Returned instance has a back thread-local mac which supplied with {@link Signature#getInstance(String, Provider)}.
     *
     * @param algorithm specified algorithm
     * @param provider  specified provider
     */
    static FsSign getInstance(String algorithm, Provider provider) {
        return new SignImpl(() -> {
            try {
                return Signature.getInstance(algorithm, provider);
            } catch (Exception e) {
                throw new FsSecurityException(e);
            }
        });
    }

    /**
     * Returns back {@link Signature} if it has, or null if it doesn't have one.
     * The back {@link Signature} maybe thread-local, that is, returned value may be not only one instance.
     */
    @Nullable
    Signature getSignature();
}
