package xyz.srclab.common.proxy;

public interface ClassProxy<T> {

    static <T> ClassProxyBuilder<T> newBuilder(Class<T> type) {
        return ClassProxyBuilder.newBuilder(type);
    }

    T newInstance();

    T newInstance(Class<?>[] parameterTypes, Object[] arguments);
}
