package xyz.srclab.common.util.proxy;

import xyz.srclab.annotation.Immutable;

@Immutable
public interface ClassProxyProvider {

    <T> ProxyClass.Builder<T> newBuilder(Class<T> type);
}
