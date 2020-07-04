package xyz.srclab.common.util.proxy;

import xyz.srclab.common.KitvaBoot;

final class ProxyClassSupport {

    private static final ProxyClassProvider proxyClassProvider = KitvaBoot.getProvider(ProxyClassProvider.class);

    static <T> ProxyClassBuilder<T> newBuilder(Class<T> superClass) {
        return proxyClassProvider.newBuilder(superClass);
    }
}
