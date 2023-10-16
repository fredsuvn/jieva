package xyz.fsgik.common.security;

import xyz.fsgik.annotations.Nullable;
import xyz.fsgik.annotations.ThreadSafe;

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
@ThreadSafe
public interface FsKeyGen extends SecurityAlgorithm {

    /**
     * Returns new instance of specified algorithm.
     * Returned instance has a back thread-local {@link KeyGenerator}
     * which supplied with {@link KeyGenerator#getInstance(String)}.
     *
     * @param algorithm specified algorithm
     * @return new instance of specified algorithm
     */
    static FsKeyGen getInstance(String algorithm) {
        return new KeyGenImpl(algorithm, () -> {
            try {
                return KeyGenerator.getInstance(algorithm);
            } catch (Exception e) {
                throw new FsSecurityException(e);
            }
        });
    }

    /**
     * Returns new instance of specified algorithm and provider.
     * Returned instance has a back thread-local {@link KeyGenerator}
     * which supplied with {@link KeyGenerator#getInstance(String, String)}.
     *
     * @param algorithm specified algorithm
     * @param provider  specified provider
     * @return new instance of specified algorithm
     */
    static FsKeyGen getInstance(String algorithm, String provider) {
        return new KeyGenImpl(algorithm, () -> {
            try {
                return KeyGenerator.getInstance(algorithm, provider);
            } catch (Exception e) {
                throw new FsSecurityException(e);
            }
        });
    }

    /**
     * Returns new instance of specified algorithm and provider.
     * Returned instance has a back thread-local {@link KeyGenerator}
     * which supplied with {@link KeyGenerator#getInstance(String, Provider)}.
     *
     * @param algorithm specified algorithm
     * @param provider  specified provider
     * @return new instance of specified algorithm
     */
    static FsKeyGen getInstance(String algorithm, Provider provider) {
        return new KeyGenImpl(algorithm, () -> {
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
     * @return key of specified algorithm
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
     * @return key of specified algorithm
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
     *
     * @return back {@link KeyGenerator} if it has, or null if it doesn't have one
     */
    @Nullable
    KeyGenerator getKeyGenerator();

    /**
     * Generates key.
     *
     * @return the key
     */
    Key generateKey();

    /**
     * Generates key with specified size.
     *
     * @param size specified key size
     * @return the key
     */
    Key generateKey(int size);

    /**
     * Generates with specified key size and secure random.
     *
     * @param size         specified key size
     * @param secureRandom secure random
     * @return the key
     */
    Key generateKey(int size, SecureRandom secureRandom);

    /**
     * Generates with algorithm parameter spec.
     *
     * @param spec algorithm parameter spec
     * @return the key
     */
    Key generateKey(AlgorithmParameterSpec spec);

    /**
     * Generates with secure random.
     *
     * @param secureRandom secure random
     * @return the key
     */
    Key generateKey(SecureRandom secureRandom);

    /**
     * Generates with algorithm parameter spec and secure random.
     *
     * @param spec         algorithm parameter spec
     * @param secureRandom secure random
     * @return the key
     */
    Key generateKey(AlgorithmParameterSpec spec, SecureRandom secureRandom);
}
