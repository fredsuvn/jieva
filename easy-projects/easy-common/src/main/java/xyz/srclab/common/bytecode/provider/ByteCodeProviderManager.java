package xyz.srclab.common.bytecode.provider;

import xyz.srclab.annotation.concurrent.ThreadSafe;
import xyz.srclab.common.base.EnvironmentHelper;
import xyz.srclab.common.bytecode.provider.cglib.CglibByteCodeProvider;
import xyz.srclab.common.bytecode.provider.spring.SpringByteCodeProvider;
import xyz.srclab.common.provider.AbstractProviderManager;

@ThreadSafe
public class ByteCodeProviderManager extends AbstractProviderManager<ByteCodeProvider> {

    public static final ByteCodeProviderManager INSTANCE = new ByteCodeProviderManager();

    @Override
    protected ByteCodeProvider createDefaultProvider() {
        if (EnvironmentHelper.hasClass("org.springframework.cglib.proxy.Enhancer")) {
            return SpringByteCodeProvider.INSTANCE;
        }
        if (EnvironmentHelper.hasClass("net.sf.cglib.proxy.Enhancer")) {
            return CglibByteCodeProvider.INSTANCE;
        }
        throw new IllegalStateException("Can not create default byte code provider, " +
                "cause neither spring nor cglib classes are found.");
    }
}
