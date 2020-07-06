package xyz.srclab.common.lang.count;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author sunqian
 */
public class CounterSupport {

    static Counter newCounter() {
        return new CounterImpl();
    }

    static Counter newCounter(long value) {
        return new CounterImpl(value);
    }

    static Counter newAtomicCounter() {
        return new AtomicCounterImpl();
    }

    static Counter newAtomicCounter(long value) {
        return new AtomicCounterImpl(value);
    }

    private static final class CounterImpl implements Counter {

        private long value;

        private CounterImpl() {
            this(0);
        }

        private CounterImpl(long value) {
            this.value = value;
        }

        @Override
        public long getLong() {
            return value;
        }

        @Override
        public void setLong(long value) {
            this.value = value;
        }

        @Override
        public long addAndGetLong(long value) {
            this.value += value;
            return this.value;
        }

        @Override
        public long getLongAndAdd(long value) {
            long temp = this.value;
            this.value += value;
            return temp;
        }

        @Override
        public long getLongAndIncrement() {
            return value++;
        }

        @Override
        public long incrementAndGetLong() {
            return ++value;
        }
    }

    private static final class AtomicCounterImpl implements Counter {

        private final AtomicLong value;

        private AtomicCounterImpl() {
            this.value = new AtomicLong();
        }

        private AtomicCounterImpl(long value) {
            this.value = new AtomicLong(value);
        }

        @Override
        public long getLong() {
            return value.get();
        }

        @Override
        public void setLong(long value) {
            this.value.set(value);
        }

        @Override
        public long addAndGetLong(long value) {
            return this.value.addAndGet(value);
        }

        @Override
        public long getLongAndAdd(long value) {
            return this.value.getAndAdd(value);
        }

        @Override
        public long getLongAndIncrement() {
            return value.getAndIncrement();
        }

        @Override
        public long incrementAndGetLong() {
            return value.incrementAndGet();
        }
    }
}
