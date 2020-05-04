package xyz.srclab.common.util.proxy;

import xyz.srclab.common.ToovaBoot;

final class ProxyClassSupport {

    private static final ClassProxyProvider classProxyProvider = ToovaBoot.getProvider(ClassProxyProvider.class);

    static <T> ProxyClass.Builder<T> newBuilder(Class<T> superClass) {
        return classProxyProvider.newBuilder(superClass);
    }
}
