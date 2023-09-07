package xyz.srclab.common.security;

import xyz.srclab.annotations.Nullable;

import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;

/**
 * Denotes key generator, maybe has a back {@link KeyGenerator}.
 *
 * @author fredsuvn
 * @see KeyGenerator
 */
public interface FsKeyGen {

    /**
     * Returns new instance of specified algorithm.
     * Returned instance has a back thread-local mac which supplied with {@link KeyGenerator#getInstance(String)}.
     *
     * @param algorithm specified algorithm
     */
    static FsKeyGen getInstance(String algorithm) {
        return new KeyGenImpl(() -> {
            try {
                return KeyGenerator.getInstance(algorithm);
            } catch (Exception e) {
                throw new FsSecurityException(e);
            }
        });
    }

    /**
     * Returns new instance of specified algorithm and provider.
     * Returned instance has a back thread-local mac which supplied with {@link KeyGenerator#getInstance(String, String)}.
     *
     * @param algorithm specified algorithm
     * @param provider  specified provider
     */
    static FsKeyGen getInstance(String algorithm, String provider) {
        return new KeyGenImpl(() -> {
            try {
                return KeyGenerator.getInstance(algorithm, provider);
            } catch (Exception e) {
                throw new FsSecurityException(e);
            }
        });
    }

    /**
     * Returns new instance of specified algorithm and provider.
     * Returned instance has a back thread-local mac which supplied with {@link KeyGenerator#getInstance(String, Provider)}.
     *
     * @param algorithm specified algorithm
     * @param provider  specified provider
     */
    static FsKeyGen getInstance(String algorithm, Provider provider) {
        return new KeyGenImpl(() -> {
            try {
                return KeyGenerator.getInstance(algorithm, provider);
            } catch (Exception e) {
                throw new FsSecurityException(e);
            }
        });
    }

    /**
     * Generates key of specified algorithm from given array.
     *
     * @param algorithm specified algorithm
     * @param bytes     given array
     */
    static SecretKeySpec generate(String algorithm, byte[] bytes) {
        try {
            return generate(algorithm, bytes, 0, bytes.length);
        } catch (FsSecurityException e) {
            throw e;
        } catch (Exception e) {
            throw new FsSecurityException(e);
        }
    }

    /**
     * Generates key of specified algorithm from given array of specified length start from given offset.
     *
     * @param algorithm specified algorithm
     * @param bytes     given array
     * @param offset    start offset
     * @param length    specified length
     */
    static SecretKeySpec generate(String algorithm, byte[] bytes, int offset, int length) {
        try {
            return new SecretKeySpec(bytes, offset, length, algorithm);
        } catch (FsSecurityException e) {
            throw e;
        } catch (Exception e) {
            throw new FsSecurityException(e);
        }
    }

    /**
     * Returns back {@link KeyGenerator} if it has, or null if it doesn't have one.
     * The back {@link KeyGenerator} maybe thread-local, that is, returned value may be not only one instance.
     */
    @Nullable
    KeyGenerator getKeyGenerator();

    /**
     * Generates key.
     */
    Key generateKey();

    /**
     * Generates key with specified size.
     *
     * @param size specified key size
     */
    Key generateKey(int size);

    /**
     * Generates with specified key size and secure random.
     *
     * @param size         specified key size
     * @param secureRandom secure random
     */
    Key generateKey(int size, SecureRandom secureRandom);

    /**
     * Generates with algorithm parameter spec.
     *
     * @param spec algorithm parameter spec
     */
    Key generateKey(AlgorithmParameterSpec spec);

    /**
     * Generates with secure random.
     *
     * @param secureRandom secure random
     */
    Key generateKey(SecureRandom secureRandom);

    /**
     * Generates with algorithm parameter spec and secure random.
     *
     * @param spec         algorithm parameter spec
     * @param secureRandom secure random
     */
    Key generateKey(AlgorithmParameterSpec spec, SecureRandom secureRandom);
}
