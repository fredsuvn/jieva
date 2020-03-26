package xyz.srclab.common.bytecode.proxy;

import xyz.srclab.common.bytecode.provider.ByteCodeProviderManagement;

public interface ProxyClassBuilderProvider {

    static ProxyClassBuilderProvider getInstance() {
        return ByteCodeProviderManagement.getInstance().getProxyClassBuilderProvider();
    }

    <T> ProxyClass.Builder<T> newBuilder(Class<T> superClass);
}
