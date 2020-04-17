package xyz.srclab.common.proxy.provider;

import xyz.srclab.common.pattern.provider.AbstractProviderManager;
import xyz.srclab.common.proxy.provider.bytecode.ByteCodeClassProxyProvider;

public class ClassProxyProviderManager extends AbstractProviderManager<ClassProxyProvider> {

    public static final ClassProxyProviderManager INSTANCE = new ClassProxyProviderManager();

    @Override
    protected ClassProxyProvider createDefaultProvider() {
        return ByteCodeClassProxyProvider.INSTANCE;
    }
}
