package xyz.srclab.common.lang.tuple;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Cast;
import xyz.srclab.common.base.Checker;
import xyz.srclab.common.string.StringHelper;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author sunqian
 */
final class Tuple0 {

    static Tuple newTuple(Object... values) {
        return new TupleImpl(values);
    }

    static MutableTuple newMutableTuple(Object... values) {
        return new MutableTupleImpl(values);
    }

    static MutableTuple newMutableTuple(int length) {
        return new MutableTupleImpl(new Object[length]);
    }

    static <A, B> Pair<A, B> newPair(@Nullable A first, @Nullable B second) {
        return new PairImpl<>(first, second);
    }

    static <A, B> MutablePair<A, B> newMutablePair() {
        return new MutablePairImpl<>();
    }

    static <A, B> MutablePair<A, B> newMutablePair(@Nullable A first, @Nullable B second) {
        return new MutablePairImpl<>(first, second);
    }

    static <A, B, C> Triple<A, B, C> newTriple(@Nullable A first, @Nullable B second, @Nullable C third) {
        return new TripleImpl<>(first, second, third);
    }

    static <A, B, C> MutableTriple<A, B, C> newMutableTriple() {
        return new MutableTripleImpl<>();
    }

    static <A, B, C> MutableTriple<A, B, C> newMutableTriple(
            @Nullable A first, @Nullable B second, @Nullable C third) {
        return new MutableTripleImpl<>(first, second, third);
    }

    private static abstract class TupleImplBase implements Tuple {

        protected final Object[] values;

        protected TupleImplBase(Object... values) {
            this.values = values;
        }

        @Override
        public <T> @Nullable T get(int index) {
            return Cast.nullable(values[index]);
        }

        @Override
        public <T> T getNonNull(int index) throws NullPointerException {
            return Objects.requireNonNull(get(index));
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (!(object instanceof TupleImplBase)) {
                return false;
            }
            TupleImplBase that = (TupleImplBase) object;
            return Arrays.equals(values, that.values);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(values);
        }

        @Override
        public String toString() {
            return "(" + StringHelper.join(", ", values) + ")";
        }
    }

    private static final class TupleImpl extends TupleImplBase {
        private TupleImpl(Object... values) {
            super(values);
        }
    }

    private static final class MutableTupleImpl extends TupleImplBase implements MutableTuple {

        private MutableTupleImpl(Object... values) {
            super(values);
        }

        @Override
        public void set(int index, @Nullable Object value) {
            this.values[index] = value;
        }
    }

    private static final class PairImpl<A, B> implements Pair<A, B> {

        private final @Nullable A first;
        private final @Nullable B second;

        private PairImpl(@Nullable A first, @Nullable B second) {
            this.first = first;
            this.second = second;
        }

        @Override
        @Nullable
        public A first() {
            return first;
        }

        @Override
        @Nullable
        public B second() {
            return second;
        }

        @Override
        public <T> @Nullable T get(int index) {
            Checker.checkIndex(index >= 0 && index < 2, index);
            return Cast.nullable(index == 0 ? first : second);
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) return true;
            if (object == null || getClass() != object.getClass()) return false;
            PairImpl<?, ?> pair = (PairImpl<?, ?>) object;
            return Objects.equals(first, pair.first) &&
                    Objects.equals(second, pair.second);
        }

        @Override
        public int hashCode() {
            return Objects.hash(first, second);
        }

        @Override
        public String toString() {
            return "(" + first + ", " + second + ")";
        }
    }

    private static final class MutablePairImpl<A, B> implements MutablePair<A, B> {

        private @Nullable A first;
        private @Nullable B second;

        private MutablePairImpl() {
            this(null, null);
        }

        private MutablePairImpl(@Nullable A first, @Nullable B second) {
            this.first = first;
            this.second = second;
        }

        @Override
        @Nullable
        public A first() {
            return first;
        }

        @Override
        @Nullable
        public B second() {
            return second;
        }

        @Override
        public <T> @Nullable T get(int index) {
            Checker.checkIndex(index >= 0 && index < 2, index);
            return Cast.nullable(index == 0 ? first : second);
        }

        @Override
        public void first(@Nullable A value) {
            first = value;
        }

        @Override
        public void second(@Nullable B value) {
            second = value;
        }

        @Override
        public void set(int index, @Nullable Object value) {
            Checker.checkIndex(index >= 0 && index < 2, index);
            if (index == 0) {
                first(Cast.nullable(value));
            } else {
                second(Cast.nullable(value));
            }
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) return true;
            if (object == null || getClass() != object.getClass()) return false;
            MutablePairImpl<?, ?> that = (MutablePairImpl<?, ?>) object;
            return Objects.equals(first, that.first) &&
                    Objects.equals(second, that.second);
        }

        @Override
        public int hashCode() {
            return Objects.hash(first, second);
        }

        @Override
        public String toString() {
            return "(" + first + ", " + second + ")";
        }
    }

    private static final class TripleImpl<A, B, C> implements Triple<A, B, C> {

        private final @Nullable A first;
        private final @Nullable B second;
        private final @Nullable C third;

        private TripleImpl(@Nullable A first, @Nullable B second, @Nullable C third) {
            this.first = first;
            this.second = second;
            this.third = third;
        }

        @Override
        @Nullable
        public A first() {
            return first;
        }

        @Override
        @Nullable
        public B second() {
            return second;
        }

        @Override
        @Nullable
        public C third() {
            return third;
        }

        @Override
        public <T> @Nullable T get(int index) {
            Checker.checkIndex(index >= 0 && index < 3, index);
            return Cast.nullable(index == 0 ? first : (index == 1 ? second : third));
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) return true;
            if (object == null || getClass() != object.getClass()) return false;
            TripleImpl<?, ?, ?> triple = (TripleImpl<?, ?, ?>) object;
            return Objects.equals(first, triple.first) &&
                    Objects.equals(second, triple.second) &&
                    Objects.equals(third, triple.third);
        }

        @Override
        public int hashCode() {
            return Objects.hash(first, second, third);
        }

        @Override
        public String toString() {
            return "(" + first + ", " + second + ", " + third;
        }
    }

    private static final class MutableTripleImpl<A, B, C> implements MutableTriple<A, B, C> {

        private @Nullable A first;
        private @Nullable B second;
        private @Nullable C third;

        private MutableTripleImpl() {
            this(null, null, null);
        }

        private MutableTripleImpl(@Nullable A first, @Nullable B second, @Nullable C third) {
            this.first = first;
            this.second = second;
            this.third = third;
        }

        @Override
        @Nullable
        public A first() {
            return first;
        }

        @Override
        @Nullable
        public B second() {
            return second;
        }

        @Override
        @Nullable
        public C third() {
            return third;
        }

        @Override
        public <T> @Nullable T get(int index) {
            Checker.checkIndex(index >= 0 && index < 3, index);
            return Cast.nullable(index == 0 ? first : (index == 1 ? second : third));
        }

        @Override
        public void first(@Nullable A value) {
            first = value;
        }

        @Override
        public void second(@Nullable B value) {
            second = value;
        }

        @Override
        public void third(@Nullable C value) {
            third = value;
        }

        @Override
        public void set(int index, @Nullable Object value) {
            Checker.checkIndex(index >= 0 && index < 3, index);
            if (index == 0) {
                first(Cast.nullable(value));
            } else if (index == 1) {
                second(Cast.nullable(value));
            } else {
                third(Cast.nullable(value));
            }
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) return true;
            if (object == null || getClass() != object.getClass()) return false;
            MutableTripleImpl<?, ?, ?> that = (MutableTripleImpl<?, ?, ?>) object;
            return Objects.equals(first, that.first) &&
                    Objects.equals(second, that.second) &&
                    Objects.equals(third, that.third);
        }

        @Override
        public int hashCode() {
            return Objects.hash(first, second, third);
        }

        @Override
        public String toString() {
            return "(" + first + ", " + second + ", " + third;
        }
    }
}
