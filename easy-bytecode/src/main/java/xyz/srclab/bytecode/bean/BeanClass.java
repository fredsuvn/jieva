package xyz.srclab.bytecode.bean;

import xyz.srclab.annotation.concurrent.ThreadSafe;

@ThreadSafe
public interface BeanClass<T> {

    static Builder<Object> newBuilder() {
        return BeanClassBuilderHelper.newBuilder(Object.class);
    }

    static <T> Builder<T> newBuilder(Class<T> superClass) {
        return BeanClassBuilderHelper.newBuilder(superClass);
    }

    T newInstance();

    interface Builder<T> {

        Builder<T> addProperty(String name, Class<?> type);

        BeanClass<T> build();
    }
}
