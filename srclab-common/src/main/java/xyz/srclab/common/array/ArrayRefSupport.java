package xyz.srclab.common.array;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Check;

final class ArrayRefSupport {

    static <T> ArrayRef<T> newArrayRef(T[] array) {
        return new ArrayRefImpl<>(array);
    }

    static <T> ArrayRef<T> newArrayRef(T[] array, int startIndex, int endIndex) {
        Check.checkRangeInBounds(startIndex, endIndex, array.length);
        return new ArrayRefImpl<>(array, startIndex, endIndex);
    }

    private static final class ArrayRefImpl<T> implements ArrayRef<T> {

        private final T[] origin;
        private final int startIndex;
        private final int endIndex;
        private final Class<?> componentType;

        ArrayRefImpl(T[] array) {
            this(array, 0, array.length);
        }

        ArrayRefImpl(T[] array, int startIndex, int endIndex) {
            this.origin = array;
            this.startIndex = startIndex;
            this.endIndex = endIndex;
            this.componentType = array.getClass().getComponentType();
        }

        @Override
        public T[] origin() {
            return origin;
        }

        @Override
        public int originStartIndex() {
            return startIndex;
        }

        @Override
        public int originEndIndex() {
            return endIndex;
        }

        @Override
        public T get(int index) {
            return origin[startIndex + index];
        }

        @Override
        public ArrayRef<T> set(int index, @Nullable T value) {
            origin[startIndex + index] = value;
            return this;
        }

        @Override
        public int length() {
            return endIndex - startIndex;
        }

        @Override
        public Class<?> componentType() {
            return componentType;
        }
    }
}
