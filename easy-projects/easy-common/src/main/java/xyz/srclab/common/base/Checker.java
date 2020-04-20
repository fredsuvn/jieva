package xyz.srclab.common.base;

/**
 * @author sunqian
 */
public class Checker {

    public static void checkBoundsFromTo(int length, int from, int to) {
        if (from > to) {
            throw new IllegalArgumentException("from > to");
        }
        if (from < 0 || to > length) {
            throw new IndexOutOfBoundsException("from: " + from + ", to: " + to);
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
}
