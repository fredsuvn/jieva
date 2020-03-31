package xyz.srclab.bytecode.bean;

public interface BeanClass<T> {

    static Builder<Object> newBuilder() {
        return Builder.newBuilder(Object.class);
    }

    static <T> Builder<T> newBuilder(Class<T> superClass) {
        return Builder.newBuilder(superClass);
    }

    T newInstance();

    interface Builder<T> {

        static <T> Builder<T> newBuilder(Class<T> superClass) {
            return BeanClassBuilderProvider.getInstance().newBuilder(superClass);
        }

        Builder<T> addProperty(String name, Class<?> type);

        BeanClass<T> build();
    }
}
