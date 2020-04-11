package xyz.srclab.common.proxy.provider;

import xyz.srclab.common.base.EnvironmentHelper;
import xyz.srclab.common.provider.AbstractProviderManager;
import xyz.srclab.common.proxy.provider.bytecode.ByteCodeClassProxyProvider;
import xyz.srclab.common.proxy.provider.jdk.JdkClassProxyProvider;

public class ClassProxyProviderManager extends AbstractProviderManager<ClassProxyProvider> {

    public static final ClassProxyProviderManager INSTANCE = new ClassProxyProviderManager();

    @Override
    protected ClassProxyProvider createDefaultProvider() {
        return EnvironmentHelper.hasPackage("xyz.srclab.bytecode") ?
                ByteCodeClassProxyProvider.INSTANCE : JdkClassProxyProvider.INSTANCE;
    }
}
