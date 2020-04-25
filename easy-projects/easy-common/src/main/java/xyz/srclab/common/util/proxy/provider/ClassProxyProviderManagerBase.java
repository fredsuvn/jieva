package xyz.srclab.common.util.proxy.provider;

import xyz.srclab.common.pattern.provider.AbstractProviderManager;
import xyz.srclab.common.util.proxy.provider.bytecode.ByteCodeClassProxyProvider;

public class ClassProxyProviderManagerBase extends AbstractProviderManager<ClassProxyProvider> {

    public static final ClassProxyProviderManagerBase INSTANCE = new ClassProxyProviderManagerBase();

    @Override
    protected ClassProxyProvider createDefaultProvider() {
        return ByteCodeClassProxyProvider.INSTANCE;
    }
}
