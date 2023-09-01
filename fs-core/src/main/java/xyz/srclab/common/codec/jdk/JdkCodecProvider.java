package xyz.srclab.common.codec.jdk;

import xyz.srclab.common.codec.FsCodecProvider;
import xyz.srclab.common.codec.FsEncryptor;
import xyz.srclab.common.codec.ProviderEncryptor;

/**
 * JDK codec provider.
 *
 * @author fredsuvn
 */
public class JdkCodecProvider implements FsCodecProvider {

    public static final JdkCodecProvider INSTANCE = new JdkCodecProvider();

    private JdkCodecProvider() {
    }

    @Override
    public FsEncryptor getEncryptor(String algorithmName) {
        return new ProviderEncryptor(algorithmName, null);
    }
}
