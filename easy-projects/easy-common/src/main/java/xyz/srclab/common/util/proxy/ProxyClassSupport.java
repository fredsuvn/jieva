package xyz.srclab.common.util.proxy;

import xyz.srclab.common.EasyBoot;

final class ProxyClassSupport {

    private static final ClassProxyProvider classProxyProvider =
            EasyBoot.getProvider(ClassProxyProvider.class.getName());

    static <T> ProxyClass.Builder<T> newBuilder(Class<T> superClass) {
        return classProxyProvider.newBuilder(superClass);
    }
}
