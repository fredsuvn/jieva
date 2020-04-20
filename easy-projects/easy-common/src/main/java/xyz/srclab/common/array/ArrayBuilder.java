package xyz.srclab.common.array;

import xyz.srclab.common.reflect.type.TypeRef;

public interface ArrayBuilder<A> {

    static <A, E> NewArrayBuilder<A, E> fill(A array, Class<E> componentType) {
        return new NewArrayBuilder<>(array, componentType);
    }

    static <A, E> NewArrayBuilder<A, E> fill(A array, TypeRef<E> componentType) {
        return new NewArrayBuilder<>(array, componentType);
    }

    static <A, E> NewArrayBuilder<A, E> newArray(Class<A> arrayType, Class<E> componentType, int length) {
        return new NewArrayBuilder<>(arrayType, componentType, length);
    }

    static <A, E> NewArrayBuilder<A, E> newArray(Class<A> arrayType, TypeRef<E> componentType, int length) {
        return new NewArrayBuilder<>(arrayType, componentType, length);
    }

    static <A, E> NewArrayBuilder<A, E> newArray(TypeRef<A> arrayType, Class<E> componentType, int length) {
        return new NewArrayBuilder<>(arrayType, componentType, length);
    }

    static <A, E> NewArrayBuilder<A, E> newArray(TypeRef<A> arrayType, TypeRef<E> componentType, int length) {
        return new NewArrayBuilder<>(arrayType, componentType, length);
    }

    A build();
}
