package xyz.srclab.common.bytecode;

import xyz.srclab.common.bytecode.cglib.CglibClassBuilder;
import xyz.srclab.common.bytecode.spring.SpringClassBuilder;

class ClassBuilderImpl<T> implements ClassBuilder<T> {

    static ClassBuilderImpl<Object> newBuilder() {
        return new ClassBuilderImpl<>(Object.class);
    }

    static <T> ClassBuilderImpl<T> newBuilder(Class<T> superClass) {
        return new ClassBuilderImpl<>(superClass);
    }

    private final ClassBuilder<T> delegate;

    ClassBuilderImpl(Class<T> superClass) {
        Package springPackage = Package.getPackage("org.springframework.cglib.proxy");
        if (springPackage != null) {
            this.delegate = new SpringClassBuilder<>(superClass);
        } else {
            this.delegate = new CglibClassBuilder<>(superClass);
        }
    }

    @Override
    public ClassBuilder<T> setInterfaces(Iterable<Class<?>> interfaces) {
        return delegate.setInterfaces(interfaces);
    }

    @Override
    public ClassBuilder<T> addMethod(
            String name, Class<?>[] parameterTypes, MethodBody<Void> methodBody) {
        return delegate.addMethod(name, parameterTypes, methodBody);
    }

    @Override
    public <R> ClassBuilder<T> addMethod(
            String name, Class<?>[] parameterTypes, Class<R> returnType, MethodBody<R> methodBody) {
        return delegate.addMethod(name, parameterTypes, returnType, methodBody);
    }

    @Override
    public ClassConstructor<T> build() {
        return delegate.build();
    }
}
