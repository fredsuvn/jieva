package xyz.srclab.common.array;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.builder.CacheStateBuilder;
import xyz.srclab.common.lang.TypeRef;
import xyz.srclab.common.reflect.type.TypeHelper;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.util.function.Function;

public class NewArrayBuilder<A, E> extends CacheStateBuilder<A> implements ArrayBuilder<A> {

    private final A array;
    private @Nullable Function<Integer, E> eachElement;

    NewArrayBuilder(A array, Class<E> componentType) {
        if (!array.getClass().isArray()) {
            throw new IllegalStateException("Must be array: " + array);
        }
        this.array = array;
    }

    NewArrayBuilder(A array, TypeRef<E> componentType) {
        if (!array.getClass().isArray()) {
            throw new IllegalStateException("Must be array: " + array);
        }
        this.array = array;
    }

    NewArrayBuilder(Class<A> arrayType, Class<E> componentType, int length) {
        if (!arrayType.isArray()) {
            throw new IllegalStateException("Must be array: " + arrayType);
        }
        this.array = ArrayHelper.newArray(componentType, length);
    }

    NewArrayBuilder(Class<A> arrayType, TypeRef<E> componentType, int length) {
        if (!arrayType.isArray()) {
            throw new IllegalStateException("Must be array: " + arrayType);
        }
        this.array = ArrayHelper.newArray(componentType.getRawType(), length);
    }

    NewArrayBuilder(TypeRef<A> arrayType, Class<E> componentType, int length) {
        Type type = arrayType.getType();
        if (!(type instanceof GenericArrayType) && !TypeHelper.getRawClass(type).isArray()) {
            throw new IllegalStateException("Must be array: " + arrayType);
        }
        this.array = ArrayHelper.newArray(componentType, length);
    }

    NewArrayBuilder(TypeRef<A> arrayType, TypeRef<E> componentType, int length) {
        Type type = arrayType.getType();
        if (!(type instanceof GenericArrayType) && !TypeHelper.getRawClass(type).isArray()) {
            throw new IllegalStateException("Must be array: " + arrayType);
        }
        this.array = ArrayHelper.newArray(componentType.getRawType(), length);
    }

    public NewArrayBuilder<A, E> setEachElement(Function<Integer, E> eachElement) {
        this.changeState();
        this.eachElement = eachElement;
        return this;
    }

    @Override
    protected A buildNew() {
        if (eachElement != null) {
            for (int i = 0; i < Array.getLength(array); i++) {
                Array.set(array, i, eachElement.apply(i));
            }
        }
        return array;
    }
}
