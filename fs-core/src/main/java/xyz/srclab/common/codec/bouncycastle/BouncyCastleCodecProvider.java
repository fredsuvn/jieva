package xyz.srclab.common.codec.bouncycastle;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import xyz.srclab.common.codec.FsCodecProvider;
import xyz.srclab.common.codec.FsEncryptor;
import xyz.srclab.common.codec.jdk.JdkCodecProvider;

/**
 * <a href="https://www.bouncycastle.org">Bouncy Castle</a> codec provider.
 *
 * @author fredsuvn
 */
public class BouncyCastleCodecProvider implements FsCodecProvider {

    public static final BouncyCastleCodecProvider INSTANCE = new BouncyCastleCodecProvider();

    private final BouncyCastleProvider bouncyCastleProvider;

    private BouncyCastleCodecProvider() {
        bouncyCastleProvider = new BouncyCastleProvider();
    }

    @Override
    public FsEncryptor getEncryptor(String algorithmName) {
        //return new ProviderEncryptor(algorithmName, bouncyCastleProvider);
        return JdkCodecProvider.INSTANCE.getEncryptor(algorithmName);
    }
}
