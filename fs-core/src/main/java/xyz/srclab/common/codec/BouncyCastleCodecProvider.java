package xyz.srclab.common.codec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * JDK codec provider.
 *
 * @author fredsuvn
 */
public class BouncyCastleCodecProvider implements FsCodecProvider {

    public static final BouncyCastleCodecProvider INSTANCE = new BouncyCastleCodecProvider();

    private BouncyCastleProvider bouncyCastleProvider;

    private BouncyCastleCodecProvider() {
        // bouncyCastleProvider = new BouncyCastleProvider();
    }

    @Override
    public FsEncoder getEncoder(String algorithmName) {
        // Using JDK
        return JdkCodecProvider.INSTANCE.getEncoder(algorithmName);
    }
}
