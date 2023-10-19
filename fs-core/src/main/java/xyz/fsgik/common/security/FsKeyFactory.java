package xyz.fsgik.common.security;

import xyz.fsgik.annotations.Nullable;
import xyz.fsgik.annotations.ThreadSafe;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.spec.KeySpec;

/**
 * Denotes key factory, maybe has a back {@link KeyFactory}.
 *
 * @author fredsuvn
 * @see KeyFactory
 */
@ThreadSafe
public interface FsKeyFactory extends SecurityAlgorithm {

    /**
     * Returns new instance of specified algorithm.
     * Returned instance has a back thread-local {@link KeyFactory}
     * which supplied with {@link KeyFactory#getInstance(String)}.
     *
     * @param algorithm specified algorithm
     * @return new instance of specified algorithm
     */
    static FsKeyFactory getInstance(String algorithm) {
        return new KeyFactoryImpl(algorithm, () -> {
            try {
                return KeyFactory.getInstance(algorithm);
            } catch (Exception e) {
                throw new FsSecurityException(e);
            }
        });
    }

    /**
     * Returns new instance of specified algorithm and provider.
     * Returned instance has a back thread-local {@link KeyFactory}
     * which supplied with {@link KeyFactory#getInstance(String, String)}.
     *
     * @param algorithm specified algorithm
     * @param provider  specified provider
     * @return new instance of specified algorithm
     */
    static FsKeyFactory getInstance(String algorithm, String provider) {
        return new KeyFactoryImpl(algorithm, () -> {
            try {
                return KeyFactory.getInstance(algorithm, provider);
            } catch (Exception e) {
                throw new FsSecurityException(e);
            }
        });
    }

    /**
     * Returns new instance of specified algorithm and provider.
     * Returned instance has a back thread-local {@link KeyFactory}
     * which supplied with {@link KeyFactory#getInstance(String, Provider)}.
     *
     * @param algorithm specified algorithm
     * @param provider  specified provider
     * @return new instance of specified algorithm
     */
    static FsKeyFactory getInstance(String algorithm, Provider provider) {
        return new KeyFactoryImpl(algorithm, () -> {
            try {
                return KeyFactory.getInstance(algorithm, provider);
            } catch (Exception e) {
                throw new FsSecurityException(e);
            }
        });
    }

    /**
     * Returns back {@link KeyFactory} if it has, or null if it doesn't have one.
     * The back {@link KeyFactory} maybe thread-local, that is, returned value may be not only one instance.
     *
     * @return back {@link KeyFactory} if it has, or null if it doesn't have one
     */
    @Nullable
    KeyFactory getKeyFactory();

    /**
     * Generates public key by given key spec.
     *
     * @param spec given key spec
     * @return public key
     */
    PublicKey generatePublic(KeySpec spec);

    /**
     * Generates private key by given key spec.
     *
     * @param spec given key spec
     * @return private key
     */
    PrivateKey generatePrivate(KeySpec spec);
}
