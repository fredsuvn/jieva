package xyz.srclab.common.bytecode.provider;

import xyz.srclab.common.bytecode.provider.cglib.CglibByteCodeProvider;
import xyz.srclab.common.provider.AbstractProviderManagement;

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
