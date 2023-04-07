package xyz.srclab.common.base;

import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * Utilities for String.
 *
 * @author fredsuvn
 */
public class FsString {

    /**
     * Builds a String concatenated from given arguments
     *
     * @param args given arguments
     */
    public static String string(String... args) {
        int length = 0;
        for (String arg : args) {
            length += arg.length();
        }
        char[] chars = new char[length];
        int offset = 0;
        for (String arg : args) {
            arg.getChars(0, arg.length(), chars, offset);
            offset += arg.length();
        }
        return new String(chars);
    }

    /**
     * Builds a String concatenated from given arguments
     *
     * @param args given arguments
     */
    public static String string(Object... args) {
        if (FsObject.equals(String.class, args.getClass().getComponentType())) {
            return string((String[]) args);
        }
        return string(FsObject.array(args, new String[args.length], String::valueOf));
    }

    /**
     * Returns a lazy string concatenated from given arguments.
     * <p>
     * The returned string is lazy, it only computes the result of concatenating once
     * when first call the toString() method, before and after this, given arguments just are held and dropped.
     *
     * @param args given arguments
     */
    public static CharSequence lazyString(Object... args) {
        return LazyString.ofArray(args);
    }

    /**
     * Returns a lazy string concatenated from given arguments.
     * <p>
     * The returned string is lazy, it only computes the result of concatenating once
     * when first call the toString() method, before and after this, given arguments just are held and dropped.
     *
     * @param args given arguments
     */
    public static CharSequence lazyString(Iterable<?> args) {
        return LazyString.ofIterable(args);
    }

    /**
     * Returns a lazy string supplied from given supplier.
     * <p>
     * The returned string is lazy, it only computes the result of {@link Supplier#get()} once
     * when first call the toString() method, before and after this, given arguments just are held and dropped.
     *
     * @param supplier given supplier
     */
    public static CharSequence lazyString(Supplier<?> supplier) {
        return LazyString.ofSupplier(supplier);
    }

    private static final class LazyString {

        private static CharSequence ofArray(Object[] args) {
            return new OfArray(args);
        }

        private static CharSequence ofIterable(Iterable<?> args) {
            return new OfIterable(args);
        }

        private static CharSequence ofSupplier(Supplier<?> supplier) {
            return new OfSupplier(supplier);
        }

        private static abstract class Abs implements CharSequence {

            protected String toString;

            @Override
            public int length() {
                return toString().length();
            }

            @Override
            public char charAt(int index) {
                return toString().charAt(index);
            }

            @NotNull
            @Override
            public CharSequence subSequence(int start, int end) {
                return toString().subSequence(start, end);
            }

            @Override
            public String toString() {
                if (toString == null) {
                    toString = buildString();
                }
                return toString;
            }

            @Override
            public boolean equals(Object o) {
                return this == o;
            }

            @Override
            public int hashCode() {
                return toString().hashCode();
            }

            protected abstract String buildString();
        }

        private static final class OfArray extends Abs {
            private Object[] args;

            private OfArray(Object[] args) {
                this.args = args;
            }

            @Override
            protected String buildString() {
                String result = string(args);
                args = null;
                return result;
            }
        }

        private static final class OfIterable extends Abs {
            private Iterable<?> args;

            private OfIterable(Iterable<?> args) {
                this.args = args;
            }

            @Override
            protected String buildString() {
                StringBuilder sb = new StringBuilder();
                for (Object arg : args) {
                    sb.append(arg);
                }
                args = null;
                return sb.toString();
            }
        }

        private static final class OfSupplier extends Abs {
            private Supplier<?> supplier;

            private OfSupplier(Supplier<?> supplier) {
                this.supplier = supplier;
            }

            @Override
            protected String buildString() {
                String result = FsObject.toString(supplier.get());
                supplier = null;
                return result;
            }
        }
    }
}
