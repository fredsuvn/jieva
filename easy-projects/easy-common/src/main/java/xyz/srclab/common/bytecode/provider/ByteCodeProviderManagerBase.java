package xyz.srclab.common.bytecode.provider;

import xyz.srclab.annotation.concurrent.ThreadSafe;
import xyz.srclab.common.environment.ClassPathHelper;
import xyz.srclab.common.bytecode.provider.cglib.CglibByteCodeProvider;
import xyz.srclab.common.bytecode.provider.spring.SpringByteCodeProvider;
import xyz.srclab.common.pattern.provider.AbstractProviderManager;

@ThreadSafe
public class ByteCodeProviderManagerBase extends AbstractProviderManager<ByteCodeProvider> {

    public static final ByteCodeProviderManagerBase INSTANCE = new ByteCodeProviderManagerBase();

    @Override
    protected ByteCodeProvider createDefaultProvider() {
        if (ClassPathHelper.hasClass("org.springframework.cglib.proxy.Enhancer")) {
            return SpringByteCodeProvider.INSTANCE;
        }
        if (ClassPathHelper.hasClass("net.sf.cglib.proxy.Enhancer")) {
            return CglibByteCodeProvider.INSTANCE;
        }
        throw new IllegalStateException("Can not create default byte code provider, " +
                "cause neither spring nor cglib classes are found.");
    }
}
