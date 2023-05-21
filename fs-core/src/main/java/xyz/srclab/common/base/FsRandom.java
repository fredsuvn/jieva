package xyz.srclab.common.base;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Random to obtain specified value of type {@link T} in given rules. Using {@link Builder} to build.
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
     * Returns specified value in given rules.
     */
    T next();

    /**
     * Builder to create {@link FsRandom}.
     * <p>
     * Use {@link #score(int, Object)} method to specify the score for specified value.
     * When call the {@link FsRandom#next()} method, each value's probability of occurrence is proportional to the
     * percentage of its specified score to the total score.
     */
    class Builder<T> {

        private int currentScore = 0;
        private List<Part<T>> parts = new LinkedList<>();
        private Random random;

        /**
         * Specifies the score for specified value.
         * <p>
         * When call the {@link FsRandom#next()} method, each value's probability of occurrence is proportional to the
         * percentage of its specified score to the total score.
         *
         * @param score the score
         * @param value specified value
         */
        public <R extends T> Builder<R> score(int score, T value) {
            parts.add(new Part<>(currentScore, currentScore + score, value));
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
            private final List<Part<T>> parts;

            private Impl(Random random, List<Part<T>> parts) {
                this.random = random;
                this.parts = parts;
            }

            @Override
            public T next() {
                int min = parts.get(0).from;
                int max = parts.get(parts.size() - 1).to;
                int next = random.nextInt(max - min) + min;
                for (Part<T> part : parts) {
                    if (next >= part.from && next < part.to) {
                        return part.value;
                    }
                }
                //never reach
                throw new IllegalStateException("Random part cannot find.");
            }
        }

        private static final class Part<T> {

            private final int from;
            private final int to;
            private final T value;

            private Part(int from, int to, T value) {
                this.from = from;
                this.to = to;
                this.value = value;
            }
        }
    }
}
