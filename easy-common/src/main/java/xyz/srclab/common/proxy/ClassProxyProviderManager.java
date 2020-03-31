package xyz.srclab.common.proxy;

import xyz.srclab.common.base.EnvironmentHelper;
import xyz.srclab.common.provider.AbstractProviderManager;
import xyz.srclab.common.proxy.bytecode.BytecodeClassProxyProvider;
import xyz.srclab.common.proxy.jdk.JdkClassProxyProvider;

public class ClassProxyProviderManager extends AbstractProviderManager<ClassProxyProvider> {

    public static ClassProxyProviderManager getInstance() {
        return INSTANCE;
    }

    private static final ClassProxyProviderManager INSTANCE = new ClassProxyProviderManager();

    @Override
    protected ClassProxyProvider createDefaultProvider() {
        return EnvironmentHelper.hasPackage("xyz.srclab.bytecode") ?
                BytecodeClassProxyProvider.getInstance() : JdkClassProxyProvider.getInstance();
    }
}
