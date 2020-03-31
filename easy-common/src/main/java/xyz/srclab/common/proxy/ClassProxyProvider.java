package xyz.srclab.common.proxy;

public interface ClassProxyProvider {

    <T> ClassProxy.Builder<T> newBuilder(Class<T> type);
}
