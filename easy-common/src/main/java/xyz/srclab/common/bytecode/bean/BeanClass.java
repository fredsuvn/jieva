package xyz.srclab.common.bytecode.bean;

public interface BeanClass<T> {

    static Builder<Object> newBuilder() {
        return Builder.newBuilder();
    }

    static <T> Builder<T> newBuilder(Class<T> superClass) {
        return Builder.newBuilder(superClass);
    }

    T newInstance();

    interface Builder<T> {

        static Builder<Object> newBuilder() {
            return BeanOperator.getInstance().newBuilder();
        }

        static <T> Builder<T> newBuilder(Class<T> superClass) {
            return BeanOperator.getInstance().newBuilder(superClass);
        }

        Builder<T> addProperty(String name, Class<?> type);

        BeanClass<T> build();
    }
}
