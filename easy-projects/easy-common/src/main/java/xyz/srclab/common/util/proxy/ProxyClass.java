package xyz.srclab.common.util.proxy;

import xyz.srclab.annotation.Immutable;

@Immutable
public interface ProxyClass<T> {

    static <T> ProxyClassBuilder<T> newBuilder(Class<T> type) {
        return ProxyClassSupport.newBuilder(type);
    }

    T newInstance();

    T newInstance(Class<?>[] parameterTypes, Object[] args);

    Class<T> getProxyClass();
}
