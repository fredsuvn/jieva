package xyz.fsgik.common.security;

import xyz.fsgik.annotations.Nullable;
import xyz.fsgik.annotations.ThreadSafe;

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
@ThreadSafe
public interface FsSign extends Prepareable {

    /**
     * Returns new instance of specified algorithm.
     * Returned instance has a back thread-local {@link Signature}
     * which supplied with {@link Signature#getInstance(String)}.
     *
     * @param algorithm specified algorithm
     * @return new instance of specified algorithm
     */
    static FsSign getInstance(String algorithm) {
        return new SignImpl(algorithm, () -> {
            try {
                return Signature.getInstance(algorithm);
            } catch (Exception e) {
                throw new FsSecurityException(e);
            }
        });
    }

    /**
     * Returns new instance of specified algorithm and provider.
     * Returned instance has a back thread-local {@link Signature}
     * which supplied with {@link Signature#getInstance(String, String)}.
     *
     * @param algorithm specified algorithm
     * @param provider  specified provider
     * @return new instance of specified algorithm
     */
    static FsSign getInstance(String algorithm, String provider) {
        return new SignImpl(algorithm, () -> {
            try {
                return Signature.getInstance(algorithm, provider);
            } catch (Exception e) {
                throw new FsSecurityException(e);
            }
        });
    }

    /**
     * Returns new instance of specified algorithm and provider.
     * Returned instance has a back thread-local {@link Signature}
     * which supplied with {@link Signature#getInstance(String, Provider)}.
     *
     * @param algorithm specified algorithm
     * @param provider  specified provider
     * @return new instance of specified algorithm
     */
    static FsSign getInstance(String algorithm, Provider provider) {
        return new SignImpl(algorithm, () -> {
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
     *
     * @return back {@link Signature} if it has, or null if it doesn't have one
     */
    @Nullable
    Signature getSignature();
}
