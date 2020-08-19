package xyz.srclab.common.lang.count;

final class IntCounterSupport {

    static IntCounter newSimpleIntCounter() {
        return new SimpleIntCounter();
    }

    static IntCounter newSimpleIntCounter(int value) {
        return new SimpleIntCounter(value);
    }

    private static final class SimpleIntCounter implements IntCounter {

        private int value;

        SimpleIntCounter() {
            this(0);
        }

        SimpleIntCounter(int value) {
            this.value = value;
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
}
