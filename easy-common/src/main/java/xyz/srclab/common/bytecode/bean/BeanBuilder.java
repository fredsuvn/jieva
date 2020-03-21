package xyz.srclab.common.bytecode.bean;

public interface BeanBuilder<T> {

    static BeanBuilder<Object> newBuilder() {
        return BeanOperator.getInstance().newBuilder();
    }

    static <T> BeanBuilder<T> newBuilder(Class<T> superClass) {
        return BeanOperator.getInstance().newBuilder(superClass);
    }

    BeanBuilder<T> addProperty(String name, Class<?> type);

    BeanClass<T> build();
}
