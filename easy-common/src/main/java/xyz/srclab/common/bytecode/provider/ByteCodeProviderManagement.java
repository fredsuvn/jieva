package xyz.srclab.common.bytecode.provider;

import xyz.srclab.common.bytecode.provider.cglib.CglibByteCodeProvider;

public class ByteCodeProviderManagement {

    private static ByteCodeProvider provider = CglibByteCodeProvider.getInstance();

    public static void registerProvider(ByteCodeProvider byteCodeProvider) {
        provider = byteCodeProvider;
    }

    public static ByteCodeProvider getByteCodeProvider() {
        return provider;
    }
}
