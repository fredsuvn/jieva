package xyz.srclab.common.proxy.provider;

import xyz.srclab.common.base.EnvironmentHelper;
import xyz.srclab.common.provider.AbstractProviderManager;
import xyz.srclab.common.proxy.provider.bytecode.ByteCodeClassProxyProvider;
import xyz.srclab.common.proxy.provider.jdk.JdkClassProxyProvider;

public class ClassProxyProviderManager extends AbstractProviderManager<ClassProxyProvider> {

    public static ClassProxyProviderManager getInstance() {
        return INSTANCE;
    }

    private static final ClassProxyProviderManager INSTANCE = new ClassProxyProviderManager();

    @Override
    protected ClassProxyProvider createDefaultProvider() {
        return EnvironmentHelper.findPackage("xyz.srclab.bytecode").isPresent() ?
                ByteCodeClassProxyProvider.getInstance() : JdkClassProxyProvider.getInstance();
    }
}
