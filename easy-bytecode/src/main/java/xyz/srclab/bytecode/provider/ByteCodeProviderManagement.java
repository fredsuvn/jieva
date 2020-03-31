package xyz.srclab.bytecode.provider;

import xyz.srclab.annotation.concurrent.ThreadSafe;
import xyz.srclab.bytecode.provider.cglib.CglibByteCodeProvider;
import xyz.srclab.common.provider.AbstractProviderManagement;

@ThreadSafe
public class ByteCodeProviderManagement extends AbstractProviderManagement<ByteCodeProvider> {

    public static ByteCodeProviderManagement getInstance() {
        return INSTANCE;
    }

    private static final ByteCodeProviderManagement INSTANCE = new ByteCodeProviderManagement();

    @Override
    protected ByteCodeProvider createDefaultProvider() {
        return CglibByteCodeProvider.getInstance();
    }
}
