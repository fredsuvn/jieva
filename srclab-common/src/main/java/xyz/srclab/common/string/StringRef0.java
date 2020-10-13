package xyz.srclab.common.string;

import xyz.srclab.common.base.Check;

import java.util.function.IntPredicate;

/**
 * @author sunqian
 */
final class StringRef0 {

    static <T extends CharSequence> StringRef<T> newStringRef(T origin, int start, int end) {
        Check.checkRangeInLength(start, end, origin.length());
        return new StringRefImpl<>(origin, start, end);
    }

    private static final class StringRefImpl<T extends CharSequence> implements StringRef<T> {

        private static final IntPredicate TRIM_PREDICATE = i -> i == ' ';

        private final T origin;
        private int start;
        private int end;

        private StringRefImpl(T origin, int start, int end) {
            this.origin = origin;
            this.start = start;
            this.end = end;
        }

        @Override
        public T getOrigin() {
            return origin;
        }

        @Override
        public int start() {
            return start;
        }

        @Override
        public void start(int start) {
            checkStart(start, end);
            this.start = start;
        }

        private void checkStart(int start, int end) {
            if (start < 0) {
                throw new IllegalArgumentException("start < 0: " + start);
            }
            if (start > end) {
                throw new IllegalArgumentException("start > end: start = " + start + ", end = " + end);
            }
        }

        @Override
        public int end() {
            return end;
        }

        @Override
        public void end(int end) {
            checkEnd(start, end);
            this.end = end;
        }

        private void checkEnd(int start, int end) {
            if (end < 0) {
                throw new IllegalArgumentException("end < 0: " + end);
            }
            if (end < start) {
                throw new IllegalArgumentException("end < start>: end = " + end + ", start = " + start);
            }
        }

        @Override
        public void trim() {
            trim(TRIM_PREDICATE);
        }

        @Override
        public void trim(IntPredicate predicate) {
            trimStart(predicate);
            trimEnd(predicate);
        }

        @Override
        public void trimStart() {
            trimStart(TRIM_PREDICATE);
        }

        @Override
        public void trimStart(IntPredicate predicate) {
            int start = this.start;
            while (start < this.end && predicate.test(origin.charAt(start))) {
                start++;
            }
            this.start = start;
        }

        @Override
        public void trimEnd() {
            trimEnd(TRIM_PREDICATE);
        }

        @Override
        public void trimEnd(IntPredicate predicate) {
            int end = this.end;
            while (end > this.start && predicate.test(origin.charAt(end - 1))) {
                end--;
            }
            this.end = end;
        }

        @Override
        public StringRef<T> clone() {
            return new StringRefImpl<>(origin, start, end);
        }

        @Override
        public int length() {
            return end - start;
        }

        @Override
        public char charAt(int index) {
            return origin.charAt(start + index);
        }

        @Override
        public StringRef<T> subSequence(int start, int end) {
            Check.checkRangeInLength(start, end, length());
            int offset = this.start + start;
            int length = end - start;
            return new StringRefImpl<>(origin, offset, offset + length);
        }

        @Override
        public String toString() {
            return origin.subSequence(start, end).toString();
        }
    }
}
