package xyz.srclab.common.base;

import java.util.NoSuchElementException;

/**
 * @author sunqian
 */
public class Check {

    public static void checkArguments(boolean expression) {
        if (!expression) {
            throw new IllegalArgumentException();
        }
    }

    public static void checkArguments(boolean expression, Object message) {
        if (!expression) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void checkArguments(boolean expression, String messagePattern, Object... messageArgs) {
        if (!expression) {
            throw new IllegalArgumentException(Format.fastFormat(messagePattern, messageArgs));
        }
    }

    public static void checkState(boolean expression) {
        if (!expression) {
            throw new IllegalStateException();
        }
    }

    public static void checkState(boolean expression, Object message) {
        if (!expression) {
            throw new IllegalStateException(message);
        }
    }

    public static void checkState(boolean expression, String messagePattern, Object... messageArgs) {
        if (!expression) {
            throw new IllegalStateException(Format.fastFormat(messagePattern, messageArgs));
        }
    }

    public static void checkNull(boolean expression) {
        if (!expression) {
            throw new NullPointerException();
        }
    }

    public static void checkNull(boolean expression, Object message) {
        if (!expression) {
            throw new NullPointerException(message);
        }
    }

    public static void checkNull(boolean expression, String messagePattern, Object... messageArgs) {
        if (!expression) {
            throw new NullPointerException(Format.fastFormat(messagePattern, messageArgs));
        }
    }

    public static void checkSupported(boolean expression) {
        if (!expression) {
            throw new UnsupportedOperationException();
        }
    }

    public static void checkSupported(boolean expression, Object message) {
        if (!expression) {
            throw new UnsupportedOperationException(message);
        }
    }

    public static void checkSupported(boolean expression, String messagePattern, Object... messageArgs) {
        if (!expression) {
            throw new UnsupportedOperationException(Format.fastFormat(messagePattern, messageArgs));
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

    public static void checkElement(boolean expression, String messagePattern, Object... messageArgs) {
        if (!expression) {
            throw new NoSuchElementException(Format.fastFormat(messagePattern, messageArgs));
        }
    }

    public static void checkIndex(boolean expression) {
        if (!expression) {
            throw new IndexOutOfBoundsException();
        }
    }

    public static void checkIndex(boolean expression, Object index) {
        if (!expression) {
            throw new IndexOutOfBoundsException(ToString.toString(index));
        }
    }

    public static void checkIndex(boolean expression, String messagePattern, Object... messageArgs) {
        if (!expression) {
            throw new IndexOutOfBoundsException(Format.fastFormat(messagePattern, messageArgs));
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

    public static void checkIndexRange(int start, int end, int startBound, int endBound) {
        if (startBound > endBound) {
            throw new IllegalArgumentException(
                    "Range error: startBound > endBound " +
                            "[startBound : " + startBound + ", endBound: " + endBound + "]");
        }
        if (start > end) {
            throw new IndexOutOfBoundsException(
                    "Index range error: start > end [start : " + start + ", end: " + end + "]");
        }
        if (start < startBound) {
            throw new IndexOutOfBoundsException(
                    "Index range error: start < startBound " +
                            "[start: " + start + ", startBound: " + startBound + "]");
        }
        if (end > endBound) {
            throw new IndexOutOfBoundsException(
                    "Index range error: end > endBound " +
                            "[end: " + end + ", endBound: " + endBound + "]");
        }
    }

    public static void checkIndexIn(int index, int startBound, int endBound) {
        if (startBound > endBound) {
            throw new IllegalArgumentException(
                    "Range error: startBound > endBound " +
                            "[startBound : " + startBound + ", endBound: " + endBound + "]");
        }
        if (index < startBound || index > endBound) {
            throw new IndexOutOfBoundsException("Index: " + index +
                    "[startBound: " + startBound + ", endBound: " + endBound + "]");
        }
    }
}
