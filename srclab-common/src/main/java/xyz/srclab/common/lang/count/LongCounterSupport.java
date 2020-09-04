package xyz.srclab.common.lang.count;

import java.util.concurrent.atomic.AtomicLong;

final class LongCounterSupport {

    static LongCounter newSimpleLongCounter() {
        return new SimpleLongCounter(0);
    }

    static LongCounter newSimpleLongCounter(long init) {
        return new SimpleLongCounter(init);
    }

    static LongCounter newAtomicLongCounter() {
        return new AtomicLongCounter(0);
    }

    static LongCounter newAtomicLongCounter(long init) {
        return new AtomicLongCounter(init);
    }

    private static final class SimpleLongCounter implements LongCounter {

        private long value;

        private SimpleLongCounter(long init) {
            this.value = init;
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

    private static final class AtomicLongCounter implements LongCounter {

        private final AtomicLong atomicLong;

        private AtomicLongCounter(long init) {
            this.atomicLong = new AtomicLong(init);
        }

        @Override
        public long get() {
            return atomicLong.get();
        }

        @Override
        public void set(long value) {
            atomicLong.set(value);
        }

        @Override
        public void add(long value) {
            atomicLong.addAndGet(value);
        }

        @Override
        public long getAndSet(long value) {
            return atomicLong.getAndSet(value);
        }

        @Override
        public long getAndAdd(long value) {
            return atomicLong.getAndAdd(value);
        }

        @Override
        public long addAndGet(long value) {
            return atomicLong.addAndGet(value);
        }

        @Override
        public long getAndIncrement() {
            return atomicLong.getAndIncrement();
        }

        @Override
        public long incrementAndGet() {
            return atomicLong.incrementAndGet();
        }

        @Override
        public long getAndDecrement() {
            return atomicLong.getAndDecrement();
        }

        @Override
        public long decrementAndGet() {
            return atomicLong.decrementAndGet();
        }

        @Override
        public void clear() {
            atomicLong.set(0);
        }
    }
}
