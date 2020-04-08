package xyz.srclab.common.test.mark;

import xyz.srclab.common.reflect.SignatureHelper;

public class MarkHelper {

    public static Object generateDefaultMark(Marked marked, Object key) {
        return SignatureHelper.signClass(marked.getClass())
                + ":"
                + key
                + ":"
                + System.identityHashCode(key);
    }
}
