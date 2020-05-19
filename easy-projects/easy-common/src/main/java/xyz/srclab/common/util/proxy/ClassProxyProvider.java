package xyz.srclab.common.util.proxy;

import xyz.srclab.annotation.Immutable;

@Immutable
public interface ClassProxyProvider {

    <T> ProxyClassBuilder<T> newBuilder(Class<T> type);
}
