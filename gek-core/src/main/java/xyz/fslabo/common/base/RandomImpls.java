package xyz.fslabo.common.base;

import xyz.fslabo.annotations.Immutable;
import xyz.fslabo.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

final class RandomImpls {

    static final class RandomSupplierImpl<T> implements RandomSupplier<T> {

        private final Random random;
        private final List<Node<T>> nodes;
        private final int min;
        private final int max;

        @SafeVarargs
        RandomSupplierImpl(Random random, Pair<T>... pairs) {
            this.random = random;
            this.nodes = new ArrayList<>(pairs.length);
            int totalScore = 0;
            for (Pair<T> pair : pairs) {
                this.nodes.add(new Node<>(totalScore, totalScore + pair.getScore(), pair.getSupplier()));
                totalScore += pair.getScore();
            }
            this.min = nodes.get(0).from;
            this.max = nodes.get(nodes.size() - 1).to;
        }

        RandomSupplierImpl(Random random, Iterable<Pair<T>> pairs) {
            this.random = random;
            this.nodes = new ArrayList<>();
            int totalScore = 0;
            for (Pair<T> pair : pairs) {
                this.nodes.add(new Node<>(totalScore, totalScore + pair.getScore(), pair.getSupplier()));
                totalScore += pair.getScore();
            }
            this.min = nodes.get(0).from;
            this.max = nodes.get(nodes.size() - 1).to;
        }

        @Override
        public T get() {
            int next = random.nextInt(max - min) + min;
            Node<T> node = binarySearch(next);
            if (node == null) {
                throw new IllegalStateException("Child supplier cannot be found.");
            }
            return node.supplier.get();
        }

        @Nullable
        private Node<T> binarySearch(int next) {
            int left = 0;
            int right = nodes.size() - 1;
            while (left <= right) {
                int mid = left + (right - left) / 2;
                Node<T> node = nodes.get(mid);
                int compare = compare(next, node);
                if (compare == 0) {
                    return node;
                }
                if (compare > 0) {
                    left = mid + 1;
                } else {
                    right = mid - 1;
                }
            }
            return null;
        }

        private int compare(int next, Node<T> node) {
            if (next < node.from) {
                return -1;
            }
            if (next >= node.to) {
                return 1;
            }
            return 0;
        }

        @Override
        public List<T> get(int num) {
            List<T> result = new ArrayList<>(num);
            for (int i = 0; i < num; i++) {
                result.add(get());
            }
            return result;
        }
    }

    @Immutable
    private static final class Node<T> {

        private final int from;
        private final int to;
        private final Supplier<T> supplier;

        private Node(int from, int to, Supplier<T> supplier) {
            this.from = from;
            this.to = to;
            this.supplier = supplier;
        }
    }
}
