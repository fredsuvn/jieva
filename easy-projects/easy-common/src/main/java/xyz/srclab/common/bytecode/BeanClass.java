package xyz.srclab.common.bytecode;

import xyz.srclab.annotation.Immutable;

@Immutable
public interface BeanClass<T> {

    static Builder<Object> newBuilder() {
        return ByteCodeSupport.newBeanClassBuilder(Object.class);
    }

    static <T> Builder<T> newBuilder(Class<T> superClass) {
        return ByteCodeSupport.newBeanClassBuilder(superClass);
    }

    T newInstance();

    interface Builder<T> {

        Builder<T> addProperty(String name, Class<?> type);

        BeanClass<T> build();
    }
}
