package xyz.srclab.common.base;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

/**
 * Random to obtain value of type {@link T} in given rules. Using {@link Builder} to build.
 *
 * @author fredsuvn
 */
public interface FsRandom<T> {

    /**
     * Returns a new builder to build {@link FsRandom}.
     */
    static <T> Builder<T> newBuilder() {
        return new Builder<>();
    }

    /**
     * Returns next random value in given rules.
     */
    T next();

    /**
     * Returns next {@code length} random values in given rules.
     *
     * @param length number of next random values
     */
    default List<T> nextList(int length) {
        ArrayList<T> result = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            result.add(next());
        }
        return result;
    }

    /**
     * Builder to create {@link FsRandom}.
     * <p>
     * Use {@link #score(int, Object)} and {@link #score(int, Supplier)}
     * methods to specify the score for specified value.
     * When call the {@link FsRandom#next()} method, each value's probability of occurrence is proportional to the
     * percentage of its specified score to the total score.
     */
    class Builder<T> {

        private int currentScore = 0;
        private List<Part> parts = new LinkedList<>();
        private Random random;

        /**
         * Specifies the score for specified value.
         * <p>
         * When call the {@link FsRandom#next()} method, each value's probability of occurrence is proportional to the
         * percentage of its specified score to the total score,
         * the specified value will be returned in that probability.
         *
         * @param score the score
         * @param value specified value
         */
        public <R extends T> Builder<R> score(int score, T value) {
            parts.add(new ValuePart<>(currentScore, currentScore + score, value));
            currentScore += score;
            return Fs.as(this);
        }

        /**
         * Specifies the score for specified supplier.
         * <p>
         * When call the {@link FsRandom#next()} method, each value's probability of occurrence is proportional to the
         * percentage of its specified score to the total score,
         * the value gotten from specified supplier will be returned in that probability.
         *
         * @param score    the score
         * @param supplier specified supplier
         */
        public <R extends T> Builder<R> score(int score, Supplier<T> supplier) {
            parts.add(new SupplierPart<>(currentScore, currentScore + score, supplier));
            currentScore += score;
            return Fs.as(this);
        }

        /**
         * Specifies the underlying random.
         *
         * @param random underlying random
         */
        public <R extends T> Builder<R> random(Random random) {
            this.random = random;
            return Fs.as(this);
        }

        public <R extends T> FsRandom<R> build() {
            return Fs.as(new Impl<>(Fs.notNull(random, new Random()), parts));
        }

        private static final class Impl<T> implements FsRandom<T> {

            private final Random random;
            private final List<Part> parts;

            private Impl(Random random, List<Part> parts) {
                this.random = random;
                this.parts = parts;
            }

            @Override
            public T next() {
                int min = parts.get(0).from;
                int max = parts.get(parts.size() - 1).to;
                int next = random.nextInt(max - min) + min;
                for (Part part : parts) {
                    if (next >= part.from && next < part.to) {
                        if (part instanceof ValuePart) {
                            return ((ValuePart<T>) part).value;
                        }
                        if (part instanceof SupplierPart) {
                            return ((SupplierPart<T>) part).supplier.get();
                        }
                        //never reach
                        throw new IllegalStateException("Unexpected random part type: " + part.getClass() + ".");
                    }
                }
                //never reach
                throw new IllegalStateException("Random part cannot be found.");
            }
        }

        private static class Part {

            private final int from;
            private final int to;

            private Part(int from, int to) {
                this.from = from;
                this.to = to;
            }
        }

        private static final class ValuePart<T> extends Part {

            private final T value;

            private ValuePart(int from, int to, T value) {
                super(from, to);
                this.value = value;
            }
        }

        private static final class SupplierPart<T> extends Part {

            private final Supplier<T> supplier;

            private SupplierPart(int from, int to, Supplier<T> supplier) {
                super(from, to);
                this.supplier = supplier;
            }
        }
    }
}
