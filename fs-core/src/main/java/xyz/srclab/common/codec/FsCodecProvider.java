package xyz.srclab.common.codec;

import xyz.srclab.annotations.concurrent.ThreadSafe;

/**
 * Codec provider, see {@link FsAlgorithm} for commonly used algorithm.
 *
 * @author sunq62
 */
@ThreadSafe
public interface FsCodecProvider {

    /**
     * Returns default codec provider.
     * <p>
     * There are two type of provider:
     * <ul>
     *     <li>JDK provider: using JDK built-in;</li>
     *     <li>Bouncy Castle: using <a href="https://www.bouncycastle.org">Bouncy Castle</a>;</li>
     * </ul>
     * By default, system will check the required libs in current runtime to choose a provider in priority:
     * Bouncy Castle > JDK.
     * <p>
     */
    static FsCodecProvider defaultProvider() {
        return DefaultCodecProvider.INSTANCE;
    }

    /**
     * Returns encoder of specified algorithm name.
     *
     * @param algorithmName specified algorithm name
     */
    FsEncoder getEncoder(String algorithmName);

    /**
     * Returns encoder of specified algorithm name.
     *
     * @param algorithmName specified algorithm name
     */
    FsEncryptor getEncryptor(String algorithmName);
}
