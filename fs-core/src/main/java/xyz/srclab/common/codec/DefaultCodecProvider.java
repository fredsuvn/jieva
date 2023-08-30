package xyz.srclab.common.codec;

import xyz.srclab.common.codec.bouncycastle.BouncyCastleCodecProvider;
import xyz.srclab.common.codec.jdk.JdkCodecProvider;
import xyz.srclab.common.reflect.FsType;

final class DefaultCodecProvider implements FsCodecProvider {

    static FsCodecProvider INSTANCE = new DefaultCodecProvider();

    private final FsCodecProvider provider;

    DefaultCodecProvider() {
        if (FsType.hasClass("org.bouncycastle.jce.provider.BouncyCastleProvider")) {
            provider = BouncyCastleCodecProvider.INSTANCE;
        } else {
            provider = JdkCodecProvider.INSTANCE;
        }
    }

    @Override
    public FsEncoder getEncoder(String algorithmName) {
        return provider.getEncoder(algorithmName);
    }

    @Override
    public FsEncryptor getEncryptor(String algorithmName) {
        return provider.getEncryptor(algorithmName);
    }
}
