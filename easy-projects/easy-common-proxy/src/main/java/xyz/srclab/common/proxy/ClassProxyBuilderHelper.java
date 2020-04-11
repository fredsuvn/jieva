package xyz.srclab.common.proxy;

import xyz.srclab.common.proxy.provider.ClassProxyProvider;
import xyz.srclab.common.proxy.provider.ClassProxyProviderManager;

class ClassProxyBuilderHelper {

    private static final ClassProxyProvider classProxyProvider = ClassProxyProviderManager.INSTANCE.getProvider();

    static <T> ClassProxy.Builder<T> newBuilder(Class<T> superClass) {
        return classProxyProvider.newBuilder(superClass);
    }
}
