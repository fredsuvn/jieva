package xyz.fslabo.common.base.ref;

import lombok.AllArgsConstructor;
import xyz.fslabo.annotations.Nullable;

final class Impls {

    static <T> GekRef<T> newGekRef(@Nullable T value) {
        return new GekRefImpl<>(value);
    }

    static BooleanRef newBooleanRef(boolean value) {
        return new BooleanGekImpl(value);
    }

    static IntRef newIntRef(int value) {
        return new IntRefImpl(value);
    }

    static LongRef newLongRef(long value) {
        return new LongRefImpl(value);
    }

    static ShortRef newShortRef(short value) {
        return new ShortRefImpl(value);
    }

    static FloatRef newFloatRef(float value) {
        return new FloatRefImpl(value);
    }

    static DoubleRef newDoubleRef(double value) {
        return new DoubleRefImpl(value);
    }

    static ByteRef newByteRef(byte value) {
        return new ByteRefImpl(value);
    }

    static CharRef newCharRef(char value) {
        return new CharRefImpl(value);
    }

    @AllArgsConstructor
    private static final class GekRefImpl<T> implements GekRef<T> {

        private T value;

        @Override
        public @Nullable T get() {
            return value;
        }

        @Override
        public void set(@Nullable T value) {
            this.value = value;
        }
    }

    @AllArgsConstructor
    private static final class BooleanGekImpl implements BooleanRef {

        private boolean value;

        @Override
        public boolean get() {
            return value;
        }

        @Override
        public void set(boolean value) {
            this.value = value;
        }

        @Override
        public boolean toggleAndGet() {
            value = !value;
            return value;
        }

        @Override
        public boolean getAndToggle() {
            value = !value;
            return !value;
        }
    }

    @AllArgsConstructor
    private static final class IntRefImpl implements IntRef {

        private int value;

        @Override
        public int get() {
            return value;
        }

        @Override
        public void set(int value) {
            this.value = value;
        }

        @Override
        public int incrementAndGet() {
            return ++value;
        }

        @Override
        public int getAndIncrement() {
            return value++;
        }

        @Override
        public int add(int v) {
            value += v;
            return value;
        }
    }

    @AllArgsConstructor
    private static final class LongRefImpl implements LongRef {

        private long value;

        @Override
        public long get() {
            return value;
        }

        @Override
        public void set(long value) {
            this.value = value;
        }

        @Override
        public long incrementAndGet() {
            return ++value;
        }

        @Override
        public long getAndIncrement() {
            return value++;
        }

        @Override
        public long add(long v) {
            value += v;
            return value;
        }
    }

    @AllArgsConstructor
    private static final class ShortRefImpl implements ShortRef {

        private short value;

        @Override
        public short get() {
            return value;
        }

        @Override
        public void set(short value) {
            this.value = value;
        }

        @Override
        public short incrementAndGet() {
            return ++value;
        }

        @Override
        public short getAndIncrement() {
            return value++;
        }

        @Override
        public short add(short v) {
            value += v;
            return value;
        }
    }

    @AllArgsConstructor
    private static final class FloatRefImpl implements FloatRef {

        private float value;

        @Override
        public float get() {
            return value;
        }

        @Override
        public void set(float value) {
            this.value = value;
        }

        @Override
        public float add(float v) {
            value += v;
            return value;
        }
    }

    @AllArgsConstructor
    private static final class DoubleRefImpl implements DoubleRef {

        private double value;

        @Override
        public double get() {
            return value;
        }

        @Override
        public void set(double value) {
            this.value = value;
        }

        @Override
        public double add(double v) {
            value += v;
            return value;
        }
    }

    @AllArgsConstructor
    private static final class ByteRefImpl implements ByteRef {

        private byte value;

        @Override
        public byte get() {
            return value;
        }

        @Override
        public void set(byte value) {
            this.value = value;
        }

        @Override
        public byte incrementAndGet() {
            return ++value;
        }

        @Override
        public byte getAndIncrement() {
            return value++;
        }

        @Override
        public byte add(byte v) {
            value += v;
            return value;
        }
    }

    @AllArgsConstructor
    private static final class CharRefImpl implements CharRef {

        private char value;

        @Override
        public char get() {
            return value;
        }

        @Override
        public void set(char value) {
            this.value = value;
        }
    }
}
