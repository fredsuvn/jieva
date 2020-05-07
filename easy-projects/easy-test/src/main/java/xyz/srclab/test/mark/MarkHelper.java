package xyz.srclab.test.mark;

public class MarkHelper {

    public static Object generateMark(MarkTesting markTesting, Object key) {
        return markTesting.getClass().getName()
                + ":"
                + key
                + ":"
                + System.identityHashCode(key);
    }
}
