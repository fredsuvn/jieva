package xyz.srclab.common.bytecode;

import xyz.srclab.annotation.Immutable;

import java.util.Map;

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

        Builder<T> addProperties(Map<String, Class<?>> properties);

        BeanClass<T> build();
    }
}
