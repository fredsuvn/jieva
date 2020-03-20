package xyz.srclab.common.bytecode;

public interface ClassBuilder<T> {

    static ClassBuilder<Object> newBuilder() {
        return ClassBuilderImpl.newBuilder();
    }

    static <T> ClassBuilder<T> newBuilder(Class<T> superClass) {
        return ClassBuilderImpl.newBuilder(superClass);
    }

    ClassBuilder<T> setInterfaces(Iterable<Class<?>> interfaces);

    ClassBuilder<T> addMethod(
            String name, Class<?>[] parameterTypes, MethodBody<Void> methodBody);

    <R> ClassBuilder<T> addMethod(
            String name, Class<?>[] parameterTypes, Class<R> returnType, MethodBody<R> methodBody);

    ClassConstructor<T> build();
}
