package xyz.srclab.common.bytecode.bean;

import xyz.srclab.annotations.Immutable;

@Immutable
public interface BeanClass<T> {

    static Builder<Object> newBuilder() {
        return BeanClassBuilderSupport.newBuilder(Object.class);
    }

    static <T> Builder<T> newBuilder(Class<T> superClass) {
        return BeanClassBuilderSupport.newBuilder(superClass);
    }

    T newInstance();

    interface Builder<T> {

        Builder<T> addProperty(String name, Class<?> type);

        BeanClass<T> build();
    }
}
