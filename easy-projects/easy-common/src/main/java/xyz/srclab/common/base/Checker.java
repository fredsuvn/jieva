package xyz.srclab.common.base;

import java.util.NoSuchElementException;

/**
 * @author sunqian
 */
public class Checker {

    public static void checkSubBounds(int length, int start, int end) {
        if (start > end) {
            throw new IllegalArgumentException("Sub bounds setting error: start : " + start + ", end: " + end);
        }
        if (start < 0 || end > length) {
            throw new IndexOutOfBoundsException("start: " + start + ", end: " + end);
        }
    }

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

    public static void checkElement(boolean expression) {
        if (!expression) {
            throw new NoSuchElementException();
        }
    }

    public static void checkElementByKey(boolean expression, Object key) {
        if (!expression) {
            throw new NoSuchElementException("key: " + key);
        }
    }
}
