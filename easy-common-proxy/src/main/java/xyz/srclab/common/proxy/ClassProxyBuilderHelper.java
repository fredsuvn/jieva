package xyz.srclab.common.proxy;

class ClassProxyBuilderHelper {

    private static final ClassProxyProvider classProxyProvider = ClassProxyProviderManager.getInstance().getProvider();

    static <T> ClassProxy.Builder<T> newBuilder(Class<T> superClass) {
        return classProxyProvider.newBuilder(superClass);
    }
}
