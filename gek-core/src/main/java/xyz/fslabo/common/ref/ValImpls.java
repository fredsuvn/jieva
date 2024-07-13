package xyz.fslabo.common.ref;

import lombok.AllArgsConstructor;
import xyz.fslabo.annotations.Nullable;

final class ValImpls {

    static Val<?> OF_NULL = of(null);

    static <T> Val<T> of(@Nullable T value) {
        return new ValImpl<>(value);
    }

    static BooleanVal ofBoolean(boolean value) {
        return new BooleanValImpl(value);
    }

    static IntVal ofInt(int value) {
        return new IntValImpl(value);
    }

    static LongVal ofLong(long value) {
        return new LongValImpl(value);
    }

    static ShortVal ofShort(short value) {
        return new ShortValImpl(value);
    }

    static FloatVal ofFloat(float value) {
        return new FloatValImpl(value);
    }

    static DoubleVal ofDouble(double value) {
        return new DoubleValImpl(value);
    }

    static ByteVal ofByte(byte value) {
        return new ByteValImpl(value);
    }

    static CharVal ofChar(char value) {
        return new CharValImpl(value);
    }

    @AllArgsConstructor
    private static final class ValImpl<T> implements Val<T> {

        private final T value;

        @Override
        public @Nullable T get() {
            return value;
        }
    }

    @AllArgsConstructor
    private static final class BooleanValImpl implements BooleanVal {

        private final boolean value;

        @Override
        public boolean get() {
            return value;
        }
    }

    @AllArgsConstructor
    private static final class IntValImpl implements IntVal {

        private final int value;

        @Override
        public int get() {
            return value;
        }
    }

    @AllArgsConstructor
    private static final class LongValImpl implements LongVal {

        private final long value;

        @Override
        public long get() {
            return value;
        }
    }

    @AllArgsConstructor
    private static final class ShortValImpl implements ShortVal {

        private final short value;

        @Override
        public short get() {
            return value;
        }
    }

    @AllArgsConstructor
    private static final class FloatValImpl implements FloatVal {

        private final float value;

        @Override
        public float get() {
            return value;
        }
    }

    @AllArgsConstructor
    private static final class DoubleValImpl implements DoubleVal {

        private final double value;

        @Override
        public double get() {
            return value;
        }
    }

    @AllArgsConstructor
    private static final class ByteValImpl implements ByteVal {

        private final byte value;

        @Override
        public byte get() {
            return value;
        }
    }

    @AllArgsConstructor
    private static final class CharValImpl implements CharVal {

        private final char value;

        @Override
        public char get() {
            return value;
        }
    }
}
