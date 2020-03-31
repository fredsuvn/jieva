package xyz.srclab.bytecode.provider;

import xyz.srclab.annotation.concurrent.ThreadSafe;
import xyz.srclab.bytecode.provider.cglib.CglibByteCodeProvider;
import xyz.srclab.bytecode.provider.cglib.SpringCglibByteCodeProvider;
import xyz.srclab.common.base.EnvironmentHelper;
import xyz.srclab.common.provider.AbstractProviderManagement;

@ThreadSafe
public class ByteCodeProviderManagement extends AbstractProviderManagement<ByteCodeProvider> {

    public static ByteCodeProviderManagement getInstance() {
        return INSTANCE;
    }

    private static final ByteCodeProviderManagement INSTANCE = new ByteCodeProviderManagement();

    @Override
    protected ByteCodeProvider createDefaultProvider() {
        return EnvironmentHelper.hasPackage("org.springframework.cglib.proxy") ?
                SpringCglibByteCodeProvider.getInstance() : CglibByteCodeProvider.getInstance();
    }
}
