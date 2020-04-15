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
}
