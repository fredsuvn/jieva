package xyz.srclab.common.array;

import xyz.srclab.annotation.DefaultNullable;
import xyz.srclab.common.lang.TypeRef;
import xyz.srclab.common.reflect.type.TypeHelper;

import java.lang.reflect.Type;
import java.util.function.Function;

public interface ArrayBuilder<T> {

    static <T> ArrayBuilder<T> withArray(T[] array) {
        return new ArrayBuilderImpl<>(array);
    }

    static <T> ArrayBuilder<T> withArray(Class<T> type, int length) {
        return new ArrayBuilderImpl<>(type, length);
    }

    static <T> ArrayBuilder<T> withArray(TypeRef<T> typeRef, int length) {
        return new ArrayBuilderImpl<>(typeRef.getType(), length);
    }

    ArrayBuilder<T> setEachElement(Function<Integer, T> eachElement);

    T[] build();

    final class ArrayBuilderImpl<T> implements ArrayBuilder<T> {

        private final T[] array;
        private @DefaultNullable Function<Integer, T> eachElement;

        private ArrayBuilderImpl(T[] array) {
            this.array = array;
        }

        private ArrayBuilderImpl(Class<T> type, int length) {
            this.array = ArrayHelper.newArray(type, length);
        }

        private ArrayBuilderImpl(Type type, int length) {
            this(TypeHelper.getRawClass(type), length);
        }

        public ArrayBuilderImpl<T> setEachElement(Function<Integer, T> eachElement) {
            this.eachElement = eachElement;
            return this;
        }

        @Override
        public T[] build() {
            if (eachElement != null) {
                for (int i = 0; i < array.length; i++) {
                    array[i] = eachElement.apply(i);
                }
            }
            return array;
        }
    }
}
