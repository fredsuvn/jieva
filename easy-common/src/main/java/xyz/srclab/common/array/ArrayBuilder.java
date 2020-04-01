package xyz.srclab.common.array;

import xyz.srclab.common.lang.TypeRef;

public interface ArrayBuilder<A> {

    static <E, A> NewArrayBuilder<E, A> fill(A array) {
        return new NewArrayBuilder<>(array);
    }

    static <E, A> NewArrayBuilder<E, A> newArray(Class<A> arrayType, int length) {
        return new NewArrayBuilder<>(arrayType, length);
    }

    static <E, A> NewArrayBuilder<E, A> newArray(TypeRef<A> arrayType, int length) {
        return new NewArrayBuilder<>(arrayType.getRawType(), length);
    }

    static <OE, NE, A> MapArrayBuilder<OE, NE, A> map(Object array, Class<A> arrayType) {
        return new MapArrayBuilder<>(array, arrayType);
    }

    static <OE, NE, A> MapArrayBuilder<OE, NE, A> map(Iterable<OE> iterable, Class<A> arrayType) {
        return new MapArrayBuilder<>(iterable, arrayType);
    }

    A build();
}
