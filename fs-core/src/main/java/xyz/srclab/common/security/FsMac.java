package xyz.srclab.common.security;

import xyz.srclab.annotations.Nullable;

import javax.crypto.Mac;
import java.security.Provider;

/**
 * Denotes cipher for MAC generation, maybe has a back {@link Mac}.
 * <p>
 * It is recommended to use method-chaining:
 * <pre>
 *     byte[] macBytes = mac.prepare(bytes).key(key).bufferSize(size).mac().doFinal();
 * </pre>
 *
 * @author fredsuvn
 * @see Mac
 */
public interface FsMac extends Prepareable {

    /**
     * Returns new instance of specified algorithm.
     * Returned instance has a back thread-local mac which supplied with {@link Mac#getInstance(String)}.
     *
     * @param algorithm specified algorithm
     */
    static FsMac getInstance(String algorithm) {
        return new MacImpl(() -> {
            try {
                return Mac.getInstance(algorithm);
            } catch (Exception e) {
                throw new FsSecurityException(e);
            }
        });
    }

    /**
     * Returns new instance of specified algorithm and provider.
     * Returned instance has a back thread-local mac which supplied with {@link Mac#getInstance(String, String)}.
     *
     * @param algorithm specified algorithm
     * @param provider  specified provider
     */
    static FsMac getInstance(String algorithm, String provider) {
        return new MacImpl(() -> {
            try {
                return Mac.getInstance(algorithm, provider);
            } catch (Exception e) {
                throw new FsSecurityException(e);
            }
        });
    }

    /**
     * Returns new instance of specified algorithm and provider.
     * Returned instance has a back thread-local mac which supplied with {@link Mac#getInstance(String, Provider)}.
     *
     * @param algorithm specified algorithm
     * @param provider  specified provider
     */
    static FsMac getInstance(String algorithm, Provider provider) {
        return new MacImpl(() -> {
            try {
                return Mac.getInstance(algorithm, provider);
            } catch (Exception e) {
                throw new FsSecurityException(e);
            }
        });
    }

    /**
     * Returns back {@link Mac} if it has, or null if it doesn't have one.
     * The back {@link Mac} maybe thread-local, that is, returned value may be not only one instance.
     */
    @Nullable
    Mac getMac();

    /**
     * Returns MAC length.
     */
    int getMacLength();
}
