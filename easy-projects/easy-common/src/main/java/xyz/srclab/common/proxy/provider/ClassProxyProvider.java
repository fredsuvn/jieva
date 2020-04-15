package xyz.srclab.common.proxy.provider;

import xyz.srclab.annotations.Immutable;
import xyz.srclab.common.proxy.ClassProxy;

@Immutable
public interface ClassProxyProvider {

    <T> ClassProxy.Builder<T> newBuilder(Class<T> type);
}
