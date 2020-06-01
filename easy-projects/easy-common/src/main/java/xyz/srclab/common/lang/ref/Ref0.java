package xyz.srclab.common.lang.ref;

import xyz.srclab.annotation.Nullable;

/**
 * @author sunqian
 */
final class Ref0 {

    static <T> Ref<T> newRef() {
        return new RefImpl<>();
    }

    static <T> Ref<T> newRef(@Nullable T value) {
        return new RefImpl<>(value);
    }

    static BooleanRef newBooleanRef() {
        return new BooleanRefImpl();
    }

    static BooleanRef newBooleanRef(boolean value) {
        return new BooleanRefImpl(value);
    }

    static ByteRef newByteRef() {
        return new ByteRefImpl();
    }

    static ByteRef newByteRef(byte value) {
        return new ByteRefImpl(value);
    }

    static ShortRef newShortRef() {
        return new ShortRefImpl();
    }

    static ShortRef newShortRef(short value) {
        return new ShortRefImpl(value);
    }

    static CharRef newCharRef() {
        return new CharRefImpl();
    }

    static CharRef newCharRef(char value) {
        return new CharRefImpl(value);
    }

    static IntRef newIntRef() {
        return new IntRefImpl();
    }

    static IntRef newIntRef(int value) {
        return new IntRefImpl(value);
    }

    static LongRef newLongRef() {
        return new LongRefImpl();
    }

    static LongRef newLongRef(long value) {
        return new LongRefImpl(value);
    }

    static FloatRef newFloatRef() {
        return new FloatRefImpl();
    }

    static FloatRef newFloatRef(float value) {
        return new FloatRefImpl(value);
    }

    static DoubleRef newDoubleRef() {
        return new DoubleRefImpl();
    }

    static DoubleRef newDoubleRef(double value) {
        return new DoubleRefImpl(value);
    }

    private static final class RefImpl<T> implements Ref<T> {

        private @Nullable T value;

        RefImpl() {
        }

        RefImpl(@Nullable T value) {
            set(value);
        }

        @Override
        public T get() {
            return value;
        }

        @Override
        public void set(@Nullable T value) {
            this.value = value;
        }
    }

    private static final class BooleanRefImpl implements BooleanRef {

        private boolean value;

        private BooleanRefImpl() {
        }

        private BooleanRefImpl(boolean value) {
            set(value);
        }

        @Override
        public boolean get() {
            return value;
        }

        @Override
        public void set(boolean value) {
            this.value = value;
        }
    }

    private static final class ByteRefImpl implements ByteRef {

        private byte value;

        private ByteRefImpl() {
        }

        private ByteRefImpl(byte value) {
            set(value);
        }

        @Override
        public byte get() {
            return value;
        }

        @Override
        public void set(byte value) {
            this.value = value;
        }
    }

    private static final class ShortRefImpl implements ShortRef {

        private short value;

        private ShortRefImpl() {
        }

        private ShortRefImpl(short value) {
            set(value);
        }

        @Override
        public short get() {
            return value;
        }

        @Override
        public void set(short value) {
            this.value = value;
        }
    }

    private static final class CharRefImpl implements CharRef {

        private char value;

        private CharRefImpl() {
        }

        private CharRefImpl(char value) {
            set(value);
        }

        @Override
        public char get() {
            return value;
        }

        @Override
        public void set(char value) {
            this.value = value;
        }
    }

    private static final class IntRefImpl implements IntRef {

        private int value;

        private IntRefImpl() {
        }

        private IntRefImpl(int value) {
            set(value);
        }

        @Override
        public int get() {
            return value;
        }

        @Override
        public void set(int value) {
            this.value = value;
        }
    }

    private static final class LongRefImpl implements LongRef {

        private long value;

        private LongRefImpl() {
        }

        private LongRefImpl(long value) {
            set(value);
        }

        @Override
        public long get() {
            return value;
        }

        @Override
        public void set(long value) {
            this.value = value;
        }
    }

    private static final class FloatRefImpl implements FloatRef {

        private float value;

        private FloatRefImpl() {
        }

        private FloatRefImpl(float value) {
            set(value);
        }

        @Override
        public float get() {
            return value;
        }

        @Override
        public void set(float value) {
            this.value = value;
        }
    }

    private static final class DoubleRefImpl implements DoubleRef {

        private double value;

        private DoubleRefImpl() {
        }

        private DoubleRefImpl(double value) {
            set(value);
        }

        @Override
        public double get() {
            return value;
        }

        @Override
        public void set(double value) {
            this.value = value;
        }
    }
}
