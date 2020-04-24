package xyz.srclab.common.util.proxy.provider;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.common.util.proxy.ClassProxy;

@Immutable
public interface ClassProxyProvider {

    <T> ClassProxy.Builder<T> newBuilder(Class<T> type);
}
