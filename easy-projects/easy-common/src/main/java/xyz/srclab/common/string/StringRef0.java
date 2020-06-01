package xyz.srclab.common.string;

import kotlin.text.StringsKt;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Checker;

/**
 * @author sunqian
 */
final class StringRef0 {

    private static final class StringRefImpl<T extends CharSequence> implements StringRef<T> {

        private final T origin;
        private final int start;
        private final int end;

        StringRefImpl(T origin, int start, int end) {
            this.origin = origin;
            this.start = start;
            this.end = end;
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
            Checker.checkBounds(length(), start, end);
            int offset = this.start + start;
            int length = end - start;
            return new StringRefImpl<>(getOrigin(), offset, offset + length + 1);
        }

        @Override
        public StringRef<T> trim() {
            CharSequence charSequence = StringsKt.trimS(this);
            return charSequence == this ? this :

        }

        @Override
        public StringRef<T> trimStart() {
            return null;
        }

        @Override
        public StringRef<T> trimEnd() {
            return null;
        }

        @Override
        public T getOrigin() {
            return origin;
        }

        @Override
        public String toString() {
            return getSubSequence().toString();
        }

        private @Nullable CharSequence subCache;

        private CharSequence getSubSequence() {
            if (subCache == null) {
                subCache = origin.subSequence(start, end);
            }
            return subCache;
        }
    }
}
