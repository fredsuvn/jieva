package xyz.srclab.test.mark;

public class MarkHelper {

    public static Object generateDefaultMark(Marked marked, Object key) {
        return marked.getClass().getName()
                + ":"
                + key
                + ":"
                + System.identityHashCode(key);
    }
}
