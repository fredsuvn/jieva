package xyz.srclab.common.lang.count;

import java.util.concurrent.atomic.AtomicInteger;

final class IntCounterSupport {

    static IntCounter newSimpleIntCounter() {
        return new SimpleIntCounter(0);
    }

    static IntCounter newSimpleIntCounter(int init) {
        return new SimpleIntCounter(init);
    }

    static IntCounter newAtomicIntCounter() {
        return new AtomicIntCounter(0);
    }

    static IntCounter newAtomicIntCounter(int init) {
        return new AtomicIntCounter(init);
    }

    private static final class SimpleIntCounter implements IntCounter {

        private int value;

        private SimpleIntCounter(int init) {
            this.value = init;
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

    private static final class AtomicIntCounter implements IntCounter {

        private final AtomicInteger atomicInteger;

        private AtomicIntCounter(int init) {
            this.atomicInteger = new AtomicInteger(init);
        }

        @Override
        public int get() {
            return atomicInteger.get();
        }

        @Override
        public void set(int value) {
            atomicInteger.set(value);
        }

        @Override
        public void add(int value) {
            atomicInteger.addAndGet(value);
        }

        @Override
        public int getAndSet(int value) {
            return atomicInteger.getAndSet(value);
        }

        @Override
        public int getAndAdd(int value) {
            return atomicInteger.getAndAdd(value);
        }

        @Override
        public int addAndGet(int value) {
            return atomicInteger.addAndGet(value);
        }

        @Override
        public int getAndIncrement() {
            return atomicInteger.getAndIncrement();
        }

        @Override
        public int incrementAndGet() {
            return atomicInteger.incrementAndGet();
        }

        @Override
        public int getAndDecrement() {
            return atomicInteger.getAndDecrement();
        }

        @Override
        public int decrementAndGet() {
            return atomicInteger.decrementAndGet();
        }

        @Override
        public void clear() {
            atomicInteger.set(0);
        }
    }
}
