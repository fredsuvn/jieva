package xyz.srclab.common.bytecode.proxy;

import xyz.srclab.common.bytecode.provider.ByteCodeProviderManagement;

public interface ProxyClassBuilderProvider {

    static ProxyClassBuilderProvider getInstance() {
        return ByteCodeProviderManagement.getByteCodeProvider().getProxyClassBuilderProvider();
    }

    <T> ProxyClass.Builder<T> newBuilder(Class<T> superClass);
}
