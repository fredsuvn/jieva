package xyz.srclab.bytecode.provider;

import xyz.srclab.annotation.concurrent.ThreadSafe;
import xyz.srclab.bytecode.provider.cglib.CglibByteCodeProvider;
import xyz.srclab.bytecode.provider.cglib.SpringCglibByteCodeProvider;
import xyz.srclab.common.base.EnvironmentHelper;
import xyz.srclab.common.provider.AbstractProviderManager;

@ThreadSafe
public class ByteCodeProviderManager extends AbstractProviderManager<ByteCodeProvider> {

    public static final ByteCodeProviderManager INSTANCE = new ByteCodeProviderManager();

    @Override
    protected ByteCodeProvider createDefaultProvider() {
        return EnvironmentHelper.hasPackage("org.springframework.cglib.proxy") ?
                SpringCglibByteCodeProvider.INSTANCE : CglibByteCodeProvider.INSTANCE;
    }
}
