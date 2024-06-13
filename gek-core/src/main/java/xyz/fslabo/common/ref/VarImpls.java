package xyz.fslabo.common.ref;

import lombok.AllArgsConstructor;
import xyz.fslabo.annotations.Nullable;

final class VarImpls {

    static <T> Var<T> newVar(@Nullable T value) {
        return new VarImpl<>(value);
    }

    static BooleanVar newBooleanVar(boolean value) {
        return new BooleanVarImpl(value);
    }

    static IntVar newIntVar(int value) {
        return new IntVarImpl(value);
    }

    static LongVar newLongVar(long value) {
        return new LongVarImpl(value);
    }

    static ShortVar newShortVar(short value) {
        return new ShortVarImpl(value);
    }

    static FloatVar newFloatVar(float value) {
        return new FloatVarImpl(value);
    }

    static DoubleVar newDoubleVar(double value) {
        return new DoubleVarImpl(value);
    }

    static ByteVar newByteVar(byte value) {
        return new ByteVarImpl(value);
    }

    static CharVar newCharVar(char value) {
        return new CharVarImpl(value);
    }

    @AllArgsConstructor
    private static final class VarImpl<T> implements Var<T> {

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
    private static final class BooleanVarImpl implements BooleanVar {

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
    private static final class IntVarImpl implements IntVar {

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
    private static final class LongVarImpl implements LongVar {

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
    private static final class ShortVarImpl implements ShortVar {

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
    private static final class FloatVarImpl implements FloatVar {

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
    private static final class DoubleVarImpl implements DoubleVar {

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
    private static final class ByteVarImpl implements ByteVar {

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
    private static final class CharVarImpl implements CharVar {

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
