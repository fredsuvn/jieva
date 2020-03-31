package xyz.srclab.bytecode.proxy;

import xyz.srclab.bytecode.provider.ByteCodeProviderManagement;

public interface ProxyClassBuilderProvider {

    static ProxyClassBuilderProvider getInstance() {
        return ByteCodeProviderManagement.getInstance().getProvider().getProxyClassBuilderProvider();
    }

    <T> ProxyClass.Builder<T> newBuilder(Class<T> superClass);
}
