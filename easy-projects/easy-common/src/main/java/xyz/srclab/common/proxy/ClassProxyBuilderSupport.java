package xyz.srclab.common.proxy;

import xyz.srclab.common.proxy.provider.ClassProxyProvider;
import xyz.srclab.common.proxy.provider.ClassProxyProviderManagerBase;

class ClassProxyBuilderSupport {

    private static final ClassProxyProvider classProxyProvider = ClassProxyProviderManagerBase.INSTANCE.getProvider();

    static <T> ClassProxy.Builder<T> newBuilder(Class<T> superClass) {
        return classProxyProvider.newBuilder(superClass);
    }
}
