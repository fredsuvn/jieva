package xyz.srclab.common.base;

import java.util.NoSuchElementException;
import java.util.function.Supplier;

/**
 * @author sunqian
 */
public class Checker {

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
            throw new NoSuchElementException(String.valueOf(key));
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

    public static void checkBounds(int length, int start, int end) {
        if (start > end) {
            throw new IllegalArgumentException(
                    "Bounds setting error: start > end [start : " + start + ", end: " + end + "]");
        }
        if (start < 0) {
            throw new IndexOutOfBoundsException("start < 0: " + start);
        }
        if (end > length) {
            throw new IndexOutOfBoundsException("end > length [end: " + end + ", length: " + length + "]");
        }
    }
}
