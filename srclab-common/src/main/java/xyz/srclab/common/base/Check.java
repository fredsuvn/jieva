package xyz.srclab.common.base;

import java.util.NoSuchElementException;
import java.util.function.Supplier;

/**
 * @author sunqian
 */
public class Check {

    public static void checkArguments(boolean expression) {
        if (!expression) {
            throw new IllegalArgumentException();
        }
    }

    public static void checkArguments(boolean expression, String message) {
        if (!expression) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void checkArguments(boolean expression, Supplier<String> messageSupplier) {
        if (!expression) {
            throw new IllegalArgumentException(messageSupplier.get());
        }
    }

    public static void checkState(boolean expression) {
        if (!expression) {
            throw new IllegalStateException();
        }
    }

    public static void checkState(boolean expression, String message) {
        if (!expression) {
            throw new IllegalStateException(message);
        }
    }

    public static void checkState(boolean expression, Supplier<String> messageSupplier) {
        if (!expression) {
            throw new IllegalStateException(messageSupplier.get());
        }
    }

    public static void checkNull(boolean expression) {
        if (!expression) {
            throw new NullPointerException();
        }
    }

    public static void checkNull(boolean expression, String message) {
        if (!expression) {
            throw new NullPointerException(message);
        }
    }

    public static void checkNull(boolean expression, Supplier<String> messageSupplier) {
        if (!expression) {
            throw new NullPointerException(messageSupplier.get());
        }
    }

    public static void checkSupported(boolean expression) {
        if (!expression) {
            throw new UnsupportedOperationException();
        }
    }

    public static void checkSupported(boolean expression, String message) {
        if (!expression) {
            throw new UnsupportedOperationException(message);
        }
    }

    public static void checkSupported(boolean expression, Supplier<String> messageSupplier) {
        if (!expression) {
            throw new UnsupportedOperationException(messageSupplier.get());
        }
    }

    public static void checkElement(boolean expression) {
        if (!expression) {
            throw new NoSuchElementException();
        }
    }

    public static void checkElement(boolean expression, Object key) {
        if (!expression) {
            throw new NoSuchElementException(ToString.toString(key));
        }
    }

    public static void checkIndex(boolean expression) {
        if (!expression) {
            throw new IndexOutOfBoundsException();
        }
    }

    public static void checkIndex(boolean expression, int index) {
        if (!expression) {
            throw new IndexOutOfBoundsException("index: " + index);
        }
    }

    public static void checkIndex(boolean expression, Supplier<String> messageSupplier) {
        if (!expression) {
            throw new IndexOutOfBoundsException(messageSupplier.get());
        }
    }

    public static void checkIndexRange(int start, int end, int length) {
        if (start > end) {
            throw new IllegalArgumentException(
                    "Index range error: start > end [start : " + start + ", end: " + end + "]");
        }
        if (start < 0) {
            throw new IndexOutOfBoundsException("Index range error: start < 0 [start: " + start + "]");
        }
        if (end > length) {
            throw new IndexOutOfBoundsException(
                    "Index range error: end > length [end: " + end + ", length: " + length + "]");
        }
    }

    public static void checkIndexRange(int start, int end, int originStart, int originEnd) {
        if (originStart > originEnd) {
            throw new IllegalArgumentException(
                    "Index range error: originStart > originEnd " +
                            "[originStart : " + originStart + ", originEnd: " + originEnd + "]");
        }
        if (start > end) {
            throw new IllegalArgumentException(
                    "Index range error: start > end [start : " + start + ", end: " + end + "]");
        }
        if (start < originStart) {
            throw new IndexOutOfBoundsException(
                    "Index range error: start < originStart " +
                            "[start: " + start + ", originStart: " + originStart + "]");
        }
        if (end > originEnd) {
            throw new IndexOutOfBoundsException(
                    "Index range error: end > originEnd " +
                            "[end: " + end + ", originEnd: " + originEnd + "]");
        }
    }
}
