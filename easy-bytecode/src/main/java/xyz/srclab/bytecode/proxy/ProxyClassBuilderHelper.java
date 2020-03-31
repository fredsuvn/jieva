package xyz.srclab.bytecode.proxy;

import xyz.srclab.bytecode.provider.ByteCodeProvider;
import xyz.srclab.bytecode.provider.ByteCodeProviderManager;

class ProxyClassBuilderHelper {

    private static final ByteCodeProvider byteCodeProvider = ByteCodeProviderManager.getInstance().getProvider();

    static <T> ProxyClass.Builder<T> newBuilder(Class<T> superClass) {
        return byteCodeProvider.newProxyClassBuilder(superClass);
    }
}
