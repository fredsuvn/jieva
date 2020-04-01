package xyz.srclab.common.array;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.builder.CacheStateBuilder;
import xyz.srclab.common.reflect.type.TypeHelper;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.function.Function;

public class NewArrayBuilder<E, A> extends CacheStateBuilder<A> implements ArrayBuilder<A> {

    private final A array;
    private @Nullable Function<Integer, E> eachElement;

    NewArrayBuilder(A array) {
        if (!array.getClass().isArray()) {
            throw new IllegalStateException("Must be array: " + array);
        }
        this.array = array;
    }

    NewArrayBuilder(Class<A> arrayType, int length) {
        this.array = ArrayHelper.newArray(arrayType.getComponentType(), length);
    }

    NewArrayBuilder(Type arrayType, int length) {
        this((Class<A>) TypeHelper.getRawClass(arrayType).getComponentType(), length);
    }

    public NewArrayBuilder<E, A> setEachElement(Function<Integer, E> eachElement) {
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
