package xyz.srclab.common.array;

import xyz.srclab.common.lang.TypeRef;

public interface ArrayBuilder<A> {

    static <E, A> NewArrayBuilder<E, A> fill(A array, Class<E> componentType) {
        return new NewArrayBuilder<>(array, componentType);
    }

    static <E, A> NewArrayBuilder<E, A> fill(A array, TypeRef<E> componentType) {
        return new NewArrayBuilder<>(array, componentType);
    }

    static <E, A> NewArrayBuilder<E, A> newArray(Class<A> arrayType, Class<E> componentType, int length) {
        return new NewArrayBuilder<>(arrayType, componentType, length);
    }

    static <E, A> NewArrayBuilder<E, A> newArray(Class<A> arrayType, TypeRef<E> componentType, int length) {
        return new NewArrayBuilder<>(arrayType, componentType, length);
    }

    static <E, A> NewArrayBuilder<E, A> newArray(TypeRef<A> arrayType, Class<E> componentType, int length) {
        return new NewArrayBuilder<>(arrayType, componentType, length);
    }

    static <E, A> NewArrayBuilder<E, A> newArray(TypeRef<A> arrayType, TypeRef<E> componentType, int length) {
        return new NewArrayBuilder<>(arrayType, componentType, length);
    }

    A build();
}
