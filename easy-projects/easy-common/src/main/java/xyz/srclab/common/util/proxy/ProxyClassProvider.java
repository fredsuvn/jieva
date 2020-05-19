package xyz.srclab.common.util.proxy;

import xyz.srclab.annotation.Immutable;

@Immutable
public interface ProxyClassProvider {

    <T> ProxyClassBuilder<T> newBuilder(Class<T> type);
}
