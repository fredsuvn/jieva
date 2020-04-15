package xyz.srclab.common.bytecode.provider;

import xyz.srclab.annotation.concurrent.ThreadSafe;
import xyz.srclab.common.base.EnvironmentHelper;
import xyz.srclab.common.bytecode.provider.cglib.CglibByteCodeProvider;
import xyz.srclab.common.bytecode.provider.cglib.SpringCglibByteCodeProvider;
import xyz.srclab.common.provider.AbstractProviderManager;

@ThreadSafe
public class ByteCodeProviderManager extends AbstractProviderManager<ByteCodeProvider> {

    public static final ByteCodeProviderManager INSTANCE = new ByteCodeProviderManager();

    @Override
    protected ByteCodeProvider createDefaultProvider() {
        return EnvironmentHelper.hasClass("org.springframework.cglib.proxy.Enhancer") ?
                SpringCglibByteCodeProvider.INSTANCE : CglibByteCodeProvider.INSTANCE;
    }
}
